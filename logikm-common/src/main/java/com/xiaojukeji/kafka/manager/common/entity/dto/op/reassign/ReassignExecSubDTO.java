package com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/6/11
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "操作迁移子任务")
public class ReassignExecSubDTO {
    @ApiModelProperty(value = "子任务ID")
    private Long subTaskId;

    @ApiModelProperty(value = "动作[modify]")
    private String action;

    @ApiModelProperty(value = "当前限流值(B/s), 完成之前可修改")
    private Long throttle;

    @ApiModelProperty(value = "限流值上限(B/s), 完成之前可修改")
    private Long maxThrottle;

    @ApiModelProperty(value = "限流值下限(B/s), 完成之前可修改")
    private Long minThrottle;

    public Long getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(Long subTaskId) {
        this.subTaskId = subTaskId;
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

    public Long getMaxThrottle() {
        return maxThrottle;
    }

    public void setMaxThrottle(Long maxThrottle) {
        this.maxThrottle = maxThrottle;
    }

    public Long getMinThrottle() {
        return minThrottle;
    }

    public void setMinThrottle(Long minThrottle) {
        this.minThrottle = minThrottle;
    }

    @Override
    public String toString() {
        return "ReassignExecSubDTO{" +
                "subTaskId=" + subTaskId +
                ", action='" + action + '\'' +
                ", throttle=" + throttle +
                ", maxThrottle=" + maxThrottle +
                ", minThrottle=" + minThrottle +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(subTaskId)
                || ValidateUtils.isBlank(action)
                || ValidateUtils.isNullOrLessThanZero(throttle)
                || ValidateUtils.isNullOrLessThanZero(maxThrottle)
                || ValidateUtils.isNullOrLessThanZero(minThrottle)
                || maxThrottle < throttle || throttle < minThrottle) {
            return false;
        }
        return true;
    }
}