package com.xiaojukeji.kafka.manager.common.entity.dto.normal;

import com.xiaojukeji.kafka.manager.common.entity.dto.ClusterTopicDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "扩分区")
public class TopicExpandDTO extends ClusterTopicDTO {

  @ApiModelProperty(value = "regionId")
  private Long regionId;

  @ApiModelProperty(value = "brokerId列表")
  private List<Integer> brokerIds;

  @ApiModelProperty(value = "新增分区数")
  private Integer partitionNum;

  public Long getRegionId() {
    return regionId;
  }

  public void setRegionId(Long regionId) {
    this.regionId = regionId;
  }

  public List<Integer> getBrokerIds() {
    return brokerIds;
  }

  public void setBrokerIds(List<Integer> brokerIds) {
    this.brokerIds = brokerIds;
  }

  public Integer getPartitionNum() {
    return partitionNum;
  }

  public void setPartitionNum(Integer partitionNum) {
    this.partitionNum = partitionNum;
  }

  public boolean paramLegal() {
    if (ValidateUtils.isNull(clusterId)
        || ValidateUtils.isNull(topicName)
        || ValidateUtils.isNull(partitionNum) || partitionNum <= 0) {
      return false;
    }
    if (ValidateUtils.isEmptyList(brokerIds) && ValidateUtils.isNull(regionId)) {
      return false;
    }
    return true;
  }
}
