package com.xiaojukeji.kafka.manager.web.model.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author huangyiminghappy@163.com, zengqiao
 * @date 2019-04-21
 */
@ApiModel(value = "AdminTopicModel")
public class AdminTopicModel {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "topicName名称")
    private String topicName;

    @ApiModelProperty(value = "分区数")
    private Integer partitionNum;

    @ApiModelProperty(value = "副本数")
    private Integer replicaNum;

    @ApiModelProperty(value = "消息保存时间(ms)")
    private Long retentionTime;

    @ApiModelProperty(value = "brokerId列表")
    private List<Integer> brokerIdList;

    @ApiModelProperty(value = "负责人列表")
    private List<String> principalList;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "regionId列表")
    private List<Long> regionIdList;

    @ApiModelProperty(value = "Topic属性列表")
    private String properties;

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

    public Long getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Long retentionTime) {
        this.retentionTime = retentionTime;
    }

    public List<Integer> getBrokerIdList() {
        return brokerIdList;
    }

    public void setBrokerIdList(List<Integer> brokerIdList) {
        this.brokerIdList = brokerIdList;
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

    public List<Long> getRegionIdList() {
        return regionIdList;
    }

    public void setRegionIdList(List<Long> regionIdList) {
        this.regionIdList = regionIdList;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "TopicCreateModel{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", partitionNum=" + partitionNum +
                ", replicaNum=" + replicaNum +
                ", retentionTime=" + retentionTime +
                ", brokerIdList=" + brokerIdList +
                ", principalList=" + principalList +
                ", description='" + description + '\'' +
                ", regionIdList=" + regionIdList +
                ", properties='" + properties + '\'' +
                '}';
    }

    public Boolean createParamLegal() {
        if (clusterId == null
                || StringUtils.isEmpty(topicName)
                || partitionNum == null || partitionNum <= 0
                || replicaNum == null || replicaNum <= 0
                || retentionTime == null || retentionTime <= 0
                || principalList == null || principalList.isEmpty()) {
            return false;
        }
        if ((brokerIdList == null || brokerIdList.isEmpty()) && (regionIdList == null || regionIdList.isEmpty())) {
            return false;
        }
        return true;
    }

    public Boolean modifyConfigParamLegal() {
        if (clusterId == null
                || StringUtils.isEmpty(topicName)
                || retentionTime == null || retentionTime <= 0
                || principalList == null || principalList.isEmpty()) {
            return false;
        }
        return true;
    }

    public Boolean expandParamLegal() {
        if (clusterId == null
                || StringUtils.isEmpty(topicName)
                || partitionNum == null || partitionNum <= 0
                || retentionTime == null || retentionTime <= 0
                || principalList == null || principalList.isEmpty()) {
            return false;
        }
        if ((brokerIdList == null || brokerIdList.isEmpty()) && (regionIdList == null || regionIdList.isEmpty())) {
            return false;
        }
        return true;
    }
}