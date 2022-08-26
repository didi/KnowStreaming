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
public class TopicMetricParam extends MetricParam {

    private Long    clusterId;
    private String  topic;
    private String  metric;
}
