package com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

public class ReassignCmbTopicProcessVO {
  @ApiModelProperty(value = "Topic名称")
  private String topicName;

  @ApiModelProperty(value = "分区ID")
  private Integer partitionId;

  @ApiModelProperty(value = "源BrokerID")
  private List<Integer> srcBrokerIdList;

  @ApiModelProperty(value = "源LogSize(B)")
  private Long logSize;

  @ApiModelProperty(value = "目标BrokerID")
  private List<Integer> destBrokerIdList;

  @ApiModelProperty(value = "已完成LogSize(B)")
  private Long completeLogSize;

  @ApiModelProperty(value = "状态")
  private Integer status;

  @ApiModelProperty(value = "迁移进度")
  private BigDecimal reassignProcess;

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

  public List<Integer> getSrcBrokerIdList() {
    return srcBrokerIdList;
  }

  public void setSrcBrokerIdList(List<Integer> srcBrokerIdList) {
    this.srcBrokerIdList = srcBrokerIdList;
  }

  public Long getLogSize() {
    return logSize;
  }

  public void setLogSize(Long logSize) {
    this.logSize = logSize;
  }

  public List<Integer> getDestBrokerIdList() {
    return destBrokerIdList;
  }

  public void setDestBrokerIdList(List<Integer> destBrokerIdList) {
    this.destBrokerIdList = destBrokerIdList;
  }

  public Long getCompleteLogSize() {
    return completeLogSize;
  }

  public void setCompleteLogSize(Long completeLogSize) {
    this.completeLogSize = completeLogSize;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public BigDecimal getReassignProcess() {
    return reassignProcess;
  }

  public void setReassignProcess(BigDecimal reassignProcess) {
    this.reassignProcess = reassignProcess;
  }
}
