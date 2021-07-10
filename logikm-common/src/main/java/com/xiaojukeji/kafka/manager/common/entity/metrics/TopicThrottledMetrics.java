package com.xiaojukeji.kafka.manager.common.entity.metrics;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaClientEnum;

import java.util.Set;

/**
 * @author zengqiao
 * @date 20/5/13
 */
public class TopicThrottledMetrics {
    private String appId;

    private Long clusterId;

    private String topicName;
    
    private KafkaClientEnum clientType;
    
    private Set<Integer> brokerIdSet;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

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

    public KafkaClientEnum getClientType() {
        return clientType;
    }

    public void setClientType(KafkaClientEnum clientType) {
        this.clientType = clientType;
    }

    public Set<Integer> getBrokerIdSet() {
        return brokerIdSet;
    }

    public void setBrokerIdSet(Set<Integer> brokerIdSet) {
        this.brokerIdSet = brokerIdSet;
    }

    @Override
    public String toString() {
        return "TopicThrottledMetrics{" +
                "appId='" + appId + '\'' +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", clientType=" + clientType +
                ", brokerIdSet=" + brokerIdSet +
                '}';
    }
}