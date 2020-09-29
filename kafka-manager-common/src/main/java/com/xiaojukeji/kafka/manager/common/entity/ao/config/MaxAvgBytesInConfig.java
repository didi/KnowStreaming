package com.xiaojukeji.kafka.manager.common.entity.ao.config;

/**
 * 峰值均值流入流量配置
 * @author zengqiao
 * @date 20/6/9
 */
public class MaxAvgBytesInConfig {
    private Integer duration;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "MaxAvgBytesInConfig{" +
                "duration=" + duration +
                '}';
    }
}