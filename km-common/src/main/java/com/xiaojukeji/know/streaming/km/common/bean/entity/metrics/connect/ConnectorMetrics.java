package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect;

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
public class ConnectorMetrics extends ConnectClusterMetrics {
    protected String connectorName;

    protected String connectorNameAndClusterId;

    public ConnectorMetrics(Long connectClusterId, String connectorName) {
        super(null, connectClusterId);
        this.connectClusterId           = connectClusterId;
        this.connectorName              = connectorName;
        this.connectorNameAndClusterId  = connectorName + "#" + connectClusterId;
    }

    public ConnectorMetrics(Long connectClusterId, String connectorName, String metricName, Float metricValue) {
        this(connectClusterId, connectorName);
        this.putMetric(metricName, metricValue);
    }

    @Override
    public String unique() {
        return "KCOR@" + connectClusterId + "@" + connectorName;
    }
}