package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zengqiao
 * @date 20/6/17
 */
@Data
@NoArgsConstructor
@ToString
public class ConnectClusterMetrics extends BaseMetrics {
    protected Long     connectClusterId;

    public ConnectClusterMetrics(Long clusterPhyId, Long connectClusterId ){
        super(clusterPhyId);
        this.connectClusterId   = connectClusterId;
    }

    public ConnectClusterMetrics(Long connectClusterId, String metricName, Float metricValue) {
        this(null, connectClusterId);
        this.putMetric(metricName, metricValue);
    }

    @Override
    public String unique() {
        return "KCC@" + clusterPhyId + "@" + connectClusterId;
    }
}