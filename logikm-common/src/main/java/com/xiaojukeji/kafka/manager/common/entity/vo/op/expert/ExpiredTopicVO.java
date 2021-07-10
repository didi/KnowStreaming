package com.xiaojukeji.kafka.manager.common.entity.vo.op.expert;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/3/30
 */
public class ExpiredTopicVO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "集群名称")
    private String clusterName;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "过期天数")
    private Integer expiredDay;

    @ApiModelProperty(value = "App名称")
    private String appName;

    @ApiModelProperty(value = "AppID")
    private String appId;

    @ApiModelProperty(value = "负责人")
    private String principals;

    @ApiModelProperty(value = "状态, -1:已通知可下线, 0:过期待通知, 1+:已通知待反馈")
    private Integer status;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getExpiredDay() {
        return expiredDay;
    }

    public void setExpiredDay(Integer expiredDay) {
        this.expiredDay = expiredDay;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ExpiredTopicVO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", expiredDay=" + expiredDay +
                ", appName='" + appName + '\'' +
                ", appId='" + appId + '\'' +
                ", principals='" + principals + '\'' +
                ", status=" + status +
                '}';
    }
}