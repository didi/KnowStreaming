package com.xiaojukeji.kafka.manager.monitor.common.monitor;

import com.xiaojukeji.kafka.manager.monitor.common.entry.Alert;
import com.xiaojukeji.kafka.manager.monitor.common.entry.Metric;

/**
 * @author zengqiao
 * @date 20/5/22
 */
public class MonitorAlertDetail {
    private Alert alert;

    private Metric metric;

    public MonitorAlertDetail(Alert alert, Metric metric) {
        this.alert = alert;
        this.metric = metric;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    @Override
    public String toString() {
        return "MonitorAlertDetail{" +
                "alert=" + alert +
                ", metric=" + metric +
                '}';
    }
}