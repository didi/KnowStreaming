package com.xiaojukeji.kafka.manager.common.entity.dto.op.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.entity.dto.ClusterTopicDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Properties;

/**
 * @author huangyiminghappy@163.com, zengqiao
 * @date 2019-04-21
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Topic创建")
public class TopicCreationDTO extends ClusterTopicDTO {
    @ApiModelProperty(value = "AppID")
    private String appId;

    @ApiModelProperty(value = "分区数")
    private Integer partitionNum;

    @ApiModelProperty(value = "副本数")
    private Integer replicaNum;

    @ApiModelProperty(value = "消息保存时间(ms)")
    private Long retentionTime;

    @ApiModelProperty(value = "brokerId列表")
    private List<Integer> brokerIdList;

    @ApiModelProperty(value = "RegionId")
    private Long regionId;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "Topic属性列表")
    private Properties properties;

    @ApiModelProperty(value = "最大写入字节数")
    private Long peakBytesIn;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Long getPeakBytesIn() {
        return peakBytesIn;
    }

    public void setPeakBytesIn(Long peakBytesIn) {
        this.peakBytesIn = peakBytesIn;
    }

    @Override
    public String toString() {
        return "TopicCreationDTO{" +
                "appId='" + appId + '\'' +
                ", partitionNum=" + partitionNum +
                ", replicaNum=" + replicaNum +
                ", retentionTime=" + retentionTime +
                ", brokerIdList=" + brokerIdList +
                ", regionId=" + regionId +
                ", description='" + description + '\'' +
                ", properties='" + properties + '\'' +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                '}';
    }

    @Override
    public boolean paramLegal() {
        if (ValidateUtils.isNull(appId)
                || ValidateUtils.isNull(clusterId)
                || ValidateUtils.isNull(topicName)
                || ValidateUtils.isNull(partitionNum) || partitionNum <= 0
                || ValidateUtils.isNull(replicaNum) || replicaNum <= 0
                || ValidateUtils.isNull(retentionTime) || retentionTime <= 0) {
            return false;
        }
        if ((ValidateUtils.isNull(brokerIdList) || brokerIdList.isEmpty()) && ValidateUtils.isNull(regionId)) {
            return false;
        }
        return true;
    }
}
