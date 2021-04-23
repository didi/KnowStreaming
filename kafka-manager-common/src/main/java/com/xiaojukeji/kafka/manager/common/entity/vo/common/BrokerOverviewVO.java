package com.xiaojukeji.kafka.manager.common.entity.vo.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 19/4/3
 */
@ApiModel(description = "Broker信息概览")
public class BrokerOverviewVO {
    @ApiModelProperty(value = "brokerId")
    private Integer brokerId;

    @ApiModelProperty(value = "主机名")
    private String host;

    @ApiModelProperty(value = "端口")
    private Integer port;

    @ApiModelProperty(value = "jmx端口")
    private Integer jmxPort;

    @ApiModelProperty(value = "启动时间")
    private Long startTime;

    @ApiModelProperty(value = "流入流量")
    private Object byteIn;

    @ApiModelProperty(value = "流出流量")
    private Object byteOut;

    @ApiModelProperty(value = "分区数")
    private Integer partitionCount;

    @ApiModelProperty(value = "失效副本分区的个数")
    private Integer underReplicatedPartitions;

    @ApiModelProperty(value = "未同步")
    private Boolean underReplicated;

    @ApiModelProperty(value = "broker状态[0:在线, -1:不在线]")
    private Integer status;

    @ApiModelProperty(value = "Region名称")
    private String regionName;

    @ApiModelProperty(value = "峰值状态")
    private Integer peakFlowStatus;

    @ApiModelProperty(value = "Kafka版本")
    private String kafkaVersion;

    @ApiModelProperty(value = "Leader数")
    private Integer leaderCount;

    public Integer getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Integer brokerId) {
        this.brokerId = brokerId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getJmxPort() {
        return jmxPort;
    }

    public void setJmxPort(Integer jmxPort) {
        this.jmxPort = jmxPort;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Object getByteIn() {
        return byteIn;
    }

    public void setByteIn(Object byteIn) {
        this.byteIn = byteIn;
    }

    public Object getByteOut() {
        return byteOut;
    }

    public void setByteOut(Object byteOut) {
        this.byteOut = byteOut;
    }

    public Integer getPartitionCount() {
        return partitionCount;
    }

    public void setPartitionCount(Integer partitionCount) {
        this.partitionCount = partitionCount;
    }

    public Integer getUnderReplicatedPartitions() {
        return underReplicatedPartitions;
    }

    public void setUnderReplicatedPartitions(Integer underReplicatedPartitions) {
        this.underReplicatedPartitions = underReplicatedPartitions;
    }

    public Boolean getUnderReplicated() {
        return underReplicated;
    }

    public void setUnderReplicated(Boolean underReplicated) {
        this.underReplicated = underReplicated;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public Integer getPeakFlowStatus() {
        return peakFlowStatus;
    }

    public void setPeakFlowStatus(Integer peakFlowStatus) {
        this.peakFlowStatus = peakFlowStatus;
    }

    public String getKafkaVersion() {
        return kafkaVersion;
    }

    public void setKafkaVersion(String kafkaVersion) {
        this.kafkaVersion = kafkaVersion;
    }

    public Integer getLeaderCount() {
        return leaderCount;
    }

    public void setLeaderCount(Integer leaderCount) {
        this.leaderCount = leaderCount;
    }

    @Override
    public String toString() {
        return "BrokerOverviewVO{" +
                "brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", jmxPort=" + jmxPort +
                ", startTime=" + startTime +
                ", byteIn=" + byteIn +
                ", byteOut=" + byteOut +
                ", partitionCount=" + partitionCount +
                ", underReplicatedPartitions=" + underReplicatedPartitions +
                ", underReplicated=" + underReplicated +
                ", status=" + status +
                ", regionName='" + regionName + '\'' +
                ", peakFlowStatus=" + peakFlowStatus +
                ", kafkaVersion='" + kafkaVersion + '\'' +
                ", leaderCount=" + leaderCount +
                '}';
    }
}
