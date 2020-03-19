package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.zookeeper.PartitionState;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.dto.consumer.ConsumerDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.consumer.ConsumerGroupDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.PartitionOffsetDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.consumer.ConsumeDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;

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
     * @param clusterId 集群Id
     * @author zengqiao
     * @date 19/5/14
     * @return java.util.List<com.didichuxing.datachannel.kafka.manager.common.entity.dto.ConsumerGroupDTO>
     */
    List<ConsumerGroupDTO> getConsumerGroupList(Long clusterId);

    /**
     * 查询消费Topic的消费组
     * @param clusterId 集群Id
     * @param topicName Topic名称
     * @author zengqiao
     * @date 19/5/14
     * @return java.util.List<com.didichuxing.datachannel.kafka.manager.common.entity.dto.ConsumerGroupDTO>
     */
    List<ConsumerGroupDTO> getConsumerGroupList(Long clusterId, String topicName);

    /**
     * 查询消费详情
     */
    List<ConsumeDetailDTO> getConsumeDetail(ClusterDO clusterDO, String topicName, ConsumerGroupDTO consumerGroupDTO);

    /**
     * 获取消费组消费的Topic列表
     * @param clusterDO 集群
     * @param consumerGroupDTO 消费组
     * @author zengqiao
     * @date 19/5/14
     * @return java.util.List<java.lang.String>
     */
    List<String> getConsumerGroupConsumedTopicList(ClusterDO clusterDO, ConsumerGroupDTO consumerGroupDTO);

    /**
     * 获取监控的消费者列表
     * @param clusterDO
     * @return
     */
    List<ConsumerDTO> getMonitoredConsumerList(ClusterDO clusterDO,
                                               Map<String, List<PartitionState>> topicNamePartitionStateListMap);

    /**
     * 重置offset
     * @param clusterDO 集群信息
     * @param topicName topic名称
     * @param consumerGroupDTO 消费组
     * @param partitionOffsetDTOList 设置的offset
     * @return List<Result>
     */
    List<Result> resetConsumerOffset(ClusterDO clusterDO,
                                     String topicName,
                                     ConsumerGroupDTO consumerGroupDTO,
                                     List<PartitionOffsetDTO> partitionOffsetDTOList);

    Map<Long, Integer> getConsumerGroupNumMap(List<ClusterDO> clusterDOList);
}
