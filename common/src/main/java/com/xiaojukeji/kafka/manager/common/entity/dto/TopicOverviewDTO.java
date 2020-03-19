package com.xiaojukeji.kafka.manager.common.entity.dto;

public class TopicOverviewDTO {
    private Long clusterId;

    private String topicName;

    private Integer replicaNum;

    private Integer partitionNum;

    private Double bytesInPerSec;

    private Double produceRequestPerSec;

    private Long updateTime;

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

    public Integer getReplicaNum() {
        return replicaNum;
    }

    public void setReplicaNum(Integer replicaNum) {
        this.replicaNum = replicaNum;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public Double getBytesInPerSec() {
        return bytesInPerSec;
    }

    public void setBytesInPerSec(Double bytesInPerSec) {
        this.bytesInPerSec = bytesInPerSec;
    }

    public Double getProduceRequestPerSec() {
        return produceRequestPerSec;
    }

    public void setProduceRequestPerSec(Double produceRequestPerSec) {
        this.produceRequestPerSec = produceRequestPerSec;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "TopicOverviewDTO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", replicaNum=" + replicaNum +
                ", partitionNum=" + partitionNum +
                ", bytesInPerSec=" + bytesInPerSec +
                ", produceRequestPerSec=" + produceRequestPerSec +
                ", updateTime=" + updateTime +
                '}';
    }
}
