package com.xiaojukeji.kafka.manager.bpm.common.entry.detail;

import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicConnection;

import java.util.List;

/**
 * @author zhongyuankai
 * @date 20/5/19
 */
public class OrderDetailDeleteAuthorityDTO extends AbstractOrderDetailData {
    private String appId;

    private String appName;

    private String appPrincipals;

    private Long physicalClusterId;

    private String physicalClusterName;

    private Long logicalClusterId;

    private String logicalClusterName;

    private String topicName;

    private Integer access;

    private List<TopicConnection> connectionList;

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

    public Long getPhysicalClusterId() {
        return physicalClusterId;
    }

    public void setPhysicalClusterId(Long physicalClusterId) {
        this.physicalClusterId = physicalClusterId;
    }

    public String getPhysicalClusterName() {
        return physicalClusterName;
    }

    public void setPhysicalClusterName(String physicalClusterName) {
        this.physicalClusterName = physicalClusterName;
    }

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

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

    public List<TopicConnection> getConnectionList() {
        return connectionList;
    }

    public void setConnectionList(List<TopicConnection> connectionList) {
        this.connectionList = connectionList;
    }

    @Override
    public String toString() {
        return "OrderDetailDeleteAuthorityDTO{" +
                "appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", appPrincipals='" + appPrincipals + '\'' +
                ", physicalClusterId=" + physicalClusterId +
                ", physicalClusterName='" + physicalClusterName + '\'' +
                ", logicalClusterId=" + logicalClusterId +
                ", logicalClusterName='" + logicalClusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", access=" + access +
                ", connectionList=" + connectionList +
                '}';
    }
}