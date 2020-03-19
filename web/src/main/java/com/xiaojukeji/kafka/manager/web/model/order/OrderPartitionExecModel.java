package com.xiaojukeji.kafka.manager.web.model.order;

import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.OrderStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 执行Topic申请工单
 * @author zengqiao
 * @date 19/6/26
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "OrderPartitionExecModel", description = "Partition工单审批")
public class OrderPartitionExecModel {
    @ApiModelProperty(value = "工单Id")
    private Long orderId;

    @ApiModelProperty(value = "审批结果, [1:通过, 2:拒绝]")
    private Integer orderStatus;

    @ApiModelProperty(value = "审批意见, 拒绝时必填")
    private String approvalOpinions;

    @ApiModelProperty(value = "分区数")
    private Integer partitionNum;

    @ApiModelProperty(value = "brokerId列表")
    private List<Integer> brokerIdList;

    @ApiModelProperty(value = "regionId列表")
    private List<Long> regionIdList;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getApprovalOpinions() {
        return approvalOpinions;
    }

    public void setApprovalOpinions(String approvalOpinions) {
        this.approvalOpinions = approvalOpinions;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public List<Integer> getBrokerIdList() {
        return brokerIdList;
    }

    public void setBrokerIdList(List<Integer> brokerIdList) {
        this.brokerIdList = brokerIdList;
    }

    public List<Long> getRegionIdList() {
        return regionIdList;
    }

    public void setRegionIdList(List<Long> regionIdList) {
        this.regionIdList = regionIdList;
    }

    @Override
    public String toString() {
        return "OrderTopicExecModel{" +
                "orderId=" + orderId +
                ", orderStatus=" + orderStatus +
                ", approvalOpinions=" + approvalOpinions +
                ", partitionNum=" + partitionNum +
                ", brokerIdList=" + brokerIdList +
                ", regionIdList='" + regionIdList + '\'' +
                '}';
    }

    private static Result checkRefuseIllegal(OrderPartitionExecModel that) {
        if (StringUtils.isEmpty(that.approvalOpinions)) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, approvalOpinions is empty");
        }
        return new Result();
    }

    private static Result checkPassedIllegal(OrderPartitionExecModel that) {
        if (that.brokerIdList == null) {
            that.brokerIdList = new ArrayList<>();
        }
        if (that.regionIdList == null) {
            that.regionIdList = new ArrayList<>();
        }
        if (that.partitionNum == null || that.partitionNum <= 0
                || (that.brokerIdList.isEmpty() && that.regionIdList.isEmpty())) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, some filed is illegal");
        }
        return new Result();
    }

    public static Result illegal(OrderPartitionExecModel that) {
        if (that == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal");
        }
        if (that.orderId == null || that.orderStatus == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, orderId or orderStatus is null");
        }
        if (OrderStatusEnum.PASSED.getCode().equals(that.orderStatus)) {
            return checkPassedIllegal(that);
        } else if (OrderStatusEnum.REFUSED.getCode().equals(that.orderStatus)) {
            return checkRefuseIllegal(that);
        }
        return new Result(StatusCode.PARAM_ERROR, "param illegal, orderStatus illegal");
    }
}