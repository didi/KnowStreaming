package com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "操作迁移任务")
public class ReassignCmbExecDTO {
  @ApiModelProperty(value = "任务ID")
  private Long taskId;

  @ApiModelProperty(value = "动作[start|cancel]")
  private String action;

  @ApiModelProperty(value = "开始时间(开始之后不可修改)")
  private Long beginTime;

  public Long getTaskId() {
    return taskId;
  }

  public void setTaskId(Long taskId) {
    this.taskId = taskId;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Long getBeginTime() {
    return beginTime;
  }

  public void setBeginTime(Long beginTime) {
    this.beginTime = beginTime;
  }
}
