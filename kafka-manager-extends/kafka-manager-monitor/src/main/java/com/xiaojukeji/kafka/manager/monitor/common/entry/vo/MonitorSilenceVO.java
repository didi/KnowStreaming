package com.xiaojukeji.kafka.manager.monitor.common.entry.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/5/18
 */
@ApiModel(description = "告警屏蔽")
public class MonitorSilenceVO {
    @ApiModelProperty(value = "屏蔽ID")
    private Long silenceId;

    @ApiModelProperty(value = "告警ID")
    private Long monitorId;

    @ApiModelProperty(value = "监控名称")
    private String monitorName;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;

    @ApiModelProperty(value = "备注")
    private String description;

    public Long getSilenceId() {
        return silenceId;
    }

    public void setSilenceId(Long silenceId) {
        this.silenceId = silenceId;
    }

    public Long getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
    }

    public String getMonitorName() {
        return monitorName;
    }

    public void setMonitorName(String monitorName) {
        this.monitorName = monitorName;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "MonitorSilenceVO{" +
                "silenceId=" + silenceId +
                ", monitorId=" + monitorId +
                ", monitorName='" + monitorName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", description='" + description + '\'' +
                '}';
    }
}