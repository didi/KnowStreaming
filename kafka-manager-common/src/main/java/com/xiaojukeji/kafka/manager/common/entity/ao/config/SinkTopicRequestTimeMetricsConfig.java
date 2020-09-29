package com.xiaojukeji.kafka.manager.common.entity.ao.config;

/**
 * @author zengqiao
 * @date 20/9/7
 */
public class SinkTopicRequestTimeMetricsConfig {
    private Long clusterId;

    private String topicName;

    private Long startId;

    private Long step;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Long getStartId() {
        return startId;
    }

    public void setStartId(Long startId) {
        this.startId = startId;
    }

    public Long getStep() {
        return step;
    }

    public void setStep(Long step) {
        this.step = step;
    }

    @Override
    public String toString() {
        return "SinkTopicRequestTimeMetricsConfig{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", startId=" + startId +
                ", step=" + step +
                '}';
    }
}