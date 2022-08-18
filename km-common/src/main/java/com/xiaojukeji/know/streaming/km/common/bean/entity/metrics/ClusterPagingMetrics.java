package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClusterPagingMetrics {
    private Long clusterId;
    private Float sortValue;
    private Map<String, Float> metricValues;
}
