package com.xiaojukeji.kafka.manager.common.entity.vo.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Topic信息
 * @author zengqiao
 * @date 19/4/1
 */
@ApiModel(description = "Topic信息概览")
public class TopicOverviewVO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "副本数")
    private Integer replicaNum;

    @ApiModelProperty(value = "分区数")
    private Integer partitionNum;

    @ApiModelProperty(value = "保存时间(ms)")
    private Long retentionTime;

    @ApiModelProperty(value = "每秒流入流量(B)")
    private Object byteIn;

    @ApiModelProperty(value = "每秒流出流量(B)")
    private Object byteOut;

    @ApiModelProperty(value = "发送请求数(个/秒)")
    private Object produceRequest;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "应用ID")
    private String appId;

    @ApiModelProperty(value = "说明")
    private String description;

    @ApiModelProperty(value = "Topic更新时间")
    private Long updateTime;

    @ApiModelProperty(value = "逻辑集群id")
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
        return "TopicOverviewVO{" +
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
