package com.xiaojukeji.kafka.manager.bpm.common.entry.apply;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

/**
 * @author zengqiao
 * @date 20/9/15
 */
public class PartitionOrderExtensionDTO {
    private Long clusterId;

    private String topicName;

    private Boolean isPhysicalClusterId;

    private Integer needIncrPartitionNum;

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

    public Boolean getIsPhysicalClusterId() {
        return isPhysicalClusterId;
    }

    public void setIsPhysicalClusterId(Boolean isPhysicalClusterId) {
        this.isPhysicalClusterId = isPhysicalClusterId;
    }

    public Integer getNeedIncrPartitionNum() {
        return needIncrPartitionNum;
    }

    public void setNeedIncrPartitionNum(Integer needIncrPartitionNum) {
        this.needIncrPartitionNum = needIncrPartitionNum;
    }

    @Override
    public String toString() {
        return "PartitionOrderExtensionDTO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", isPhysicalClusterId=" + isPhysicalClusterId +
                ", needIncrPartitionNum=" + needIncrPartitionNum +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isBlank(topicName)) {
            return false;
        }
        if (ValidateUtils.isNull(isPhysicalClusterId)) {
            this.isPhysicalClusterId = Boolean.FALSE;
        }
        return true;
    }
}