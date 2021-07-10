package com.xiaojukeji.kafka.manager.bpm.common.entry.apply;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

import java.util.List;

/**
 * @author zhongyuankai
 * @date 20/4/20
 */
public class OrderExtensionQuotaDTO {
    private Long clusterId;

    private String topicName;

    private String appId;

    private Long produceQuota;

    private Long consumeQuota;

    private Boolean isPhysicalClusterId;

    private Integer partitionNum;

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

    public Boolean getIsPhysicalClusterId() {
        return isPhysicalClusterId;
    }

    public void setIsPhysicalClusterId(Boolean isPhysicalClusterId) {
        this.isPhysicalClusterId = isPhysicalClusterId;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
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
                ValidateUtils.isNull(produceQuota) ||
                ValidateUtils.isNull(consumeQuota) ||
                (produceQuota == -1 && consumeQuota == -1)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OrderExtensionQuotaDTO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", appId='" + appId + '\'' +
                ", produceQuota=" + produceQuota +
                ", consumeQuota=" + consumeQuota +
                ", isPhysicalClusterId=" + isPhysicalClusterId +
                ", partitionNum=" + partitionNum +
                ", regionId=" + regionId +
                ", brokerIdList=" + brokerIdList +
                '}';
    }
}
