package com.xiaojukeji.kafka.manager.common.events;

import com.xiaojukeji.kafka.manager.common.entity.metrics.ConsumerMetrics;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/8/31
 */
public class ConsumerMetricsCollectedEvent extends ApplicationEvent {
    private List<ConsumerMetrics> metricsList;

    public ConsumerMetricsCollectedEvent(Object source, List<ConsumerMetrics> metricsList) {
        super(source);
        this.metricsList = metricsList;
    }

    public List<ConsumerMetrics> getMetricsList() {
        return metricsList;
    }
}