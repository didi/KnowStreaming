package com.xiaojukeji.kafka.manager.monitor.common.entry.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/5/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "监控告警")
public class MonitorSilenceDTO {
    @ApiModelProperty(value = "ID, 修改时传")
    private Long id;

    @ApiModelProperty(value = "告警ID")
    private Long monitorId;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;

    @ApiModelProperty(value = "备注")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
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
        return "MonitorSilenceDTO{" +
                "id=" + id +
                ", monitorId=" + monitorId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", description='" + description + '\'' +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(monitorId)
                || ValidateUtils.isNull(startTime)
                || ValidateUtils.isNull(endTime)
                || ValidateUtils.isNull(description)) {
            return false;
        }
        return true;
    }
}