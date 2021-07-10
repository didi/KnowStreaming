package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/9/16
 */
@ApiModel(value = "我的应用对Topic的信息")
public class TopicMyAppVO {
    @ApiModelProperty(value = "应用id")
    private String appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "负责人")
    private String appPrincipals;

    @ApiModelProperty(value = "发送Quota(B/s)")
    private Long produceQuota;

    @ApiModelProperty(value = "消费Quota(B/s)")
    private Long consumerQuota;

    @ApiModelProperty(value = "权限, 0:无权限, 1:可消费, 2:可发送, 3:可消费发送")
    private Integer access;

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

    public Long getProduceQuota() {
        return produceQuota;
    }

    public void setProduceQuota(Long produceQuota) {
        this.produceQuota = produceQuota;
    }

    public Long getConsumerQuota() {
        return consumerQuota;
    }

    public void setConsumerQuota(Long consumerQuota) {
        this.consumerQuota = consumerQuota;
    }

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

    @Override
    public String toString() {
        return "TopicMyAppVO{" +
                "appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", appPrincipals='" + appPrincipals + '\'' +
                ", produceQuota=" + produceQuota +
                ", consumerQuota=" + consumerQuota +
                ", access=" + access +
                '}';
    }
}