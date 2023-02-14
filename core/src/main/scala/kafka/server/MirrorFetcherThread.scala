package kafka.server

import com.didichuxing.datachannel.kafka.config.HAClusterConfig
import kafka.api._
import kafka.cluster.{BrokerEndPoint, Partition}
import kafka.coordinator.group.GroupMetadataManager
import kafka.log.{AppendOrigin, LogAppendInfo}
import kafka.server.AbstractFetcherThread.{ReplicaFetch, ResultWithPartitions}
import org.apache.kafka.clients.FetchSessionHandler
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.errors.{CorruptRecordException, KafkaStorageException}
import org.apache.kafka.common.internals.Topic
import org.apache.kafka.common.metrics.Metrics
import org.apache.kafka.common.protocol.{ApiKeys, Errors}
import org.apache.kafka.common.record.{MemoryRecords, Records}
import org.apache.kafka.common.requests.EpochEndOffset.{UNDEFINED_EPOCH, UNDEFINED_EPOCH_OFFSET}
import org.apache.kafka.common.requests._
import org.apache.kafka.common.utils.{LogContext, Time}

import java.nio.ByteBuffer
import java.util.Optional
import scala.collection.JavaConverters._
import scala.collection.{Map, Set, mutable}

/**
 * @author leewei
 * @date 2021/9/28
 */
class MirrorFetcherThread(name: String,
                          fetcherId: Int,
                          sourceBroker: BrokerEndPoint,
                          config: HAClusterConfig,
                          failedPartitions: FailedPartitions,
                          partitionLatestFetchOffsets: mutable.HashMap[TopicPartition, Long],
                          replicaMgr: ReplicaManager,
                          groupManager: GroupMetadataManager,
                          metrics: Metrics,
                          time: Time,
                          quota: ReplicaQuota,
                          leaderEndpointBlockingSend: Option[BlockingSend] = None,
                          brokerId: Int,
                          clusterId: String)
  extends AbstractFetcherThread(name = name,
                                clientId = name,
                                sourceBroker = sourceBroker,
                                failedPartitions,
                                fetchBackOffMs = config.getInt(HAClusterConfig.FETCH_BACKOFF_MS_CONFIG),
                                isInterruptible = false,
                                replicaMgr.brokerTopicStats) {

  private val replicaId = if (config.isDidiKafka) FetchRequest.MIRROR_START_REPLICA_ID + brokerId else FetchRequest.CONSUMER_REPLICA_ID
  private val logContext = new LogContext(s"[MirrorFetcher replicaId=$replicaId, " +
                                          s"local clusterId=$clusterId, " +
                                          s"remote clusterId=${sourceBroker.remoteCluster.get}, " +
                                          s"leaderId=${sourceBroker.id}, " +
                                          s"fetcherId=$fetcherId] ")
  this.logIdent = logContext.logPrefix

  private val brokerProtocolVersion = config.brokerProtocolVersion()

  // client id is local cluster id, for remote consumer quota
  private val leaderEndpoint = leaderEndpointBlockingSend.getOrElse(
    new MirrorFetcherBlockingSend(sourceBroker, config, metrics, time, fetcherId,
      s"client-mirror-cluster-$clusterId-${sourceBroker.remoteCluster.get}-fetcher-$fetcherId", logContext))

  private val maxWait = config.getInt(HAClusterConfig.FETCH_MAX_WAIT_MS_CONFIG)
  private val minBytes = config.getInt(HAClusterConfig.FETCH_MIN_BYTES_CONFIG)
  private val maxBytes = config.getInt(HAClusterConfig.FETCH_MAX_BYTES_CONFIG)
  private val fetchSize = config.getInt(HAClusterConfig.MAX_PARTITION_FETCH_BYTES_CONFIG)
  private val brokerSupportsLeaderEpochRequest = brokerProtocolVersion >= KAFKA_0_11_0_IV2
  val fetchSessionHandler = new FetchSessionHandler(logContext, sourceBroker.id)

  // not use epoch to truncate data, see isOffsetForLeaderEpochSupported
  override protected def latestEpoch(topicPartition: TopicPartition): Option[Int] = {
    replicaMgr.getPartitionOrExceptionByRemoteTopicPartition(topicPartition, expectLeader = true).localLogOrException.latestEpoch
  }

  override protected def logStartOffset(topicPartition: TopicPartition): Long = {
    if (topicPartition.topic().equals(Topic.GROUP_METADATA_TOPIC_NAME))
      0L
    else
      replicaMgr.getPartitionOrExceptionByRemoteTopicPartition(topicPartition, expectLeader = true).localLogOrException.logStartOffset
  }

  override protected def logEndOffset(topicPartition: TopicPartition): Long = {
    if (topicPartition.topic().equals(Topic.GROUP_METADATA_TOPIC_NAME))
      fetchState(topicPartition).map(_.fetchOffset).getOrElse(0L)
    else
      replicaMgr.getPartitionOrExceptionByRemoteTopicPartition(topicPartition, expectLeader = true).localLogOrException.logEndOffset
  }
  // not use epoch to truncate data, see isOffsetForLeaderEpochSupported
  override protected def endOffsetForEpoch(topicPartition: TopicPartition, epoch: Int): Option[OffsetAndEpoch] = {
    replicaMgr.getPartitionOrExceptionByRemoteTopicPartition(topicPartition, expectLeader = true).localLogOrException.endOffsetForEpoch(epoch)
  }

  override def initiateShutdown(): Boolean = {
    val justShutdown = super.initiateShutdown()
    if (justShutdown) {
      // This is thread-safe, so we don't expect any exceptions, but catch and log any errors
      // to avoid failing the caller, especially during shutdown. We will attempt to close
      // leaderEndpoint after the thread terminates.
      try {
        leaderEndpoint.initiateClose()
      } catch {
        case t: Throwable =>
          error(s"Failed to initiate shutdown of leader endpoint $leaderEndpoint after initiating replica fetcher thread shutdown", t)
      }
    }
    justShutdown
  }

  override def awaitShutdown(): Unit = {
    super.awaitShutdown()
    // We don't expect any exceptions here, but catch and log any errors to avoid failing the caller,
    // especially during shutdown. It is safe to catch the exception here without causing correctness
    // issue because we are going to shutdown the thread and will not re-use the leaderEndpoint anyway.
    try {
      leaderEndpoint.close()
    } catch {
      case t: Throwable =>
        error(s"Failed to close leader endpoint $leaderEndpoint after shutting down replica fetcher thread", t)
    }
  }

  override def addPartitions(initialFetchStates: Map[TopicPartition, OffsetAndEpoch]): Set[TopicPartition] = {
    initialFetchStates.foreach { case (tp, initialFetchState) =>
      partitionLatestFetchOffsets.put(tp, initialFetchState.offset)
    }
    super.addPartitions(initialFetchStates)
  }

  override def removePartitions(topicPartitions: Set[TopicPartition]): Unit = {
    partitionLatestFetchOffsets --= topicPartitions
    super.removePartitions(topicPartitions)
    // clean mirror group remote partition loading state
    topicPartitions.filter(_.topic().equals(Topic.GROUP_METADATA_TOPIC_NAME))
      .foreach(groupManager.cleanRemotePartitionLoadingState)
  }

  override protected def onPartitionFenced(topicPartition: TopicPartition, requestEpoch: Option[Int]): Boolean = {
    if (topicPartition.topic().equals(Topic.GROUP_METADATA_TOPIC_NAME))
      true
    else
      super.onPartitionFenced(topicPartition, requestEpoch)
  }

  override def processPartitionData(topicPartition: TopicPartition,
                                    fetchOffset: Long,
                                    partitionData: FetchData): Option[LogAppendInfo] = {

    val logAppendInfoOpt = if (topicPartition.topic().equals(Topic.GROUP_METADATA_TOPIC_NAME)) {
      groupManager.doMirrorGroupsAndOffsets(topicPartition, fetchOffset, partitionData, clusterId, sourceBroker.remoteCluster.get)
    } else {
      doMirrorRecordsToLeader(topicPartition, fetchOffset, partitionData)
    }
    logAppendInfoOpt.foreach { logAppendInfo =>
      if (logAppendInfo.validBytes > 0)
        partitionLatestFetchOffsets.put(topicPartition, logAppendInfo.lastOffset + 1)
    }
    logAppendInfoOpt
  }

  private def doMirrorRecordsToLeader(topicPartition: TopicPartition,
                           fetchOffset: Long,
                           partitionData: FetchData): Option[LogAppendInfo] = {
    val records = toMemoryRecords(partitionData.records)

    val partition = replicaMgr.getPartitionOrExceptionByRemoteTopicPartition(topicPartition, expectLeader = true)
    val log = partition.localLogOrException
    val localTopicPartition = partition.topicPartition

    // 如果mirror topic被producer写入数据这里会校验失败并停止拉取数据，将这个分区移到failedPartitions中
    if (fetchOffset != log.logEndOffset)
      throw new IllegalStateException("Offset mismatch for local partition %s: fetched offset = %d, log end offset = %d.".format(
        localTopicPartition, fetchOffset, log.logEndOffset))

    if (!partition.isLeader) {
      // mark partiton delay
      throw new CorruptRecordException(s"Invalid partition state, $topicPartition is not be a leader.")
    }

    if (isTraceEnabled)
      trace("MirrorFetcher has replica log end offset %d for local partition %s. Received %d messages and leader hw %d"
        .format(log.logEndOffset, localTopicPartition, records.sizeInBytes, partitionData.highWatermark))

    // update remote high watermark
    partition.updateRemoteHighWatermark(partitionData.highWatermark)

    // 不涉及消息协议转换，直接append到leader里，如果涉及消息协议转换，拆分单一batch，分批写入leader
    if (!config.isSplitFetchedBatches && records.hasMatchingMagic(log.config.messageFormatVersion.recordVersion.value)) {
      doAppendMemoryRecordsToLeader(partition, records)
    } else {
      // split MemoryRecords
      var lastAppendInfo: Option[LogAppendInfo] = None
      var validBytesCount = 0
      for (batch <- records.batches.asScala) {
        val buffer = ByteBuffer.allocate(batch.sizeInBytes)
        batch.writeTo(buffer)
        buffer.flip
        val singleBatchRecords = MemoryRecords.readableRecords(buffer)
        lastAppendInfo = doAppendMemoryRecordsToLeader(partition, singleBatchRecords)
        lastAppendInfo.map(_.validBytes).foreach(b => validBytesCount += b)
      }
      lastAppendInfo.map(info => LogAppendInfo(info.firstOffset, info.lastOffset, info.maxTimestamp, info.offsetOfMaxTimestamp, info.logAppendTime, info.logStartOffset,
        info.recordConversionStats, info.sourceCodec, info.targetCodec, info.shallowCount, validBytesCount, info.offsetsMonotonic, info.lastOffsetOfFirstBatch))
    }
  }

  private def doAppendMemoryRecordsToLeader(partition: Partition, records: MemoryRecords): Option[LogAppendInfo] = {
    val log = partition.localLogOrException
    val localTopicPartition = partition.topicPartition
    val firstOffset = log.logEndOffset
    // Append the leader's messages to the log
    // 如果是Mirror，不分配offset
    val logAppendInfo = partition.appendRecordsToLeader(records, AppendOrigin.Mirror, requiredAcks = 1)

    if (isTraceEnabled)
      trace("MirrorFetcher has replica log end offset %d after appending %d bytes of messages for local partition %s"
        .format(log.logEndOffset, records.sizeInBytes, localTopicPartition))

    // Leader no need 镜像Topic和源Topic独自管理Topic保留时间
    // val leaderLogStartOffset = partitionData.logStartOffset
    // log.maybeIncrementLogStartOffset(leaderLogStartOffset)

    // Traffic from both in-sync and out of sync replicas are accounted for in replication quota to ensure total replication
    // traffic doesn't exceed quota.
    // throttle by remote topic partition
    // if (quota.isThrottled(topicPartition))
    quota.record(records.sizeInBytes)

    // Add topic metrics
    val numAppendedMessages = logAppendInfo.firstOffset.map(_ => logAppendInfo.numMessages)
      .getOrElse(if (logAppendInfo.validBytes > 0) logAppendInfo.lastOffset - firstOffset + 1 else 0) // 对于0.10到dkafka2.5(0.10)同步，为设置firstOffset，从消息leo计算消息写入数

    brokerTopicStats.topicStats(localTopicPartition.topic).totalProduceRequestRate.mark()
    brokerTopicStats.allTopicsStats.totalProduceRequestRate.mark()
    brokerTopicStats.topicStats(localTopicPartition.topic).bytesInRate.mark(records.sizeInBytes)
    brokerTopicStats.allTopicsStats.bytesInRate.mark(records.sizeInBytes)
    brokerTopicStats.topicStats(localTopicPartition.topic).messagesInRate.mark(numAppendedMessages)
    brokerTopicStats.allTopicsStats.messagesInRate.mark(numAppendedMessages)

    val conversionCount = logAppendInfo.recordConversionStats.numRecordsConverted()
    if (conversionCount > 0) {
      brokerTopicStats.topicStats(localTopicPartition.topic).produceMessageConversionsRate.mark(conversionCount)
      brokerTopicStats.allTopicsStats.produceMessageConversionsRate.mark(conversionCount)
    }

    brokerTopicStats.updateMirrorBytesIn(records.sizeInBytes)

    Option(logAppendInfo)
  }

  override protected def fetchFromLeader(fetchRequest: FetchRequest.Builder): Map[TopicPartition, FetchData] = {
    try {
      val clientResponse = leaderEndpoint.sendRequest(fetchRequest)
      val fetchResponse = clientResponse.responseBody.asInstanceOf[FetchResponse[Records]]
      if (!fetchSessionHandler.handleResponse(fetchResponse)) {
        Map.empty
      } else {
        fetchResponse.responseData.asScala
      }
    } catch {
      case t: Throwable =>
        fetchSessionHandler.handleError(t)
        throw t
    }
  }

  override protected def fetchEarliestOffsetFromLeader(topicPartition: TopicPartition, currentLeaderEpoch: Int): Long = {
    fetchOffsetFromLeader(topicPartition, currentLeaderEpoch, ListOffsetRequest.EARLIEST_TIMESTAMP)
  }

  override protected def fetchLatestOffsetFromLeader(topicPartition: TopicPartition, currentLeaderEpoch: Int): Long = {
    fetchOffsetFromLeader(topicPartition, currentLeaderEpoch, ListOffsetRequest.LATEST_TIMESTAMP)
  }

  // currentLeaderEpoch: -1表示不存在epoch
  private def fetchOffsetFromLeader(topicPartition: TopicPartition, currentLeaderEpoch: Int, earliestOrLatest: Long): Long = {
    val requestPartitionData = new ListOffsetRequest.PartitionData(earliestOrLatest,
      if (currentLeaderEpoch == -1) Optional.empty[Integer]() else Optional.of[Integer](currentLeaderEpoch))
    val requestPartitions = Map(topicPartition -> requestPartitionData)
    val requestBuilder = ListOffsetRequest.Builder.forReplica(ApiKeys.LIST_OFFSETS.latestVersion, replicaId)
      .setTargetTimes(requestPartitions.asJava)

    val clientResponse = leaderEndpoint.sendRequest(requestBuilder)
    val response = clientResponse.responseBody.asInstanceOf[ListOffsetResponse]

    val responsePartitionData = response.responseData.get(topicPartition)
    responsePartitionData.error match {
      case Errors.NONE =>
        if (brokerProtocolVersion >= KAFKA_0_10_1_IV2)
          responsePartitionData.offset
        else
          responsePartitionData.offsets.get(0)
      case error => throw error.exception
    }
  }

  override def buildFetch(partitionMap: Map[TopicPartition, PartitionFetchState]): ResultWithPartitions[Option[ReplicaFetch]] = {
    val partitionsWithError = mutable.Set[TopicPartition]()

    val builder = fetchSessionHandler.newBuilder(partitionMap.size, false)
    partitionMap.foreach { case (topicPartition, fetchState) =>
      // We will not include a replica in the fetch request if it should be throttled.
      if (fetchState.isReadyForFetch && !quota.isQuotaExceeded) {
        try {
          val logStartOffset = this.logStartOffset(topicPartition)
          builder.add(topicPartition, new FetchRequest.PartitionData(
            fetchState.fetchOffset, logStartOffset, fetchSize,
            if (fetchState.currentLeaderEpoch == -1) Optional.empty[Integer]() else Optional.of[Integer](fetchState.currentLeaderEpoch)))
        } catch {
          case _: KafkaStorageException =>
            // The replica has already been marked offline due to log directory failure and the original failure should have already been logged.
            // This partition should be removed from ReplicaFetcherThread soon by ReplicaManager.handleLogDirFailure()
            partitionsWithError += topicPartition
        }
      }
    }

    val fetchData = builder.build()
    val fetchRequestOpt = if (fetchData.sessionPartitions.isEmpty && fetchData.toForget.isEmpty) {
      None
    } else {
      val requestBuilder = new FetchRequest.Builder(ApiKeys.FETCH.oldestVersion,
                                                    ApiKeys.FETCH.latestVersion,
                                                    replicaId,
                                                    maxWait,
                                                    minBytes,
                                                    fetchData.toSend)
      requestBuilder.setMaxBytes(maxBytes)
        .toForget(fetchData.toForget)
        .metadata(fetchData.metadata)
      Some(ReplicaFetch(fetchData.sessionPartitions(), requestBuilder))
    }

    ResultWithPartitions(fetchRequestOpt, partitionsWithError)
  }

  /**
   * Truncate the log for each partition's epoch based on leader's returned epoch and offset.
   * The logic for finding the truncation offset is implemented in AbstractFetcherThread.getOffsetTruncationState
   */
  override def truncate(topicPartition: TopicPartition, offsetTruncationState: OffsetTruncationState): Unit = {
    if (topicPartition.topic().equals(Topic.GROUP_METADATA_TOPIC_NAME))
      return

    val localTopicPartition = replicaMgr.getLocalTopicPartitionByRemoteTopicPartition(topicPartition).get
    val partition = replicaMgr.nonOfflinePartition(localTopicPartition).get
    val log = partition.localLogOrException

    partition.truncateTo(offsetTruncationState.offset, isFuture = false)

    if (offsetTruncationState.offset < log.highWatermark)
      warn(s"Truncating local partition $localTopicPartition to offset ${offsetTruncationState.offset} below high watermark " +
        s"${log.highWatermark}")

    // mark the future replica for truncation only when we do last truncation
    if (offsetTruncationState.truncationCompleted)
      replicaMgr.replicaAlterLogDirsManager.markPartitionsForTruncation(brokerId, localTopicPartition,
        offsetTruncationState.offset)
  }

  override protected def truncateFullyAndStartAt(topicPartition: TopicPartition, offset: Long): Unit = {
    if (topicPartition.topic().equals(Topic.GROUP_METADATA_TOPIC_NAME))
      return

    val localTopicPartition = replicaMgr.getLocalTopicPartitionByRemoteTopicPartition(topicPartition).get
    val partition = replicaMgr.nonOfflinePartition(localTopicPartition).get
    partition.truncateFullyAndStartAt(offset, isFuture = false)
  }

  // not use epoch to truncate data, see isOffsetForLeaderEpochSupported
  override def fetchEpochEndOffsets(partitions: Map[TopicPartition, EpochData]): Map[TopicPartition, EpochEndOffset] = {
    if (partitions.isEmpty) {
      debug("Skipping leaderEpoch request since all partitions do not have an epoch")
      return Map.empty
    }

    val epochRequest = new OffsetsForLeaderEpochRequest.Builder(ApiKeys.OFFSET_FOR_LEADER_EPOCH.oldestVersion(),
                                                                ApiKeys.OFFSET_FOR_LEADER_EPOCH.latestVersion,
                                                                partitions.asJava,
                                                                replicaId)

    debug(s"Sending offset for leader epoch request $epochRequest")

    try {
      val response = leaderEndpoint.sendRequest(epochRequest)
      val responseBody = response.responseBody.asInstanceOf[OffsetsForLeaderEpochResponse]
      debug(s"Received leaderEpoch response $response")
      responseBody.responses.asScala
    } catch {
      case t: Throwable =>
        warn(s"Error when sending leader epoch request for $partitions", t)

        // if we get any unexpected exception, mark all partitions with an error
        val error = Errors.forException(t)
        partitions.map { case (tp, _) =>
          tp -> new EpochEndOffset(error, UNDEFINED_EPOCH, UNDEFINED_EPOCH_OFFSET)
        }
    }
  }

  // remote leader epoch not match local epoch
  override protected def isOffsetForLeaderEpochSupported(topicPartition: TopicPartition): Boolean = {
    if (topicPartition.topic().equals(Topic.GROUP_METADATA_TOPIC_NAME))
      false
    else
      brokerSupportsLeaderEpochRequest
  }
}
