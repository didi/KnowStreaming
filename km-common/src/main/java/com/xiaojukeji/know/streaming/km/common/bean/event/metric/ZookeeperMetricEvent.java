package com.xiaojukeji.know.streaming.km.common.bean.event.metric;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ZookeeperMetrics;
import lombok.Getter;

import java.util.List;

/**
 * @author didi
 */
@Getter
public class ZookeeperMetricEvent extends BaseMetricEvent {

    private final List<ZookeeperMetrics> zookeeperMetrics;

    public ZookeeperMetricEvent(Object source, List<ZookeeperMetrics> zookeeperMetrics) {
        super( source );
        this.zookeeperMetrics = zookeeperMetrics;
    }
}
