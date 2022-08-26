package com.xiaojukeji.know.streaming.km.common.bean.event.metric;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ReplicationMetrics;
import lombok.Getter;

import java.util.List;

/**
 * @author didi
 */
@Getter
public class ReplicaMetricEvent extends BaseMetricEvent{

    private List<ReplicationMetrics> replicationMetrics;

    public ReplicaMetricEvent(Object source, List<ReplicationMetrics> replicationMetrics) {
        super( source );
        this.replicationMetrics = replicationMetrics;
    }
}
