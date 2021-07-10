package com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/4/16
 */
@ApiModel(description="集群信息")
public class ClusterBaseVO {
    @ApiModelProperty(value="集群Id")
    private Long clusterId;

    @ApiModelProperty(value="集群名称")
    private String clusterName;

    @ApiModelProperty(value="ZK地址")
    private String zookeeper;

    @ApiModelProperty(value="bootstrap地址")
    private String bootstrapServers;

    @ApiModelProperty(value="kafka版本")
    private String kafkaVersion;

    @ApiModelProperty(value="数据中心")
    private String idc;

    @ApiModelProperty(value="集群类型")
    private Integer mode;

    @ApiModelProperty(value="Kafka安全配置")
    private String securityProperties;

    @ApiModelProperty(value="Jmx配置")
    private String jmxProperties;

    @ApiModelProperty(value="1:监控中, 0:暂停监控")
    private Integer status;

    @ApiModelProperty(value="接入时间")
    private Date gmtCreate;

    @ApiModelProperty(value="修改时间")
    private Date gmtModify;

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

    @Override
    public String toString() {
        return "ClusterBaseVO{" +
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
                '}';
    }
}