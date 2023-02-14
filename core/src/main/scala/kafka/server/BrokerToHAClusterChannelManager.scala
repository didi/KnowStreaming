package kafka.server

import com.didichuxing.datachannel.kafka.config.HAClusterConfig
import com.didichuxing.datachannel.kafka.config.manager.ClusterConfigManager
import kafka.utils.ShutdownableThread
import org.apache.kafka.clients.{ApiVersions, ClientDnsLookup, ClientResponse, ClientUtils, CommonClientConfigs, ManualMetadataUpdater, Metadata, NetworkClient, NetworkClientUtils, RequestCompletionHandler}
import org.apache.kafka.common.Node
import org.apache.kafka.common.internals.ClusterResourceListeners
import org.apache.kafka.common.metrics.Metrics
import org.apache.kafka.common.network.{NetworkReceive, Selectable, Selector}
import org.apache.kafka.common.requests.AbstractRequest
import org.apache.kafka.common.security.auth.KafkaPrincipal
import org.apache.kafka.common.utils.{LogContext, Time}

import java.net.InetSocketAddress
import java.util
import java.util.Properties
import java.util.concurrent.LinkedBlockingDeque
import scala.collection.mutable
import scala.collection.JavaConverters._

object BrokerToHAClusterChannelManager {
  def apply(time: Time, metrics: Metrics, config: KafkaConfig, channelName: String, threadNamePrefix: String, retryTimeoutMs: Int = 30000): BrokerToHAClusterChannelManager = {
    new BrokerToHAClusterChannelManager(time, metrics, config, channelName, threadNamePrefix, retryTimeoutMs)
  }
}

class BrokerToHAClusterChannelManager(time: Time, metrics: Metrics, config: KafkaConfig, channelName: String, threadNamePrefix: String, retryTimeoutMs: Int
                                     ) extends ShutdownableThread(threadNamePrefix) with ConfigHandler {
  private val logContext = new LogContext(s"[BrokerToHAClusterChannelManager broker=${config.brokerId} name=$channelName] ")
  private val requestQueue = new LinkedBlockingDeque[BrokerToHAClusterQueueItem]()
  private val networkClientMap = mutable.Map[String, NetworkClient]()
  private val lock = new Object

  override def processConfigChanges(clusterId: String, props: Properties): Unit = lock synchronized {
    networkClientMap.get(clusterId).foreach { client =>
      client.close()
      networkClientMap -= clusterId
      info(s"HA Cluster $clusterId config $props has be chenged, do reload network client.")
    }
  }

  def getOrCreateNetworkClient(clusterId: String): Option[NetworkClient] = lock synchronized {
    if (!networkClientMap.contains(clusterId)) {
      try {
        if (ClusterConfigManager.existsHACluster(clusterId)) {
          val haClusterConfig = ClusterConfigManager.getHAClusterConfig(clusterId)
          val client = newRequestThread(haClusterConfig, clusterId)
          networkClientMap += (clusterId -> client)
        }
      } catch {
        case e: Exception =>
          error(s"Can not create ha cluster $clusterId network client", e)
      }
    }
    networkClientMap.get(clusterId)
  }

  def getReadyNode(now: Long, client: NetworkClient): Node = {
    val node = client.leastLoadedNode(now)
    if (node != null && NetworkClientUtils.awaitReady(client, node, time, 10000)) {
      return node
    }
    Node.noNode()
  }

  override def doWork(): Unit = {
    val request = pollFromRequestQueue()
    if (request != null) {
      val now = time.milliseconds()
      try {
        getOrCreateNetworkClient(request.haClusterId) match {
          case Some(client) =>
            val node = getReadyNode(now, client)
            if (now - request.createdTimeMs >= retryTimeoutMs) {
              requestQueue.remove(request)
              request.callback.onTimeout()
            } else {
              if (!node.equals(Node.noNode())) {
                requestQueue.remove(request)
                val clientRequest = client.newClientRequest(node.id().toString, request.request, request.createdTimeMs, true, config.requestTimeoutMs, handleResponse(request))
                client.send(clientRequest, now)
              }
            }
          case None =>
            requestQueue.remove(request)
            request.callback.onError(s"${request.name} did not find the accessed ha cluster")
        }
      } catch {
        case e: Exception =>
          error(s"${request.name} accessed ha cluster ${request.haClusterId} error", e)
      }
    }
  }

  def pollFromRequestQueue(): BrokerToHAClusterQueueItem = {
    val queueItem = requestQueue.peek()
    val now = time.milliseconds()
    val requestCount = networkClientMap.map(client => {
      client._2.poll(0, now)
      client._2.inFlightRequestCount()
    }).count(p => p > 0)
    if (queueItem == null && requestCount == 0) {
      requestQueue.take()
    } else {
      queueItem
    }
  }

  private def newRequestThread(config: HAClusterConfig, clusterId: String): NetworkClient = {
    val brokerList = config.getList(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG)
    val addresses: util.List[InetSocketAddress] = ClientUtils.parseAndValidateAddresses(brokerList, ClientDnsLookup.DEFAULT)
    val metadata = new Metadata(config.getLong(HAClusterConfig.RETRY_BACKOFF_MS_CONFIG), config.getLong(HAClusterConfig.METADATA_MAX_AGE_CONFIG), logContext, new ClusterResourceListeners)
    metadata.bootstrap(addresses)
    val networkClient = {
      val channelBuilder = ClientUtils.createChannelBuilder(config, time, logContext)
      val selector = new Selector(
        NetworkReceive.UNLIMITED,
        Selector.NO_IDLE_TIMEOUT_MS,
        metrics,
        time,
        channelName,
        Map("HAClusterId" -> clusterId).asJava,
        false,
        channelBuilder,
        logContext
      )
      new NetworkClient(
        selector,
        metadata,
        s"broker-to-ha-cluster-$clusterId",
        1,
        0,
        0,
        Selectable.USE_DEFAULT_BUFFER_SIZE,
        Selectable.USE_DEFAULT_BUFFER_SIZE,
        config.getInt(HAClusterConfig.REQUEST_TIMEOUT_MS_CONFIG),
        ClientDnsLookup.DEFAULT,
        time,
        true,
        new ApiVersions(),
        logContext
      )
    }
    networkClient
  }

  def sendRequest(haClusterId: String, request: AbstractRequest.Builder[_ <: AbstractRequest], callback: HAClusterRequestCompletionHandler, principal: KafkaPrincipal): Unit = {
    requestQueue.add(BrokerToHAClusterQueueItem(haClusterId, principal.getName, time.milliseconds(), request, callback))
  }

  def handleResponse(queueItem: BrokerToHAClusterQueueItem)(response: ClientResponse): Unit = {
    if (response.authenticationException != null) {
      error(s"Request ${queueItem.request} failed due to authentication error with ha cluster ${queueItem.haClusterId}",
        response.authenticationException)
      queueItem.callback.onComplete(response)
    } else if (response.wasDisconnected()) {
      requestQueue.putFirst(queueItem)
    } else {
      queueItem.callback.onComplete(response)
    }
  }
}

abstract class HAClusterRequestCompletionHandler extends RequestCompletionHandler {

  def onTimeout(): Unit

  def onError(error: String): Unit
}

case class BrokerToHAClusterQueueItem(haClusterId: String, name: String, createdTimeMs: Long, request: AbstractRequest.Builder[_ <: AbstractRequest], callback: HAClusterRequestCompletionHandler)