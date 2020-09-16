package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.constant.OffsetStoreLocation;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.dto.consumer.ConsumeDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.entity.dto.consumer.ConsumerDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.consumer.ConsumerGroupDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.PartitionOffsetDTO;
import com.xiaojukeji.kafka.manager.common.exception.ConfigException;
import com.xiaojukeji.kafka.manager.common.utils.DefaultThreadFactory;
import com.xiaojukeji.kafka.manager.common.utils.zk.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.ConsumerMetadataCache;
import com.xiaojukeji.kafka.manager.service.cache.KafkaClientCache;
import com.xiaojukeji.kafka.manager.service.service.ConsumerService;
import com.xiaojukeji.kafka.manager.service.service.TopicService;
import com.xiaojukeji.kafka.manager.common.utils.zk.ZkPathUtil;
import kafka.admin.AdminClient;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.collection.JavaConversions;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

/**
 * @author tukun
 * @date 2015/11/12
 */
@Service("consumerService")
public class ConsumerServiceImpl implements ConsumerService {
    private final static Logger logger = LoggerFactory.getLogger(ConsumerServiceImpl.class);

    @Autowired
    private TopicService topicService;

    private final ExecutorService consumerListThreadPool = Executors.newFixedThreadPool(50, new DefaultThreadFactory("ConsumerPool"));

    @Override
    public List<ConsumerGroupDTO> getConsumerGroupList(Long clusterId) {
        List<ConsumerGroupDTO> consumerGroupDTOList = new ArrayList<>();
        for (OffsetStoreLocation location: OffsetStoreLocation.values()) {
            Set<String> consumerGroupSet = null;
            if (OffsetStoreLocation.ZOOKEEPER.equals(location)) {
                // 获取ZK中的消费组
                consumerGroupSet = ConsumerMetadataCache.getGroupInZkMap(clusterId);
            } else if (OffsetStoreLocation.BROKER.equals(location)) {
                // 获取Broker中的消费组
                consumerGroupSet = ConsumerMetadataCache.getGroupInBrokerMap(clusterId);
            }
            if (consumerGroupSet == null) {
                continue;
            }
            for (String consumerGroup : consumerGroupSet) {
                consumerGroupDTOList.add(new ConsumerGroupDTO(clusterId, consumerGroup, location));            }
        }
        return consumerGroupDTOList;
    }

    @Override
    public List<ConsumerGroupDTO> getConsumerGroupList(Long clusterId, String topicName) {
        List<ConsumerGroupDTO> consumerGroupDTOList = new ArrayList<>();

        for (OffsetStoreLocation location: OffsetStoreLocation.values()) {
            Set<String> consumerGroupSet = null;
            if (OffsetStoreLocation.ZOOKEEPER.equals(location)) {
                // 获取ZK中的消费组
                consumerGroupSet = ConsumerMetadataCache.getTopicConsumerGroupInZk(clusterId, topicName);
            } else if (OffsetStoreLocation.BROKER.equals(location)) {
                // 获取Broker中的消费组
                consumerGroupSet = ConsumerMetadataCache.getTopicConsumerGroupInBroker(clusterId, topicName);
            }
            if (consumerGroupSet == null) {
                continue;
            }
            for (String consumerGroup : consumerGroupSet) {
                consumerGroupDTOList.add(new ConsumerGroupDTO(clusterId, consumerGroup, location));
            }
        }
        return consumerGroupDTOList;
    }

    @Override
    public List<ConsumeDetailDTO> getConsumeDetail(ClusterDO clusterDO, String topicName, ConsumerGroupDTO consumeGroupDTO) {
        TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(clusterDO.getId(), topicName);
        if (topicMetadata == null) {
            return null;
        }

        List<ConsumeDetailDTO> consumerGroupDetailDTOList = null;
        if (OffsetStoreLocation.ZOOKEEPER.equals(consumeGroupDTO.getOffsetStoreLocation())) {
            consumerGroupDetailDTOList = getConsumerPartitionStateInZK(clusterDO, topicMetadata, consumeGroupDTO);
        } else if (OffsetStoreLocation.BROKER.equals(consumeGroupDTO.getOffsetStoreLocation())){
            consumerGroupDetailDTOList = getConsumerPartitionStateInBroker(clusterDO, topicMetadata, consumeGroupDTO);
        }
        if (consumerGroupDetailDTOList == null) {
            return null;
        }

        Map<TopicPartition, Long> topicPartitionLongMap = topicService.getTopicPartitionOffset(clusterDO, topicName);
        if (topicPartitionLongMap == null) {
            return consumerGroupDetailDTOList;
        }
        for (ConsumeDetailDTO consumerGroupDetailDTO : consumerGroupDetailDTOList) {
            consumerGroupDetailDTO.setOffset(topicPartitionLongMap.get(new TopicPartition(topicName, consumerGroupDetailDTO.getPartitionId())));
        }
        return consumerGroupDetailDTOList;
    }

    @Override
    public List<String> getConsumerGroupConsumedTopicList(ClusterDO cluster, ConsumerGroupDTO consumerGroupDTO) {
        if (cluster == null || consumerGroupDTO == null) {
            return new ArrayList<>();
        }
        return ConsumerMetadataCache.getConsumerGroupConsumedTopicList(cluster.getId(),consumerGroupDTO.getOffsetStoreLocation().getLocation(), consumerGroupDTO.getConsumerGroup());
    }

    @Override
    public List<ConsumerDTO> getMonitoredConsumerList(final ClusterDO clusterDO,
                                                      final Map<TopicPartition, Long> allPartitionOffsetMap) {
        List<ConsumerGroupDTO> consumerGroupDTOList = getConsumerGroupList(clusterDO.getId());
        if (consumerGroupDTOList == null || consumerGroupDTOList.isEmpty()) {
            return new ArrayList<>();
        }

        FutureTask<List<ConsumerDTO>>[] taskList = new FutureTask[consumerGroupDTOList.size()];
        for (int i = 0; i < consumerGroupDTOList.size(); i++) {
            final ConsumerGroupDTO consumerGroupDTO = consumerGroupDTOList.get(i);
            taskList[i] = new FutureTask<>(new Callable<List<ConsumerDTO>>() {
                @Override
                public List<ConsumerDTO> call() throws Exception {
                    try {
                        return getMonitoredConsumer(clusterDO, consumerGroupDTO, allPartitionOffsetMap);
                    } catch (Exception e) {
                        logger.error("get monitored consumer error, group:{}", consumerGroupDTO.getConsumerGroup(), e);
                    }
                    return null;
                }
            });
            consumerListThreadPool.submit(taskList[i]);
        }

        List<ConsumerDTO> consumerList = new ArrayList<>();
        for (FutureTask<List<ConsumerDTO>> task : taskList) {
            List<ConsumerDTO> dtoList = null;
            try {
                dtoList = task.get();
            } catch (Exception e) {
                logger.error("getMonitoredConsumerList@ConsumeServiceImpl, ", e);
            }
            if (dtoList == null) {
                continue;
            }
            consumerList.addAll(dtoList);
        }
        return consumerList;
    }

    private List<ConsumerDTO> getMonitoredConsumer(ClusterDO clusterDO,
                                                   ConsumerGroupDTO consumerGroupDTO,
                                                   Map<TopicPartition, Long> allPartitionOffsetMap) {
        List<ConsumerDTO> dtoList = new ArrayList<>();

        List<String> topicNameList = ConsumerMetadataCache.getConsumerGroupConsumedTopicList(
                clusterDO.getId(),
                consumerGroupDTO.getOffsetStoreLocation().getLocation(),
                consumerGroupDTO.getConsumerGroup()
        );
        for (String topicName : topicNameList) {
            TopicMetadata metadata = ClusterMetadataManager.getTopicMetaData(clusterDO.getId(), topicName);
            if (metadata == null || metadata.getPartitionNum() <= 0) {
                continue;
            }
            if (!allPartitionOffsetMap.containsKey(new TopicPartition(topicName, 0))) {
                Map<TopicPartition, Long> offsetMap = topicService.getTopicPartitionOffset(clusterDO, topicName);
                if (offsetMap == null) {
                    offsetMap = new HashMap<>();
                }
                allPartitionOffsetMap.putAll(offsetMap);
            }

            Map<Integer, Long> consumerOffsetMap = null;
            if (consumerGroupDTO.getOffsetStoreLocation().equals(OffsetStoreLocation.ZOOKEEPER)) {
                consumerOffsetMap = getTopicConsumerOffsetInZK(clusterDO, metadata, consumerGroupDTO);
            } else if (consumerGroupDTO.getOffsetStoreLocation().equals(OffsetStoreLocation.BROKER)) {
                consumerOffsetMap = getTopicConsumerOffsetInBroker(clusterDO, topicName, consumerGroupDTO);
            }

            Map<Integer, Long> partitionOffsetMap = new HashMap<>();
            for (int partitionId = 0; partitionId < metadata.getPartitionNum(); ++partitionId) {
                Long offset = allPartitionOffsetMap.get(new TopicPartition(topicName, partitionId));
                if (offset == null) {
                    continue;
                }
                partitionOffsetMap.put(partitionId, offset);
            }

            ConsumerDTO consumerDTO = new ConsumerDTO();
            consumerDTO.setClusterId(clusterDO.getId());
            consumerDTO.setTopicName(topicName);
            consumerDTO.setConsumerGroup(consumerGroupDTO.getConsumerGroup());
            consumerDTO.setLocation(consumerGroupDTO.getOffsetStoreLocation().getLocation());
            consumerDTO.setPartitionOffsetMap(partitionOffsetMap);
            consumerDTO.setConsumerOffsetMap(consumerOffsetMap);
            dtoList.add(consumerDTO);
        }
        return dtoList;
    }

    @Override
    public List<Result> resetConsumerOffset(ClusterDO clusterDO, String topicName, ConsumerGroupDTO consumerGroupDTO, List<PartitionOffsetDTO> partitionOffsetDTOList) {
        Map<TopicPartition, Long> offsetMap = partitionOffsetDTOList.stream().collect(Collectors.toMap(elem -> {return new TopicPartition(topicName, elem.getPartitionId());}, PartitionOffsetDTO::getOffset));
        List<Result> resultList = new ArrayList<>();

        // 创建KafkaConsumer, 修正offset值
        KafkaConsumer<String, String> kafkaConsumer = null;
        try {
            Properties properties = KafkaClientCache.createProperties(clusterDO, false);
            properties.setProperty("group.id", consumerGroupDTO.getConsumerGroup());
            kafkaConsumer = new KafkaConsumer<>(properties);
            checkAndCorrectPartitionOffset(kafkaConsumer, offsetMap);
            return resetConsumerOffset(clusterDO, kafkaConsumer, consumerGroupDTO, offsetMap);
        } catch (Exception e) {
            logger.error("resetConsumerOffset@ConsumeServiceImpl, create kafka consumer failed, clusterId:{} topicName:{} consumerGroup:{} partition:{}.", clusterDO.getId(), topicName, consumerGroupDTO, partitionOffsetDTOList, e);
            resultList.add(new Result(StatusCode.OPERATION_ERROR, "reset failed, create KafkaConsumer or check offset failed"));
        } finally {
            if (kafkaConsumer != null) {
                kafkaConsumer.close();
            }
        }
        return new ArrayList<>();
    }

    private List<Result> resetConsumerOffset(ClusterDO cluster, KafkaConsumer<String, String> kafkaConsumer, ConsumerGroupDTO consumerGroupDTO, Map<TopicPartition, Long> offsetMap) {
        List<Result> resultList = new ArrayList<>();

        for(Map.Entry<TopicPartition, Long> entry: offsetMap.entrySet()){
            TopicPartition tp =  entry.getKey();
            Long offset = entry.getValue();
            try {
                if (consumerGroupDTO.getOffsetStoreLocation().equals(OffsetStoreLocation.ZOOKEEPER)) {
                    resetConsumerOffsetInZK(cluster, consumerGroupDTO.getConsumerGroup(), tp, offset);
                } else if (consumerGroupDTO.getOffsetStoreLocation().equals(OffsetStoreLocation.BROKER)) {
                    resetConsumerOffsetInBroker(kafkaConsumer, tp, offset);
                }
            } catch (Exception e) {
                logger.error("resetConsumerOffset@ConsumeServiceImpl, reset failed, clusterId:{} consumerGroup:{} topic-partition:{}.", cluster.getId(), consumerGroupDTO, tp, e);
                resultList.add(new Result());
            }
            resultList.add(new Result());
        }
        return resultList;
    }

    private void checkAndCorrectPartitionOffset(KafkaConsumer<String, String> kafkaConsumer, Map<TopicPartition, Long> offsetMap) {
        List<TopicPartition> topicPartitionList = new ArrayList<>(offsetMap.keySet());
        Map<TopicPartition, Long> beginningOffsets = kafkaConsumer.beginningOffsets(topicPartitionList);
        Map<TopicPartition, Long> endOffsets = kafkaConsumer.endOffsets(topicPartitionList);
        for (TopicPartition tp: topicPartitionList) {
            Long offset = offsetMap.get(tp);
            Long earliestOffset = beginningOffsets.get(tp);
            Long largestOffset = endOffsets.get(tp);
            if (earliestOffset != null && offset < earliestOffset) {
                offsetMap.put(tp, earliestOffset);
            } else if (largestOffset != null && largestOffset < offset) {
                offsetMap.put(tp, largestOffset);
            }
        }
    }

    private void resetConsumerOffsetInZK(ClusterDO cluster,
                                         String consumerGroup,
                                         TopicPartition topicPartition,
                                         Long offset) throws Exception {
        ZkConfigImpl zkConfig = ClusterMetadataManager.getZKConfig(cluster.getId());
        String offsetPath = ZkPathUtil.getConsumerGroupOffsetTopicPartitionNode(consumerGroup, topicPartition.topic(), topicPartition.partition());
        zkConfig.setNodeStat(offsetPath, offset.toString());
    }

    private void resetConsumerOffsetInBroker(KafkaConsumer kafkaConsumer,
                                             TopicPartition topicPartition,
                                             Long offset) throws Exception {
        kafkaConsumer.assign(Arrays.asList(topicPartition));
        kafkaConsumer.seek(topicPartition, offset);
        kafkaConsumer.commitSync();
    }

    private Map<Integer, Long> getTopicConsumerOffsetInZK(ClusterDO clusterDO,
                                                          TopicMetadata topicMetadata,
                                                          ConsumerGroupDTO consumerGroupDTO) {
        Map<Integer, Long> offsetMap = new HashMap<>();

        ZkConfigImpl zkConfig = ClusterMetadataManager.getZKConfig(clusterDO.getId());
        for (int partitionId = 0; partitionId < topicMetadata.getPartitionNum(); ++partitionId) {
            //offset存储于zk中
            String consumerGroupOffsetLocation = ZkPathUtil.getConsumerGroupOffsetTopicPartitionNode(consumerGroupDTO.getConsumerGroup(), topicMetadata.getTopic(), partitionId);
            String offset = null;
            try {
                Stat stat = zkConfig.getNodeStat(consumerGroupOffsetLocation);
                if (stat == null) {
                    continue;
                }
                offset = zkConfig.get(consumerGroupOffsetLocation);
                offsetMap.put(partitionId, Long.valueOf(offset));
            } catch (ConfigException e) {
                e.printStackTrace();
            }
        }
        return offsetMap;
    }

    private Map<Integer, Long> getTopicConsumerOffsetInBroker(ClusterDO clusterDO,
                                                              String topicName,
                                                              ConsumerGroupDTO consumerGroupDTO) {
        Map<Integer, String> offsetsFromBroker = getOffsetByGroupAndTopicFromBroker(clusterDO, consumerGroupDTO.getConsumerGroup(), topicName);
        if (offsetsFromBroker == null || offsetsFromBroker.isEmpty()) {
            return new HashMap<>(0);
        }

        Map<Integer, Long> offsetMap = new HashMap<>(offsetsFromBroker.size());
        for (Map.Entry<Integer, String> entry: offsetsFromBroker.entrySet()) {
            try {
                offsetMap.put(entry.getKey(), Long.valueOf(entry.getValue()));
            } catch (Exception e) {
                logger.error("get topic consumer offset failed, clusterId:{} topicName:{} consumerGroup:{}."
                        , clusterDO.getId(), topicName, consumerGroupDTO.getConsumerGroup());
            }
        }
        return offsetMap;
    }

    private Map<Integer, String> getConsumeIdMap(Long clusterId, String topicName, String consumerGroup) {
        AdminClient.ConsumerGroupSummary consumerGroupSummary = ConsumerMetadataCache.getConsumerGroupSummary(clusterId, consumerGroup);
        if (consumerGroupSummary == null) {
            return new HashMap<>();
        }
        Map<Integer, String> consumerIdMap = new HashMap<>();
        for (scala.collection.immutable.List<AdminClient.ConsumerSummary> scalaSubConsumerSummaryList: JavaConversions.asJavaList(consumerGroupSummary.consumers().toList())) {
            List<AdminClient.ConsumerSummary> subConsumerSummaryList = JavaConversions.asJavaList(scalaSubConsumerSummaryList);
            for (AdminClient.ConsumerSummary consumerSummary: subConsumerSummaryList) {
                for (TopicPartition tp: JavaConversions.asJavaList(consumerSummary.assignment())) {
                    if (!tp.topic().equals(topicName)) {
                        continue;
                    }
                    consumerIdMap.put(tp.partition(), consumerSummary.host().substring(1, consumerSummary.host().length()) + ":" + consumerSummary.consumerId());
                }
            }
        }
        return consumerIdMap;
    }

    private List<ConsumeDetailDTO> getConsumerPartitionStateInBroker(ClusterDO clusterDO, TopicMetadata topicMetadata, ConsumerGroupDTO consumerGroupDTO) {
        Map<Integer, String> consumerIdMap = getConsumeIdMap(clusterDO.getId(), topicMetadata.getTopic(), consumerGroupDTO.getConsumerGroup());
        Map<Integer, String> consumeOffsetMap = getOffsetByGroupAndTopicFromBroker(clusterDO, consumerGroupDTO.getConsumerGroup(), topicMetadata.getTopic());

        List<ConsumeDetailDTO> consumeDetailDTOList = new ArrayList<>();
        for (int partitionId : topicMetadata.getPartitionMap().getPartitions().keySet()) {
            ConsumeDetailDTO consumeDetailDTO = new ConsumeDetailDTO();
            consumeDetailDTO.setPartitionId(partitionId);
            String consumeOffsetStr = consumeOffsetMap.get(partitionId);
            try {
                consumeDetailDTO.setConsumeOffset(StringUtils.isEmpty(consumeOffsetStr)? null: Long.valueOf(consumeOffsetStr));
            } catch (Exception e) {
                logger.error("getConsumerPartitionStateInBroker@ConsumerServiceImpl, illegal consumer offset, clusterId:{} topicName:{} consumerGroup:{} offset:{}.", clusterDO.getId(), topicMetadata.getTopic(), consumerGroupDTO.getConsumerGroup(), consumeOffsetStr, e);
            }
            consumeDetailDTO.setConsumerId(consumerIdMap.get(partitionId));
            consumeDetailDTOList.add(consumeDetailDTO);
        }
        return consumeDetailDTOList;
    }

    private List<ConsumeDetailDTO> getConsumerPartitionStateInZK(ClusterDO clusterDO,
                                                                 TopicMetadata topicMetadata,
                                                                 ConsumerGroupDTO consumerGroupDTO) {
        ZkConfigImpl zkConfig = ClusterMetadataManager.getZKConfig(clusterDO.getId());

        List<ConsumeDetailDTO> consumeDetailDTOList = new ArrayList<>();
        for (Integer partitionId : topicMetadata.getPartitionMap().getPartitions().keySet()) {
            String consumeGroupPath = ZkPathUtil.getConsumerGroupOffsetTopicPartitionNode(consumerGroupDTO.getConsumerGroup(), topicMetadata.getTopic(), partitionId);
            String consumeOffset = null;
            try {
                consumeOffset = zkConfig.get(consumeGroupPath);
            } catch (ConfigException e) {
                logger.error("get consumeOffset error for zk path:{}", consumeGroupPath, e);
            }
            String consumeIdZkPath = ZkPathUtil.getConsumerGroupOwnersTopicPartitionNode(consumerGroupDTO.getConsumerGroup(), topicMetadata.getTopic(), partitionId);
            String consumerId = null;
            try {
                consumerId = zkConfig.get(consumeIdZkPath);
            } catch (ConfigException e) {
//                logger.error("get consumerId error for zk path:{}", consumeIdZkPath, e);
            }

            ConsumeDetailDTO consumeDetailDTO = new ConsumeDetailDTO();
            consumeDetailDTO.setPartitionId(partitionId);
            consumeDetailDTO.setConsumerId(consumerId);
            consumeDetailDTO.setPartitionId(partitionId);
            if (!StringUtils.isEmpty(consumeOffset)) {
                consumeDetailDTO.setConsumeOffset(Long.valueOf(consumeOffset));
            }
            consumeDetailDTOList.add(consumeDetailDTO);
        }
        return consumeDetailDTOList;
    }

    /**
     * 根据group,topic获取broker中的group中的各个消费者的offset
     */
    private Map<Integer, String> getOffsetByGroupAndTopicFromBroker(ClusterDO clusterDO,
                                                                    String consumerGroup,
                                                                    String topicName) {
        Map<Integer, String> result = new HashMap<>();
        AdminClient client = KafkaClientCache.getAdminClient(clusterDO.getId());
        if (null == client) {
            return result;
        }
        Map<TopicPartition, Object> offsetMap = JavaConversions.asJavaMap(client.listGroupOffsets(consumerGroup));
        for (Map.Entry<TopicPartition, Object> entry : offsetMap.entrySet()) {
            TopicPartition topicPartition = entry.getKey();
            if (topicPartition.topic().equals(topicName)) {
                result.put(topicPartition.partition(), entry.getValue().toString());
            }
        }
        return result;
    }

    @Override
    public Map<Long, Integer> getConsumerGroupNumMap(List<ClusterDO> clusterDOList) {
        Map<Long, Integer> consumerGroupNumMap = new HashMap<>();
        for (ClusterDO clusterDO: clusterDOList) {
            Integer zkConsumerGroupNum = ConsumerMetadataCache.getGroupInZkMap(clusterDO.getId()).size();
            Integer brokerConsumerGroupNum = ConsumerMetadataCache.getGroupInBrokerMap(clusterDO.getId()).size();
            consumerGroupNumMap.put(clusterDO.getId(), zkConsumerGroupNum + brokerConsumerGroupNum);
        }
        return consumerGroupNumMap;
    }
}
