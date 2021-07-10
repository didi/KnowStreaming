package com.xiaojukeji.kafka.manager.kcm.common.entry.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/27
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description="集群操作")
public class ClusterTaskActionDTO {
    @ApiModelProperty(value="任务ID")
    private Long taskId;

    @ApiModelProperty(value="动作")
    private String action;

    @ApiModelProperty(value="主机")
    private String hostname;

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

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String toString() {
        return "ClusterTaskActionDTO{" +
                "taskId=" + taskId +
                ", action='" + action + '\'' +
                ", hostname='" + hostname + '\'' +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(taskId)
                || ValidateUtils.isBlank(action)) {
            return false;
        }
        return true;
    }
}