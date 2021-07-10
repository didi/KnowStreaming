package com.xiaojukeji.kafka.manager.bpm.common.entry.apply;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import java.util.List;

/**
 * @author  zhongyuankai
 * @date 20/4/20
 */
public class OrderExtensionApplyTopicDTO {
    private Long clusterId;

    private String topicName;

    private String appId;

    private Long peakBytesIn;

    private String orderId;

    private String systemCode;

    private Boolean callback;

    private Boolean isPhysicalClusterId;

    private String kafkaManagerEnv;

    private Integer replicaNum;

    private Integer partitionNum;

    private Long retentionTime;

    private Long regionId;

    private List<Integer> brokerIdList;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getPeakBytesIn() {
        return peakBytesIn;
    }

    public void setPeakBytesIn(Long peakBytesIn) {
        this.peakBytesIn = peakBytesIn;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public Boolean getCallback() {
        return callback;
    }

    public void setCallback(Boolean callback) {
        this.callback = callback;
    }

    public Boolean isPhysicalClusterId() {
        return isPhysicalClusterId;
    }

    public void setPhysicalClusterId(Boolean physicalClusterId) {
        isPhysicalClusterId = physicalClusterId;
    }

    public String getKafkaManagerEnv() {
        return kafkaManagerEnv;
    }

    public void setKafkaManagerEnv(String kafkaManagerEnv) {
        this.kafkaManagerEnv = kafkaManagerEnv;
    }

    public Boolean getPhysicalClusterId() {
        return isPhysicalClusterId;
    }

    public Integer getReplicaNum() {
        return replicaNum;
    }

    public void setReplicaNum(Integer replicaNum) {
        this.replicaNum = replicaNum;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public Long getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Long retentionTime) {
        this.retentionTime = retentionTime;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public List<Integer> getBrokerIdList() {
        return brokerIdList;
    }

    public void setBrokerIdList(List<Integer> brokerIdList) {
        this.brokerIdList = brokerIdList;
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(appId) ||
                ValidateUtils.isNull(clusterId) ||
                ValidateUtils.isNull(topicName) ||
                ValidateUtils.isNullOrLessThanZero(peakBytesIn)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OrderExtensionApplyTopicDTO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", appId='" + appId + '\'' +
                ", peakBytesIn=" + peakBytesIn +
                ", orderId='" + orderId + '\'' +
                ", systemCode='" + systemCode + '\'' +
                ", callback=" + callback +
                ", isPhysicalClusterId=" + isPhysicalClusterId +
                ", kafkaManagerEnv='" + kafkaManagerEnv + '\'' +
                ", replicaNum=" + replicaNum +
                ", partitionNum=" + partitionNum +
                ", retentionTime=" + retentionTime +
                ", regionId=" + regionId +
                ", brokerIdList=" + brokerIdList +
                '}';
    }
}