package com.xiaojukeji.kafka.manager.common.entity.ao.topic;

/**
 * @author zengqiao
 * @date 20/5/12
 */
public class TopicDTO {
    private Long logicalClusterId;

    private String logicalClusterName;

    private String topicName;

    private String description;

    private String appId;

    private String appName;

    private String appPrincipals;

    private Boolean needAuth;

    public Long getLogicalClusterId() {
        return logicalClusterId;
    }

    public void setLogicalClusterId(Long logicalClusterId) {
        this.logicalClusterId = logicalClusterId;
    }

    public String getLogicalClusterName() {
        return logicalClusterName;
    }

    public void setLogicalClusterName(String logicalClusterName) {
        this.logicalClusterName = logicalClusterName;
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
        return "TopicDTO{" +
                "logicalClusterId=" + logicalClusterId +
                ", logicalClusterName='" + logicalClusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", description='" + description + '\'' +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", appPrincipals='" + appPrincipals + '\'' +
                ", needAuth=" + needAuth +
                '}';
    }
}