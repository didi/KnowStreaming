package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
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
public class ConnectorTaskMetrics extends BaseMetrics {
    private Long connectClusterId;

    private String connectorName;

    private Integer taskId;

    public ConnectorTaskMetrics(Long connectClusterId, String connectorName, Integer taskId) {
        this.connectClusterId   = connectClusterId;
        this.connectorName      = connectorName;
        this.taskId             = taskId;
    }

    public static ConnectorTaskMetrics initWithMetric(Long connectClusterId, String connectorName, Integer taskId, String metricName, Float value) {
        ConnectorTaskMetrics metrics = new ConnectorTaskMetrics(connectClusterId, connectorName, taskId);
        metrics.putMetric(metricName,value);
        return metrics;
    }

    @Override
    public String unique() {
        return "KCOR@" + connectClusterId + "@" + connectorName + "@" + taskId;
    }
}
