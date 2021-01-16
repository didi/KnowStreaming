package com.xiaojukeji.kafka.manager.openapi.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.*;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionOffsetDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumerGroup;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.openapi.ThirdPartService;
import com.xiaojukeji.kafka.manager.openapi.common.dto.*;
import com.xiaojukeji.kafka.manager.service.cache.KafkaClientPool;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.*;
import kafka.admin.AdminClient;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.collection.JavaConversions;

import java.util.*;

/**
 * @author zengqiao
 * @date 20/5/22
 */
@Service("thirdPartService")
public class ThirdPartServiceImpl implements ThirdPartService {
    private static Logger LOGGER = LoggerFactory.getLogger(ThirdPartServiceImpl.class);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private ConsumerService consumerService;

    @Override
    public Result<ConsumeHealthEnum> checkConsumeHealth(Long clusterId,
                                                        String topicName,
                                                        String consumerGroup,
                                                        Long maxDelayTime) {
        ClusterDO clusterDO = clusterService.getById(clusterId);
        if (ValidateUtils.isNull(clusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
        if (ValidateUtils.isNull(topicMetadata)) {
            return Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST);
        }

        // 获取消费组当前的offset
        Map<TopicPartition, Object> consumeOffsetMap = listGroupOffsets(clusterId, consumerGroup);
        if (ValidateUtils.isNull(consumeOffsetMap)) {
            return new Result<>(ConsumeHealthEnum.UNKNOWN);
        }
        if (consumeOffsetMap.isEmpty()) {
            return Result.buildFrom(ResultStatus.CONSUMER_GROUP_NOT_EXIST);
        }

        Long delayTimestamp = System.currentTimeMillis() - maxDelayTime;

        // 获取指定时间的offset
        Map<TopicPartition, OffsetAndTimestamp> offsetAndTimeMap =
                offsetsForTimes(clusterDO, topicMetadata, delayTimestamp);
        if (ValidateUtils.isNull(offsetAndTimeMap)) {
            return new Result<>(ConsumeHealthEnum.UNKNOWN);
        }

        for (TopicPartition tp : offsetAndTimeMap.keySet()) {
            OffsetAndTimestamp offsetAndTimestamp = offsetAndTimeMap.get(tp);
            Long consumeOffset = (Long) consumeOffsetMap.get(tp);
            if (ValidateUtils.isNull(consumeOffset)) {
                return new Result<>(ConsumeHealthEnum.UNKNOWN);
            }

            if (offsetAndTimestamp.offset() <= consumeOffset) {
                // 健康的
                continue;
            }

            return new Result<>(ConsumeHealthEnum.UNHEALTH);
        }
        return new Result<>(ConsumeHealthEnum.HEALTH);
    }

    private Map<TopicPartition, Object> listGroupOffsets(Long clusterId, String consumerGroup) {
        AdminClient client = KafkaClientPool.getAdminClient(clusterId);
        if (ValidateUtils.isNull(client)) {
            return null;
        }
        try {
            return JavaConversions.asJavaMap(client.listGroupOffsets(consumerGroup));
        } catch (Exception e) {
            LOGGER.error("list group offsets failed, clusterId:{}, consumerGroup:{}.", clusterId, consumerGroup, e);
        }
        return null;
    }

    private Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes(ClusterDO clusterDO,
                                                                    TopicMetadata topicMetadata,
                                                                    Long timestamp) {
        KafkaConsumer kafkaConsumer = null;
        try {
            kafkaConsumer = KafkaClientPool.borrowKafkaConsumerClient(clusterDO);
            if (ValidateUtils.isNull(kafkaConsumer)) {
                return null;
            }
            Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
            for (Integer partitionId : topicMetadata.getPartitionMap().getPartitions().keySet()) {
                timestampsToSearch.put(new TopicPartition(topicMetadata.getTopic(), partitionId), timestamp);
            }
            return kafkaConsumer.offsetsForTimes(timestampsToSearch);
        } catch (Exception e) {
            LOGGER.error("get offset for time failed, clusterDO:{} topicMetadata:{} timestamp:{}.",
                    clusterDO, topicMetadata, timestamp, e);
        } finally {
            KafkaClientPool.returnKafkaConsumerClient(clusterDO.getId(), kafkaConsumer);
        }
        return null;
    }

    @Override
    public List<Result> resetOffsets(ClusterDO clusterDO, OffsetResetDTO dto) {
        if (ValidateUtils.isNull(dto)) {
            return null;
        }

        List<PartitionOffsetDTO> offsetDTOList = this.getPartitionOffsetDTOList(clusterDO, dto);
        if (ValidateUtils.isEmptyList(offsetDTOList)) {
            return null;
        }

        OffsetLocationEnum offsetLocation = dto.getLocation().equals(
                OffsetLocationEnum.ZOOKEEPER.location) ? OffsetLocationEnum.ZOOKEEPER : OffsetLocationEnum.BROKER;
        ResultStatus result = checkConsumerGroupExist(clusterDO, dto.getTopicName(), dto.getConsumerGroup(), offsetLocation, dto.getCreateIfAbsent());
        if (ResultStatus.SUCCESS.getCode() != result.getCode()) {
            return null;
        }
        ConsumerGroup consumerGroup = new ConsumerGroup(clusterDO.getId(), dto.getConsumerGroup(), OffsetLocationEnum.getOffsetStoreLocation(dto.getLocation()));
        return consumerService.resetConsumerOffset(
                clusterDO,
                dto.getTopicName(),
                consumerGroup,
                offsetDTOList
        );
    }

    private List<PartitionOffsetDTO> getPartitionOffsetDTOList(ClusterDO clusterDO, OffsetResetDTO dto) {
        List<PartitionOffsetDTO> offsetDTOList = dto.getPartitionOffsetDTOList();
        if (!ValidateUtils.isEmptyList(offsetDTOList)) {
            return offsetDTOList;
        }

        offsetDTOList = topicService.getPartitionOffsetList(clusterDO, dto.getTopicName(), dto.getTimestamp());
        if (!ValidateUtils.isEmptyList(offsetDTOList)) {
            return offsetDTOList;
        }

        Map<TopicPartition, Long> endOffsetMap = topicService.getPartitionOffset(clusterDO, dto.getTopicName(), OffsetPosEnum.END);
        if (ValidateUtils.isEmptyMap(endOffsetMap)) {
            return new ArrayList<>();
        }

        Map<TopicPartition, Long> beginOffsetMap = topicService.getPartitionOffset(clusterDO, dto.getTopicName(), OffsetPosEnum.BEGINNING);
        if (ValidateUtils.isEmptyMap(beginOffsetMap)) {
            return new ArrayList<>();
        }

        offsetDTOList = new ArrayList<>();
        for (Map.Entry<TopicPartition, Long> entry: endOffsetMap.entrySet()) {
            Long beginOffset = beginOffsetMap.get(entry.getKey());
            if (ValidateUtils.isNull(beginOffset) || !beginOffset.equals(entry.getValue())) {
                // offset 不相等, 表示还有数据, 则直接返回
                return new ArrayList<>();
            }
            offsetDTOList.add(new PartitionOffsetDTO(entry.getKey().partition(), entry.getValue()));
        }
        return offsetDTOList;
    }

    private ResultStatus checkConsumerGroupExist(ClusterDO clusterDO,
                                           String topicName,
                                           String consumerGroup,
                                           OffsetLocationEnum offsetLocation,
                                           Boolean createIfAbsent) {
        if (createIfAbsent) {
            // 如果不存在, 则直接创建
            return isCreateIfAbsentOverflow(clusterDO, topicName);
        }
        if (!consumerService.checkConsumerGroupExist(offsetLocation, clusterDO.getId(), topicName, consumerGroup)) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        return ResultStatus.SUCCESS;

    }

    /**
     * 限制单天单集群的重置次数不能超过20个
     * <clusterId-topicName, timestamp * 100 + count>
     */
    private static final Map<String, Long> createIfAbsentCountMap = new HashMap<>();

    private synchronized ResultStatus isCreateIfAbsentOverflow(ClusterDO clusterDO, String topicName) {
        String key = clusterDO.getId() + "_" + topicName;
        Long timestampAndCount = createIfAbsentCountMap.get(key);
        if (ValidateUtils.isNull(timestampAndCount) ||
                (System.currentTimeMillis() - (timestampAndCount / 100) >= (24 *60 * 60 * 1000))) {
            // 24小时卫触发, 统计归0
            timestampAndCount = System.currentTimeMillis() * 100L + 1;
        } else if (timestampAndCount % 100 > 20) {
            return ResultStatus.OPERATION_FORBIDDEN;
        }  else {
            timestampAndCount += 1;
        }
        createIfAbsentCountMap.put(key, timestampAndCount);
        return ResultStatus.SUCCESS;
    }
}