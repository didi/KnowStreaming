package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/3/31
 */
@ApiModel(description = "Topic信息")
public class TopicMineVO {
    @ApiModelProperty(value = "逻辑集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "逻辑集群名称")
    private String clusterName;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "流入流量(B/s)")
    private Object bytesIn;

    @ApiModelProperty(value = "流出流量(B/s)")
    private Object bytesOut;

    @ApiModelProperty(value = "AppID")
    private String appId;

    @ApiModelProperty(value = "App名称")
    private String appName;

    @ApiModelProperty(value = "App负责人")
    private String appPrincipals;

    @ApiModelProperty(value = "状态, 0:无权限, 1:可消费 2:可发送 3:可消费发送 4:可管理")
    private Integer access;

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

    public Object getBytesIn() {
        return bytesIn;
    }

    public void setBytesIn(Object bytesIn) {
        this.bytesIn = bytesIn;
    }

    public Object getBytesOut() {
        return bytesOut;
    }

    public void setBytesOut(Object bytesOut) {
        this.bytesOut = bytesOut;
    }

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

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

    @Override
    public String toString() {
        return "TopicMineVO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", bytesIn=" + bytesIn +
                ", bytesOut=" + bytesOut +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", appPrincipals='" + appPrincipals + '\'' +
                ", access=" + access +
                '}';
    }
}