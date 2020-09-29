package com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@ApiModel(description="集群信息")
public class ClusterDetailVO extends ClusterBaseVO {
    @ApiModelProperty(value="Broker数")
    private Integer brokerNum;

    @ApiModelProperty(value="Topic数")
    private Integer topicNum;

    @ApiModelProperty(value="ConsumerGroup数")
    private Integer consumerGroupNum;

    @ApiModelProperty(value="ControllerID")
    private Integer controllerId;

    @ApiModelProperty(value="Region数")
    private Integer regionNum;

    public Integer getBrokerNum() {
        return brokerNum;
    }

    public void setBrokerNum(Integer brokerNum) {
        this.brokerNum = brokerNum;
    }

    public Integer getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(Integer topicNum) {
        this.topicNum = topicNum;
    }

    public Integer getConsumerGroupNum() {
        return consumerGroupNum;
    }

    public void setConsumerGroupNum(Integer consumerGroupNum) {
        this.consumerGroupNum = consumerGroupNum;
    }

    public Integer getControllerId() {
        return controllerId;
    }

    public void setControllerId(Integer controllerId) {
        this.controllerId = controllerId;
    }

    public Integer getRegionNum() {
        return regionNum;
    }

    public void setRegionNum(Integer regionNum) {
        this.regionNum = regionNum;
    }

    @Override
    public String toString() {
        return "ClusterDetailVO{" +
                "brokerNum=" + brokerNum +
                ", topicNum=" + topicNum +
                ", consumerGroupNum=" + consumerGroupNum +
                ", controllerId=" + controllerId +
                ", regionNum=" + regionNum +
                "} " + super.toString();
    }
}