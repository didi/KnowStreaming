package com.xiaojukeji.know.streaming.km.persistence.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.po.health.HealthCheckResultPO;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class DataBaseDataLocalCache {
    @Value(value = "${cache.metric.topic-size:2000}")
    private Long topicLatestMetricsCacheSize;

    @Value(value = "${cache.metric.cluster-size:2000}")
    private Long clusterLatestMetricsCacheSize;

    @Value(value = "${cache.metadata.partition-size:2000}")
    private Long partitionsCacheSize;

    @Value(value = "${cache.metadata.health-check-result-size:10000}")
    private Long healthCheckResultCacheSize;

    @Value(value = "${cache.metadata.ha-topic-size:10000}")
    private Long haTopicCacheSize;

    private static Cache<Long, Map<String, TopicMetrics>> topicLatestMetricsCache;

    private static Cache<Long, ClusterMetrics> clusterLatestMetricsCache;

    private static Cache<Long, Map<String, List<Partition>>> partitionsCache;

    private static Cache<Long, Map<String, List<HealthCheckResultPO>>> healthCheckResultCache;

    private static Cache<String, Boolean> haTopicCache;

    @PostConstruct
    private void init() {
        topicLatestMetricsCache = Caffeine.newBuilder()
                .expireAfterWrite(360, TimeUnit.SECONDS)
                .maximumSize(topicLatestMetricsCacheSize)
                .build();

        clusterLatestMetricsCache = Caffeine.newBuilder()
                .expireAfterWrite(180, TimeUnit.SECONDS)
                .maximumSize(clusterLatestMetricsCacheSize)
                .build();

        partitionsCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .maximumSize(partitionsCacheSize)
                .build();

        healthCheckResultCache = Caffeine.newBuilder()
                .expireAfterWrite(90, TimeUnit.SECONDS)
                .maximumSize(healthCheckResultCacheSize)
                .build();

        haTopicCache = Caffeine.newBuilder()
                .expireAfterWrite(90, TimeUnit.SECONDS)
                .maximumSize(haTopicCacheSize)
                .build();
    }

    public static Map<String, TopicMetrics> getTopicMetrics(Long clusterPhyId) {
        return topicLatestMetricsCache.getIfPresent(clusterPhyId);
    }

    public static void putTopicMetrics(Long clusterPhyId, Map<String, TopicMetrics> metricsMap) {
        topicLatestMetricsCache.put(clusterPhyId, metricsMap);
    }

    public static ClusterMetrics getClusterLatestMetrics(Long clusterPhyId) {
        return clusterLatestMetricsCache.getIfPresent(clusterPhyId);
    }

    public static void putClusterLatestMetrics(Long clusterPhyId, ClusterMetrics metrics) {
        clusterLatestMetricsCache.put(clusterPhyId, metrics);
    }

    public static Map<String, List<Partition>> getPartitions(Long clusterPhyId) {
        return partitionsCache.getIfPresent(clusterPhyId);
    }

    public static void putPartitions(Long clusterPhyId, Map<String, List<Partition>> partitionMap) {
        partitionsCache.put(clusterPhyId, partitionMap);
    }

    public static Map<String, List<HealthCheckResultPO>> getHealthCheckResults(Long clusterId, HealthCheckDimensionEnum dimensionEnum) {
        return healthCheckResultCache.getIfPresent(getHealthCheckCacheKey(clusterId, dimensionEnum.getDimension()));
    }

    public static void putHealthCheckResults(Long cacheKey, Map<String, List<HealthCheckResultPO>> poMap) {
        healthCheckResultCache.put(cacheKey, poMap);
    }

    public static void putHealthCheckResults(Long clusterId, HealthCheckDimensionEnum dimensionEnum, Map<String, List<HealthCheckResultPO>> poMap) {
        healthCheckResultCache.put(getHealthCheckCacheKey(clusterId, dimensionEnum.getDimension()), poMap);
    }

    public static Long getHealthCheckCacheKey(Long clusterId, Integer dimensionCode) {
        return clusterId * HealthCheckDimensionEnum.MAX_VAL.getDimension() + dimensionCode;
    }

    public static void putHaTopic(Long clusterPhyId, String topicName) {
        String key = clusterPhyId + "@" + topicName;
        haTopicCache.put(key, true);
    }

    public static boolean isHaTopic(Long clusterPhyId, String topicName) {
        String key = clusterPhyId + "@" + topicName;
        return haTopicCache.getIfPresent(key) != null;
    }

    /**************************************************** private method ****************************************************/

    private DataBaseDataLocalCache() {
    }
}
