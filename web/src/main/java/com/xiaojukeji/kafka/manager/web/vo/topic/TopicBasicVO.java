package com.xiaojukeji.kafka.manager.web.vo.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Topic的基本信息
 * @author zengqiao
 * @date 19/4/1
 */
@ApiModel(value = "Topic基本信息")
public class TopicBasicVO {
    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "分区数")
    private Integer partitionNum;

    @ApiModelProperty(value = "副本数")
    private Integer replicaNum;

    @ApiModelProperty(value = "占用Broker数")
    private Integer brokerNum;

    @ApiModelProperty(value = "保留时间(ms)")
    private Long retentionTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "负责人")
    private String principals;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "regionNames")
    private String regionNames;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
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

    public Long getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Long retentionTime) {
        this.retentionTime = retentionTime;
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

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegionNames() {
        return regionNames;
    }

    public void setRegionNames(String regionNames) {
        this.regionNames = regionNames;
    }

    @Override
    public String toString() {
        return "TopicBasicVO{" +
                "topicName='" + topicName + '\'' +
                ", partitionNum=" + partitionNum +
                ", replicaNum=" + replicaNum +
                ", brokerNum=" + brokerNum +
                ", retentionTime=" + retentionTime +
                ", modifyTime=" + modifyTime +
                ", createTime=" + createTime +
                ", principals='" + principals + '\'' +
                ", description='" + description + '\'' +
                ", regionNames='" + regionNames + '\'' +
                '}';
    }
}
