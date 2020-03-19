package com.xiaojukeji.kafka.manager.common.entity;

/**
 * ConsumerMetrics
 * @author tukun
 * @date 2015/11/12
 */
public class ConsumerMetrics {
    private Long clusterId;

    private String topicName;

    private String consumerGroup;

    private String location;

    private Long sumLag;

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

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getSumLag() {
        return sumLag;
    }

    public void setSumLag(Long sumLag) {
        this.sumLag = sumLag;
    }

    @Override
    public String toString() {
        return "ConsumerMetrics{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", location='" + location + '\'' +
                ", sumLag=" + sumLag +
                '}';
    }
}
