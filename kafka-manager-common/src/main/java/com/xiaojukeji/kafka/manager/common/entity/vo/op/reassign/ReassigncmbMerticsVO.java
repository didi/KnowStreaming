package com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class ReassigncmbMerticsVO {
  @ApiModelProperty(value = "Topic名称")
  private String topicName;

  @ApiModelProperty(value = "ByteIn(B/s)")
  private Double ByteIn;

  @ApiModelProperty(value = "近三天峰值流量(B/s)")
  private List<Double> peakFlow;

  @ApiModelProperty(value = "消费延迟")
  private String cosumeDelay;

  @ApiModelProperty(value = "原本的保存时间(ms)")
  private Long originalRetentionTime;

  @ApiModelProperty(value = "maxlogsize(B)")
  private Long maxLogSize;

  public String getTopicName() {
    return topicName;
  }

  public void setTopicName(String topicName) {
    this.topicName = topicName;
  }

  public Double getByteIn() {
    return ByteIn;
  }

  public void setByteIn(Double byteIn) {
    ByteIn = byteIn;
  }

  public List<Double> getPeakFlow() {
    return peakFlow;
  }

  public void setPeakFlow(List<Double> peakFlow) {
    this.peakFlow = peakFlow;
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

  public Long getMaxLogSize() {
    return maxLogSize;
  }

  public void setMaxLogSize(Long maxLogSize) {
    this.maxLogSize = maxLogSize;
  }
}
