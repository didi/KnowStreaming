package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.OffsetLocationEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumerGroupDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionOffsetDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumeDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * consumer相关的服务接口
 * @author tukun
 * @date 2015/11/12.
 */
public interface ConsumerService {
    /**
     * 获取消费组列表
     */
    List<ConsumerGroupDTO> getConsumerGroupList(Long clusterId);

    /**
     * 查询消费Topic的消费组
     */
    List<ConsumerGroupDTO> getConsumerGroupList(Long clusterId, String topicName);

    /**
     * 查询消费详情
     */
    List<ConsumeDetailDTO> getConsumeDetail(ClusterDO clusterDO, String topicName, ConsumerGroupDTO consumerGroupDTO);

    /**
     * 获取消费组消费的Topic列表
     */
    List<String> getConsumerGroupConsumedTopicList(Long clusterId, String consumerGroup, String location);

    Map<Integer, Long> getConsumerOffset(ClusterDO clusterDO,
                                         String topicName,
                                         ConsumerGroupDTO consumerGroupDTO);

    /**
     * 重置offset
     */
    List<Result> resetConsumerOffset(ClusterDO clusterDO,
                                     String topicName,
                                     ConsumerGroupDTO consumerGroupDTO,
                                     List<PartitionOffsetDTO> partitionOffsetDTOList);

    Map<Long, Integer> getConsumerGroupNumMap(List<ClusterDO> clusterDOList);

    boolean checkConsumerGroupExist(OffsetLocationEnum offsetLocation, Long id, String topicName, String consumerGroup);
}
