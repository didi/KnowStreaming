package com.xiaojukeji.kafka.manager.service.strategy;


import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;

/**
 * @author zengqiao
 * @date 20/9/23
 */
public abstract class AbstractHealthScoreStrategy {
    public abstract Integer calBrokerHealthScore(Long clusterId, Integer brokerId, BrokerMetrics brokerMetrics);

    public abstract Integer calBrokerHealthScore(Long clusterId, Integer brokerId);

    public abstract Integer calTopicHealthScore(Long clusterId, String topicName);
}