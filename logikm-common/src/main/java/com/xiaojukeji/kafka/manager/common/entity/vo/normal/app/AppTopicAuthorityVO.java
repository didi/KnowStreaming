package com.xiaojukeji.kafka.manager.common.entity.vo.normal.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
@ApiModel(value = "AppTopicAuthority")
public class AppTopicAuthorityVO {
    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "appId")
    private String appId;

    @ApiModelProperty(value = "权限: 0:无权限, 1:可消费 2:可发送 3:可发送消费 4:可管理")
    private Integer access;

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

    @Override
    public String toString() {
        return "AppTopicAuthorityVO{" +
                ", topicName='" + topicName + '\'' +
                ", appId='" + appId + '\'' +
                ", access=" + access +
                '}';
    }
}
