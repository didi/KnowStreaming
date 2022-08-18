package com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplicationMetricParam extends MetricParam {

    private Long        clusterPhyId;
    private String      topic;
    private Integer     brokerId;
    private Integer     partitionId;
    private String      metric;
}
