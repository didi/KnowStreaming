package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author wyb
 * @date 2022/11/2
 */
@Data
@NoArgsConstructor
@ToString
public class ConnectWorkerMetrics extends ConnectClusterMetrics {
    private String workerId;

    public ConnectWorkerMetrics(Long connectClusterId, String workerId, String metricName, Float metricValue) {
        super(null, connectClusterId);
        this.workerId = workerId;
        this.putMetric(metricName, metricValue);
    }

    @Override
    public String unique() {
        return "KCW@" + clusterPhyId + "@" + connectClusterId + "@" + workerId;
    }
}
