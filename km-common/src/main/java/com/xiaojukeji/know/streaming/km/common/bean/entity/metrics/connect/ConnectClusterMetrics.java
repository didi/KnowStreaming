package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zengqiao
 * @date 20/6/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ConnectClusterMetrics extends BaseMetrics {
    private Long     connectClusterId;

    public ConnectClusterMetrics(Long clusterPhyId, Long connectClusterId){
        super(clusterPhyId);
        this.connectClusterId   = connectClusterId;
    }

    public static ConnectClusterMetrics initWithMetric(Long connectClusterId, String metric, Float value) {
        ConnectClusterMetrics brokerMetrics = new ConnectClusterMetrics(connectClusterId, connectClusterId);
        brokerMetrics.putMetric(metric, value);
        return brokerMetrics;
    }

    @Override
    public String unique() {
        return "KCC@" + clusterPhyId + "@" + connectClusterId;
    }
}