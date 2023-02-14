/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// THIS CODE IS AUTOMATICALLY GENERATED.  DO NOT EDIT.

package org.apache.kafka.common.message;

import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.protocol.ApiMessage;
import org.apache.kafka.common.protocol.types.Schema;

public enum ApiMessageType {
    PRODUCE("Produce", (short) 0, ProduceRequestData.SCHEMAS, ProduceResponseData.SCHEMAS),
    FETCH("Fetch", (short) 1, FetchRequestData.SCHEMAS, FetchResponseData.SCHEMAS),
    LIST_OFFSET("ListOffset", (short) 2, ListOffsetRequestData.SCHEMAS, ListOffsetResponseData.SCHEMAS),
    METADATA("Metadata", (short) 3, MetadataRequestData.SCHEMAS, MetadataResponseData.SCHEMAS),
    LEADER_AND_ISR("LeaderAndIsr", (short) 4, LeaderAndIsrRequestData.SCHEMAS, LeaderAndIsrResponseData.SCHEMAS),
    STOP_REPLICA("StopReplica", (short) 5, StopReplicaRequestData.SCHEMAS, StopReplicaResponseData.SCHEMAS),
    UPDATE_METADATA("UpdateMetadata", (short) 6, UpdateMetadataRequestData.SCHEMAS, UpdateMetadataResponseData.SCHEMAS),
    CONTROLLED_SHUTDOWN("ControlledShutdown", (short) 7, ControlledShutdownRequestData.SCHEMAS, ControlledShutdownResponseData.SCHEMAS),
    OFFSET_COMMIT("OffsetCommit", (short) 8, OffsetCommitRequestData.SCHEMAS, OffsetCommitResponseData.SCHEMAS),
    OFFSET_FETCH("OffsetFetch", (short) 9, OffsetFetchRequestData.SCHEMAS, OffsetFetchResponseData.SCHEMAS),
    FIND_COORDINATOR("FindCoordinator", (short) 10, FindCoordinatorRequestData.SCHEMAS, FindCoordinatorResponseData.SCHEMAS),
    JOIN_GROUP("JoinGroup", (short) 11, JoinGroupRequestData.SCHEMAS, JoinGroupResponseData.SCHEMAS),
    HEARTBEAT("Heartbeat", (short) 12, HeartbeatRequestData.SCHEMAS, HeartbeatResponseData.SCHEMAS),
    LEAVE_GROUP("LeaveGroup", (short) 13, LeaveGroupRequestData.SCHEMAS, LeaveGroupResponseData.SCHEMAS),
    SYNC_GROUP("SyncGroup", (short) 14, SyncGroupRequestData.SCHEMAS, SyncGroupResponseData.SCHEMAS),
    DESCRIBE_GROUPS("DescribeGroups", (short) 15, DescribeGroupsRequestData.SCHEMAS, DescribeGroupsResponseData.SCHEMAS),
    LIST_GROUPS("ListGroups", (short) 16, ListGroupsRequestData.SCHEMAS, ListGroupsResponseData.SCHEMAS),
    SASL_HANDSHAKE("SaslHandshake", (short) 17, SaslHandshakeRequestData.SCHEMAS, SaslHandshakeResponseData.SCHEMAS),
    API_VERSIONS("ApiVersions", (short) 18, ApiVersionsRequestData.SCHEMAS, ApiVersionsResponseData.SCHEMAS),
    CREATE_TOPICS("CreateTopics", (short) 19, CreateTopicsRequestData.SCHEMAS, CreateTopicsResponseData.SCHEMAS),
    DELETE_TOPICS("DeleteTopics", (short) 20, DeleteTopicsRequestData.SCHEMAS, DeleteTopicsResponseData.SCHEMAS),
    DELETE_RECORDS("DeleteRecords", (short) 21, DeleteRecordsRequestData.SCHEMAS, DeleteRecordsResponseData.SCHEMAS),
    INIT_PRODUCER_ID("InitProducerId", (short) 22, InitProducerIdRequestData.SCHEMAS, InitProducerIdResponseData.SCHEMAS),
    OFFSET_FOR_LEADER_EPOCH("OffsetForLeaderEpoch", (short) 23, OffsetForLeaderEpochRequestData.SCHEMAS, OffsetForLeaderEpochResponseData.SCHEMAS),
    ADD_PARTITIONS_TO_TXN("AddPartitionsToTxn", (short) 24, AddPartitionsToTxnRequestData.SCHEMAS, AddPartitionsToTxnResponseData.SCHEMAS),
    ADD_OFFSETS_TO_TXN("AddOffsetsToTxn", (short) 25, AddOffsetsToTxnRequestData.SCHEMAS, AddOffsetsToTxnResponseData.SCHEMAS),
    END_TXN("EndTxn", (short) 26, EndTxnRequestData.SCHEMAS, EndTxnResponseData.SCHEMAS),
    WRITE_TXN_MARKERS("WriteTxnMarkers", (short) 27, WriteTxnMarkersRequestData.SCHEMAS, WriteTxnMarkersResponseData.SCHEMAS),
    TXN_OFFSET_COMMIT("TxnOffsetCommit", (short) 28, TxnOffsetCommitRequestData.SCHEMAS, TxnOffsetCommitResponseData.SCHEMAS),
    DESCRIBE_ACLS("DescribeAcls", (short) 29, DescribeAclsRequestData.SCHEMAS, DescribeAclsResponseData.SCHEMAS),
    CREATE_ACLS("CreateAcls", (short) 30, CreateAclsRequestData.SCHEMAS, CreateAclsResponseData.SCHEMAS),
    DELETE_ACLS("DeleteAcls", (short) 31, DeleteAclsRequestData.SCHEMAS, DeleteAclsResponseData.SCHEMAS),
    DESCRIBE_CONFIGS("DescribeConfigs", (short) 32, DescribeConfigsRequestData.SCHEMAS, DescribeConfigsResponseData.SCHEMAS),
    ALTER_CONFIGS("AlterConfigs", (short) 33, AlterConfigsRequestData.SCHEMAS, AlterConfigsResponseData.SCHEMAS),
    ALTER_REPLICA_LOG_DIRS("AlterReplicaLogDirs", (short) 34, AlterReplicaLogDirsRequestData.SCHEMAS, AlterReplicaLogDirsResponseData.SCHEMAS),
    DESCRIBE_LOG_DIRS("DescribeLogDirs", (short) 35, DescribeLogDirsRequestData.SCHEMAS, DescribeLogDirsResponseData.SCHEMAS),
    SASL_AUTHENTICATE("SaslAuthenticate", (short) 36, SaslAuthenticateRequestData.SCHEMAS, SaslAuthenticateResponseData.SCHEMAS),
    CREATE_PARTITIONS("CreatePartitions", (short) 37, CreatePartitionsRequestData.SCHEMAS, CreatePartitionsResponseData.SCHEMAS),
    CREATE_DELEGATION_TOKEN("CreateDelegationToken", (short) 38, CreateDelegationTokenRequestData.SCHEMAS, CreateDelegationTokenResponseData.SCHEMAS),
    RENEW_DELEGATION_TOKEN("RenewDelegationToken", (short) 39, RenewDelegationTokenRequestData.SCHEMAS, RenewDelegationTokenResponseData.SCHEMAS),
    EXPIRE_DELEGATION_TOKEN("ExpireDelegationToken", (short) 40, ExpireDelegationTokenRequestData.SCHEMAS, ExpireDelegationTokenResponseData.SCHEMAS),
    DESCRIBE_DELEGATION_TOKEN("DescribeDelegationToken", (short) 41, DescribeDelegationTokenRequestData.SCHEMAS, DescribeDelegationTokenResponseData.SCHEMAS),
    DELETE_GROUPS("DeleteGroups", (short) 42, DeleteGroupsRequestData.SCHEMAS, DeleteGroupsResponseData.SCHEMAS),
    ELECT_LEADERS("ElectLeaders", (short) 43, ElectLeadersRequestData.SCHEMAS, ElectLeadersResponseData.SCHEMAS),
    INCREMENTAL_ALTER_CONFIGS("IncrementalAlterConfigs", (short) 44, IncrementalAlterConfigsRequestData.SCHEMAS, IncrementalAlterConfigsResponseData.SCHEMAS),
    ALTER_PARTITION_REASSIGNMENTS("AlterPartitionReassignments", (short) 45, AlterPartitionReassignmentsRequestData.SCHEMAS, AlterPartitionReassignmentsResponseData.SCHEMAS),
    LIST_PARTITION_REASSIGNMENTS("ListPartitionReassignments", (short) 46, ListPartitionReassignmentsRequestData.SCHEMAS, ListPartitionReassignmentsResponseData.SCHEMAS),
    OFFSET_DELETE("OffsetDelete", (short) 47, OffsetDeleteRequestData.SCHEMAS, OffsetDeleteResponseData.SCHEMAS),
    BUFFER("Buffer", (short) 1000, BufferRequestData.SCHEMAS, BufferResponseData.SCHEMAS);
    
    private final String name;
    private final short apiKey;
    private final Schema[] requestSchemas;
    private final Schema[] responseSchemas;
    
    ApiMessageType(String name, short apiKey, Schema[] requestSchemas, Schema[] responseSchemas) {
        this.name = name;
        this.apiKey = apiKey;
        this.requestSchemas = requestSchemas;
        this.responseSchemas = responseSchemas;
    }
    
    public static ApiMessageType fromApiKey(short apiKey) {
        switch (apiKey) {
            case 0:
                return PRODUCE;
            case 1:
                return FETCH;
            case 2:
                return LIST_OFFSET;
            case 3:
                return METADATA;
            case 4:
                return LEADER_AND_ISR;
            case 5:
                return STOP_REPLICA;
            case 6:
                return UPDATE_METADATA;
            case 7:
                return CONTROLLED_SHUTDOWN;
            case 8:
                return OFFSET_COMMIT;
            case 9:
                return OFFSET_FETCH;
            case 10:
                return FIND_COORDINATOR;
            case 11:
                return JOIN_GROUP;
            case 12:
                return HEARTBEAT;
            case 13:
                return LEAVE_GROUP;
            case 14:
                return SYNC_GROUP;
            case 15:
                return DESCRIBE_GROUPS;
            case 16:
                return LIST_GROUPS;
            case 17:
                return SASL_HANDSHAKE;
            case 18:
                return API_VERSIONS;
            case 19:
                return CREATE_TOPICS;
            case 20:
                return DELETE_TOPICS;
            case 21:
                return DELETE_RECORDS;
            case 22:
                return INIT_PRODUCER_ID;
            case 23:
                return OFFSET_FOR_LEADER_EPOCH;
            case 24:
                return ADD_PARTITIONS_TO_TXN;
            case 25:
                return ADD_OFFSETS_TO_TXN;
            case 26:
                return END_TXN;
            case 27:
                return WRITE_TXN_MARKERS;
            case 28:
                return TXN_OFFSET_COMMIT;
            case 29:
                return DESCRIBE_ACLS;
            case 30:
                return CREATE_ACLS;
            case 31:
                return DELETE_ACLS;
            case 32:
                return DESCRIBE_CONFIGS;
            case 33:
                return ALTER_CONFIGS;
            case 34:
                return ALTER_REPLICA_LOG_DIRS;
            case 35:
                return DESCRIBE_LOG_DIRS;
            case 36:
                return SASL_AUTHENTICATE;
            case 37:
                return CREATE_PARTITIONS;
            case 38:
                return CREATE_DELEGATION_TOKEN;
            case 39:
                return RENEW_DELEGATION_TOKEN;
            case 40:
                return EXPIRE_DELEGATION_TOKEN;
            case 41:
                return DESCRIBE_DELEGATION_TOKEN;
            case 42:
                return DELETE_GROUPS;
            case 43:
                return ELECT_LEADERS;
            case 44:
                return INCREMENTAL_ALTER_CONFIGS;
            case 45:
                return ALTER_PARTITION_REASSIGNMENTS;
            case 46:
                return LIST_PARTITION_REASSIGNMENTS;
            case 47:
                return OFFSET_DELETE;
            case 1000:
                return BUFFER;
            default:
                throw new UnsupportedVersionException("Unsupported API key " + apiKey);
        }
    }
    
    public ApiMessage newRequest() {
        switch (apiKey) {
            case 0:
                return new ProduceRequestData();
            case 1:
                return new FetchRequestData();
            case 2:
                return new ListOffsetRequestData();
            case 3:
                return new MetadataRequestData();
            case 4:
                return new LeaderAndIsrRequestData();
            case 5:
                return new StopReplicaRequestData();
            case 6:
                return new UpdateMetadataRequestData();
            case 7:
                return new ControlledShutdownRequestData();
            case 8:
                return new OffsetCommitRequestData();
            case 9:
                return new OffsetFetchRequestData();
            case 10:
                return new FindCoordinatorRequestData();
            case 11:
                return new JoinGroupRequestData();
            case 12:
                return new HeartbeatRequestData();
            case 13:
                return new LeaveGroupRequestData();
            case 14:
                return new SyncGroupRequestData();
            case 15:
                return new DescribeGroupsRequestData();
            case 16:
                return new ListGroupsRequestData();
            case 17:
                return new SaslHandshakeRequestData();
            case 18:
                return new ApiVersionsRequestData();
            case 19:
                return new CreateTopicsRequestData();
            case 20:
                return new DeleteTopicsRequestData();
            case 21:
                return new DeleteRecordsRequestData();
            case 22:
                return new InitProducerIdRequestData();
            case 23:
                return new OffsetForLeaderEpochRequestData();
            case 24:
                return new AddPartitionsToTxnRequestData();
            case 25:
                return new AddOffsetsToTxnRequestData();
            case 26:
                return new EndTxnRequestData();
            case 27:
                return new WriteTxnMarkersRequestData();
            case 28:
                return new TxnOffsetCommitRequestData();
            case 29:
                return new DescribeAclsRequestData();
            case 30:
                return new CreateAclsRequestData();
            case 31:
                return new DeleteAclsRequestData();
            case 32:
                return new DescribeConfigsRequestData();
            case 33:
                return new AlterConfigsRequestData();
            case 34:
                return new AlterReplicaLogDirsRequestData();
            case 35:
                return new DescribeLogDirsRequestData();
            case 36:
                return new SaslAuthenticateRequestData();
            case 37:
                return new CreatePartitionsRequestData();
            case 38:
                return new CreateDelegationTokenRequestData();
            case 39:
                return new RenewDelegationTokenRequestData();
            case 40:
                return new ExpireDelegationTokenRequestData();
            case 41:
                return new DescribeDelegationTokenRequestData();
            case 42:
                return new DeleteGroupsRequestData();
            case 43:
                return new ElectLeadersRequestData();
            case 44:
                return new IncrementalAlterConfigsRequestData();
            case 45:
                return new AlterPartitionReassignmentsRequestData();
            case 46:
                return new ListPartitionReassignmentsRequestData();
            case 47:
                return new OffsetDeleteRequestData();
            case 1000:
                return new BufferRequestData();
            default:
                throw new UnsupportedVersionException("Unsupported request API key " + apiKey);
        }
    }
    
    public ApiMessage newResponse() {
        switch (apiKey) {
            case 0:
                return new ProduceResponseData();
            case 1:
                return new FetchResponseData();
            case 2:
                return new ListOffsetResponseData();
            case 3:
                return new MetadataResponseData();
            case 4:
                return new LeaderAndIsrResponseData();
            case 5:
                return new StopReplicaResponseData();
            case 6:
                return new UpdateMetadataResponseData();
            case 7:
                return new ControlledShutdownResponseData();
            case 8:
                return new OffsetCommitResponseData();
            case 9:
                return new OffsetFetchResponseData();
            case 10:
                return new FindCoordinatorResponseData();
            case 11:
                return new JoinGroupResponseData();
            case 12:
                return new HeartbeatResponseData();
            case 13:
                return new LeaveGroupResponseData();
            case 14:
                return new SyncGroupResponseData();
            case 15:
                return new DescribeGroupsResponseData();
            case 16:
                return new ListGroupsResponseData();
            case 17:
                return new SaslHandshakeResponseData();
            case 18:
                return new ApiVersionsResponseData();
            case 19:
                return new CreateTopicsResponseData();
            case 20:
                return new DeleteTopicsResponseData();
            case 21:
                return new DeleteRecordsResponseData();
            case 22:
                return new InitProducerIdResponseData();
            case 23:
                return new OffsetForLeaderEpochResponseData();
            case 24:
                return new AddPartitionsToTxnResponseData();
            case 25:
                return new AddOffsetsToTxnResponseData();
            case 26:
                return new EndTxnResponseData();
            case 27:
                return new WriteTxnMarkersResponseData();
            case 28:
                return new TxnOffsetCommitResponseData();
            case 29:
                return new DescribeAclsResponseData();
            case 30:
                return new CreateAclsResponseData();
            case 31:
                return new DeleteAclsResponseData();
            case 32:
                return new DescribeConfigsResponseData();
            case 33:
                return new AlterConfigsResponseData();
            case 34:
                return new AlterReplicaLogDirsResponseData();
            case 35:
                return new DescribeLogDirsResponseData();
            case 36:
                return new SaslAuthenticateResponseData();
            case 37:
                return new CreatePartitionsResponseData();
            case 38:
                return new CreateDelegationTokenResponseData();
            case 39:
                return new RenewDelegationTokenResponseData();
            case 40:
                return new ExpireDelegationTokenResponseData();
            case 41:
                return new DescribeDelegationTokenResponseData();
            case 42:
                return new DeleteGroupsResponseData();
            case 43:
                return new ElectLeadersResponseData();
            case 44:
                return new IncrementalAlterConfigsResponseData();
            case 45:
                return new AlterPartitionReassignmentsResponseData();
            case 46:
                return new ListPartitionReassignmentsResponseData();
            case 47:
                return new OffsetDeleteResponseData();
            case 1000:
                return new BufferResponseData();
            default:
                throw new UnsupportedVersionException("Unsupported response API key " + apiKey);
        }
    }
    
    public short apiKey() {
        return this.apiKey;
    }
    
    public Schema[] requestSchemas() {
        return this.requestSchemas;
    }
    
    public Schema[] responseSchemas() {
        return this.responseSchemas;
    }
    
    @Override
    public String toString() {
        return this.name();
    }
    
    public short requestHeaderVersion(short _version) {
        switch (apiKey) {
            case 0:
                return (short) 1;
            case 1:
                return (short) 1;
            case 2:
                return (short) 1;
            case 3:
                if (_version >= 9) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 4:
                if (_version >= 4) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 5:
                if (_version >= 2) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 6:
                if (_version >= 6) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 7:
                if (_version == 0) {
                    return (short) 0;
                }
                if (_version >= 3) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 8:
                if (_version >= 8) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 9:
                if (_version >= 6) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 10:
                if (_version >= 3) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 11:
                if (_version >= 6) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 12:
                if (_version >= 4) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 13:
                if (_version >= 4) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 14:
                if (_version >= 4) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 15:
                if (_version >= 5) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 16:
                if (_version >= 3) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 17:
                return (short) 1;
            case 18:
                if (_version >= 3) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 19:
                if (_version >= 5) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 20:
                if (_version >= 4) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 21:
                return (short) 1;
            case 22:
                if (_version >= 2) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 23:
                return (short) 1;
            case 24:
                return (short) 1;
            case 25:
                return (short) 1;
            case 26:
                return (short) 1;
            case 27:
                return (short) 1;
            case 28:
                if (_version >= 3) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 29:
                if (_version >= 2) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 30:
                if (_version >= 2) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 31:
                if (_version >= 2) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 32:
                return (short) 1;
            case 33:
                return (short) 1;
            case 34:
                return (short) 1;
            case 35:
                return (short) 1;
            case 36:
                if (_version >= 2) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 37:
                if (_version >= 2) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 38:
                if (_version >= 2) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 39:
                if (_version >= 2) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 40:
                if (_version >= 2) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 41:
                if (_version >= 2) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 42:
                if (_version >= 2) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 43:
                if (_version >= 2) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 44:
                if (_version >= 1) {
                    return (short) 2;
                } else {
                    return (short) 1;
                }
            case 45:
                return (short) 2;
            case 46:
                return (short) 2;
            case 47:
                return (short) 1;
            case 1000:
                return (short) 1;
            default:
                throw new UnsupportedVersionException("Unsupported API key " + apiKey);
        }
    }
    
    public short responseHeaderVersion(short _version) {
        switch (apiKey) {
            case 0:
                return (short) 0;
            case 1:
                return (short) 0;
            case 2:
                return (short) 0;
            case 3:
                if (_version >= 9) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 4:
                if (_version >= 4) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 5:
                if (_version >= 2) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 6:
                if (_version >= 6) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 7:
                if (_version >= 3) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 8:
                if (_version >= 8) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 9:
                if (_version >= 6) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 10:
                if (_version >= 3) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 11:
                if (_version >= 6) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 12:
                if (_version >= 4) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 13:
                if (_version >= 4) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 14:
                if (_version >= 4) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 15:
                if (_version >= 5) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 16:
                if (_version >= 3) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 17:
                return (short) 0;
            case 18:
                return (short) 0;
            case 19:
                if (_version >= 5) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 20:
                if (_version >= 4) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 21:
                return (short) 0;
            case 22:
                if (_version >= 2) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 23:
                return (short) 0;
            case 24:
                return (short) 0;
            case 25:
                return (short) 0;
            case 26:
                return (short) 0;
            case 27:
                return (short) 0;
            case 28:
                if (_version >= 3) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 29:
                if (_version >= 2) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 30:
                if (_version >= 2) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 31:
                if (_version >= 2) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 32:
                return (short) 0;
            case 33:
                return (short) 0;
            case 34:
                return (short) 0;
            case 35:
                return (short) 0;
            case 36:
                if (_version >= 2) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 37:
                if (_version >= 2) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 38:
                if (_version >= 2) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 39:
                if (_version >= 2) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 40:
                if (_version >= 2) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 41:
                if (_version >= 2) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 42:
                if (_version >= 2) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 43:
                if (_version >= 2) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 44:
                if (_version >= 1) {
                    return (short) 1;
                } else {
                    return (short) 0;
                }
            case 45:
                return (short) 1;
            case 46:
                return (short) 1;
            case 47:
                return (short) 0;
            case 1000:
                return (short) 0;
            default:
                throw new UnsupportedVersionException("Unsupported API key " + apiKey);
        }
    }
}
