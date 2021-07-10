package com.xiaojukeji.kafka.manager.monitor.common.entry.dto;

/**
 * @author zengqiao
 * @date 20/5/22
 */
public class MetricPoint {
    private Double value;

    private Long timestamp;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AlertPoint{" +
                "value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}