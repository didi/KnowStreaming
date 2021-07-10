package com.xiaojukeji.kafka.manager.common.entity.vo.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/28
 */
@ApiModel(description="集群限流信息")
public class TopicThrottleVO {
    @ApiModelProperty(value="Topic名称")
    private String topicName;

    @ApiModelProperty(value="AppId")
    private String appId;

    @ApiModelProperty(value="BrokerId列表")
    private List<Integer> brokerIdList;

    @ApiModelProperty(value = "客户端类型[Produce|Fetch]")
    private String throttleClientType;

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

    public List<Integer> getBrokerIdList() {
        return brokerIdList;
    }

    public void setBrokerIdList(List<Integer> brokerIdList) {
        this.brokerIdList = brokerIdList;
    }

    public String getThrottleClientType() {
        return throttleClientType;
    }

    public void setThrottleClientType(String throttleClientType) {
        this.throttleClientType = throttleClientType;
    }

    @Override
    public String toString() {
        return "TopicThrottleVO{" +
                "topicName='" + topicName + '\'' +
                ", appId='" + appId + '\'' +
                ", brokerIdList=" + brokerIdList +
                ", throttleClientType='" + throttleClientType + '\'' +
                '}';
    }
}