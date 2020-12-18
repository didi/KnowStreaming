package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Topic的基本信息
 * @author zengqiao
 * @date 19/4/1
 */
@ApiModel(description = "Topic基本信息")
public class TopicBasicVO {
    @ApiModelProperty(value = "集群id")
    private Long clusterId;

    @ApiModelProperty(value = "应用id")
    private String appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "分区数")
    private Integer partitionNum;

    @ApiModelProperty(value = "副本数")
    private Integer replicaNum;

    @ApiModelProperty(value = "负责人")
    private String principals;

    @ApiModelProperty(value = "存储时间(ms)")
    private Long retentionTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "健康分")
    private Integer score;

    @ApiModelProperty(value = "压缩格式")
    private String topicCodeC;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "集群地址")
    private String bootstrapServers;

    @ApiModelProperty(value = "所属region")
    private List<String> regionNameList;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
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

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getAppId() {
        return appId;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public Long getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Long retentionTime) {
        this.retentionTime = retentionTime;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public List<String> getRegionNameList() {
        return regionNameList;
    }

    public void setRegionNameList(List<String> regionNameList) {
        this.regionNameList = regionNameList;
    }

    @Override
    public String toString() {
        return "TopicBasicVO{" +
                "clusterId=" + clusterId +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", partitionNum=" + partitionNum +
                ", replicaNum=" + replicaNum +
                ", principals='" + principals + '\'' +
                ", retentionTime=" + retentionTime +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", score=" + score +
                ", topicCodeC='" + topicCodeC + '\'' +
                ", description='" + description + '\'' +
                ", bootstrapServers='" + bootstrapServers + '\'' +
                ", regionNameList=" + regionNameList +
                '}';
    }
}
