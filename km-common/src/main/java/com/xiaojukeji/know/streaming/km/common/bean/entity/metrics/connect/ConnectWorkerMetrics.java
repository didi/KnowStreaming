package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author wyb
 * @date 2022/11/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ConnectWorkerMetrics extends BaseMetrics {

    private Long connectClusterId;

    private String workerId;

    public static ConnectWorkerMetrics initWithMetric(Long connectClusterId, String workerId, String metric, Float value) {
        ConnectWorkerMetrics connectWorkerMetrics = new ConnectWorkerMetrics();
        connectWorkerMetrics.setConnectClusterId(connectClusterId);
        connectWorkerMetrics.setWorkerId(workerId);
        connectWorkerMetrics.putMetric(metric, value);
        return connectWorkerMetrics;
    }

    @Override
    public String unique() {
        return "KCC@" + clusterPhyId + "@" + connectClusterId + "@" + workerId;
    }
}
