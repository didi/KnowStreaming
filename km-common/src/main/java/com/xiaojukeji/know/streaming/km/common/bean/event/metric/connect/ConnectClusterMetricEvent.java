package com.xiaojukeji.know.streaming.km.common.bean.event.metric.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.BaseMetricEvent;
import lombok.Getter;

import java.util.List;

/**
 * @author wyb
 * @date 2022/11/7
 */
@Getter
public class ConnectClusterMetricEvent extends BaseMetricEvent {
    private List<ConnectClusterMetrics> connectClusterMetrics;

    public ConnectClusterMetricEvent(Object source,  List<ConnectClusterMetrics> connectClusterMetrics) {
        super(source);
        this.connectClusterMetrics = connectClusterMetrics;
    }
}
