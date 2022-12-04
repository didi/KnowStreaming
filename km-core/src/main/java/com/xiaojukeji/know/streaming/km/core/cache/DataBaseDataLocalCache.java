package com.xiaojukeji.know.streaming.km.core.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DataBaseDataLocalCache {
    private static final Cache<Long, ClusterMetrics> clusterLatestMetricsCache = Caffeine.newBuilder()
            .expireAfterWrite(180, TimeUnit.SECONDS)
            .maximumSize(500)
            .build();

    private static final Cache<Long, Map<String, List<Partition>>> partitionsCache = Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .maximumSize(500)
            .build();

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

    /**************************************************** private method ****************************************************/

    private DataBaseDataLocalCache() {
    }
}
