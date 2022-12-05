package com.xiaojukeji.know.streaming.km.core.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.po.health.HealthCheckResultPO;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DataBaseDataLocalCache {
    private static final Cache<Long, Map<String, TopicMetrics>> topicLatestMetricsCache = Caffeine.newBuilder()
            .expireAfterWrite(360, TimeUnit.SECONDS)
            .maximumSize(500)
            .build();

    private static final Cache<Long, ClusterMetrics> clusterLatestMetricsCache = Caffeine.newBuilder()
            .expireAfterWrite(180, TimeUnit.SECONDS)
            .maximumSize(500)
            .build();

    private static final Cache<Long, Map<String, List<Partition>>> partitionsCache = Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .maximumSize(500)
            .build();

    private static final Cache<Long, Map<String, List<HealthCheckResultPO>>> healthCheckResultCache = Caffeine.newBuilder()
            .expireAfterWrite(90, TimeUnit.SECONDS)
            .maximumSize(1000)
            .build();

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

    /**************************************************** private method ****************************************************/

    private DataBaseDataLocalCache() {
    }
}
