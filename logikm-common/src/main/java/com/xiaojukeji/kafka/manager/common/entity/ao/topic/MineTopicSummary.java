package com.xiaojukeji.kafka.manager.common.entity.ao.topic;

/**
 * @author zengqiao
 * @date 20/5/12
 */
public class MineTopicSummary {
    private Long logicalClusterId;

    private String logicalClusterName;

    private Long physicalClusterId;

    private String topicName;

    private Object bytesIn;

    private Object bytesOut;

    private String appId;

    private String appName;

    private String appPrincipals;

    private Integer access;

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

    public Long getPhysicalClusterId() {
        return physicalClusterId;
    }

    public void setPhysicalClusterId(Long physicalClusterId) {
        this.physicalClusterId = physicalClusterId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Object getBytesIn() {
        return bytesIn;
    }

    public void setBytesIn(Object bytesIn) {
        this.bytesIn = bytesIn;
    }

    public Object getBytesOut() {
        return bytesOut;
    }

    public void setBytesOut(Object bytesOut) {
        this.bytesOut = bytesOut;
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

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

    @Override
    public String toString() {
        return "MineTopicSummary{" +
                "logicalClusterId=" + logicalClusterId +
                ", logicalClusterName='" + logicalClusterName + '\'' +
                ", physicalClusterId=" + physicalClusterId +
                ", topicName='" + topicName + '\'' +
                ", bytesIn=" + bytesIn +
                ", bytesOut=" + bytesOut +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", appPrincipals='" + appPrincipals + '\'' +
                ", access=" + access +
                '}';
    }
}