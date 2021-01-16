package com.xiaojukeji.kafka.manager.common.entity.vo.normal.consumer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 21/01/14
 */
@ApiModel(value = "Topic消费组概要信息")
public class ConsumerGroupSummaryVO {
    @ApiModelProperty(value = "消费组名称")
    private String consumerGroup;

    @ApiModelProperty(value = "使用的AppID")
    private String appIds;

    @ApiModelProperty(value = "offset存储位置")
    private String location;

    @ApiModelProperty(value = "消费组状态")
    private String state;

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getAppIds() {
        return appIds;
    }

    public void setAppIds(String appIds) {
        this.appIds = appIds;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "ConsumerGroupSummaryVO{" +
                "consumerGroup='" + consumerGroup + '\'' +
                ", appIds=" + appIds +
                ", location='" + location + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
