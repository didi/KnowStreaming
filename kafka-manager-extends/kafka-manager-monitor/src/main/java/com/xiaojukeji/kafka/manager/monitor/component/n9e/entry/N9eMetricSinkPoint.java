package com.xiaojukeji.kafka.manager.monitor.component.n9e.entry;

import com.xiaojukeji.kafka.manager.monitor.common.entry.sink.MonitorBaseSinkTag;

/**
 * 夜莺上报监控数据点
 * @author zengqiao
 * @date 20/8/26
 */
public class N9eMetricSinkPoint {
    /**
     * 指标名
     */
    private String metric;

    /**
     * 指标对应的机器
     */
    private String endpoint;

    /**
     * 指标值
     */
    private String value;

    /**
     * 上报周期
     */
    private int step;

    /**
     * 当前时间戳，单位为s
     */
    private long timestamp;

    /**
     * tags
     */
    private MonitorBaseSinkTag tags;

    public N9eMetricSinkPoint(String metric,
                              String value,
                              int step,
                              long timestamp,
                              MonitorBaseSinkTag tags) {
        this.metric = metric;
        this.endpoint = tags.getHost();
        this.value = value;
        this.step = step;
        this.timestamp = timestamp;
        this.tags = tags;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public MonitorBaseSinkTag getTags() {
        return tags;
    }

    public void setTags(MonitorBaseSinkTag tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "N9eMetricSinkPoint{" +
                "metric='" + metric + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", value='" + value + '\'' +
                ", step=" + step +
                ", timestamp=" + timestamp +
                ", tags=" + tags +
                '}';
    }
}