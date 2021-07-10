package com.xiaojukeji.kafka.manager.monitor.common.entry;

import com.xiaojukeji.kafka.manager.monitor.common.entry.sink.AbstractMonitorSinkTag;

/**
 * @author huangjw
 * @date 17/5/24.
 */
public class MetricSinkPoint {
    /**
     * 指标名
     */
    private String name;

    /**
     * 指标值
     */
    private Double value;

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
    private AbstractMonitorSinkTag tags;

    public MetricSinkPoint(String name, double value, int step, long timestamp, AbstractMonitorSinkTag tags) {
        this.name = name;
        this.value = value;
        this.step = step;
        this.timestamp = timestamp;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
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

    public AbstractMonitorSinkTag getTags() {
        return tags;
    }

    public void setTags(AbstractMonitorSinkTag tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "MetricSinkPoint{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", timestamp=" + timestamp +
                ", tags=" + tags +
                ", step=" + step +
                '}';
    }
}
