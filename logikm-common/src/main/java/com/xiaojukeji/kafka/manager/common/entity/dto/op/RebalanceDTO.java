package com.xiaojukeji.kafka.manager.common.entity.dto.op;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.bizenum.RebalanceDimensionEnum;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 19/7/8
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "优先副本选举")
public class RebalanceDTO {
    @ApiModelProperty(value = "clusterId")
    private Long clusterId;

    @ApiModelProperty(value = "RegionId")
    private Long regionId;

    @ApiModelProperty(value = "brokerId")
    private Integer brokerId;

    @ApiModelProperty(value = "TopicName")
    private String topicName;

    @ApiModelProperty(value = "分区ID")
    private Integer partitionId;

    @ApiModelProperty(value = "维度[0: Cluster维度, 1: Region维度, 2:Broker维度, 3:Topic维度, 4:Partition纬度]")
    private Integer dimension;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Integer getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Integer brokerId) {
        this.brokerId = brokerId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(Integer partitionId) {
        this.partitionId = partitionId;
    }

    public Integer getDimension() {
        return dimension;
    }

    public void setDimension(Integer dimension) {
        this.dimension = dimension;
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(clusterId)
                || (RebalanceDimensionEnum.REGION.getCode().equals(dimension) && ValidateUtils.isNull(regionId))
                || (RebalanceDimensionEnum.BROKER.getCode().equals(dimension) && ValidateUtils.isNull(brokerId))
                || (RebalanceDimensionEnum.TOPIC.getCode().equals(dimension) && ValidateUtils.isNull(topicName))
                || (RebalanceDimensionEnum.PARTITION.getCode().equals(dimension) && (ValidateUtils.isNull(topicName) || ValidateUtils.isNull(partitionId))) ) {
            return false;
        }
        return true;
    }
}