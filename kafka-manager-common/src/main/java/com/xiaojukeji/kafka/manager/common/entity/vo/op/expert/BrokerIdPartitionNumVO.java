package com.xiaojukeji.kafka.manager.common.entity.vo.op.expert;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/5/15
 */
@ApiModel(description = "Region热点Topic")
public class BrokerIdPartitionNumVO {
    @ApiModelProperty(value = "BrokerId")
    private Integer brokeId;

    @ApiModelProperty(value = "分区数")
    private Integer partitionNum;

    public Integer getBrokeId() {
        return brokeId;
    }

    public void setBrokeId(Integer brokeId) {
        this.brokeId = brokeId;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    @Override
    public String toString() {
        return "BrokerIdPartitionNumVO{" +
                "brokeId=" + brokeId +
                ", partitionNum=" + partitionNum +
                '}';
    }
}