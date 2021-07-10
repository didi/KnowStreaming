package com.xiaojukeji.kafka.manager.bpm.common.handle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/20
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "工单批量审批")
public class OrderHandleBatchDTO {
    @ApiModelProperty(value = "工单集合")
    private List<Long> orderIdList;

    @ApiModelProperty(value = "审批状态, 1:通过, 2:拒绝")
    private Integer status;

    @ApiModelProperty(value = "审批意见")
    private String opinion;

    public List<Long> getOrderIdList() {
        return orderIdList;
    }

    public void setOrderIdList(List<Long> orderIdList) {
        this.orderIdList = orderIdList;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    @Override
    public String toString() {
        return "OrderHandleBatchDTO{" +
                "orderIdList=" + orderIdList +
                ", status=" + status +
                ", opinion='" + opinion + '\'' +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isEmptyList(orderIdList) || ValidateUtils.isNull(status)) {
            return false;
        }
        return true;
    }
}