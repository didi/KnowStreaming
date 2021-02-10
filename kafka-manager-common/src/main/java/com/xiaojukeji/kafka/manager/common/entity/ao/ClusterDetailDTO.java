package com.xiaojukeji.kafka.manager.common.entity.ao;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/4/23
 */
public class ClusterDetailDTO {
    private Long clusterId;

    private String clusterName;

    private String zookeeper;

    private String bootstrapServers;

    private String kafkaVersion;

    private String idc;

    private Integer mode;

    private String securityProperties;

    private String jmxProperties;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private Integer brokerNum;

    private Integer topicNum;

    private Integer consumerGroupNum;

    private Integer controllerId;

    private Integer regionNum;

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

    public String getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(String zookeeper) {
        this.zookeeper = zookeeper;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getKafkaVersion() {
        return kafkaVersion;
    }

    public void setKafkaVersion(String kafkaVersion) {
        this.kafkaVersion = kafkaVersion;
    }

    public String getIdc() {
        return idc;
    }

    public void setIdc(String idc) {
        this.idc = idc;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getSecurityProperties() {
        return securityProperties;
    }

    public void setSecurityProperties(String securityProperties) {
        this.securityProperties = securityProperties;
    }

    public String getJmxProperties() {
        return jmxProperties;
    }

    public void setJmxProperties(String jmxProperties) {
        this.jmxProperties = jmxProperties;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    public Integer getBrokerNum() {
        return brokerNum;
    }

    public void setBrokerNum(Integer brokerNum) {
        this.brokerNum = brokerNum;
    }

    public Integer getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(Integer topicNum) {
        this.topicNum = topicNum;
    }

    public Integer getConsumerGroupNum() {
        return consumerGroupNum;
    }

    public void setConsumerGroupNum(Integer consumerGroupNum) {
        this.consumerGroupNum = consumerGroupNum;
    }

    public Integer getControllerId() {
        return controllerId;
    }

    public void setControllerId(Integer controllerId) {
        this.controllerId = controllerId;
    }

    public Integer getRegionNum() {
        return regionNum;
    }

    public void setRegionNum(Integer regionNum) {
        this.regionNum = regionNum;
    }

    @Override
    public String toString() {
        return "ClusterDetailDTO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", zookeeper='" + zookeeper + '\'' +
                ", bootstrapServers='" + bootstrapServers + '\'' +
                ", kafkaVersion='" + kafkaVersion + '\'' +
                ", idc='" + idc + '\'' +
                ", mode=" + mode +
                ", securityProperties='" + securityProperties + '\'' +
                ", jmxProperties='" + jmxProperties + '\'' +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                ", brokerNum=" + brokerNum +
                ", topicNum=" + topicNum +
                ", consumerGroupNum=" + consumerGroupNum +
                ", controllerId=" + controllerId +
                ", regionNum=" + regionNum +
                '}';
    }
}