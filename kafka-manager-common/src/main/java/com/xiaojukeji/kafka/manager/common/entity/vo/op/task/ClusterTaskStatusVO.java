package com.xiaojukeji.kafka.manager.common.entity.vo.op.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/27
 */
@ApiModel(value="任务状态详情")
public class ClusterTaskStatusVO {
    @ApiModelProperty(value="任务ID")
    private Long taskId;

    @ApiModelProperty(value="任务状态: 30:运行中(展示暂停), 40:暂停(展示开始), 100:完成(都置灰)")
    private Integer status;

    @ApiModelProperty(value="正处于回滚的状态")
    private Boolean rollback;

    @ApiModelProperty(value="任务总数")
    private Integer sumCount;

    @ApiModelProperty(value="成功总数")
    private Integer successCount;

    @ApiModelProperty(value="失败总数")
    private Integer failedCount;

    @ApiModelProperty(value="执行中总数")
    private Integer runningCount;

    @ApiModelProperty(value="等待总数")
    private Integer waitingCount;

    @ApiModelProperty(value="子任务状态")
    private List<ClusterTaskSubStatusVO> subTaskStatusList;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getRollback() {
        return rollback;
    }

    public void setRollback(Boolean rollback) {
        this.rollback = rollback;
    }

    public Integer getSumCount() {
        return sumCount;
    }

    public void setSumCount(Integer sumCount) {
        this.sumCount = sumCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Integer failedCount) {
        this.failedCount = failedCount;
    }

    public Integer getRunningCount() {
        return runningCount;
    }

    public void setRunningCount(Integer runningCount) {
        this.runningCount = runningCount;
    }

    public Integer getWaitingCount() {
        return waitingCount;
    }

    public void setWaitingCount(Integer waitingCount) {
        this.waitingCount = waitingCount;
    }

    public List<ClusterTaskSubStatusVO> getSubTaskStatusList() {
        return subTaskStatusList;
    }

    public void setSubTaskStatusList(List<ClusterTaskSubStatusVO> subTaskStatusList) {
        this.subTaskStatusList = subTaskStatusList;
    }

    @Override
    public String toString() {
        return "ClusterTaskStatusVO{" +
                "taskId=" + taskId +
                ", status=" + status +
                ", rollback=" + rollback +
                ", sumCount=" + sumCount +
                ", successCount=" + successCount +
                ", failedCount=" + failedCount +
                ", runningCount=" + runningCount +
                ", waitingCount=" + waitingCount +
                ", subTaskStatusList=" + subTaskStatusList +
                '}';
    }
}