package com.xiaojukeji.kafka.manager.web.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * 迁移任务
 * @author zengqiao
 * @date 19/7/13
 */
public class MigrationTaskVO {
    @ApiModelProperty(value = "任务ID")
    private Long taskId;

    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "集群名称")
    private String clusterName;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "任务状态")
    private Integer status;

    @ApiModelProperty(value = "限流值")
    private Long throttle;

    @ApiModelProperty(value = "任务创建时间")
    private Long gmtCreate;

    @ApiModelProperty(value = "操作人")
    private String operator;

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

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getThrottle() {
        return throttle;
    }

    public void setThrottle(Long throttle) {
        this.throttle = throttle;
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

    @Override
    public String toString() {
        return "MigrationTaskVO{" +
                "taskId=" + taskId +
                ", clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", status=" + status +
                ", throttle=" + throttle +
                ", gmtCreate=" + gmtCreate +
                ", operator='" + operator + '\'' +
                '}';
    }
}