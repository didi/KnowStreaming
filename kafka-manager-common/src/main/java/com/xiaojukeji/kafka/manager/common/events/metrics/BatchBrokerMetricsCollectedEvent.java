package com.xiaojukeji.kafka.manager.common.events.metrics;

import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/8/31
 */
public class BatchBrokerMetricsCollectedEvent extends BaseMetricsCollectedEvent {
    private final List<BrokerMetrics> metricsList;

    public BatchBrokerMetricsCollectedEvent(Object source, Long physicalClusterId, Long collectTime, List<BrokerMetrics> metricsList) {
        super(source, physicalClusterId, collectTime);
        this.metricsList = metricsList;
    }

    public List<BrokerMetrics> getMetricsList() {
        return metricsList;
    }
}