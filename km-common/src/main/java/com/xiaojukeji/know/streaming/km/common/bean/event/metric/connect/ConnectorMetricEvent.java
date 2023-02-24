package com.xiaojukeji.know.streaming.km.common.bean.event.metric.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectorMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.BaseMetricEvent;
import lombok.Getter;

import java.util.List;

/**
 * @author wyb
 * @date 2022/11/7
 */
@Getter
public class ConnectorMetricEvent extends BaseMetricEvent {
    private List<ConnectorMetrics> connectorMetricsList;

    public ConnectorMetricEvent(Object source, List<ConnectorMetrics> connectorMetricsList) {
        super(source);
        this.connectorMetricsList = connectorMetricsList;
    }
}
