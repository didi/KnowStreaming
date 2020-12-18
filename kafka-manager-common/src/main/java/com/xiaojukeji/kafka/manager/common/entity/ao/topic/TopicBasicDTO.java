package com.xiaojukeji.kafka.manager.common.entity.ao.topic;

import java.util.List;

/**
 * @author arthur
 * @date 2018/09/03
 */
public class TopicBasicDTO {
    private Long clusterId;

    private String appId;

    private String appName;

    private String principals;

    private String topicName;

    private String description;

    private List<String> regionNameList;

    private Integer score;

    private String topicCodeC;

    private Integer partitionNum;

    private Integer replicaNum;

    private Integer brokerNum;

    private Long modifyTime;

    private Long createTime;

    private Long retentionTime;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getRegionNameList() {
        return regionNameList;
    }

    public void setRegionNameList(List<String> regionNameList) {
        this.regionNameList = regionNameList;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getTopicCodeC() {
        return topicCodeC;
    }

    public void setTopicCodeC(String topicCodeC) {
        this.topicCodeC = topicCodeC;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public Integer getReplicaNum() {
        return replicaNum;
    }

    public void setReplicaNum(Integer replicaNum) {
        this.replicaNum = replicaNum;
    }

    public Integer getBrokerNum() {
        return brokerNum;
    }

    public void setBrokerNum(Integer brokerNum) {
        this.brokerNum = brokerNum;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Long retentionTime) {
        this.retentionTime = retentionTime;
    }

    @Override
    public String toString() {
        return "TopicBasicDTO{" +
                "clusterId=" + clusterId +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", principals='" + principals + '\'' +
                ", topicName='" + topicName + '\'' +
                ", description='" + description + '\'' +
                ", regionNameList='" + regionNameList + '\'' +
                ", score=" + score +
                ", topicCodeC='" + topicCodeC + '\'' +
                ", partitionNum=" + partitionNum +
                ", replicaNum=" + replicaNum +
                ", brokerNum=" + brokerNum +
                ", modifyTime=" + modifyTime +
                ", createTime=" + createTime +
                ", retentionTime=" + retentionTime +
                '}';
    }
}
