package com.xiaojukeji.know.streaming.km.rebalance.algorithm.metric;

/**
 * @author leewei
 * @date 2022/4/29
 */
public interface MetricStore {
    Metrics getMetrics(String clusterName, int beforeSeconds);
}
