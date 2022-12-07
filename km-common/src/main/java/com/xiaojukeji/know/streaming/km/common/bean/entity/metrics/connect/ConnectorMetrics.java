package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zengqiao
 * @date 20/6/17
 */
@Data
@NoArgsConstructor
@ToString
public class ConnectorMetrics extends BaseMetrics {
    private Long connectClusterId;

    private String connectorName;

    private String connectorNameAndClusterId;

    public ConnectorMetrics(Long connectClusterId, String connectorName) {
        super(null);
        this.connectClusterId           = connectClusterId;
        this.connectorName              = connectorName;
        this.connectorNameAndClusterId  = connectorName + "#" + connectClusterId;
    }

    public static ConnectorMetrics initWithMetric(Long connectClusterId, String connectorName, String metricName, Float value) {
        ConnectorMetrics metrics = new ConnectorMetrics(connectClusterId, connectorName);
        metrics.putMetric(metricName, value);
        return metrics;
    }

    @Override
    public String unique() {
        return "KCOR@" + connectClusterId + "@" + connectorName;
    }
}