package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.OffsetPosEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.OffsetLocationEnum;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumeDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumerGroup;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumerGroupSummary;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionOffsetDTO;
import com.xiaojukeji.kafka.manager.common.exception.ConfigException;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.ConsumerMetadataCache;
import com.xiaojukeji.kafka.manager.service.cache.KafkaClientPool;
import com.xiaojukeji.kafka.manager.service.service.ConsumerService;
import com.xiaojukeji.kafka.manager.service.service.TopicService;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkPathUtil;
import kafka.admin.AdminClient;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.types.SchemaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.collection.JavaConversions;

import java.util.*;
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

    @Override
    public List<ConsumerGroup> getConsumerGroupList(Long clusterId) {
        List<ConsumerGroup> consumerGroupList = new ArrayList<>();
        for (OffsetLocationEnum location: OffsetLocationEnum.values()) {
            Set<String> consumerGroupSet = null;
            if (OffsetLocationEnum.ZOOKEEPER.equals(location)) {
                // 获取ZK中的消费组
                consumerGroupSet = ConsumerMetadataCache.getGroupInZkMap(clusterId);
            } else if (OffsetLocationEnum.BROKER.equals(location)) {
                // 获取Broker中的消费组
                consumerGroupSet = ConsumerMetadataCache.getGroupInBrokerMap(clusterId);
            }
            if (ValidateUtils.isEmptySet(consumerGroupSet)) {
                continue;
            }
            for (String consumerGroup : consumerGroupSet) {
                consumerGroupList.add(new ConsumerGroup(clusterId, consumerGroup, location));
            }
        }
        return consumerGroupList;
    }

    @Override
    public List<ConsumerGroup> getConsumerGroupList(Long clusterId, String topicName) {
        List<ConsumerGroup> consumerGroupList = new ArrayList<>();

        for (OffsetLocationEnum location: OffsetLocationEnum.values()) {
            Set<String> consumerGroupSet = null;
            if (OffsetLocationEnum.ZOOKEEPER.equals(location)) {
                // 获取ZK中的消费组
                consumerGroupSet = ConsumerMetadataCache.getTopicConsumerGroupInZk(clusterId, topicName);
            } else if (OffsetLocationEnum.BROKER.equals(location)) {
                // 获取Broker中的消费组
                consumerGroupSet = ConsumerMetadataCache.getTopicConsumerGroupInBroker(clusterId, topicName);
            }
            if (ValidateUtils.isEmptySet(consumerGroupSet)) {
                continue;
            }
            for (String consumerGroup : consumerGroupSet) {
                consumerGroupList.add(new ConsumerGroup(clusterId, consumerGroup, location));
            }
        }
        return consumerGroupList;
    }

    @Override
    public List<ConsumerGroupSummary> getConsumerGroupSummaries(Long clusterId, String topicName) {
        List<ConsumerGroup> consumerGroupList = this.getConsumerGroupList(clusterId, topicName);
        if (ValidateUtils.isEmptyList(consumerGroupList)) {
            return Collections.emptyList();
        }

        List<ConsumerGroupSummary> summaryList = new ArrayList<>();
        for (ConsumerGroup consumerGroup: consumerGroupList) {
            ConsumerGroupSummary consumerGroupSummary = null;
            if (OffsetLocationEnum.ZOOKEEPER.equals(consumerGroup.getOffsetStoreLocation())) {
                consumerGroupSummary = new ConsumerGroupSummary();
                consumerGroupSummary.setClusterId(consumerGroup.getClusterId());
                consumerGroupSummary.setConsumerGroup(consumerGroup.getConsumerGroup());
                consumerGroupSummary.setOffsetStoreLocation(consumerGroup.getOffsetStoreLocation());
            } else {
                consumerGroupSummary = getConsumerGroupSummary(clusterId, topicName, consumerGroup.getConsumerGroup());
            }
            summaryList.add(consumerGroupSummary);
        }
        return summaryList;
    }

    private ConsumerGroupSummary getConsumerGroupSummary(Long clusterId, String topicName, String consumerGroup) {
        ConsumerGroupSummary summary = new ConsumerGroupSummary();
        summary.setClusterId(clusterId);
        summary.setConsumerGroup(consumerGroup);
        summary.setOffsetStoreLocation(OffsetLocationEnum.BROKER);
        summary.setAppIdList(new ArrayList<>());
        summary.setState("");
        try {
            AdminClient adminClient = KafkaClientPool.getAdminClient(clusterId);

            AdminClient.ConsumerGroupSummary consumerGroupSummary = adminClient.describeConsumerGroup(consumerGroup);
            if (ValidateUtils.isNull(consumerGroupSummary)) {
                return summary;
            }
            summary.setState(consumerGroupSummary.state());

            Iterator<scala.collection.immutable.List<AdminClient.ConsumerSummary>> it = JavaConversions.asJavaIterator(consumerGroupSummary.consumers().iterator());
            while (it.hasNext()) {
                List<AdminClient.ConsumerSummary> consumerSummaryList = JavaConversions.asJavaList(it.next());
                for (AdminClient.ConsumerSummary consumerSummary: consumerSummaryList) {
                    List<TopicPartition> topicPartitionList = JavaConversions.asJavaList(consumerSummary.assignment());
                    if (ValidateUtils.isEmptyList(topicPartitionList)) {
                        continue;
                    }
                    if (topicPartitionList.stream().anyMatch(elem -> elem.topic().equals(topicName)) && consumerSummary.clientId().contains(".")) {
                        String [] splitArray = consumerSummary.clientId().split("\\.");
                        summary.getAppIdList().add(splitArray[0]);
                    }
                }
            }
        } catch (SchemaException e) {
            logger.error("class=ConsumerServiceImpl||method=getConsumerGroupSummary||clusterId={}||topicName={}||consumerGroup={}||errMsg={}||schema exception",
                    clusterId, topicName, consumerGroup, e.getMessage());
        } catch (Exception e) {
            logger.error("class=ConsumerServiceImpl||method=getConsumerGroupSummary||clusterId={}||topicName={}||consumerGroup={}||errMsg={}||throws exception",
                    clusterId, topicName, consumerGroup, e.getMessage());
        }
        summary.setAppIdList(new ArrayList<>(new HashSet<>(summary.getAppIdList())));
        return summary;
    }

    @Override
    public List<ConsumeDetailDTO> getConsumeDetail(ClusterDO clusterDO, String topicName, ConsumerGroup consumerGroup) {
        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterDO.getId(), topicName);
        if (topicMetadata == null) {
            logger.warn("class=ConsumerServiceImpl||method=getConsumeDetail||clusterId={}||topicName={}||msg=topicMetadata is null!",
                    clusterDO.getId(), topicName);
            return null;
        }

        List<ConsumeDetailDTO> consumerGroupDetailDTOList = null;
        if (OffsetLocationEnum.ZOOKEEPER.equals(consumerGroup.getOffsetStoreLocation())) {
            consumerGroupDetailDTOList = getConsumerPartitionStateInZK(clusterDO, topicMetadata, consumerGroup);
        } else if (OffsetLocationEnum.BROKER.equals(consumerGroup.getOffsetStoreLocation())){
            consumerGroupDetailDTOList = getConsumerPartitionStateInBroker(clusterDO, topicMetadata, consumerGroup);
        }
        if (consumerGroupDetailDTOList == null) {
            logger.info("class=ConsumerServiceImpl||method=getConsumeDetail||msg=consumerGroupDetailDTOList is null!");
            return null;
        }

        Map<TopicPartition, Long> topicPartitionLongMap = topicService.getPartitionOffset(clusterDO, topicName, OffsetPosEnum.END);
        if (topicPartitionLongMap == null) {
            return consumerGroupDetailDTOList;
        }
        for (ConsumeDetailDTO consumerGroupDetailDTO : consumerGroupDetailDTOList) {
            consumerGroupDetailDTO.setOffset(topicPartitionLongMap.get(new TopicPartition(topicName, consumerGroupDetailDTO.getPartitionId())));
        }
        return consumerGroupDetailDTOList;
    }

    @Override
    public List<String> getConsumerGroupConsumedTopicList(Long clusterId, String consumerGroup, String location) {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isNull(consumerGroup)
                || ValidateUtils.isNull(location)) {
            return new ArrayList<>();
        }
        return ConsumerMetadataCache.getConsumerGroupConsumedTopicList(clusterId, consumerGroup, location);
    }

    @Override
    public List<Result> resetConsumerOffset(ClusterDO clusterDO, String topicName, ConsumerGroup consumerGroup, List<PartitionOffsetDTO> partitionOffsetDTOList) {
        Map<TopicPartition, Long> offsetMap = partitionOffsetDTOList.stream().collect(Collectors.toMap(elem -> {return new TopicPartition(topicName, elem.getPartitionId());}, PartitionOffsetDTO::getOffset));
        List<Result> resultList = new ArrayList<>();

        // 创建KafkaConsumer, 修正offset值
        KafkaConsumer<String, String> kafkaConsumer = null;
        try {
            Properties properties = KafkaClientPool.createProperties(clusterDO, false);
            properties.setProperty("group.id", consumerGroup.getConsumerGroup());
            kafkaConsumer = new KafkaConsumer<>(properties);
            checkAndCorrectPartitionOffset(kafkaConsumer, offsetMap);
            return resetConsumerOffset(clusterDO, kafkaConsumer, consumerGroup, offsetMap);
        } catch (Exception e) {
            logger.error("create kafka consumer failed, clusterId:{} topicName:{} consumerGroup:{} partition:{}.", clusterDO.getId(), topicName, consumerGroup, partitionOffsetDTOList, e);
            resultList.add(new Result(
                    ResultStatus.OPERATION_FAILED.getCode(),
                    "reset failed, create KafkaConsumer or check offset failed"
            ));
        } finally {
            if (kafkaConsumer != null) {
                kafkaConsumer.close();
            }
        }
        return resultList;
    }

    private List<Result> resetConsumerOffset(ClusterDO cluster, KafkaConsumer<String, String> kafkaConsumer, ConsumerGroup consumerGroup, Map<TopicPartition, Long> offsetMap) {
        List<Result> resultList = new ArrayList<>();

        for(Map.Entry<TopicPartition, Long> entry: offsetMap.entrySet()){
            TopicPartition tp =  entry.getKey();
            Long offset = entry.getValue();
            try {
                if (consumerGroup.getOffsetStoreLocation().equals(OffsetLocationEnum.ZOOKEEPER)) {
                    resetConsumerOffsetInZK(cluster, consumerGroup.getConsumerGroup(), tp, offset);
                } else if (consumerGroup.getOffsetStoreLocation().equals(OffsetLocationEnum.BROKER)) {
                    resetConsumerOffsetInBroker(kafkaConsumer, tp, offset);
                }
            } catch (Exception e) {
                logger.error("reset failed, clusterId:{} consumerGroup:{} topic-partition:{}.", cluster.getId(), consumerGroup, tp, e);
                resultList.add(new Result(
                        ResultStatus.OPERATION_FAILED.getCode(),
                        "reset failed..."));
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
        ZkConfigImpl zkConfig = PhysicalClusterMetadataManager.getZKConfig(cluster.getId());
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

    @Override
    public Map<Integer, Long> getConsumerOffset(ClusterDO clusterDO,
                                                String topicName,
                                                ConsumerGroup consumerGroup) {
        if (ValidateUtils.isNull(clusterDO) || ValidateUtils.isBlank(topicName) || ValidateUtils.isNull(consumerGroup)) {
            return null;
        }
        if (OffsetLocationEnum.BROKER.equals(consumerGroup.getOffsetStoreLocation())) {
            return getConsumerOffsetFromBK(clusterDO, topicName, consumerGroup.getConsumerGroup());
        } else if (OffsetLocationEnum.ZOOKEEPER.equals(consumerGroup.getOffsetStoreLocation())) {
            return getConsumerOffsetFromZK(clusterDO.getId(), topicName, consumerGroup.getConsumerGroup());
        }
        return null;
    }

    private Map<Integer, Long> getConsumerOffsetFromZK(Long clusterId, String topicName, String consumerGroup) {
        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
        if (ValidateUtils.isNull(topicMetadata)) {
            return new HashMap<>(0);
        }
        ZkConfigImpl zkConfig = PhysicalClusterMetadataManager.getZKConfig(clusterId);

        Map<Integer, Long> consumerOffsetMap = new HashMap<>(topicMetadata.getPartitionNum());
        for (Integer partitionId : topicMetadata.getPartitionMap().getPartitions().keySet()) {
            String consumerGroupOffsetLocation =
                    ZkPathUtil.getConsumerGroupOffsetTopicPartitionNode(consumerGroup, topicName, partitionId);
            try {
                consumerOffsetMap.put(partitionId, Long.valueOf(zkConfig.get(consumerGroupOffsetLocation)));
            } catch (Exception e) {
                logger.error("get consumer offset from zk failed, clusterId:{} topicName:{} consumerGroup:{}.",
                        clusterId, topicName, consumerGroup, e);
            }
        }
        return consumerOffsetMap;
    }

    private Map<Integer, Long> getConsumerOffsetFromBK(ClusterDO clusterDO,
                                                       String topicName,
                                                       String consumerGroup) {
        Map<Integer, String> stringOffsetMap =
                getOffsetByGroupAndTopicFromBroker(clusterDO, consumerGroup, topicName);
        if (ValidateUtils.isNull(stringOffsetMap)) {
            return new HashMap<>(0);
        }

        Map<Integer, Long> offsetMap = new HashMap<>(stringOffsetMap.size());
        for (Map.Entry<Integer, String> entry: stringOffsetMap.entrySet()) {
            try {
                offsetMap.put(entry.getKey(), Long.valueOf(entry.getValue()));
            } catch (Exception e) {
                logger.error("get consumer offset from bk failed, clusterId:{} topicName:{} consumerGroup:{}.",
                        clusterDO.getId(), topicName, consumerGroup, e);
            }
        }
        return offsetMap;
    }

    private Map<Integer, String> getConsumeIdMap(Long clusterId, String topicName, String consumerGroup) {
        AdminClient.ConsumerGroupSummary consumerGroupSummary = ConsumerMetadataCache.getConsumerGroupSummary(clusterId, consumerGroup);
        if (consumerGroupSummary == null) {
            return new HashMap<>(0);
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

    private List<ConsumeDetailDTO> getConsumerPartitionStateInBroker(ClusterDO clusterDO, TopicMetadata topicMetadata, ConsumerGroup consumerGroup) {
        Map<Integer, String> consumerIdMap = getConsumeIdMap(clusterDO.getId(), topicMetadata.getTopic(), consumerGroup.getConsumerGroup());
        Map<Integer, String> consumeOffsetMap = getOffsetByGroupAndTopicFromBroker(clusterDO, consumerGroup.getConsumerGroup(), topicMetadata.getTopic());

        List<ConsumeDetailDTO> consumeDetailDTOList = new ArrayList<>();
        for (int partitionId : topicMetadata.getPartitionMap().getPartitions().keySet()) {
            ConsumeDetailDTO consumeDetailDTO = new ConsumeDetailDTO();
            consumeDetailDTO.setPartitionId(partitionId);
            String consumeOffsetStr = consumeOffsetMap.get(partitionId);
            try {
                consumeDetailDTO.setConsumeOffset(StringUtils.isEmpty(consumeOffsetStr)? null: Long.valueOf(consumeOffsetStr));
            } catch (Exception e) {
                logger.error("illegal consumer offset, clusterId:{} topicName:{} consumerGroup:{} offset:{}.", clusterDO.getId(), topicMetadata.getTopic(), consumerGroup.getConsumerGroup(), consumeOffsetStr, e);
            }
            consumeDetailDTO.setConsumerId(consumerIdMap.get(partitionId));
            consumeDetailDTOList.add(consumeDetailDTO);
        }
        return consumeDetailDTOList;
    }

    private List<ConsumeDetailDTO> getConsumerPartitionStateInZK(ClusterDO clusterDO, TopicMetadata topicMetadata, ConsumerGroup consumerGroup) {
        ZkConfigImpl zkConfig = PhysicalClusterMetadataManager.getZKConfig(clusterDO.getId());

        List<ConsumeDetailDTO> consumeDetailDTOList = new ArrayList<>();
        for (Integer partitionId : topicMetadata.getPartitionMap().getPartitions().keySet()) {
            String consumeGroupPath = ZkPathUtil.getConsumerGroupOffsetTopicPartitionNode(consumerGroup.getConsumerGroup(), topicMetadata.getTopic(), partitionId);
            String consumeOffset = null;
            try {
                consumeOffset = zkConfig.get(consumeGroupPath);
            } catch (ConfigException e) {
                logger.error("get consumeOffset error for zk path:{}", consumeGroupPath, e);
            }
            String consumeIdZkPath = ZkPathUtil.getConsumerGroupOwnersTopicPartitionNode(consumerGroup.getConsumerGroup(), topicMetadata.getTopic(), partitionId);
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
        AdminClient client = KafkaClientPool.getAdminClient(clusterDO.getId());
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

    @Override
    public boolean checkConsumerGroupExist(OffsetLocationEnum offsetLocation, Long clusterId, String topicName, String consumerGroup) {
        List<ConsumerGroup>  consumerGroupList = getConsumerGroupList(clusterId, topicName).stream()
                .filter(group -> offsetLocation.location.equals(group.getOffsetStoreLocation().location) && consumerGroup.equals(group.getConsumerGroup()))
                .collect(Collectors.toList());
        return !ValidateUtils.isEmptyList(consumerGroupList);
    }
}
