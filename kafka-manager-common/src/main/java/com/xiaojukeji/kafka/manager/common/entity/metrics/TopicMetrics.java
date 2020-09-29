package com.xiaojukeji.kafka.manager.common.entity.metrics;

/**
 * @author zengqiao
 * @date 20/6/17
 */
public class TopicMetrics extends BaseMetrics {
    private String appId;

    private Long clusterId;

    private String topicName;

    public TopicMetrics(Long clusterId, String topicName) {
        super();
        this.clusterId = clusterId;
        this.topicName = topicName;
    }

    public TopicMetrics(String appId, Long clusterId, String topicName) {
        super();
        this.appId = appId;
        this.clusterId = clusterId;
        this.topicName = topicName;
    }

    public String getAppId() {
        return appId;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public String getTopicName() {
        return topicName;
    }

    @Override
    public String toString() {
        return "TopicMetrics{" +
                "appId='" + appId + '\'' +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", metricsMap=" + metricsMap +
                '}';
    }
}