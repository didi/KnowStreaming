package com.xiaojukeji.kafka.manager.bpm.common.handle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/17
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Topic工单审批参数")
public class OrderHandleTopicDTO {
    @ApiModelProperty(value = "副本数")
    private Integer replicaNum;

    @ApiModelProperty(value = "分区数")
    private Integer partitionNum;

    @ApiModelProperty(value = "保存时间(ms)")
    private Long retentionTime;

    @ApiModelProperty(value = "RegionID")
    private Long regionId;

    @ApiModelProperty(value = "BrokerId列表")
    private List<Integer> brokerIdList;

    public Integer getReplicaNum() {
        return replicaNum;
    }

    public void setReplicaNum(Integer replicaNum) {
        this.replicaNum = replicaNum;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public Long getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Long retentionTime) {
        this.retentionTime = retentionTime;
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
        return "OrderHandleTopicDTO{" +
                "replicaNum=" + replicaNum +
                ", partitionNum=" + partitionNum +
                ", retentionTime=" + retentionTime +
                ", regionId=" + regionId +
                ", brokerIdList=" + brokerIdList +
                '}';
    }

    public boolean isExistNullParam() {
        if (ValidateUtils.isNullOrLessThanZero(replicaNum)
                || ValidateUtils.isNullOrLessThanZero(partitionNum)
                || ValidateUtils.isNullOrLessThanZero(retentionTime)){
            return true;
        }
        if (ValidateUtils.isNullOrLessThanZero(regionId)
                && ValidateUtils.isEmptyList(brokerIdList)) {
            return true;
        }
        return false;
    }
}