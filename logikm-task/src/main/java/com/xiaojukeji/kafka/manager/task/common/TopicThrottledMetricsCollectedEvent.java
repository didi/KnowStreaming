package com.xiaojukeji.kafka.manager.task.common;

import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicThrottledMetrics;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/9/24
 */
public class TopicThrottledMetricsCollectedEvent extends BaseTaskEvent {
    private List<TopicThrottledMetrics> metricsList;

    public TopicThrottledMetricsCollectedEvent(Object source,
                                               long startTime,
                                               List<TopicThrottledMetrics> metricsList) {
        super(source, startTime);
        this.metricsList = metricsList;
    }

    public List<TopicThrottledMetrics> getMetricsList() {
        return metricsList;
    }
}