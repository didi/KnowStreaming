package com.xiaojukeji.kafka.manager.bpm.common.entry.detail;

import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/5/18
 */
public class QuotaOrderDetailData extends AbstractOrderDetailData {
    private String appId;

    private String appName;

    private String appPrincipals;

    private Long physicalClusterId;

    private String physicalClusterName;

    private Long logicalClusterId;

    private String logicalClusterName;

    private String topicName;

    private Long produceQuota;

    private Long consumeQuota;

    private Long oldProduceQuota;

    private Long oldConsumeQuota;

    private Double bytesIn;

    private List<Double> maxAvgBytesInList;

    private List<Integer> topicBrokerIdList;

    private List<Integer> regionBrokerIdList;

    private List<String> regionNameList;

    private Integer partitionNum;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPrincipals() {
        return appPrincipals;
    }

    public void setAppPrincipals(String appPrincipals) {
        this.appPrincipals = appPrincipals;
    }

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

    public Long getProduceQuota() {
        return produceQuota;
    }

    public void setProduceQuota(Long produceQuota) {
        this.produceQuota = produceQuota;
    }

    public Long getConsumeQuota() {
        return consumeQuota;
    }

    public void setConsumeQuota(Long consumeQuota) {
        this.consumeQuota = consumeQuota;
    }

    public Long getOldProduceQuota() {
        return oldProduceQuota;
    }

    public void setOldProduceQuota(Long oldProduceQuota) {
        this.oldProduceQuota = oldProduceQuota;
    }

    public Long getOldConsumeQuota() {
        return oldConsumeQuota;
    }

    public void setOldConsumeQuota(Long oldConsumeQuota) {
        this.oldConsumeQuota = oldConsumeQuota;
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

    public List<Integer> getTopicBrokerIdList() {
        return topicBrokerIdList;
    }

    public void setTopicBrokerIdList(List<Integer> topicBrokerIdList) {
        this.topicBrokerIdList = topicBrokerIdList;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    @Override
    public String toString() {
        return "OrderDetailQuotaDTO{" +
                "appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", appPrincipals='" + appPrincipals + '\'' +
                ", physicalClusterId=" + physicalClusterId +
                ", physicalClusterName='" + physicalClusterName + '\'' +
                ", logicalClusterId=" + logicalClusterId +
                ", logicalClusterName='" + logicalClusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", produceQuota=" + produceQuota +
                ", consumeQuota=" + consumeQuota +
                ", oldProduceQuota=" + oldProduceQuota +
                ", oldConsumeQuota=" + oldConsumeQuota +
                ", bytesIn=" + bytesIn +
                ", maxAvgBytesInList=" + maxAvgBytesInList +
                ", topicBrokerIdList=" + topicBrokerIdList +
                ", regionBrokerIdList=" + regionBrokerIdList +
                ", regionNameList=" + regionNameList +
                ", partitionNum=" + partitionNum +
                '}';
    }
}