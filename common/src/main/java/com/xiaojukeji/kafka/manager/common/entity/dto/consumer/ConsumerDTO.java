package com.xiaojukeji.kafka.manager.common.entity.dto.consumer;

import java.util.Map;

/**
 * Consumer实体类
 * @author tukun
 * @date 2015/11/12
 */
public class ConsumerDTO {
    private Long clusterId;

    private String topicName;

    private String consumerGroup;

    private String location;

    private Map<Integer, Long> partitionOffsetMap;

    private Map<Integer, Long> consumerOffsetMap;

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

    public Map<Integer, Long> getPartitionOffsetMap() {
        return partitionOffsetMap;
    }

    public void setPartitionOffsetMap(Map<Integer, Long> partitionOffsetMap) {
        this.partitionOffsetMap = partitionOffsetMap;
    }

    public Map<Integer, Long> getConsumerOffsetMap() {
        return consumerOffsetMap;
    }

    public void setConsumerOffsetMap(Map<Integer, Long> consumerOffsetMap) {
        this.consumerOffsetMap = consumerOffsetMap;
    }

    @Override
    public String toString() {
        return "ConsumerDTO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", location='" + location + '\'' +
                ", partitionOffsetMap=" + partitionOffsetMap +
                ", consumerOffsetMap=" + consumerOffsetMap +
                '}';
    }
}
