package com.xiaojukeji.kafka.manager.common.entity.ao;

import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;

/**
 * @author zengqiao_cn@163.com
 * @date 19/4/21
 */
public class BrokerOverviewDTO {
    private Integer brokerId;

    private String host;

    private Integer port;

    private Integer jmxPort;

    private Long startTime;

    private Object byteIn;

    private Object byteOut;

    private Integer partitionCount;

    private Integer underReplicatedPartitions;

    private Boolean underReplicated;

    private Integer status;

    private Integer peakFlowStatus;

    private String kafkaVersion;

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

    public static BrokerOverviewDTO newInstance(BrokerMetadata brokerMetadata,
                                                BrokerMetrics brokerMetrics,
                                                String kafkaVersion) {
        BrokerOverviewDTO brokerOverviewDTO = new BrokerOverviewDTO();
        brokerOverviewDTO.setBrokerId(brokerMetadata.getBrokerId());
        brokerOverviewDTO.setHost(brokerMetadata.getHost());
        brokerOverviewDTO.setPort(brokerMetadata.getPort());
        brokerOverviewDTO.setJmxPort(brokerMetadata.getJmxPort());
        brokerOverviewDTO.setStartTime(brokerMetadata.getTimestamp());
        brokerOverviewDTO.setStatus(0);
        if (brokerMetrics == null) {
            return brokerOverviewDTO;
        }
        brokerOverviewDTO.setByteIn(
                brokerMetrics.getSpecifiedMetrics("BytesInPerSecOneMinuteRate")
        );
        brokerOverviewDTO.setByteOut(
                brokerMetrics.getSpecifiedMetrics("BytesOutPerSecOneMinuteRate")
        );
        brokerOverviewDTO.setPartitionCount(
                brokerMetrics.getSpecifiedMetrics("PartitionCountValue", Integer.class)
        );
        brokerOverviewDTO.setUnderReplicatedPartitions(
                brokerMetrics.getSpecifiedMetrics("UnderReplicatedPartitionsValue", Integer.class)
        );

        if (!ValidateUtils.isNull(brokerOverviewDTO.getUnderReplicatedPartitions())) {
            brokerOverviewDTO.setUnderReplicated(brokerOverviewDTO.getUnderReplicatedPartitions() > 0);
        }
        brokerOverviewDTO.setLeaderCount(
                brokerMetrics.getSpecifiedMetrics("LeaderCountValue", Integer.class)
        );
        brokerOverviewDTO.setKafkaVersion(kafkaVersion);
        return brokerOverviewDTO;
    }


}