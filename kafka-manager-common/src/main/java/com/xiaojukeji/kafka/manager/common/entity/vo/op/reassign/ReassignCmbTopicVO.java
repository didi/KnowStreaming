package com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class ReassignCmbTopicVO {
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

  @ApiModelProperty(value = "maxlogsize(B)")
  private Long maxLogSize;

  @ApiModelProperty(value = "完成迁移分区数")
  private Integer completedPartitionNum;

  @ApiModelProperty(value = "总的分区数")
  private Integer totalPartitionNum;

  @ApiModelProperty(value = "预计剩余时长(ms)")
  private Long remainTime;

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

  public Long getRemainTime() {
    return remainTime;
  }

  public void setRemainTime(Long remainTime) {
    this.remainTime = remainTime;
  }
}
