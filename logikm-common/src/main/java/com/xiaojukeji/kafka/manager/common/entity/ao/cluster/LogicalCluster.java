package com.xiaojukeji.kafka.manager.common.entity.ao.cluster;

/**
 * @author zengqiao
 * @date 20/4/1
 */
public class LogicalCluster {
    private Long logicalClusterId;

    private String logicalClusterName;

    private String logicalClusterIdentification;

    private Integer mode;

    private Integer topicNum;

    private String clusterVersion;

    private Long physicalClusterId;

    private String bootstrapServers;

    private String description;

    private Long gmtCreate;

    private Long gmtModify;

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

    public String getLogicalClusterIdentification() {
        return logicalClusterIdentification;
    }

    public void setLogicalClusterIdentification(String logicalClusterIdentification) {
        this.logicalClusterIdentification = logicalClusterIdentification;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(Integer topicNum) {
        this.topicNum = topicNum;
    }

    public String getClusterVersion() {
        return clusterVersion;
    }

    public void setClusterVersion(String clusterVersion) {
        this.clusterVersion = clusterVersion;
    }

    public Long getPhysicalClusterId() {
        return physicalClusterId;
    }

    public void setPhysicalClusterId(Long physicalClusterId) {
        this.physicalClusterId = physicalClusterId;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Long getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Long gmtModify) {
        this.gmtModify = gmtModify;
    }

    @Override
    public String toString() {
        return "LogicalCluster{" +
                "logicalClusterId=" + logicalClusterId +
                ", logicalClusterName='" + logicalClusterName + '\'' +
                ", logicalClusterIdentification='" + logicalClusterIdentification + '\'' +
                ", mode=" + mode +
                ", topicNum=" + topicNum +
                ", clusterVersion='" + clusterVersion + '\'' +
                ", physicalClusterId=" + physicalClusterId +
                ", bootstrapServers='" + bootstrapServers + '\'' +
                ", description='" + description + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}