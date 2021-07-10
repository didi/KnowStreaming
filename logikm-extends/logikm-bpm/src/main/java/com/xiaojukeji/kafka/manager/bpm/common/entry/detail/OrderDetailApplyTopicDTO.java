package com.xiaojukeji.kafka.manager.bpm.common.entry.detail;

import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/5/18
 */
public class OrderDetailApplyTopicDTO extends AbstractOrderDetailData {
    private String appId;

    private String appName;

    private String appPrincipals;

    private Long physicalClusterId;

    private String physicalClusterName;

    private Long logicalClusterId;

    private String logicalClusterName;

    private List<Long> regionIdList;

    private String topicName;

    private Long peakBytesIn;

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

    public List<Long> getRegionIdList() {
        return regionIdList;
    }

    public void setRegionIdList(List<Long> regionIdList) {
        this.regionIdList = regionIdList;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Long getPeakBytesIn() {
        return peakBytesIn;
    }

    public void setPeakBytesIn(Long peakBytesIn) {
        this.peakBytesIn = peakBytesIn;
    }

    @Override
    public String toString() {
        return "OrderDetailApplyTopicDTO{" +
                "appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", appPrincipals='" + appPrincipals + '\'' +
                ", physicalClusterId=" + physicalClusterId +
                ", physicalClusterName='" + physicalClusterName + '\'' +
                ", logicalClusterId=" + logicalClusterId +
                ", logicalClusterName='" + logicalClusterName + '\'' +
                ", regionIdList=" + regionIdList +
                ", topicName='" + topicName + '\'' +
                ", peakBytesIn=" + peakBytesIn +
                '}';
    }
}