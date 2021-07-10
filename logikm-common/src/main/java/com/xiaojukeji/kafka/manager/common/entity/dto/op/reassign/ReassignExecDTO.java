package com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.bizenum.TopicReassignActionEnum;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao_cn@163.com
 * @date 19/4/17
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "操作迁移任务")
public class ReassignExecDTO {
    @ApiModelProperty(value = "任务ID")
    private Long taskId;

    @ApiModelProperty(value = "动作[start|modify|cancel]")
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

    @Override
    public String toString() {
        return "ReassignExecDTO{" +
                "taskId=" + taskId +
                ", action='" + action + '\'' +
                ", beginTime=" + beginTime +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(taskId)
                || ValidateUtils.isBlank(action)) {
            return false;
        }

        if (TopicReassignActionEnum.MODIFY.getAction().equals(action)
                && ValidateUtils.isNullOrLessThanZero(beginTime)) {
            return false;
        }
        return true;
    }
}