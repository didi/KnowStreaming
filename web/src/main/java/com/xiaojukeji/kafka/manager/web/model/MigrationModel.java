package com.xiaojukeji.kafka.manager.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 迁移Model
 * @author zengqiao_cn@163.com
 * @date 19/4/17
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "MigrationModel", description = "迁移Model")
public class MigrationModel {
    @ApiModelProperty(value = "任务Id")
    private Long taskId;

    @ApiModelProperty(value = "动作[start|modify|cancel]")
    private String action;

    @ApiModelProperty(value = "限流值[会覆盖上一次的限流值]")
    private Long throttle;

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

    public Long getThrottle() {
        return throttle;
    }

    public void setThrottle(Long throttle) {
        this.throttle = throttle;
    }

    @Override
    public String toString() {
        return "MigrationModel{" +
                "taskId=" + taskId +
                ", action='" + action + '\'' +
                ", throttle=" + throttle +
                '}';
    }
}