package com.xiaojukeji.kafka.manager.web.vo.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ClusterDetailVO
 * @author huangyiminghappy@163.com
 * @date 2019/3/15
 */
@ApiModel(value="ClusterDetailVO", description="集群详细信息")
public class ClusterDetailVO {
    @ApiModelProperty(value="集群Id")
    private Long clusterId;

    @ApiModelProperty(value="集群名称")
    private String clusterName;

    @ApiModelProperty(value="集群ZK地址")
    private String zookeeper;

    @ApiModelProperty(value="bootstrap地址")
    private String bootstrapServers;

    @ApiModelProperty(value="kafka版本")
    private String kafkaVersion;

    @ApiModelProperty(value="broker数量")
    private Integer brokerNum;

    @ApiModelProperty(value="topic数量")
    private Integer topicNum;

    @ApiModelProperty(value="consumerGroup数量")
    private Integer consumerGroupNum;

    @ApiModelProperty(value="controllerId")
    private Integer controllerId;

    @ApiModelProperty(value="安全协议")
    private String securityProtocol;

    @ApiModelProperty(value="SASL机制")
    private String saslMechanism;

    @ApiModelProperty(value="SASL的JSSA配置")
    private String saslJaasConfig;

    @ApiModelProperty(value="regionNum数")
    private Integer regionNum;

    @ApiModelProperty(value = "开启告警[0:不开启, 1:开启]")
    private Integer alarmFlag;

    @ApiModelProperty(value="是否已经删除，0不删除，1删除")
    private Integer status;

    @ApiModelProperty(value="集群创建时间")
    private Long gmtCreate;

    @ApiModelProperty(value="集群修改时间")
    private Long gmtModify;

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

    public String getSecurityProtocol() {
        return securityProtocol;
    }

    public void setSecurityProtocol(String securityProtocol) {
        this.securityProtocol = securityProtocol;
    }

    public String getSaslMechanism() {
        return saslMechanism;
    }

    public void setSaslMechanism(String saslMechanism) {
        this.saslMechanism = saslMechanism;
    }

    public String getSaslJaasConfig() {
        return saslJaasConfig;
    }

    public void setSaslJaasConfig(String saslJaasConfig) {
        this.saslJaasConfig = saslJaasConfig;
    }

    public Integer getRegionNum() {
        return regionNum;
    }

    public void setRegionNum(Integer regionNum) {
        this.regionNum = regionNum;
    }

    public Integer getAlarmFlag() {
        return alarmFlag;
    }

    public void setAlarmFlag(Integer alarmFlag) {
        this.alarmFlag = alarmFlag;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
        return "ClusterDetailVO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", zookeeper='" + zookeeper + '\'' +
                ", bootstrapServers='" + bootstrapServers + '\'' +
                ", kafkaVersion='" + kafkaVersion + '\'' +
                ", brokerNum=" + brokerNum +
                ", topicNum=" + topicNum +
                ", consumerGroupNum=" + consumerGroupNum +
                ", controllerId=" + controllerId +
                ", securityProtocol='" + securityProtocol + '\'' +
                ", saslMechanism='" + saslMechanism + '\'' +
                ", saslJaasConfig='" + saslJaasConfig + '\'' +
                ", regionNum=" + regionNum +
                ", alarmFlag=" + alarmFlag +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}
