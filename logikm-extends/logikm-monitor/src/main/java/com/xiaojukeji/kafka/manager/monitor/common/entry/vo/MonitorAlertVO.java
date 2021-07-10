package com.xiaojukeji.kafka.manager.monitor.common.entry.vo;

import com.xiaojukeji.kafka.manager.monitor.common.entry.dto.MetricPoint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/18
 */
@ApiModel(description = "告警信息")
public class MonitorAlertVO {
    @ApiModelProperty(value = "告警ID")
    private Long alertId;

    @ApiModelProperty(value = "监控ID")
    private Long monitorId;

    @ApiModelProperty(value = "监控名称")
    private String monitorName;

    @ApiModelProperty(value = "监控级别")
    private Integer monitorPriority;

    @ApiModelProperty(value = "告警状态")
    private Integer alertStatus;

    @ApiModelProperty(value = "告警开始时间")
    private Long startTime;

    @ApiModelProperty(value = "告警结束时间")
    private Long endTime;

    @ApiModelProperty(value = "告警的指标")
    private String metric;

    @ApiModelProperty(value = "触发值")
    private Double value;

    @ApiModelProperty(value = "现场值")
    private List<MetricPoint> points;

    @ApiModelProperty(value = "告警组")
    private List<String> groups;

    @ApiModelProperty(value = "表达式")
    private String info;

    public Long getAlertId() {
        return alertId;
    }

    public void setAlertId(Long alertId) {
        this.alertId = alertId;
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

    public Integer getMonitorPriority() {
        return monitorPriority;
    }

    public void setMonitorPriority(Integer monitorPriority) {
        this.monitorPriority = monitorPriority;
    }

    public Integer getAlertStatus() {
        return alertStatus;
    }

    public void setAlertStatus(Integer alertStatus) {
        this.alertStatus = alertStatus;
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

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public List<MetricPoint> getPoints() {
        return points;
    }

    public void setPoints(List<MetricPoint> points) {
        this.points = points;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "MonitorAlertVO{" +
                "alertId=" + alertId +
                ", monitorId=" + monitorId +
                ", monitorName='" + monitorName + '\'' +
                ", monitorPriority=" + monitorPriority +
                ", alertStatus=" + alertStatus +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", metric='" + metric + '\'' +
                ", value=" + value +
                ", points=" + points +
                ", groups=" + groups +
                ", info='" + info + '\'' +
                '}';
    }
}