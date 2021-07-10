package com.xiaojukeji.kafka.manager.common.entity.ao.topic;

/**
 * Topic概览信息
 * @author zengqiao
 * @date 20/5/14
 */
public class TopicOverview {
    private Long clusterId;

    private String topicName;

    private Integer replicaNum;

    private Integer partitionNum;

    private Long retentionTime;

    private Object byteIn;

    private Object byteOut;

    private Object produceRequest;

    private String appName;

    private String appId;

    private String description;

    private Long updateTime;

    private Long logicalClusterId;

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

    public Long getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Long retentionTime) {
        this.retentionTime = retentionTime;
    }

    public Object getByteIn() {
        return byteIn;
    }

    public void setByteIn(Object byteIn) {
        this.byteIn = byteIn;
    }

    public Object getByteOut() {
        return byteOut;
    }

    public void setByteOut(Object byteOut) {
        this.byteOut = byteOut;
    }

    public Object getProduceRequest() {
        return produceRequest;
    }

    public void setProduceRequest(Object produceRequest) {
        this.produceRequest = produceRequest;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getLogicalClusterId() {
        return logicalClusterId;
    }

    public void setLogicalClusterId(Long logicalClusterId) {
        this.logicalClusterId = logicalClusterId;
    }

    @Override
    public String toString() {
        return "TopicOverview{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", replicaNum=" + replicaNum +
                ", partitionNum=" + partitionNum +
                ", retentionTime=" + retentionTime +
                ", byteIn=" + byteIn +
                ", byteOut=" + byteOut +
                ", produceRequest=" + produceRequest +
                ", appName='" + appName + '\'' +
                ", appId='" + appId + '\'' +
                ", description='" + description + '\'' +
                ", updateTime=" + updateTime +
                ", logicalClusterId=" + logicalClusterId +
                '}';
    }
}
