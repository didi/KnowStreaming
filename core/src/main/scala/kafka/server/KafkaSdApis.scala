/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kafka.server

import kafka.network.RequestChannel
import kafka.utils.Logging
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.internals.FatalExitError
import org.apache.kafka.common.metrics.Metrics
import org.apache.kafka.common.protocol.ApiKeys
import org.apache.kafka.common.record._
import org.apache.kafka.common.utils.Time

import scala.collection.Map


/**
 * Logic to handle the various Kafka requests
 */
class KafkaSdApis(val requestChannel: RequestChannel,
                  val brokerId: Int,
                  val config: KafkaConfig,
                  val metrics: Metrics,
                  val discoverer: Discoverer,
                  time: Time) extends Logging {

  type FetchResponseStats = Map[TopicPartition, RecordConversionStats]
  this.logIdent = "[KafkaSDApi-%d] ".format(brokerId)

  def close(): Unit = {
    info("Shutdown complete.")
  }

  /**
   * Top-level method that handles all requests and multiplexes to the right api
   */
  def handle(request: RequestChannel.Request): Unit = {
    try {
      trace(s"Handling request:${request.requestDesc(true)} from connection ${request.context.connectionId};" +
        s"securityProtocol:${request.context.securityProtocol},principal:${request.context.principal}")
      request.header.apiKey match {
        //case ApiKeys.PRODUCE => handleProduceRequest(request)
        //case ApiKeys.FETCH => handleFetchRequest(request)
        //case ApiKeys.LIST_OFFSETS => handleListOffsetRequest(request)
        case ApiKeys.METADATA => discoverer.handleRequest(request)
        //case ApiKeys.LEADER_AND_ISR => handleLeaderAndIsrRequest(request)
        //case ApiKeys.STOP_REPLICA => handleStopReplicaRequest(request)
        //case ApiKeys.UPDATE_METADATA => handleUpdateMetadataRequest(request)
        //case ApiKeys.CONTROLLED_SHUTDOWN => handleControlledShutdownRequest(request)
        //case ApiKeys.OFFSET_COMMIT => handleOffsetCommitRequest(request)
        //case ApiKeys.OFFSET_FETCH => handleOffsetFetchRequest(request)
        case ApiKeys.FIND_COORDINATOR => discoverer.handleRequest(request)
        //case ApiKeys.JOIN_GROUP => handleJoinGroupRequest(request)
        //case ApiKeys.HEARTBEAT => handleHeartbeatRequest(request)
        //case ApiKeys.LEAVE_GROUP => handleLeaveGroupRequest(request)
        //case ApiKeys.SYNC_GROUP => handleSyncGroupRequest(request)
        //case ApiKeys.DESCRIBE_GROUPS => handleDescribeGroupRequest(request)
        //case ApiKeys.LIST_GROUPS => handleListGroupsRequest(request)
        //case ApiKeys.SASL_HANDSHAKE => handleSaslHandshakeRequest(request)
        case ApiKeys.API_VERSIONS => discoverer.handleRequest(request)
        //case ApiKeys.CREATE_TOPICS => handleCreateTopicsRequest(request)
        //case ApiKeys.DELETE_TOPICS => handleDeleteTopicsRequest(request)
        //case ApiKeys.DELETE_RECORDS => handleDeleteRecordsRequest(request)
        case ApiKeys.INIT_PRODUCER_ID => discoverer.handleRequest(request)
        //case ApiKeys.OFFSET_FOR_LEADER_EPOCH => handleOffsetForLeaderEpochRequest(request)
        //case ApiKeys.ADD_PARTITIONS_TO_TXN => handleAddPartitionToTxnRequest(request)
        //case ApiKeys.ADD_OFFSETS_TO_TXN => handleAddOffsetsToTxnRequest(request)
        //case ApiKeys.END_TXN => handleEndTxnRequest(request)
        //case ApiKeys.WRITE_TXN_MARKERS => handleWriteTxnMarkersRequest(request)
        //case ApiKeys.TXN_OFFSET_COMMIT => handleTxnOffsetCommitRequest(request)
        //case ApiKeys.DESCRIBE_ACLS => handleDescribeAcls(request)
        //case ApiKeys.CREATE_ACLS => handleCreateAcls(request)
        //case ApiKeys.DELETE_ACLS => handleDeleteAcls(request)
        //case ApiKeys.ALTER_CONFIGS => handleAlterConfigsRequest(request)
        //case ApiKeys.DESCRIBE_CONFIGS => handleDescribeConfigsRequest(request)
        //case ApiKeys.ALTER_REPLICA_LOG_DIRS => handleAlterReplicaLogDirsRequest(request)
        //case ApiKeys.DESCRIBE_LOG_DIRS => handleDescribeLogDirsRequest(request)
        //case ApiKeys.SASL_AUTHENTICATE => handleSaslAuthenticateRequest(request)
        //case ApiKeys.CREATE_PARTITIONS => handleCreatePartitionsRequest(request)
        //case ApiKeys.CREATE_DELEGATION_TOKEN => handleCreateTokenRequest(request)
        //case ApiKeys.RENEW_DELEGATION_TOKEN => handleRenewTokenRequest(request)
        //case ApiKeys.EXPIRE_DELEGATION_TOKEN => handleExpireTokenRequest(request)
        //case ApiKeys.DESCRIBE_DELEGATION_TOKEN => handleDescribeTokensRequest(request)
        //case ApiKeys.DELETE_GROUPS => handleDeleteGroupsRequest(request)
        //case ApiKeys.ELECT_LEADERS => handleElectReplicaLeader(request)
        //case ApiKeys.INCREMENTAL_ALTER_CONFIGS => handleIncrementalAlterConfigsRequest(request)
        //case ApiKeys.ALTER_PARTITION_REASSIGNMENTS => handleAlterPartitionReassignmentsRequest(request)
        //case ApiKeys.LIST_PARTITION_REASSIGNMENTS => handleListPartitionReassignmentsRequest(request)
        //case ApiKeys.OFFSET_DELETE => handleOffsetDeleteRequest(request)
        case _ => discoverer.handleError(request, "INVALID_REQUEST_ID")
      }
    } catch {
      case e: FatalExitError => throw e
      case e: Throwable => discoverer.handleError(request, e.getMessage)
    } finally {
      // The local completion time may be set while processing the request. Only record it if it's unset.
      if (request.apiLocalCompleteTimeNanos < 0)
        request.apiLocalCompleteTimeNanos = time.nanoseconds
    }
  }
}
