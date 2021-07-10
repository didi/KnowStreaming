package com.xiaojukeji.kafka.manager.common.entity.ao.remote;

/**
 * @author zengqiao
 * @date 20/8/31
 */
public class KafkaTopicMetrics {
    private Long clusterId;

    private String topic;

    private Integer partitionNum;

    private Double messagesInPerSec;

    private Double bytesInPerSec;

    private Long timestamp;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public Double getMessagesInPerSec() {
        return messagesInPerSec;
    }

    public void setMessagesInPerSec(Double messagesInPerSec) {
        this.messagesInPerSec = messagesInPerSec;
    }

    public Double getBytesInPerSec() {
        return bytesInPerSec;
    }

    public void setBytesInPerSec(Double bytesInPerSec) {
        this.bytesInPerSec = bytesInPerSec;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "KafkaTopicMetrics{" +
                "clusterId=" + clusterId +
                ", topic='" + topic + '\'' +
                ", partitionNum=" + partitionNum +
                ", messagesInPerSec=" + messagesInPerSec +
                ", bytesInPerSec=" + bytesInPerSec +
                ", timestamp=" + timestamp +
                '}';
    }
}