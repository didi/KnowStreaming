package com.xiaojukeji.kafka.manager.common.entity.metrics;

/**
 * @author zengqiao
 * @date 20/6/18
 */
public class ClusterMetrics extends BaseMetrics {
    private Long clusterId;

    public ClusterMetrics(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    @Override
    public String toString() {
        return "ClusterMetrics{" +
                "clusterId=" + clusterId +
                ", metricsMap=" + metricsMap +
                '}';
    }
}