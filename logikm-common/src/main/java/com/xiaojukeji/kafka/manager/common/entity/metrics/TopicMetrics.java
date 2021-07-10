package com.xiaojukeji.kafka.manager.common.entity.metrics;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/6/17
 */
public class TopicMetrics extends BaseMetrics {
    private String appId;

    private Long clusterId;

    private String topicName;

    private List<BrokerMetrics> brokerMetricsList;

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

    public TopicMetrics(String appId, Long clusterId, String topicName, List<BrokerMetrics> brokerMetricsList) {
        super();
        this.appId = appId;
        this.clusterId = clusterId;
        this.topicName = topicName;
        this.brokerMetricsList = brokerMetricsList;
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

    public void setBrokerMetricsList(List<BrokerMetrics> brokerMetricsList) {
        this.brokerMetricsList = brokerMetricsList;
    }

    public List<BrokerMetrics> getBrokerMetricsList() {
        return brokerMetricsList;
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