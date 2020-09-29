package com.xiaojukeji.kafka.manager.monitor.common.entry;

import com.xiaojukeji.kafka.manager.monitor.common.entry.dto.MetricPoint;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/28
 */
public class Metric {
    private String metric;

    private Integer step;

    private List<MetricPoint> values;

    private Integer comparison;

    private Integer delta;

    private Boolean origin;

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public List<MetricPoint> getValues() {
        return values;
    }

    public void setValues(List<MetricPoint> values) {
        this.values = values;
    }

    public Integer getComparison() {
        return comparison;
    }

    public void setComparison(Integer comparison) {
        this.comparison = comparison;
    }

    public Integer getDelta() {
        return delta;
    }

    public void setDelta(Integer delta) {
        this.delta = delta;
    }

    public Boolean getOrigin() {
        return origin;
    }

    public void setOrigin(Boolean origin) {
        this.origin = origin;
    }

    @Override
    public String toString() {
        return "Metric{" +
                "metric='" + metric + '\'' +
                ", step=" + step +
                ", values=" + values +
                ", comparison=" + comparison +
                ", delta=" + delta +
                ", origin=" + origin +
                '}';
    }
}