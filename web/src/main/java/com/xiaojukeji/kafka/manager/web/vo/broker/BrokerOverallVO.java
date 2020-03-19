package com.xiaojukeji.kafka.manager.web.vo.broker;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 19/4/21
 */
@ApiModel(value = "BrokerOverallVO", description = "Broker总揽")
public class BrokerOverallVO {
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

    @ApiModelProperty(value = "流入流量(MB/s)")
    private Double bytesInPerSec;

    @ApiModelProperty(value = "分区数")
    private Integer partitionCount;

    @ApiModelProperty(value = "未同步分区数")
    private Integer notUnderReplicatedPartitionCount;

    @ApiModelProperty(value = "leader数")
    private Integer leaderCount;

    @ApiModelProperty(value = "region名称")
    private String regionName;

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

    public Double getBytesInPerSec() {
        return bytesInPerSec;
    }

    public void setBytesInPerSec(Double bytesInPerSec) {
        this.bytesInPerSec = bytesInPerSec;
    }

    public Integer getPartitionCount() {
        return partitionCount;
    }

    public void setPartitionCount(Integer partitionCount) {
        this.partitionCount = partitionCount;
    }

    public Integer getNotUnderReplicatedPartitionCount() {
        return notUnderReplicatedPartitionCount;
    }

    public void setNotUnderReplicatedPartitionCount(Integer notUnderReplicatedPartitionCount) {
        this.notUnderReplicatedPartitionCount = notUnderReplicatedPartitionCount;
    }

    public Integer getLeaderCount() {
        return leaderCount;
    }

    public void setLeaderCount(Integer leaderCount) {
        this.leaderCount = leaderCount;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public String toString() {
        return "BrokerOverallVO{" +
                "brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", jmxPort=" + jmxPort +
                ", startTime=" + startTime +
                ", bytesInPerSec=" + bytesInPerSec +
                ", partitionCount=" + partitionCount +
                ", notUnderReplicatedPartitionCount=" + notUnderReplicatedPartitionCount +
                ", leaderCount=" + leaderCount +
                ", regionName='" + regionName + '\'' +
                '}';
    }
}
