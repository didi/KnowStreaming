package com.xiaojukeji.kafka.manager.web.vo.consumer;

/**
 * @author zengqiao
 * @date 19/4/3
 */
public class ConsumerGroupDetailVO {
    private Long clusterId;

    private String topicName;

    private String consumerGroup;

    private String location;

    private Integer partitionId;

    private String clientId;

    private Long consumeOffset;

    private Long partitionOffset;

    private Long lag;

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

    public Integer getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(Integer partitionId) {
        this.partitionId = partitionId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Long getConsumeOffset() {
        return consumeOffset;
    }

    public void setConsumeOffset(Long consumeOffset) {
        this.consumeOffset = consumeOffset;
    }

    public Long getPartitionOffset() {
        return partitionOffset;
    }

    public void setPartitionOffset(Long partitionOffset) {
        this.partitionOffset = partitionOffset;
    }

    public Long getLag() {
        return lag;
    }

    public void setLag(Long lag) {
        this.lag = lag;
    }

    @Override
    public String toString() {
        return "ConsumerGroupDetailVO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", location='" + location + '\'' +
                ", partitionId=" + partitionId +
                ", clientId='" + clientId + '\'' +
                ", consumeOffset=" + consumeOffset +
                ", partitionOffset=" + partitionOffset +
                ", lag=" + lag +
                '}';
    }
}
