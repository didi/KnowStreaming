package kafka.server

import com.didichuxing.datachannel.kafka.config.manager.ClusterConfigManager
import kafka.cluster.BrokerEndPoint
import kafka.coordinator.group.GroupMetadataManager
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.config.ConfigException
import org.apache.kafka.common.metrics.Metrics
import org.apache.kafka.common.utils.Time

import scala.collection.{Set, mutable}

/**
 * @author leewei
 * @date 2021/9/28
 */
class MirrorFetcherManager(brokerConfig: KafkaConfig,
                           replicaManager: ReplicaManager,
                           metrics: Metrics,
                           time: Time,
                           threadNamePrefix: Option[String] = None,
                           quotaManager: ReplicationQuotaManager)
  extends AbstractFetcherManager[MirrorFetcherThread](
    name = "MirrorFetcherManager on broker " + brokerConfig.brokerId,
    clientId = "Mirror",
    numFetchers = brokerConfig.mirrorNumFetchers) {

  private val partitionLatestFetchOffsets = new mutable.HashMap[TopicPartition, Long]

  var groupManagerOpt: Option[GroupMetadataManager] = None

  override def createFetcherThread(fetcherId: Int, sourceBroker: BrokerEndPoint): MirrorFetcherThread = {
    val prefix = threadNamePrefix.map(tp => s"$tp:").getOrElse("")
    val threadName = s"${prefix}MirrorFetcherThread-$fetcherId-remote-cluster-${sourceBroker.remoteCluster.get}-${sourceBroker.id}"
    val config = sourceBroker.remoteCluster.map(ClusterConfigManager.getHAClusterConfig)
      .getOrElse(throw new ConfigException(s"Can't get remote cluster $sourceBroker configs"))
    new MirrorFetcherThread(threadName, fetcherId, sourceBroker, config, failedPartitions, partitionLatestFetchOffsets,
      replicaManager, groupManagerOpt.getOrElse(throw new ConfigException("Can't get group metadata manager")),
      metrics, time, quotaManager, None,
      brokerConfig.brokerId, brokerConfig.clusterId + "")
  }

  def setGroupMetadataManager(groupManager: GroupMetadataManager): Unit = {
    this.groupManagerOpt = Option(groupManager)
  }

  def allFetchedPartitions(): Set[TopicPartition] = {
    fetcherThreadMap.values.flatMap(_.allPartitions()).toSet
  }

  def allDelayedPartitions(): Set[TopicPartition] = {
    fetcherThreadMap.values.flatMap(_.delayedPartitions()).toSet
  }

  def allFailedPartitions(): Set[TopicPartition] = {
    failedPartitions.listAll()
  }

  def allPartitions(): Set[TopicPartition] = {
    allFetchedPartitions() ++ allFailedPartitions()
  }

  def latestFetchOffset(topicPartition: TopicPartition): Option[Long] = {
    partitionLatestFetchOffsets.get(topicPartition)
  }

  def shutdown(): Unit = {
    info("shutting down")
    closeAllFetchers()
    info("shutdown completed")
  }
}

