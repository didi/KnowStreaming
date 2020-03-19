package com.xiaojukeji.kafka.manager.common.entity.dto.alarm;

/**
 * 策略表达式
 * @author zengqiao
 * @date 19/12/16
 */
public class AlarmStrategyExpressionDTO {
    private String metric;

    private String opt;

    private Long threshold;

    private Integer duration;

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public Long getThreshold() {
        return threshold;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "AlarmStrategyExpressionModel{" +
                "metric='" + metric + '\'' +
                ", opt='" + opt + '\'' +
                ", threshold=" + threshold +
                ", duration=" + duration +
                '}';
    }

    public boolean legal() {
        if (metric == null
                || opt == null
                || threshold == null
                || duration == null || duration <= 0) {
            return false;
        }
        return true;
    }
}