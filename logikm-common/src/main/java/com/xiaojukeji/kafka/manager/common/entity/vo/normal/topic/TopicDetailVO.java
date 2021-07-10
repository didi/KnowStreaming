package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/7/12
 */
@ApiModel(value = "TopicDetailVO", description = "Topic详情")
public class TopicDetailVO {
    private Long clusterId;

    private String topicName;

    private List<String> principalList;

    private String description;

    private Long retentionTime;

    private String properties;

    private Integer replicaNum;

    private Integer partitionNum;

    private Long gmtCreate;

    private Long gmtModify;

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

    public List<String> getPrincipalList() {
        return principalList;
    }

    public void setPrincipalList(List<String> principalList) {
        this.principalList = principalList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Long retentionTime) {
        this.retentionTime = retentionTime;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public void setReplicaNum(Integer replicaNum) {
        this.replicaNum = replicaNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Long getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Long gmtModify) {
        this.gmtModify = gmtModify;
    }

    @Override
    public String toString() {
        return "TopicDetailVO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", principalList=" + principalList +
                ", description='" + description + '\'' +
                ", retentionTime=" + retentionTime +
                ", properties='" + properties + '\'' +
                ", replicaNum=" + replicaNum +
                ", partitionNum=" + partitionNum +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}