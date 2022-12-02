package com.xiaojukeji.know.streaming.km.common.bean.event.metric;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import lombok.Getter;

import java.util.List;

/**
 * @author didi
 */
@Getter
public class ClusterMetricEvent extends BaseMetricEvent{

    private final List<ClusterMetrics> clusterMetrics;

    public ClusterMetricEvent(Object source, List<ClusterMetrics> clusterMetrics) {
        super( source );
        this.clusterMetrics = clusterMetrics;
    }
}
