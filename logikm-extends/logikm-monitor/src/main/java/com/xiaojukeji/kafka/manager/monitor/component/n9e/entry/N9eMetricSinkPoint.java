package com.xiaojukeji.kafka.manager.monitor.component.n9e.entry;

import com.xiaojukeji.kafka.manager.monitor.common.entry.sink.AbstractMonitorSinkTag;

/**
 * 夜莺上报监控数据点
 * @author zengqiao
 * @date 20/8/26
 */
public class N9eMetricSinkPoint {
    /**
     * 节点ID
     */
    private String nid;

    /**
     * 指标名
     */
    private String metric;

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
    private String tags;

    public N9eMetricSinkPoint(String nid,
                              String metric,
                              Double value,
                              int step,
                              long timestamp,
                              AbstractMonitorSinkTag tags) {
        this.nid = nid;
        this.metric = metric;
        this.value = value;
        this.step = step;
        this.timestamp = timestamp;
        this.tags = tags.convert2Tags();
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "N9eMetricSinkPoint{" +
                "nid=" + nid +
                ", metric='" + metric + '\'' +
                ", value=" + value +
                ", step=" + step +
                ", timestamp=" + timestamp +
                ", tags='" + tags + '\'' +
                '}';
    }
}