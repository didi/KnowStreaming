package com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.MetricParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wyb
 * @date 2022/11/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectClusterMetricParam extends MetricParam {

    private Long connectClusterId;

    private String metric;

}
