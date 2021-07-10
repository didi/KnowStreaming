package com.xiaojukeji.kafka.manager.monitor.common.entry.vo;

import io.swagger.annotations.ApiModel;

/**
 * @author zengqiao
 * @date 20/5/21
 */
@ApiModel(description = "告警详情")
public class MonitorAlertDetailVO {
    private MonitorAlertVO monitorAlert;

    private MonitorMetricVO monitorMetric;

    public MonitorAlertVO getMonitorAlert() {
        return monitorAlert;
    }

    public void setMonitorAlert(MonitorAlertVO monitorAlert) {
        this.monitorAlert = monitorAlert;
    }

    public MonitorMetricVO getMonitorMetric() {
        return monitorMetric;
    }

    public void setMonitorMetric(MonitorMetricVO monitorMetric) {
        this.monitorMetric = monitorMetric;
    }

    @Override
    public String toString() {
        return "MonitorAlertDetailVO{" +
                "monitorAlert=" + monitorAlert +
                ", monitorMetric=" + monitorMetric +
                '}';
    }
}