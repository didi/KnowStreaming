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
package com.didichuxing.datachannel.kafka.partition;

import com.didichuxing.datachannel.kafka.cache.DataCache;
import com.didichuxing.datachannel.kafka.cache.DataProvider;
import com.didichuxing.datachannel.kafka.cache.ZkUtil;
import kafka.server.MetadataCache;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.record.MemoryRecords;
import org.apache.kafka.common.requests.MetadataResponse;
import org.apache.kafka.common.requests.ProduceResponse;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

public class PartitionRouter {
/*
    private static final Logger log = LoggerFactory.getLogger(PartitionRouter.class);

    final private String CACHE_NAME = "kafka_partition";

    //meta data cache for get number of partition in the topic.
    private MetadataCache metadataCache;

    //map topic name to parition mapping table.
    private DataCache<String, PartitionMappingTable> cache = null;

    private PartitionRouter(){}

    static public PartitionRouter getInstance() {
        return PartitionRouterHolder.INSTANCE;
    }

    public void start(String clusterId, int brokerId, ZooKeeper zooKeeper, MetadataCache metadataCache,
                      ScheduledExecutorService scheduledExecutorService, String gatewayUrl) throws Exception {
        log.info("Partition Router startup");
        ZkUtil zkUtil = new ZkUtil(zooKeeper);
        DataProvider dataProvider = new PartitionDataProvider(clusterId, gatewayUrl);
        cache = new DataCache<>(CACHE_NAME, brokerId, dataProvider, scheduledExecutorService,
                zkUtil, 60000, 4*3600*1000);
        this.metadataCache = metadataCache;
    }

    public void shutdown() {
        log.info("Partition Router shutdown");
        if (cache != null) {
            cache.stop();
        }
    }

    public MetadataResponse updateMetadataResponse(MetadataResponse response, int controllerId, int version) {
        // this function update metadata response to route the disable partitions
        boolean changed = false;
        Collection<MetadataResponse.TopicMetadata> topicMetadatas = response.topicMetadata();
        //look update all topics
        for (MetadataResponse.TopicMetadata topicMetadata : topicMetadatas) {
            changed = updateTopicMetaData(topicMetadata) | changed;
        }
        //if need update. reconstruct request
        if (changed) {
            return new MetadataResponse((List<Node>)response.brokers(), response.clusterId(),
                    controllerId, (List<MetadataResponse.TopicMetadata>)response.topicMetadata(), version);
        }
        return response;
    }

    private boolean updateTopicMetaData(MetadataResponse.TopicMetadata topicMetadata) {
        if (topicMetadata.error() != Errors.NONE) {
            return false;
        }

        //get partition mapping table.
        PartitionMappingTable partitionMappingTable = cache.get(topicMetadata.topic());
        if (partitionMappingTable == null) {
            //no disable partition
            return false;
        }

        List<MetadataResponse.PartitionMetadata> partitionMetadatas = topicMetadata.partitionMetadata();
        List<MetadataResponse.PartitionMetadata> newPartitionMetadatas = new ArrayList<>();

        //look up all topic
        boolean replace = false;
        for (MetadataResponse.PartitionMetadata partitionMetadata : partitionMetadatas) {
            replace = replace | updatePartitionMetaData(partitionMetadata, partitionMappingTable, newPartitionMetadatas);
        }
        // if has disable partiont, rebuild topic metadata
        if (replace) {
            topicMetadata.partitionMetadata().clear();
            if (!newPartitionMetadatas.isEmpty()) {
                topicMetadata.partitionMetadata().addAll(newPartitionMetadatas);
            } else {
                log.error("Topic has not enable partitions: {}", topicMetadata.topic());
            }
            return true;
        }
        return false;
    }


    private boolean updatePartitionMetaData(MetadataResponse.PartitionMetadata partitionMetadata,
                                             PartitionMappingTable partitionMappingTable,
                                             List<MetadataResponse.PartitionMetadata> newPartitionMetadatas) {
        if (partitionMetadata.error() != Errors.NONE) {
            return false;
        }

        //partition is disable
        int partition = partitionMetadata.partition();
        if (isDisable(partition, partitionMappingTable)) {
            log.debug("Route topicpartion: {}:{} to disalbe",
                    partitionMappingTable.getTopicName(), partitionMetadata.partition());
            return true;
        }

        Node leaderNode = partitionMetadata.leader();
        int newPartition = partitionMappingTable.getMapToPartition(partitionMetadata.partition());
        //partion need to route
        if (newPartition != partitionMetadata.partition()) {
            log.debug("Route topicpartion: {}:{} to {}", partitionMappingTable.getTopicName(),
                    partitionMetadata.partition(), newPartition);
            //construct new partition metadata.
            MetadataResponse.PartitionMetadata newPartitionMetadata = new MetadataResponse.PartitionMetadata(
                    partitionMetadata.error(), newPartition, leaderNode, partitionMetadata.replicas(),
                    partitionMetadata.isr());
            newPartitionMetadatas.add(newPartitionMetadata);
        } else {
            newPartitionMetadatas.add(partitionMetadata);
        }
        return true;
    }

    public boolean updateProduceRequest(Map<TopicPartition, MemoryRecords> requestData,
                                        HashMap<String, PartitionMappingTable> partitionMappingTables) {
        Map<TopicPartition, MemoryRecords> topicPartitionMemoryRecordsMap = requestData;
        //lookup all TopicPartitons.
        Iterator<Map.Entry<TopicPartition, MemoryRecords>> iterator = topicPartitionMemoryRecordsMap.entrySet().iterator();
        Map<TopicPartition, MemoryRecords> newEntries = new HashMap<>();
        while(iterator.hasNext()) {
            Map.Entry<TopicPartition, MemoryRecords> entry = iterator.next();
            TopicPartition topicPartition = entry.getKey();

            //topic not have disable partitions
            PartitionMappingTable partitionMappingTable = partitionMappingTables.get(topicPartition.topic());
            int fromPartition = partitionMappingTable.getMapFromPartition(topicPartition.partition());

            //need restore origin partitions, update request
            if (fromPartition != topicPartition.partition()) {
                log.debug("Restore topicpartion: {}:{} to {}", partitionMappingTable.getTopicName(),
                        fromPartition, topicPartition.partition());
                TopicPartition realTopicPartion = new TopicPartition(topicPartition.topic(), fromPartition);
                MemoryRecords memoryRecords = entry.getValue();
                iterator.remove();
                newEntries.put(realTopicPartion, memoryRecords);
            }
        }
        topicPartitionMemoryRecordsMap.putAll(newEntries);
        return !newEntries.isEmpty();
    }

    public ProduceResponse updateProduceRespones(ProduceResponse response, int version,
                                                HashMap<String, PartitionMappingTable> partitionMappingTables) {
        Map<TopicPartition, ProduceResponse.PartitionResponse> responses = response.responses();
        Map<TopicPartition, ProduceResponse.PartitionResponse> newResponses = new HashMap<>();

        //lookup all topics
        Iterator<Map.Entry<TopicPartition, ProduceResponse.PartitionResponse>> iterator = responses.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<TopicPartition, ProduceResponse.PartitionResponse> entry = iterator.next();
            TopicPartition topicPartition = entry.getKey();
            ProduceResponse.PartitionResponse partitionResponse = entry.getValue();

            PartitionMappingTable partitionMappingTable = partitionMappingTables.get(topicPartition.topic());
            updatePartitionResponse(topicPartition, partitionResponse, responses, newResponses, partitionMappingTable);
        }

        //has disable partition need reconstruct respones
        if (!newResponses.isEmpty()) {
            return new ProduceResponse(newResponses, response.getThrottleTime(), version);
        }
        return response;
    }

    private void updatePartitionResponse(TopicPartition topicPartition,
                                         ProduceResponse.PartitionResponse partitionResponse,
                                         Map<TopicPartition, ProduceResponse.PartitionResponse> responses,
                                         Map<TopicPartition, ProduceResponse.PartitionResponse> newResponses,
                                         PartitionMappingTable partitionMappingTable) {
        //no disable partitions
        if (partitionMappingTable == null) {
            return;
        }

        int toPartition = partitionMappingTable.getMapToPartition(topicPartition.partition());
        //route partitions update response
        if (toPartition != topicPartition.partition()) {
            log.debug("Route topicpartion: {}:{} to {}", partitionMappingTable.getTopicName(),
                    topicPartition.partition(), toPartition);
            TopicPartition realTopicPartion = new TopicPartition(topicPartition.topic(), toPartition);

            if (newResponses.isEmpty()) {
                newResponses.putAll(responses);
            }
            newResponses.remove(topicPartition);
            newResponses.put(realTopicPartion, partitionResponse) ;
        }
    }

    public boolean getTopicPartitionIsDisable(TopicPartition topicPartition,
                                                      HashMap<String, PartitionMappingTable> partitionMappingTables) {
        PartitionMappingTable partitionMappingTable = partitionMappingTables.get(topicPartition.topic());
        if (partitionMappingTable == null || partitionMappingTable.getDisablePartitions().isEmpty()) {
            return false;
        }
        return partitionMappingTable.getMapFromPartition(topicPartition.partition()) == -1;

    }

    public HashMap<String, PartitionMappingTable> getPartitionMappingTables(Set<TopicPartition> topicPartitions) {
        HashMap<String,PartitionMappingTable> partitionMappingTables = new HashMap<>();
        for (TopicPartition topicPartition: topicPartitions ) {
            PartitionMappingTable partitionMappingTable = cache.get(topicPartition.topic());
            if (partitionMappingTable != null && !partitionMappingTable.getDisablePartitions().isEmpty()) {
                partitionMappingTables.put(topicPartition.topic(), partitionMappingTable);
            }
        }
        return partitionMappingTables;
    }

    public int getNumberPartition(String topic) {
        return metadataCache.getPartitionNumber(topic);
    }

    private boolean isDisable(int partion, PartitionMappingTable partitionMappingTable) {
        return partitionMappingTable.isDisable(partion);
    }

    private static class PartitionRouterHolder{

        private static final PartitionRouter INSTANCE = new PartitionRouter();

    }

    public class PartitionStatus {

        private PartitionMappingTable mappingTable;
        private boolean disable;

        public PartitionStatus(PartitionMappingTable mappingTable, boolean disable) {
            this.mappingTable = mappingTable;
            this.disable = disable;
        }

        public PartitionMappingTable getMappingTable() {
            return mappingTable;
        }

        public boolean isDisable() {
            return disable;
        }
    }
 */
}
