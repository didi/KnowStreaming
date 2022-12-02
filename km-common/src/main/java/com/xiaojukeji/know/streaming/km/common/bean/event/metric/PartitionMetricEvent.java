package com.xiaojukeji.know.streaming.km.common.bean.event.metric;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.PartitionMetrics;
import lombok.Getter;

import java.util.List;

/**
 * @author didi
 */
@Getter
public class PartitionMetricEvent extends BaseMetricEvent{

    private final List<PartitionMetrics> partitionMetrics;

    public PartitionMetricEvent(Object source, List<PartitionMetrics> partitionMetrics) {
        super( source );
        this.partitionMetrics = partitionMetrics;
    }
}
