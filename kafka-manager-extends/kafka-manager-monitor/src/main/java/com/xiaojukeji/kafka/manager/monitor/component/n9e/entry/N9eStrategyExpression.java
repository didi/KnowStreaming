package com.xiaojukeji.kafka.manager.monitor.component.n9e.entry;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/10/18
 */
public class N9eStrategyExpression {
    private String metric;

    private String func;

    private String eopt;

    private Integer threshold;

    private List<Integer> params;

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

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public List<Integer> getParams() {
        return params;
    }

    public void setParams(List<Integer> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "N9eStrategyExpression{" +
                "metric='" + metric + '\'' +
                ", func='" + func + '\'' +
                ", eopt='" + eopt + '\'' +
                ", threshold=" + threshold +
                ", params=" + params +
                '}';
    }
}