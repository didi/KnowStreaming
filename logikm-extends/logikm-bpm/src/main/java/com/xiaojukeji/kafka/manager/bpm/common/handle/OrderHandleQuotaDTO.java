package com.xiaojukeji.kafka.manager.bpm.common.handle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/21
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Quota工单审批参数")
public class OrderHandleQuotaDTO {
    @ApiModelProperty(value = "分区数, 非必须")
    private Integer partitionNum;

    @ApiModelProperty(value = "RegionID")
    private Long regionId;

    @ApiModelProperty(value = "BrokerId列表")
    private List<Integer> brokerIdList;

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public List<Integer> getBrokerIdList() {
        return brokerIdList;
    }

    public void setBrokerIdList(List<Integer> brokerIdList) {
        this.brokerIdList = brokerIdList;
    }

    @Override
    public String toString() {
        return "OrderHandleQuotaDTO{" +
                "partitionNum=" + partitionNum +
                "} " + super.toString();
    }

    public boolean isExistNullParam() {
        if (ValidateUtils.isNull(partitionNum) || partitionNum == 0) {
            partitionNum = 0;
            return true;
        }
        if (partitionNum > 0
                && ValidateUtils.isNullOrLessThanZero(regionId)
                && ValidateUtils.isEmptyList(brokerIdList)) {
            return true;
        }
        return false;
    }
}