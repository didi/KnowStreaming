package com.xiaojukeji.kafka.manager.common.entity.metrics;

/**
 * @author zengqiao
 * @date 20/6/17
 */
public class BrokerMetrics extends BaseMetrics {
    private Long clusterId;

    private Integer brokerId;

    public BrokerMetrics(Long clusterId, Integer brokerId) {
        super();
        this.clusterId = clusterId;
        this.brokerId = brokerId;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Integer brokerId) {
        this.brokerId = brokerId;
    }

    @Override
    public String toString() {
        return "BrokerMetrics{" +
                "clusterId=" + clusterId +
                ", brokerId=" + brokerId +
                ", metricsMap=" + metricsMap +
                '}';
    }
}