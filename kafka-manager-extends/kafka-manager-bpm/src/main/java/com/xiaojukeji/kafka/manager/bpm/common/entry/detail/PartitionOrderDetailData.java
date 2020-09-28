package com.xiaojukeji.kafka.manager.bpm.common.entry.detail;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/9/15
 */
public class PartitionOrderDetailData extends AbstractOrderDetailData {
    private Long physicalClusterId;

    private String physicalClusterName;

    private Long logicalClusterId;

    private String logicalClusterName;

    private String topicName;

    private Double bytesIn;

    private List<Double> maxAvgBytesInList;

    private List<Integer> topicBrokerIdList;

    private List<Integer> regionBrokerIdList;

    private List<String> regionNameList;

    private Integer presentPartitionNum;

    private Integer needIncrPartitionNum;

    public Long getPhysicalClusterId() {
        return physicalClusterId;
    }

    public void setPhysicalClusterId(Long physicalClusterId) {
        this.physicalClusterId = physicalClusterId;
    }

    public String getPhysicalClusterName() {
        return physicalClusterName;
    }

    public void setPhysicalClusterName(String physicalClusterName) {
        this.physicalClusterName = physicalClusterName;
    }

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

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Double getBytesIn() {
        return bytesIn;
    }

    public void setBytesIn(Double bytesIn) {
        this.bytesIn = bytesIn;
    }

    public List<Double> getMaxAvgBytesInList() {
        return maxAvgBytesInList;
    }

    public void setMaxAvgBytesInList(List<Double> maxAvgBytesInList) {
        this.maxAvgBytesInList = maxAvgBytesInList;
    }

    public List<Integer> getTopicBrokerIdList() {
        return topicBrokerIdList;
    }

    public void setTopicBrokerIdList(List<Integer> topicBrokerIdList) {
        this.topicBrokerIdList = topicBrokerIdList;
    }

    public List<Integer> getRegionBrokerIdList() {
        return regionBrokerIdList;
    }

    public void setRegionBrokerIdList(List<Integer> regionBrokerIdList) {
        this.regionBrokerIdList = regionBrokerIdList;
    }

    public List<String> getRegionNameList() {
        return regionNameList;
    }

    public void setRegionNameList(List<String> regionNameList) {
        this.regionNameList = regionNameList;
    }

    public Integer getPresentPartitionNum() {
        return presentPartitionNum;
    }

    public void setPresentPartitionNum(Integer presentPartitionNum) {
        this.presentPartitionNum = presentPartitionNum;
    }

    public Integer getNeedIncrPartitionNum() {
        return needIncrPartitionNum;
    }

    public void setNeedIncrPartitionNum(Integer needIncrPartitionNum) {
        this.needIncrPartitionNum = needIncrPartitionNum;
    }

    @Override
    public String toString() {
        return "PartitionOrderDetailData{" +
                "physicalClusterId=" + physicalClusterId +
                ", physicalClusterName='" + physicalClusterName + '\'' +
                ", logicalClusterId=" + logicalClusterId +
                ", logicalClusterName='" + logicalClusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", bytesIn=" + bytesIn +
                ", maxAvgBytesInList=" + maxAvgBytesInList +
                ", topicBrokerIdList=" + topicBrokerIdList +
                ", regionBrokerIdList=" + regionBrokerIdList +
                ", regionNameList=" + regionNameList +
                ", presentPartitionNum=" + presentPartitionNum +
                ", needIncrPartitionNum=" + needIncrPartitionNum +
                '}';
    }
}