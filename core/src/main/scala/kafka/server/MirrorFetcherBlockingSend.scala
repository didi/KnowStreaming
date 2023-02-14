package kafka.server

import com.didichuxing.datachannel.kafka.config.HAClusterConfig
import kafka.cluster.BrokerEndPoint
import org.apache.kafka.clients._
import org.apache.kafka.common.Node
import org.apache.kafka.common.metrics.Metrics
import org.apache.kafka.common.network.{NetworkReceive, Selectable, Selector}
import org.apache.kafka.common.requests.AbstractRequest
import org.apache.kafka.common.requests.AbstractRequest.Builder
import org.apache.kafka.common.utils.{LogContext, Time}

import java.net.SocketTimeoutException
import scala.collection.JavaConverters._

/**
 * @author leewei
 * @date 2021/9/28
 */
class MirrorFetcherBlockingSend(sourceBroker: BrokerEndPoint,
                                config: HAClusterConfig,
                                metrics: Metrics,
                                time: Time,
                                fetcherId: Int,
                                clientId: String,
                                logContext: LogContext) extends BlockingSend {

  private val sourceNode = new Node(sourceBroker.id, sourceBroker.host, sourceBroker.port)
  private val socketTimeout: Int = config.getInt(HAClusterConfig.REQUEST_TIMEOUT_MS_CONFIG)

  private val networkClient = {
    val channelBuilder = ClientUtils.createChannelBuilder(config, time, logContext)
    val selector = new Selector(
      NetworkReceive.UNLIMITED,
      config.getLong(HAClusterConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG),
      metrics,
      time,
      "mirror-fetcher",
      Map("remote-cluster-id" -> sourceBroker.remoteCluster.get, "broker-id" -> sourceBroker.id.toString, "fetcher-id" -> fetcherId.toString).asJava,
      false,
      channelBuilder,
      logContext
    )
    val networkClient = new NetworkClient(
      selector,
      new ManualMetadataUpdater(),
      clientId,
      1,
      0,
      0,
      Selectable.USE_DEFAULT_BUFFER_SIZE,
      config.getInt(HAClusterConfig.RECEIVE_BUFFER_CONFIG),
      config.getInt(HAClusterConfig.REQUEST_TIMEOUT_MS_CONFIG),
      ClientDnsLookup.DEFAULT,
      time,
      true,
      new ApiVersions,
      logContext
    )
    networkClient
  }

  override def sendRequest(requestBuilder: Builder[_ <: AbstractRequest]): ClientResponse = {
    try {
      if (!NetworkClientUtils.awaitReady(networkClient, sourceNode, time, socketTimeout))
        throw new SocketTimeoutException(s"Failed to connect within $socketTimeout ms")
      else {
        val clientRequest = networkClient.newClientRequest(sourceBroker.id.toString, requestBuilder,
          time.milliseconds(), true)
        NetworkClientUtils.sendAndReceive(networkClient, clientRequest, time)
      }
    }
    catch {
      case e: Throwable =>
        networkClient.close(sourceBroker.id.toString)
        throw e
    }
  }

  override def initiateClose(): Unit = {
    networkClient.initiateClose()
  }

  def close(): Unit = {
    networkClient.close()
  }
}