package com.xiaojukeji.kafka.manager.common.entity.vo.op.task;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/21
 */
public class ClusterTaskVO {
    @ApiModelProperty(value="任务Id")
    private Long taskId;

    @ApiModelProperty(value="集群ID")
    private Long clusterId;

    @ApiModelProperty(value="集群名称")
    private String clusterName;

    @ApiModelProperty(value="任务类型")
    private String taskType;

    @ApiModelProperty(value="状态")
    private Integer status;

    @ApiModelProperty(value="操作人")
    private String operator;

    @ApiModelProperty(value="创建时间")
    private Long createTime;

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

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ClusterTaskVO{" +
                "taskId=" + taskId +
                ", clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", taskType='" + taskType + '\'' +
                ", status=" + status +
                ", operator='" + operator + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}