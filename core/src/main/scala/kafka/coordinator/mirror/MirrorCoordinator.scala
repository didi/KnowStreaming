package kafka.coordinator.mirror

import com.didichuxing.datachannel.kafka.config.MirrorSyncConfig
import kafka.metrics.KafkaMetricsGroup
import kafka.server._
import kafka.utils.CoreUtils.inLock
import kafka.utils.{Logging, Scheduler}
import kafka.zk.KafkaZkClient
import org.apache.kafka.common.internals.Topic
import org.apache.kafka.common.message.CreatePartitionsRequestData.CreatePartitionsTopic
import org.apache.kafka.common.message.CreateTopicsRequestData.CreatableTopic
import org.apache.kafka.common.message.CreateTopicsResponseData.CreatableTopicResult
import org.apache.kafka.common.metrics.Metrics
import org.apache.kafka.common.utils.{Time, Utils}
import org.apache.kafka.server.authorizer.Authorizer

import java.util.Collections
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import scala.collection.JavaConverters._
import scala.collection.mutable

/**
 * @author leewei
 * @date 2021/10/21
 */

class MirrorCoordinator(config: KafkaConfig,
                        metrics: Metrics,
                        time: Time,
                        replicaManager: ReplicaManager,
                        adminManager: AdminManager,
                        scheduler: Scheduler,
                        zkClient: KafkaZkClient,
                        authorizer: Option[Authorizer]) extends Logging with KafkaMetricsGroup {

  private val fetcherManager = replicaManager.mirrorFetcherManager

  /* lock protecting access to loading and owned partition sets */
  private val partitionLock = new ReentrantLock()

  /** number of partitions for the mirror state topic */
  private val mirrorTopicPartitionCount = getMirrorTopicPartitionCount

  /* partitions of consumer groups that are assigned, using the same loading partition lock */
  private val ownedPartitions = mutable.Set[Int]()

  private val inconsistentTopics = mutable.Set[String]()

  private val isActive = new AtomicBoolean(false)

  private val mirrorSyncConfig: MirrorSyncConfig = new MirrorSyncConfig(adminManager, authorizer)

  newGauge("InconsistentTopicCount", () => inconsistentTopics.size)

  /**
   * Startup logic executed at the same time when the server starts up.
   */
  def startup(): Unit = {
    info("Starting up.")
    scheduler.schedule("sync-topic-partitions-thread", syncTopicPartitions _, period = config.mirrorSyncPartitionsIntervalMs, unit = TimeUnit.MILLISECONDS)
    scheduler.schedule("sync-topic-configs-thread", syncTopicConfigs, 10000, config.mirrorSyncAclConfigsIntervalMs)
    isActive.set(true)
    info("Startup complete.")
  }

  /**
   * Shutdown logic executed at the same time when server shuts down.
   * Ordering of actions should be reversed from the startup process.
   */
  def shutdown(): Unit = {
    info("Shutting down.")
    isActive.set(false)
    mirrorSyncConfig.close()
    info("Shutdown complete.")
  }


  def partitionFor(resourceId: String): Int = Utils.abs(resourceId.hashCode) % mirrorTopicPartitionCount

  def isPartitionOwned(partition: Int): Boolean = inLock(partitionLock) { ownedPartitions.contains(partition) }

  def isLocal(resourceId: String): Boolean = isPartitionOwned(partitionFor(resourceId))

  /**
   * Load cached state from the given partition.
   *
   * @param mirrorTopicPartitionId The partition we are now leading
   */
  def onElection(mirrorTopicPartitionId: Int): Unit = {
    inLock(partitionLock) {
      ownedPartitions.add(mirrorTopicPartitionId)
    }
  }

  /**
   * Unload cached state for the given partition.
   *
   * @param mirrorTopicPartitionId The partition we are no longer leading
   */
  def onResignation(mirrorTopicPartitionId: Int): Unit = {
    inLock(partitionLock) {
      ownedPartitions.remove(mirrorTopicPartitionId)
    }
  }

  /**
   * Gets the partition count of the mirror state topic from ZooKeeper.
   * If the topic does not exist, the default partition count is returned.
   */
  private def getMirrorTopicPartitionCount: Int = {
    zkClient.getTopicPartitionCount(Topic.MIRROR_STATE_TOPIC_NAME).getOrElse(config.mirrorStateTopicPartitions)
  }

  def syncTopicPartitions(): Unit = {
    val mirrorTopicsByLocal = replicaManager.allMirrorTopicsByLocal()
    if (mirrorTopicsByLocal.isEmpty)
      return

    val needSyncTopics = mutable.Set[String]()
    // 1. Topic的0分区所在leader负责同步分区
    fetcherManager.allPartitions()
      .filter(_.partition() == 0)
      .map(replicaManager.getPartitionOrExceptionByRemoteTopicPartition(_, expectLeader = true))
      .filter(_.isLeader)
      .foreach(p => needSyncTopics.add(p.topic))

    // 2. 刷新未知topic，请求元数据
    replicaManager.getUnknownMirrorTopicLocalNames()
      .filter(topic => isLocal(topic))
      .diff(replicaManager.unknownMirrorTopicLocalNamesNeedMetadata)
      .foreach(replicaManager.unknownMirrorTopicLocalNamesNeedMetadata.add)
    needSyncTopics ++= replicaManager.unknownMirrorTopicLocalNamesNeedMetadata

    val newPartitions = mutable.Set[CreatePartitionsTopic]()
    val newTopics = mutable.HashMap[String, CreatableTopic]()
    needSyncTopics
      .flatMap(topic => mirrorTopicsByLocal.get(topic))
      .foreach {
        case mirrorTopic: MirrorTopic =>
          val remotePartitions = replicaManager.remoteClusterMetadata(mirrorTopic.remoteCluster)
            .map(_.partitionsForTopic(mirrorTopic.remoteTopic))
            .getOrElse(Collections.emptyList)
          val remotePartitionCount = remotePartitions.size()
          val partitionCount = replicaManager.metadataCache.getNumberOfPartition(mirrorTopic.localTopic)._2

          trace(s"Mirror sync remote topic ${mirrorTopic.remoteTopic} partition count $remotePartitionCount, " +
            s"local topic ${mirrorTopic.localTopic} partition count $partitionCount ")
          if (remotePartitionCount > partitionCount) {
            inconsistentTopics += mirrorTopic.localTopic
            if (mirrorTopic.syncTopicPartitions) {
              if (partitionCount > 0) {
                newPartitions += new CreatePartitionsTopic().setName(mirrorTopic.localTopic)
                  .setCount(remotePartitionCount).setAssignments(null)
              } else {
                // 通过源集群获取副本数
                val replicationFactor = remotePartitions.get(0).replicas().length.shortValue()
                newTopics += mirrorTopic.localTopic -> new CreatableTopic().setName(mirrorTopic.localTopic)
                  .setNumPartitions(remotePartitionCount).setReplicationFactor(replicationFactor)
              }
            } else {
              warn(s"Topic partition inconsistent, remote topic ${mirrorTopic.remoteTopic} partition count $remotePartitionCount, " +
                s"local topic ${mirrorTopic.localTopic} partition count $partitionCount ")
            }
          } else {
            inconsistentTopics -= mirrorTopic.localTopic
          }
        case _ => None
      }

    // auto create internal topic __mirror_state
    if (!replicaManager.metadataCache.contains(Topic.MIRROR_STATE_TOPIC_NAME)) {
      val aliveBrokers = replicaManager.metadataCache.getAliveBrokers
      if (aliveBrokers.size < config.mirrorStateTopicReplicationFactor) {
        error(s"Number of alive brokers '${aliveBrokers.size}' does not meet the required replication factor " +
          s"'${config.mirrorStateTopicReplicationFactor}' for the ${Topic.MIRROR_STATE_TOPIC_NAME} topic (configured via " +
          s"'${KafkaConfig.DiDiMirrorStateTopicReplicationFactorProp}'). This error can be ignored if the cluster is starting up " +
          s"and not all brokers are up yet.")
      } else {
        newTopics += Topic.MIRROR_STATE_TOPIC_NAME -> new CreatableTopic().setName(Topic.MIRROR_STATE_TOPIC_NAME)
          .setNumPartitions(config.mirrorStateTopicPartitions).setReplicationFactor(config.mirrorStateTopicReplicationFactor)
      }
    }

    // 3. create topics
    if (newTopics.nonEmpty) {
      info(s"Mirror sync to create topics $newTopics")
      val includeConfigsAndMetatadata = mutable.HashMap[String, CreatableTopicResult]()
      newTopics.keys.foreach { topic =>
        includeConfigsAndMetatadata += topic -> new CreatableTopicResult().setName(topic)
      }
      adminManager.createTopics(config.requestTimeoutMs, validateOnly = false, newTopics, includeConfigsAndMetatadata, result => {
        result.foreach {
          case (topic, error) => info(s"topic $topic create topic result $error")
        }
      })
    }
    // 4. create partitions
    if (newPartitions.nonEmpty) {
      info(s"Mirror sync to create topic partitions $newPartitions")
      adminManager.createPartitions(config.requestTimeoutMs, newPartitions.toSeq, validateOnly = false, null, result => {
        result.foreach {
          case (topic, error) => info(s"topic $topic create partition result $error")
        }
      })
    }
  }

  def syncTopicConfigs(): Unit = {
    val mirrorTopics = mutable.Set[MirrorTopic]()
    replicaManager.allMirrorTopics()
      .filter(p => isLocal(p._1))
      .foreach(mirror => mirrorTopics.add(mirror._2))
    if (mirrorTopics.isEmpty) {
      return;
    }
    val needSyncAclTopic = mirrorTopics.filter(_.syncTopicAcls).toSet
    val needSyncConfigTopic = mirrorTopics.filter(_.syncTopicConfigs).toSet
    val topicSet = needSyncAclTopic ++ needSyncConfigTopic

    if (topicSet.nonEmpty) {
      val clusterSet = topicSet.groupBy(x => x.remoteCluster).keySet
      mirrorSyncConfig.initAdminClient(clusterSet.asJava)
      if (needSyncAclTopic.nonEmpty) {
        info(s"sync topic ${needSyncAclTopic.map(_.localTopic)} acls topic")
        mirrorSyncConfig.syncTopicsAcls(needSyncAclTopic.toList.asJava)
      }
      if (needSyncConfigTopic.nonEmpty) {
        info(s"sync topic ${needSyncConfigTopic.map(_.localTopic)} configs")
        mirrorSyncConfig.syncTopicConfigs(needSyncConfigTopic.toList.asJava)
      }
    } else {
      mirrorSyncConfig.close()
    }
  }

  def onConfigChanged(entityType: String, entity: String): Unit = {
    info(s"onConfigChanged  ${entityType}  ${entity}")
    entityType match {
      case ConfigType.HACluster => mirrorSyncConfig.reloadAdminClient(entity)
      case _ =>
    }
  }
}
