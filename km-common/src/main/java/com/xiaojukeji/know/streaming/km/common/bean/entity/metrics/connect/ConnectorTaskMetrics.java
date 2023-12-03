package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author wyb
 * @date 2022/11/4
 */
@Data
@NoArgsConstructor
@ToString
public class ConnectorTaskMetrics extends ConnectorMetrics {
    private Integer taskId;

    public ConnectorTaskMetrics(Long connectClusterId, String connectorName, Integer taskId) {
        this.connectClusterId   = connectClusterId;
        this.connectorName      = connectorName;
        this.taskId             = taskId;
    }

    public ConnectorTaskMetrics(Long connectClusterId, String connectorName, Integer taskId, String metricName, Float metricValue) {
        this(connectClusterId, connectorName, taskId);
        this.putMetric(metricName, metricValue);
    }

    @Override
    public String unique() {
        return "KCORT@" + connectClusterId + "@" + connectorName + "@" + taskId;
    }
}
