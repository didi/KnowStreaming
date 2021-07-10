package com.xiaojukeji.kafka.manager.common.entity.dto.normal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/8/21
 */
@ApiModel(description = "JmxSwitch开关")
@JsonIgnoreProperties(ignoreUnknown = true)
public class JmxSwitchDTO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "是否是物理集群ID, True:是, False:否")
    private Boolean isPhysicalClusterId;

    @ApiModelProperty(value = "Topic请求你JMX")
    private String topicName;

    @ApiModelProperty(value = "Topic请求你JMX")
    private Boolean openTopicRequestMetrics;

    @ApiModelProperty(value = "AppTopicJMX")
    private Boolean openAppIdTopicMetrics;

    @ApiModelProperty(value = "客户端请求JMX")
    private Boolean openClientRequestMetrics;

    @ApiModelProperty(value = "磁盘JMX")
    private Boolean openDiskMetrics;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Boolean getPhysicalClusterId() {
        return isPhysicalClusterId;
    }

    public void setPhysicalClusterId(Boolean physicalClusterId) {
        isPhysicalClusterId = physicalClusterId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Boolean getOpenTopicRequestMetrics() {
        return openTopicRequestMetrics;
    }

    public void setOpenTopicRequestMetrics(Boolean openTopicRequestMetrics) {
        this.openTopicRequestMetrics = openTopicRequestMetrics;
    }

    public Boolean getOpenAppIdTopicMetrics() {
        return openAppIdTopicMetrics;
    }

    public void setOpenAppIdTopicMetrics(Boolean openAppIdTopicMetrics) {
        this.openAppIdTopicMetrics = openAppIdTopicMetrics;
    }

    public Boolean getOpenClientRequestMetrics() {
        return openClientRequestMetrics;
    }

    public void setOpenClientRequestMetrics(Boolean openClientRequestMetrics) {
        this.openClientRequestMetrics = openClientRequestMetrics;
    }

    public Boolean getOpenDiskMetrics() {
        return openDiskMetrics;
    }

    public void setOpenDiskMetrics(Boolean openDiskMetrics) {
        this.openDiskMetrics = openDiskMetrics;
    }

    @Override
    public String toString() {
        return "JmxSwitchDTO{" +
                "clusterId=" + clusterId +
                ", isPhysicalClusterId=" + isPhysicalClusterId +
                ", topicName='" + topicName + '\'' +
                ", openTopicRequestMetrics=" + openTopicRequestMetrics +
                ", openAppIdTopicMetrics=" + openAppIdTopicMetrics +
                ", openClientRequestMetrics=" + openClientRequestMetrics +
                ", openDiskMetrics=" + openDiskMetrics +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isNull(isPhysicalClusterId)
                || ValidateUtils.isNull(topicName)) {
            return false;
        }

        if (ValidateUtils.isNull(openTopicRequestMetrics)) {
            openTopicRequestMetrics = Boolean.FALSE;
        }
        if (ValidateUtils.isNull(openAppIdTopicMetrics)) {
            openAppIdTopicMetrics = Boolean.FALSE;
        }
        if (ValidateUtils.isNull(openClientRequestMetrics)) {
            openClientRequestMetrics = Boolean.FALSE;
        }
        if (ValidateUtils.isNull(openDiskMetrics)) {
            openDiskMetrics = Boolean.FALSE;
        }
        return true;
    }
}