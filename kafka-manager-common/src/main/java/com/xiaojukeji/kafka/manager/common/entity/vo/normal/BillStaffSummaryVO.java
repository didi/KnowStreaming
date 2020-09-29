package com.xiaojukeji.kafka.manager.common.entity.vo.normal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/26
 */
@ApiModel(value = "用户月度账单概览")
public class BillStaffSummaryVO {
    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "Topic数量")
    private Integer topicNum;

    @ApiModelProperty(value = "配额")
    private Double quota;

    @ApiModelProperty(value = "金额")
    private Double cost;

    @ApiModelProperty(value = "月份")
    private String gmtMonth;

    @ApiModelProperty(value = "时间戳")
    private Long timestamp;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(Integer topicNum) {
        this.topicNum = topicNum;
    }

    public Double getQuota() {
        return quota;
    }

    public void setQuota(Double quota) {
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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "BillStaffSummaryVO{" +
                "username='" + username + '\'' +
                ", topicNum=" + topicNum +
                ", quota=" + quota +
                ", cost=" + cost +
                ", gmtMonth='" + gmtMonth + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}