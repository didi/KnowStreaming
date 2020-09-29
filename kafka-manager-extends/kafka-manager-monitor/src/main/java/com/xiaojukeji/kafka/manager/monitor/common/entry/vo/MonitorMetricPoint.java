package com.xiaojukeji.kafka.manager.monitor.common.entry.vo;

/**
 * @author zengqiao
 * @date 20/5/28
 */
public class MonitorMetricPoint {
    private Long timestamp;

    private Double value;

    public MonitorMetricPoint(Long timestamp, Double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

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
        return "MonitorMetricPoint{" +
                "value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}