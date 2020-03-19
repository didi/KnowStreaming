package com.xiaojukeji.kafka.manager.common.entity.dto;

import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.BrokerMetadata;

/**
 * @author zengqiao
 * @date 19/4/21
 */
public class BrokerOverallDTO {
    private Integer brokerId;

    private String host;

    private Integer port;

    private Integer jmxPort;

    private Long startTime;

    private Integer partitionCount;

    private Integer underReplicatedPartitions;

    private Integer leaderCount;

    private Double bytesInPerSec;

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

    public Integer getLeaderCount() {
        return leaderCount;
    }

    public void setLeaderCount(Integer leaderCount) {
        this.leaderCount = leaderCount;
    }

    public Double getBytesInPerSec() {
        return bytesInPerSec;
    }

    public void setBytesInPerSec(Double bytesInPerSec) {
        this.bytesInPerSec = bytesInPerSec;
    }

    @Override
    public String toString() {
        return "BrokerOverallDTO{" +
                "brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", jmxPort=" + jmxPort +
                ", startTime=" + startTime +
                ", partitionCount=" + partitionCount +
                ", underReplicatedPartitions=" + underReplicatedPartitions +
                ", leaderCount=" + leaderCount +
                ", bytesInPerSec=" + bytesInPerSec +
                '}';
    }

    public static BrokerOverallDTO newInstance(BrokerMetadata brokerMetadata, BrokerMetrics brokerMetrics) {
        BrokerOverallDTO brokerOverallDTO = new BrokerOverallDTO();
        brokerOverallDTO.setBrokerId(brokerMetadata.getBrokerId());
        brokerOverallDTO.setHost(brokerMetadata.getHost());
        brokerOverallDTO.setPort(brokerMetadata.getPort());
        brokerOverallDTO.setJmxPort(brokerMetadata.getJmxPort());
        brokerOverallDTO.setStartTime(brokerMetadata.getTimestamp());
        if (brokerMetrics == null) {
            return brokerOverallDTO;
        }
        brokerOverallDTO.setPartitionCount(brokerMetrics.getPartitionCount());
        brokerOverallDTO.setLeaderCount(brokerMetrics.getLeaderCount());
        brokerOverallDTO.setBytesInPerSec(brokerMetrics.getBytesInPerSec());
        brokerOverallDTO.setUnderReplicatedPartitions(brokerMetrics.getUnderReplicatedPartitions());
        return brokerOverallDTO;
    }
}