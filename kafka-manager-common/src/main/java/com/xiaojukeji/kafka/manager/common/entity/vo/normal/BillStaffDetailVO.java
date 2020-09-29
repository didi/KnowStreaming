package com.xiaojukeji.kafka.manager.common.entity.vo.normal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/26
 */
@ApiModel(value = "用户月度账单详情")
public class BillStaffDetailVO {
    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "总金额")
    private Double costSum;

    @ApiModelProperty(value = "账单详情")
    private List<BillTopicVO> billList;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getCostSum() {
        return costSum;
    }

    public void setCostSum(Double costSum) {
        this.costSum = costSum;
    }

    public List<BillTopicVO> getBillList() {
        return billList;
    }

    public void setBillList(List<BillTopicVO> billList) {
        this.billList = billList;
    }

    @Override
    public String toString() {
        return "BillStaffDetailVO{" +
                "username='" + username + '\'' +
                ", costSum=" + costSum +
                ", billList=" + billList +
                '}';
    }
}