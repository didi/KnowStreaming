package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.OffsetLocationEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumerGroup;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionOffsetDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumeDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumerGroupSummary;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;

import java.util.List;
import java.util.Map;

/**
 * consumer相关的服务接口
 * @author tukun
 * @date 2015/11/12.
 */
public interface ConsumerService {
    /**
     * 获取消费组列表
     */
    List<ConsumerGroup> getConsumerGroupList(Long clusterId);

    /**
     * 查询消费Topic的消费组
     */
    List<ConsumerGroup> getConsumerGroupList(Long clusterId, String topicName);

    /**
     * 获取消费Topic的消费组概要信息
     */
    List<ConsumerGroupSummary> getConsumerGroupSummaries(Long clusterId, String topicName);

    /**
     * 查询消费详情
     */
    List<ConsumeDetailDTO> getConsumeDetail(ClusterDO clusterDO, String topicName, ConsumerGroup consumerGroup);

    /**
     * 获取消费组消费的Topic列表
     */
    List<String> getConsumerGroupConsumedTopicList(Long clusterId, String consumerGroup, String location);

    Map<Integer, Long> getConsumerOffset(ClusterDO clusterDO, String topicName, ConsumerGroup consumerGroup);

    /**
     * 重置offset
     */
    List<Result> resetConsumerOffset(ClusterDO clusterDO,
                                     String topicName,
                                     ConsumerGroup consumerGroup,
                                     List<PartitionOffsetDTO> partitionOffsetDTOList);

    Map<Long, Integer> getConsumerGroupNumMap(List<ClusterDO> clusterDOList);

    boolean checkConsumerGroupExist(OffsetLocationEnum offsetLocation, Long id, String topicName, String consumerGroup);
}
