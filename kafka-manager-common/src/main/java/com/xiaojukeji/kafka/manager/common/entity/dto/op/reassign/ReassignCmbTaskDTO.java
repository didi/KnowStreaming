package com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "创建迁移任务")
public class ReassignCmbTaskDTO {
  @ApiModelProperty(value = "任务ID")
  private Long taskId;

  @ApiModelProperty(value = "集群ID")
  private Long clusterId;

  @ApiModelProperty(value = "迁移任务名称")
  private String taskName;

  @ApiModelProperty(value = "迁移方式(1:region，2:broker)")
  private Integer reassginWay;

  @ApiModelProperty(value = "目标RegionID")
  private Long regionId;

  @ApiModelProperty(value = "目标BrokerID列表")
  private List<Integer> brokerIdList;

  @ApiModelProperty(value = "限流值(B/s)")
  private Long throttle;

  @ApiModelProperty(value = "计划开始时间")
  private Long beginTime;

  @ApiModelProperty(value = "描述")
  private String description;

  @ApiModelProperty(value = "Topic信息列表")
  private List<ReassignCmbTopicDTO> topicList;

  public Long getTaskId() {
    return taskId;
  }

  public void setTaskId(Long taskId) {
    this.taskId = taskId;
  }

  public Long getClusterId() {
    return clusterId;
  }

  public void setClusterId(Long clusterId) {
    this.clusterId = clusterId;
  }

  public String getTaskName() {
    return taskName;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public Integer getReassginWay() {
    return reassginWay;
  }

  public void setReassginWay(Integer reassginWay) {
    this.reassginWay = reassginWay;
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

  public Long getThrottle() {
    return throttle;
  }

  public void setThrottle(Long throttle) {
    this.throttle = throttle;
  }

  public Long getBeginTime() {
    return beginTime;
  }

  public void setBeginTime(Long beginTime) {
    this.beginTime = beginTime;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<ReassignCmbTopicDTO> getTopicList() {
    return topicList;
  }

  public void setTopicList(List<ReassignCmbTopicDTO> topicList) {
    this.topicList = topicList;
  }
}
