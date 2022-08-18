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
public class BrokerMetricParam extends MetricParam {

    private Long        clusterId;

    private Integer     brokerId;

    private String      metric;
}
