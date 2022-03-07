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

    /**
     * 获取消费者offset
     * @param clusterDO 集群
     * @param topicName topic
     * @param consumerGroup 消费组
     * @return Map<partitionId, offset>
     */
    Map<Integer, Long> getConsumerOffset(ClusterDO clusterDO, String topicName, ConsumerGroup consumerGroup);

    /**
     * 重置offset
     */
    List<Result> resetConsumerOffset(ClusterDO clusterDO,
                                     String topicName,
                                     ConsumerGroup consumerGroup,
                                     List<PartitionOffsetDTO> partitionOffsetDTOList);

    /**
     * 获取每个集群消费组的个数
     * @param clusterDOList 物理集群列表
     * @return Map<clusterId, consumerGroupNums>
     */
    Map<Long, Integer> getConsumerGroupNumMap(List<ClusterDO> clusterDOList);

    /**
     * 验证消费组是否存在
     * @param offsetLocation offset存放位置
     * @param id 集群id
     * @param topicName topic
     * @param consumerGroup 消费组
     * @return true:存在，false:不存在
     */
    boolean checkConsumerGroupExist(OffsetLocationEnum offsetLocation, Long id, String topicName, String consumerGroup);
}
