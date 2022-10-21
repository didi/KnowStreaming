package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics;

import lombok.Data;
import lombok.ToString;

/**
 * @author zengqiao
 * @date 20/6/17
 */
@Data
@ToString
public class ZookeeperMetrics extends BaseMetrics {
    public ZookeeperMetrics(Long clusterPhyId) {
        super(clusterPhyId);
    }

    public static ZookeeperMetrics initWithMetric(Long clusterPhyId, String metric, Float value) {
        ZookeeperMetrics metrics = new ZookeeperMetrics(clusterPhyId);
        metrics.setClusterPhyId( clusterPhyId );
        metrics.putMetric(metric, value);
        return metrics;
    }

    @Override
    public String unique() {
        return "ZK@" + clusterPhyId;
    }
}