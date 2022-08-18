package com.xiaojukeji.know.streaming.km.core.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.PartitionMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CollectMetricsLocalCache {
    private static final Cache<String, Float> brokerMetricsCache = Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .maximumSize(2000)
            .build();

    private static final Cache<String, List<TopicMetrics>> topicMetricsCache = Caffeine.newBuilder()
            .expireAfterWrite(90, TimeUnit.SECONDS)
            .maximumSize(5000)
            .build();

    private static final Cache<String, List<PartitionMetrics>> partitionMetricsCache = Caffeine.newBuilder()
            .expireAfterWrite(90, TimeUnit.SECONDS)
            .maximumSize(10000)
            .build();

    private static final Cache<String, Float> replicaMetricsValueCache = Caffeine.newBuilder()
            .expireAfterWrite(90, TimeUnit.SECONDS)
            .maximumSize(20000)
            .build();

    public static Float getBrokerMetrics(Long clusterPhyId, Integer brokerId, String metricName) {
        return brokerMetricsCache.getIfPresent(CollectMetricsLocalCache.genBrokerMetricKey(clusterPhyId, brokerId, metricName));
    }

    public static void putBrokerMetrics(Long clusterPhyId, Integer brokerId, String metricName, Float value) {
        if (value == null) {
            return;
        }
        brokerMetricsCache.put(CollectMetricsLocalCache.genBrokerMetricKey(clusterPhyId, brokerId, metricName), value);
    }

    public static List<TopicMetrics> getTopicMetrics(Long clusterPhyId, String topicName, String metricName) {
        return topicMetricsCache.getIfPresent(CollectMetricsLocalCache.genClusterTopicMetricKey(clusterPhyId, topicName, metricName));
    }

    public static void putTopicMetrics(Long clusterPhyId, String topicName, String metricName, List<TopicMetrics> metricsList) {
        if (metricsList == null) {
            return;
        }
        topicMetricsCache.put(CollectMetricsLocalCache.genClusterTopicMetricKey(clusterPhyId, topicName, metricName), metricsList);
    }

    public static List<PartitionMetrics> getPartitionMetricsList(Long clusterPhyId, String topicName, String metricName) {
        return partitionMetricsCache.getIfPresent(CollectMetricsLocalCache.genClusterTopicMetricKey(clusterPhyId, topicName, metricName));
    }

    public static void putPartitionMetricsList(Long clusterPhyId, String topicName, String metricName, List<PartitionMetrics> metricsList) {
        if (metricsList == null) {
            return;
        }
        partitionMetricsCache.put(CollectMetricsLocalCache.genClusterTopicMetricKey(clusterPhyId, topicName, metricName), metricsList);
    }

    public static Float getReplicaMetrics(Long clusterPhyId, Integer brokerId, String topicName, Integer partitionId, String metricName) {
        return replicaMetricsValueCache.getIfPresent(CollectMetricsLocalCache.genReplicaMetricCacheKey(clusterPhyId, brokerId, topicName, partitionId, metricName));
    }

    public static void putReplicaMetrics(Long clusterPhyId, Integer brokerId, String topicName, Integer partitionId, String metricName, Float value) {
        if (value == null) {
            return;
        }
        replicaMetricsValueCache.put(CollectMetricsLocalCache.genReplicaMetricCacheKey(clusterPhyId, brokerId, topicName, partitionId, metricName), value);
    }


    /**************************************************** private method ****************************************************/


    private static String genBrokerMetricKey(Long clusterPhyId, Integer brokerId, String metricName) {
        return clusterPhyId + "@" + brokerId + "@" + metricName;
    }

    private static String genClusterTopicMetricKey(Long clusterPhyId, String topicName, String metricName) {
        return clusterPhyId + "@" + topicName + "@" + metricName;
    }

    private static String genReplicaMetricCacheKey(Long clusterPhyId, Integer brokerId, String topicName, Integer partitionId, String metricName) {
        return clusterPhyId + "@" + brokerId + "@" + topicName + "@" + partitionId + "@" + metricName;
    }
}
