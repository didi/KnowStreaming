package com.xiaojukeji.kafka.manager.monitor.common.entry;

/**
 * @author zengqiao
 * @date 20/5/27
 */
public class StrategyExpression {
    private String metric;

    private String func;

    private String eopt;

    private Long threshold;

    private String params;

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getEopt() {
        return eopt;
    }

    public void setEopt(String eopt) {
        this.eopt = eopt;
    }

    public Long getThreshold() {
        return threshold;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "StrategyExpression{" +
                "metric='" + metric + '\'' +
                ", func='" + func + '\'' +
                ", eopt='" + eopt + '\'' +
                ", threshold=" + threshold +
                ", params=" + params +
                '}';
    }

    public boolean paramLegal() {
        if (metric == null
                || func == null
                || eopt == null
                || threshold == null
                || params == null) {
            return false;
        }
        return true;
    }
}