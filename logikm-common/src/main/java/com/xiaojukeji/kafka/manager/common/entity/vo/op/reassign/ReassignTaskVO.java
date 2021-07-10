package com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign;

import io.swagger.annotations.ApiModelProperty;

/**
 * 迁移任务
 * @author zengqiao
 * @date 19/7/13
 */
public class ReassignTaskVO {
    @ApiModelProperty(value = "任务ID")
    private Long taskId;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "完成数")
    private Integer completedTopicNum;

    @ApiModelProperty(value = "总数")
    private Integer totalTopicNum;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "计划开始时间")
    private Long beginTime;

    @ApiModelProperty(value = "实际结束时间")
    private Long endTime;

    @ApiModelProperty(value = "任务创建时间")
    private Long gmtCreate;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "任务说明")
    private String description;

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

    public Integer getCompletedTopicNum() {
        return completedTopicNum;
    }

    public void setCompletedTopicNum(Integer completedTopicNum) {
        this.completedTopicNum = completedTopicNum;
    }

    public Integer getTotalTopicNum() {
        return totalTopicNum;
    }

    public void setTotalTopicNum(Integer totalTopicNum) {
        this.totalTopicNum = totalTopicNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ReassignTaskVO{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", completedTopicNum=" + completedTopicNum +
                ", totalTopicNum=" + totalTopicNum +
                ", status=" + status +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", gmtCreate=" + gmtCreate +
                ", operator='" + operator + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}