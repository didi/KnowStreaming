package com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "Topic信息列表")
public class ReassignCmbTopicDTO {
  @ApiModelProperty(value = "Topic名称")
  private String topicName;

  @ApiModelProperty(value = "byteIn(B/s)")
  private Long byteIn;

  @ApiModelProperty(value = "近三天峰值流量(B/s)")
  private List<Long> peakFlow;

  @ApiModelProperty(value = "分区ID")
  private List<Integer> partitionIdList;

  @ApiModelProperty(value = "消费延迟")
  private String cosumeDelay;

  @ApiModelProperty(value = "原本的保存时间(ms)")
  private Long originalRetentionTime;

  @ApiModelProperty(value = "迁移时的保存时间(ms)")
  private Long reassignRetentionTime;

  @ApiModelProperty(value = "maxlogsize(B/s)")
  private Long maxLogSize;

  public String getTopicName() {
    return topicName;
  }

  public void setTopicName(String topicName) {
    this.topicName = topicName;
  }

  public Long getByteIn() {
    return byteIn;
  }

  public void setByteIn(Long byteIn) {
    this.byteIn = byteIn;
  }

  public List<Long> getPeakFlow() {
    return peakFlow;
  }

  public void setPeakFlow(List<Long> peakFlow) {
    this.peakFlow = peakFlow;
  }

  public List<Integer> getPartitionIdList() {
    return partitionIdList;
  }

  public void setPartitionIdList(List<Integer> partitionIdList) {
    this.partitionIdList = partitionIdList;
  }

  public String getCosumeDelay() {
    return cosumeDelay;
  }

  public void setCosumeDelay(String cosumeDelay) {
    this.cosumeDelay = cosumeDelay;
  }

  public Long getOriginalRetentionTime() {
    return originalRetentionTime;
  }

  public void setOriginalRetentionTime(Long originalRetentionTime) {
    this.originalRetentionTime = originalRetentionTime;
  }

  public Long getReassignRetentionTime() {
    return reassignRetentionTime;
  }

  public void setReassignRetentionTime(Long reassignRetentionTime) {
    this.reassignRetentionTime = reassignRetentionTime;
  }

  public Long getMaxLogSize() {
    return maxLogSize;
  }

  public void setMaxLogSize(Long maxLogSize) {
    this.maxLogSize = maxLogSize;
  }
}
