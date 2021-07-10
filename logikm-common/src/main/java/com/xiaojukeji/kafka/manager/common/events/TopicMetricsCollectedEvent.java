package com.xiaojukeji.kafka.manager.common.events;

import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/8/31
 */
public class TopicMetricsCollectedEvent extends ApplicationEvent {
    private Long clusterId;

    private List<TopicMetrics> metricsList;

    public TopicMetricsCollectedEvent(Object source, Long clusterId, List<TopicMetrics> metricsList) {
        super(source);
        this.clusterId = clusterId;
        this.metricsList = metricsList;
    }

    public List<TopicMetrics> getMetricsList() {
        return metricsList;
    }

    public Long getClusterId() {
        return clusterId;
    }
}