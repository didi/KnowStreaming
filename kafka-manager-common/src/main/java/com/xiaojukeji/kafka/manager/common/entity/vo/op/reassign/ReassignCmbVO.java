package com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign;

import io.swagger.annotations.ApiModelProperty;

/**
 * 招行迁移任务
 */
public class ReassignCmbVO {
  @ApiModelProperty(value = "任务ID")
  private Long taskId;

  @ApiModelProperty(value = "任务名称")
  private String taskName;

  @ApiModelProperty(value = "逻辑集群ID")
  private Long logicClusterId;

  @ApiModelProperty(value = "逻辑集群名称")
  private String logicClusterName;

  @ApiModelProperty(value = "描述")
  private String description;

  @ApiModelProperty(value = "计划执行时间")
  private Long beginTime;

  @ApiModelProperty(value = "实际执行时间")
  private Long executeTime;

  @ApiModelProperty(value = "状态")
  private Integer status;

  @ApiModelProperty(value = "创建人")
  private String creator;

  @ApiModelProperty(value = "最后更新时间")
  private Long gmtModify;

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

  public Long getLogicClusterId() {
    return logicClusterId;
  }

  public void setLogicClusterId(Long logicClusterId) {
    this.logicClusterId = logicClusterId;
  }

  public String getLogicClusterName() {
    return logicClusterName;
  }

  public void setLogicClusterName(String logicClusterName) {
    this.logicClusterName = logicClusterName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getBeginTime() {
    return beginTime;
  }

  public void setBeginTime(Long beginTime) {
    this.beginTime = beginTime;
  }

  public Long getExecuteTime() {
    return executeTime;
  }

  public void setExecuteTime(Long executeTime) {
    this.executeTime = executeTime;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public Long getGmtModify() {
    return gmtModify;
  }

  public void setGmtModify(Long gmtModify) {
    this.gmtModify = gmtModify;
  }
}
