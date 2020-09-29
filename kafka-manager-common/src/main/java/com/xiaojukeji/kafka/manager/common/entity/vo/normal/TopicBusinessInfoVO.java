package com.xiaojukeji.kafka.manager.common.entity.vo.normal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhongyuankai
 * @date 20/09/08
 */
@ApiModel(value = "Topic业务信息")
public class TopicBusinessInfoVO {
    @ApiModelProperty(value = "应用id")
    private String appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "负责人")
    private String principals;

    @ApiModelProperty(value = "集群Id")
    private Long clusterId;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

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

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

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

    @Override
    public String toString() {
        return "TopicBusinessInfoVO{" +
                "appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", principals='" + principals + '\'' +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                '}';
    }
}
