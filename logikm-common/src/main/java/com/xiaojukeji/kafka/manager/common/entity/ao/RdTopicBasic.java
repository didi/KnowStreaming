package com.xiaojukeji.kafka.manager.common.entity.ao;

import java.util.List;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 20/6/10
 */
public class RdTopicBasic {
    private Long clusterId;

    private String clusterName;

    private String topicName;

    private Long retentionTime;

    private String appId;

    private String appName;

    private Properties properties;

    private String description;

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
        return "RdTopicBasic{" +
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