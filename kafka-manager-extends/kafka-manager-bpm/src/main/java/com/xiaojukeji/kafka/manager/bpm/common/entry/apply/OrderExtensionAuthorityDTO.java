package com.xiaojukeji.kafka.manager.bpm.common.entry.apply;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

/**
 * @author zhongyuankai
 * @date 20/4/22
 */
public class OrderExtensionAuthorityDTO {
    private Long clusterId;

    private String topicName;

    private String appId;

    private Integer access;

    private boolean isPhysicalClusterId;

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

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

    public boolean isPhysicalClusterId() {
        return isPhysicalClusterId;
    }

    public void setPhysicalClusterId(boolean physicalClusterId) {
        isPhysicalClusterId = physicalClusterId;
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(appId) ||
                ValidateUtils.isNull(clusterId) ||
                ValidateUtils.isNull(topicName) ||
                ValidateUtils.isNullOrLessThanZero(access)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OrderExtensionAuthorityDTO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", appId='" + appId + '\'' +
                ", access=" + access +
                ", isPhysicalClusterId=" + isPhysicalClusterId +
                '}';
    }
}
