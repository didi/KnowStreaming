package com.xiaojukeji.know.streaming.km.common.bean.event.metric;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BrokerMetrics;
import lombok.Getter;

import java.util.List;

/**
 * @author didi
 */
@Getter
public class BrokerMetricEvent extends BaseMetricEvent{

    private final List<BrokerMetrics> brokerMetrics;

    public BrokerMetricEvent(Object source, List<BrokerMetrics> brokerMetrics) {
        super( source );
        this.brokerMetrics = brokerMetrics;
    }
}
