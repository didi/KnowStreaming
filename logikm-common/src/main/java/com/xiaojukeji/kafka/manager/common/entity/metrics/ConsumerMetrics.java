package com.xiaojukeji.kafka.manager.common.entity.metrics;

import java.util.HashMap;
import java.util.Map;

/**
 * Consumer实体类
 * @author tukun
 * @date 2015/11/12
 */
public class ConsumerMetrics {
    private Long clusterId;

    private String topicName;

    private String consumerGroup;

    private String location;

    private Map<Integer, Long> partitionOffsetMap = new HashMap<>();

    private Map<Integer, Long> consumeOffsetMap = new HashMap<>();

    private long timestampUnitMs;

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

    public Map<Integer, Long> getConsumeOffsetMap() {
        return consumeOffsetMap;
    }

    public void setConsumeOffsetMap(Map<Integer, Long> consumeOffsetMap) {
        this.consumeOffsetMap = consumeOffsetMap;
    }

    public long getTimestampUnitMs() {
        return timestampUnitMs;
    }

    public void setTimestampUnitMs(long timestampUnitMs) {
        this.timestampUnitMs = timestampUnitMs;
    }

    public ConsumerMetrics newConsumerMetrics(String consumerGroup) {
        ConsumerMetrics consumerMetrics = new ConsumerMetrics();
        consumerMetrics.setConsumerGroup(consumerGroup);
        consumerMetrics.setClusterId(this.getClusterId());
        consumerMetrics.setLocation(this.getLocation());
        consumerMetrics.setTopicName(this.getTopicName());
        consumerMetrics.setConsumeOffsetMap(this.getConsumeOffsetMap());
        consumerMetrics.setPartitionOffsetMap(this.getPartitionOffsetMap());
        consumerMetrics.setTimestampUnitMs(this.getTimestampUnitMs());
        return consumerMetrics;
    }

    @Override
    public String toString() {
        return "ConsumerMetrics{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", location='" + location + '\'' +
                ", partitionOffsetMap=" + partitionOffsetMap +
                ", consumeOffsetMap=" + consumeOffsetMap +
                ", timestampUnitMs=" + timestampUnitMs +
                '}';
    }
}
