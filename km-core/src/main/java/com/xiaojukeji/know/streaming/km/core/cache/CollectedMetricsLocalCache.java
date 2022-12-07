package com.xiaojukeji.know.streaming.km.core.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.PartitionMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CollectedMetricsLocalCache {
    private static final Cache<String, Float> brokerMetricsCache = Caffeine.newBuilder()
            .expireAfterWrite(90, TimeUnit.SECONDS)
            .maximumSize(10000)
            .build();

    private static final Cache<String, List<TopicMetrics>> topicMetricsCache = Caffeine.newBuilder()
            .expireAfterWrite(90, TimeUnit.SECONDS)
            .maximumSize(10000)
            .build();

    private static final Cache<String, List<PartitionMetrics>> partitionMetricsCache = Caffeine.newBuilder()
            .expireAfterWrite(90, TimeUnit.SECONDS)
            .maximumSize(10000)
            .build();

    private static final Cache<String, Float> connectClusterMetricsCache = Caffeine.newBuilder()
            .expireAfterWrite(90, TimeUnit.SECONDS)
            .maximumSize(10000)
            .build();

    private static final Cache<String, Float> connectorMetricsCache = Caffeine.newBuilder()
            .expireAfterWrite(90, TimeUnit.SECONDS)
            .maximumSize(10000)
            .build();


    public static Float getBrokerMetrics(String brokerMetricKey) {
        return brokerMetricsCache.getIfPresent(brokerMetricKey);
    }

    public static void putBrokerMetrics(String brokerMetricKey, Float value) {
        if (value == null) {
            return;
        }

        brokerMetricsCache.put(brokerMetricKey, value);
    }

    public static List<TopicMetrics> getTopicMetrics(String topicMetricKey) {
        return topicMetricsCache.getIfPresent(topicMetricKey);
    }

    public static void putTopicMetrics(String topicMetricKey, List<TopicMetrics> metricsList) {
        if (metricsList == null) {
            return;
        }

        topicMetricsCache.put(topicMetricKey, metricsList);
    }

    public static List<PartitionMetrics> getPartitionMetricsList(String partitionMetricKey) {
        return partitionMetricsCache.getIfPresent(partitionMetricKey);
    }

    public static void putPartitionMetricsList(String partitionMetricsKey, List<PartitionMetrics> metricsList) {
        if (metricsList == null) {
            return;
        }
        partitionMetricsCache.put(partitionMetricsKey, metricsList);
    }

    public static void putConnectClusterMetrics(String connectClusterMetricKey, Float value) {
        if (value == null) {
            return;
        }
        connectClusterMetricsCache.put(connectClusterMetricKey, value);
    }

    public static Float getConnectClusterMetrics(String connectClusterMetricKey) {
        return connectClusterMetricsCache.getIfPresent(connectClusterMetricKey);
    }

    public static void putConnectorMetrics(String connectClusterMetricKey, Float value) {
        if (value == null) {
            return;
        }
        connectorMetricsCache.put(connectClusterMetricKey, value);
    }

    public static Float getConnectorMetrics(String connectClusterMetricKey) {
        return connectorMetricsCache.getIfPresent(connectClusterMetricKey);
    }

    public static String genBrokerMetricKey(Long clusterPhyId, Integer brokerId, String metricName) {
        return clusterPhyId + "@" + brokerId + "@" + metricName;
    }

    public static String genClusterTopicMetricKey(Long clusterPhyId, String topicName, String metricName) {
        return clusterPhyId + "@" + topicName + "@" + metricName;
    }

    public static String genReplicaMetricCacheKey(Long clusterPhyId, Integer brokerId, String topicName, Integer partitionId, String metricName) {
        return clusterPhyId + "@" + brokerId + "@" + topicName + "@" + partitionId + "@" + metricName;
    }

    public static String genConnectClusterMetricCacheKey(Long connectClusterId, String metricName) {
        return connectClusterId + "@" + metricName;
    }

    public static String genConnectorMetricCacheKey(Long connectClusterId, String connectorName, String metricName) {
        return connectClusterId + "@" + connectorName + '@' + metricName;
    }

    /**************************************************** private method ****************************************************/

    private CollectedMetricsLocalCache() {
    }
}
