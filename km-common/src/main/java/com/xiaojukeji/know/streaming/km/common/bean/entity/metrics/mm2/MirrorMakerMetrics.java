package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.mm2;

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
public class MirrorMakerMetrics extends BaseMetrics {
    private Long connectClusterId;

    private String connectorName;

    private String connectorNameAndClusterId;

    public MirrorMakerMetrics(Long connectClusterId, String connectorName) {
        super(null);
        this.connectClusterId           = connectClusterId;
        this.connectorName              = connectorName;
        this.connectorNameAndClusterId  = connectorName + "#" + connectClusterId;
    }

    public MirrorMakerMetrics(Long clusterPhyId, Long connectClusterId, String connectorName) {
        super(clusterPhyId);
        this.connectClusterId           = connectClusterId;
        this.connectorName              = connectorName;
        this.connectorNameAndClusterId  = connectorName + "#" + connectClusterId;
    }

    public static MirrorMakerMetrics initWithMetric(Long connectClusterId, String connectorName, String metricName, Float value) {
        MirrorMakerMetrics metrics = new MirrorMakerMetrics(connectClusterId, connectorName);
        metrics.putMetric(metricName, value);
        return metrics;
    }

    @Override
    public String unique() {
        return "KCOR@" + connectClusterId + "@" + connectorName;
    }
}