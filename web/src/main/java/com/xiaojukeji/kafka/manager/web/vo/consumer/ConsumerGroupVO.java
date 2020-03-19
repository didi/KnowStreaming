package com.xiaojukeji.kafka.manager.web.vo.consumer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 消费组信息
 * @author zengqiao
 * @date 19/4/3
 */
@ApiModel(value = "ConsumerGroupVO", description = "消费组信息")
public class ConsumerGroupVO implements Serializable {
    @ApiModelProperty(value = "消费组名称")
    private String consumerGroup;

    @ApiModelProperty(value = "存储位置")
    private String location;

    public ConsumerGroupVO(String consumerGroup, String location) {
        this.consumerGroup = consumerGroup;
        this.location = location;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ConsumerGroupVO{" +
                "consumerGroup='" + consumerGroup + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
