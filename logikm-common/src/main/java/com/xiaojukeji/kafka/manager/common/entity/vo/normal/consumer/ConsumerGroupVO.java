package com.xiaojukeji.kafka.manager.common.entity.vo.normal.consumer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * @author zhongyuankai
 * @date 20/4/8
 */
@ApiModel(value = "消费组消费Topic信息")
public class ConsumerGroupVO {
    @ApiModelProperty(value = "消费组名称")
    private String consumerGroup;

    @ApiModelProperty(value = "使用的AppID")
    private String appIds;

    @ApiModelProperty(value = "offset存储位置")
    private String location;

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

    @Override
    public String toString() {
        return "ConsumerGroupVO{" +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", appIds='" + appIds + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
