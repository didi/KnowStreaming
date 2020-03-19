package com.xiaojukeji.kafka.manager.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.StringUtils;

/**
 * cluster model
 * @author zengqiao
 * @date 19/4/15
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "ClusterModel", description = "cluster model")
public class ClusterModel {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "集群名称")
    private String clusterName;

    @ApiModelProperty(value = "ZK地址")
    private String zookeeper;

    @ApiModelProperty(value = "kafka版本")
    private String kafkaVersion;

    @ApiModelProperty(value = "bootstrapServers地址")
    private String bootstrapServers;

    @ApiModelProperty(value = "开启告警[0:不开启, 1:开启]")
    private Integer alarmFlag;

    @ApiModelProperty(value = "安全协议")
    private String securityProtocol;

    @ApiModelProperty(value = "SASL机制")
    private String saslMechanism;

    @ApiModelProperty(value = "SASL JAAS配置")
    private String saslJaasConfig;

    public Integer getAlarmFlag() {
        return alarmFlag;
    }

    public void setAlarmFlag(Integer alarmFlag) {
        this.alarmFlag = alarmFlag;
    }

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

    public String getKafkaVersion() {
        return kafkaVersion;
    }

    public void setKafkaVersion(String kafkaVersion) {
        this.kafkaVersion = kafkaVersion;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
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

    @Override
    public String toString() {
        return "ClusterModel{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", zookeeper='" + zookeeper + '\'' +
                ", kafkaVersion='" + kafkaVersion + '\'' +
                ", bootstrapServers='" + bootstrapServers + '\'' +
                ", alarmFlag=" + alarmFlag +
                ", securityProtocol='" + securityProtocol + '\'' +
                ", saslMechanism='" + saslMechanism + '\'' +
                ", saslJaasConfig='" + saslJaasConfig + '\'' +
                '}';
    }

    public boolean legal() {
        if (StringUtils.isEmpty(clusterName)
                || StringUtils.isEmpty(zookeeper)
                || StringUtils.isEmpty(kafkaVersion)
                || StringUtils.isEmpty(bootstrapServers)) {
            return false;
        }
        return true;
    }
}