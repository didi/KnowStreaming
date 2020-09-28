package com.xiaojukeji.kafka.manager.common.entity.ao.config;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/24
 */
public class CreateTopicElemConfig {
    private Long clusterId;

    private List<Integer> brokerIdList;

    private List<Long> regionIdList;

    private Integer partitionNum;

    private Integer replicaNum;

    private Integer retentionTimeUnitHour;

    private Long autoExecMaxPeakBytesInUnitB;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public List<Integer> getBrokerIdList() {
        return brokerIdList;
    }

    public void setBrokerIdList(List<Integer> brokerIdList) {
        this.brokerIdList = brokerIdList;
    }

    public List<Long> getRegionIdList() {
        return regionIdList;
    }

    public void setRegionIdList(List<Long> regionIdList) {
        this.regionIdList = regionIdList;
    }

    public Integer getReplicaNum() {
        return replicaNum;
    }

    public void setReplicaNum(Integer replicaNum) {
        this.replicaNum = replicaNum;
    }

    public Integer getRetentionTimeUnitHour() {
        return retentionTimeUnitHour;
    }

    public void setRetentionTimeUnitHour(Integer retentionTimeUnitHour) {
        this.retentionTimeUnitHour = retentionTimeUnitHour;
    }

    public Long getAutoExecMaxPeakBytesInUnitB() {
        return autoExecMaxPeakBytesInUnitB;
    }

    public void setAutoExecMaxPeakBytesInUnitB(Long autoExecMaxPeakBytesInUnitB) {
        this.autoExecMaxPeakBytesInUnitB = autoExecMaxPeakBytesInUnitB;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    @Override
    public String toString() {
        return "CreateTopicElemConfig{" +
                "clusterId=" + clusterId +
                ", brokerIdList=" + brokerIdList +
                ", regionIdList=" + regionIdList +
                ", partitionNum=" + partitionNum +
                ", replicaNum=" + replicaNum +
                ", retentionTimeUnitHour=" + retentionTimeUnitHour +
                ", autoExecMaxPeakBytesInUnitB=" + autoExecMaxPeakBytesInUnitB +
                '}';
    }
}