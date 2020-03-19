package com.xiaojukeji.kafka.manager.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 迁移详细信息
 * @author zengqiao
 * @date 19/4/16
 */
@ApiModel(value = "迁移详情")
public class MigrationDetailVO {
    @ApiModelProperty(value = "任务ID")
    private Long taskId;

    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "限流信息")
    private Long throttle;

    @ApiModelProperty(value = "任务状态")
    private Integer status;

    @ApiModelProperty(value = "迁移分区分配结果")
    private Map<Integer, List<Integer>> reassignmentMap;

    @ApiModelProperty(value = "迁移进度<partitionId, status>")
    private Map<Integer, Integer> migrationStatus;

    @ApiModelProperty(value = "任务创建时间")
    private Date gmtCreate;

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

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Long getThrottle() {
        return throttle;
    }

    public void setThrottle(Long throttle) {
        this.throttle = throttle;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Map<Integer, List<Integer>> getReassignmentMap() {
        return reassignmentMap;
    }

    public void setReassignmentMap(Map<Integer, List<Integer>> reassignmentMap) {
        this.reassignmentMap = reassignmentMap;
    }

    public Map<Integer, Integer> getMigrationStatus() {
        return migrationStatus;
    }

    public void setMigrationStatus(Map<Integer, Integer> migrationStatus) {
        this.migrationStatus = migrationStatus;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String toString() {
        return "MigrationDetailVO{" +
                "taskId=" + taskId +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", throttle=" + throttle +
                ", status=" + status +
                ", reassignmentMap=" + reassignmentMap +
                ", migrationStatus=" + migrationStatus +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}