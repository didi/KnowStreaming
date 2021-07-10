package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/8
 */
@ApiModel(value = "Topic信息")
public class TopicVO {
    @ApiModelProperty(value = "逻辑集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "逻辑集群名称")
    private String clusterName;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "Topic描述")
    private String description;

    @ApiModelProperty(value = "AppID")
    private String appId;

    @ApiModelProperty(value = "App名称")
    private String appName;

    @ApiModelProperty(value = "App负责人")
    private String appPrincipals;

    @ApiModelProperty(value = "需要鉴权, true:是 false:否")
    private Boolean needAuth;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getAppPrincipals() {
        return appPrincipals;
    }

    public void setAppPrincipals(String appPrincipals) {
        this.appPrincipals = appPrincipals;
    }

    public Boolean getNeedAuth() {
        return needAuth;
    }

    public void setNeedAuth(Boolean needAuth) {
        this.needAuth = needAuth;
    }

    @Override
    public String toString() {
        return "TopicVO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", description='" + description + '\'' +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", appPrincipals='" + appPrincipals + '\'' +
                ", needAuth=" + needAuth +
                '}';
    }
}