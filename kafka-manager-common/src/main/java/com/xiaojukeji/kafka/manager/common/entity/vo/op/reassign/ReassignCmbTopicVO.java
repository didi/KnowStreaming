package com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign;

import io.swagger.annotations.ApiModelProperty;

public class ReassignCmbTopicVO {
  @ApiModelProperty(value = "Topic名称")
  private String topicName;

  @ApiModelProperty(value = "完成迁移分区数")
  private Integer completedPartitionNum;

  @ApiModelProperty(value = "总的分区数")
  private Integer totalPartitionNum;

  @ApiModelProperty(value = "原本的保存时间(ms)")
  private Long originalRetentionTime;

  @ApiModelProperty(value = "迁移时的保存时间(ms)")
  private Long reassignRetentionTime;

  @ApiModelProperty(value = "maxlogsize(B)")
  private Long maxLogSize;

  @ApiModelProperty(value = "子任务状态")
  private Integer subStatus;

  @ApiModelProperty(value = "byteIn(B/s)")
  private Double byteIn;

  @ApiModelProperty(value = "迁移流量")
  private Double reassignFlow;

  @ApiModelProperty(value = "预计剩余时长(ms)")
  private Long remainTime;

  public String getTopicName() {
    return topicName;
  }

  public void setTopicName(String topicName) {
    this.topicName = topicName;
  }

  public Integer getCompletedPartitionNum() {
    return completedPartitionNum;
  }

  public void setCompletedPartitionNum(Integer completedPartitionNum) {
    this.completedPartitionNum = completedPartitionNum;
  }

  public Integer getTotalPartitionNum() {
    return totalPartitionNum;
  }

  public void setTotalPartitionNum(Integer totalPartitionNum) {
    this.totalPartitionNum = totalPartitionNum;
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

  public Integer getSubStatus() {
    return subStatus;
  }

  public void setSubStatus(Integer subStatus) {
    this.subStatus = subStatus;
  }

  public Double getByteIn() {
    return byteIn;
  }

  public void setByteIn(Double byteIn) {
    this.byteIn = byteIn;
  }

  public Double getReassignFlow() {
    return reassignFlow;
  }

  public void setReassignFlow(Double reassignFlow) {
    this.reassignFlow = reassignFlow;
  }

  public Long getRemainTime() {
    return remainTime;
  }

  public void setRemainTime(Long remainTime) {
    this.remainTime = remainTime;
  }
}
