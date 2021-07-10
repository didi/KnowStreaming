package com.xiaojukeji.kafka.manager.common.entity.vo.rd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 20/6/10
 */
@ApiModel(description = "Topic基本信息(RD视角)")
public class RdTopicBasicVO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "集群名称")
    private String clusterName;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "保留时间(ms)")
    private Long retentionTime;

    @ApiModelProperty(value = "应用ID")
    private String appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "Topic属性")
    private Properties properties;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "所属region")
    private List<String> regionNameList;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Long getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Long retentionTime) {
        this.retentionTime = retentionTime;
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

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
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

    @Override
    public String toString() {
        return "RdTopicBasicVO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", retentionTime=" + retentionTime +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", properties=" + properties +
                ", description='" + description + '\'' +
                ", regionNameList='" + regionNameList + '\'' +
                '}';
    }
}