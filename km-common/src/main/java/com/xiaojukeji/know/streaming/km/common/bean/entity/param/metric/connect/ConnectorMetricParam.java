package com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.MetricParam;
import com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectorTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wyb
 * @date 2022/11/2
 */
@Data
@NoArgsConstructor
public class ConnectorMetricParam extends MetricParam {
    private Long connectClusterId;

    private String connectorName;

    private String metricName;

    private ConnectorTypeEnum connectorType;

    public ConnectorMetricParam(Long connectClusterId, String connectorName, String metricName, ConnectorTypeEnum connectorType) {
        this.connectClusterId = connectClusterId;
        this.connectorName = connectorName;
        this.metricName = metricName;
        this.connectorType = connectorType;
    }
}
