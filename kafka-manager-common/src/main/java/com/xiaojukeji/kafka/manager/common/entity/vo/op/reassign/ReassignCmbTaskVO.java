package com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class ReassignCmbTaskVO {
  @ApiModelProperty(value = "集群ID")
  private Long clusterId;

  @ApiModelProperty(value = "任务ID")
  private Long taskId;

  @ApiModelProperty(value = "任务名称")
  private String taskName;

  @ApiModelProperty(value = "迁移方式(true:region，false:broker)")
  private Boolean reassginWay;

  @ApiModelProperty(value = "目标RegionID")
  private Long regionId;

  @ApiModelProperty(value = "目标Region名称")
  private String regionName;

  @ApiModelProperty(value = "目标BrokerID列表")
  private List<Integer> brokerIdList;

  @ApiModelProperty(value = "限流值(B/s)")
  private Long throttle;

  @ApiModelProperty(value = "计划开始时间")
  private Long beginTime;

  @ApiModelProperty(value = "实际开始时间")
  private Long actualBeginTime;

  @ApiModelProperty(value = "状态")
  private Integer status;

  @ApiModelProperty(value = "任务完成时间")
  private Long endTime;

  @ApiModelProperty(value = "创建人")
  private String creator;

  @ApiModelProperty(value = "任务创建时间")
  private Long gmtCreate;

  @ApiModelProperty(value = "任务最后更新时间")
  private Long gmtModify;

  @ApiModelProperty(value = "描述")
  private String description;

  @ApiModelProperty(value = "Topic信息列表")
  private List<ReassignCmbTopicVO> topicList;

  public Long getClusterId() {
    return clusterId;
  }

  public void setClusterId(Long clusterId) {
    this.clusterId = clusterId;
  }

  public Long getTaskId() {
    return taskId;
  }

  public void setTaskId(Long taskId) {
    this.taskId = taskId;
  }

  public String getTaskName() {
    return taskName;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public Boolean getReassginWay() {
    return reassginWay;
  }

  public void setReassginWay(Boolean reassginWay) {
    this.reassginWay = reassginWay;
  }

  public Long getRegionId() {
    return regionId;
  }

  public void setRegionId(Long regionId) {
    this.regionId = regionId;
  }

  public String getRegionName() {
    return regionName;
  }

  public void setRegionName(String regionName) {
    this.regionName = regionName;
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

  public Long getActualBeginTime() {
    return actualBeginTime;
  }

  public void setActualBeginTime(Long actualBeginTime) {
    this.actualBeginTime = actualBeginTime;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public Long getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Long gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public Long getGmtModify() {
    return gmtModify;
  }

  public void setGmtModify(Long gmtModify) {
    this.gmtModify = gmtModify;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<ReassignCmbTopicVO> getTopicList() {
    return topicList;
  }

  public void setTopicList(List<ReassignCmbTopicVO> topicList) {
    this.topicList = topicList;
  }
}
