package com.xiaojukeji.kafka.manager.common.entity.dto.op.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.entity.dto.ClusterTopicDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/1/2
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Topic扩分区")
public class TopicExpansionDTO extends ClusterTopicDTO {
    @ApiModelProperty(value = "新增分区数")
    private Integer partitionNum;

    @ApiModelProperty(value = "brokerId列表")
    private List<Integer> brokerIdList;

    @ApiModelProperty(value = "regionId")
    private Long regionId;

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

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    @Override
    public String toString() {
        return "TopicExpandDTO{" +
                ", partitionNum=" + partitionNum +
                ", brokerIdList=" + brokerIdList +
                ", regionId=" + regionId +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isNull(topicName)
                || ValidateUtils.isNull(partitionNum) || partitionNum <= 0) {
            return false;
        }
        if (ValidateUtils.isEmptyList(brokerIdList) && ValidateUtils.isNull(regionId)) {
            return false;
        }
        return true;
    }
}