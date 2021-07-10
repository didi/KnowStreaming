package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/5/11
 */
@ApiModel(value = "Topic账单")
public class TopicBillVO {
    @ApiModelProperty(value = "配额")
    private Long quota;

    @ApiModelProperty(value = "金额")
    private Double cost;

    @ApiModelProperty(value = "月份")
    private String gmtMonth;

    public Long getQuota() {
        return quota;
    }

    public void setQuota(Long quota) {
        this.quota = quota;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getGmtMonth() {
        return gmtMonth;
    }

    public void setGmtMonth(String gmtMonth) {
        this.gmtMonth = gmtMonth;
    }

    @Override
    public String toString() {
        return "TopicBillVO{" +
                "quota=" + quota +
                ", cost=" + cost +
                ", gmtMonth='" + gmtMonth + '\'' +
                '}';
    }
}