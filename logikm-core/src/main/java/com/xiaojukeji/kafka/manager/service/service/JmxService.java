package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaClientEnum;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionAttributeDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.PartitionState;
import org.apache.kafka.common.TopicPartition;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 从Jmx获取相关数据的服务接口
 *
 * @author tukun, zengqiao
 * @date 2015/11/11.
 */
public interface JmxService {
    /**
     * 获取Broker指标
     * @param clusterId 集群ID
     * @param brokerId brokerId
     * @param metricsCode 指标集
     * @return 如果broker不存在, 返回NULL, 其他情况返回BrokerMetrics, BrokerMetrics
     */
    BrokerMetrics getBrokerMetrics(Long clusterId, Integer brokerId, Integer metricsCode);

    TopicMetrics getTopicMetrics(Long clusterId, String topicName, Integer metricsCode, Boolean byAdd);

    TopicMetrics getTopicMetrics(Long clusterId, Integer brokerId, String topicName, Integer metricsCode, Boolean byAdd);

    /**
     * 获取topic消息压缩指标
     */
    String getTopicCodeCValue(Long clusterId, String topicName);

    List<TopicMetrics> getTopicMetrics(Long clusterId, Integer metricsCode, Boolean byAdd);

    /**
     * 从JMX中获取appId维度的的流量信息
     */
    List<TopicMetrics> getTopicAppMetrics(Long clusterId, Integer metricsCode);

    Map<TopicPartition, String> getBrokerTopicLocation(Long clusterId, Integer brokerId);

    /**
     * 获取分区位置和日志大小
     */
    Map<Integer, PartitionAttributeDTO> getPartitionAttribute(Long clusterId,
                                                              String topicName,
                                                              List<PartitionState> partitionStateList);

    /**
     * 获取被限流客户端
     */
    Set<String> getBrokerThrottleClients(Long clusterId, Integer brokerId, KafkaClientEnum kafkaClientEnum);

    String getBrokerVersion(Long clusterId, Integer brokerId);

    double getTopicAppThrottle(Long clusterId, Integer brokerId, String clientId, KafkaClientEnum kafkaClientEnum);
}
