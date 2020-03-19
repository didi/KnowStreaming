package com.xiaojukeji.kafka.manager.service.cache;

import com.xiaojukeji.kafka.manager.common.entity.ConsumerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存Metrics数据
 * @author zengqiao
 * @date 2019-04-30
 */
public class KafkaMetricsCache {
    /**
     * <clusterId, <updateTimestamp, Metrics List>>
     */
    private static Map<Long, Map.Entry<Long, List<TopicMetrics>>> TopicMetricsMap = new ConcurrentHashMap<>();

    /**
     * <clusterId, <updateTimestamp, Metrics List>>
     */
    private static Map<Long, Map.Entry<Long, List<BrokerMetrics>>> BrokerMetricsMap = new ConcurrentHashMap<>();

    /**
     * <clusterId, <updateTimestamp, Metrics List>>
     */
    private static Map<Long, Map.Entry<Long, List<ConsumerMetrics>>> ConsumerMetricsMap = new ConcurrentHashMap<>();

    public static void putTopicMetricsToCache(Long clusterId, List<TopicMetrics> metricsList) {
        if (clusterId == null || metricsList == null) {
            return;
        }
        TopicMetricsMap.put(clusterId, new AbstractMap.SimpleEntry<>(System.currentTimeMillis(), metricsList));
    }

    public static List<TopicMetrics> getTopicMetricsFromCache(Long clusterId) {
        Map.Entry<Long, List<TopicMetrics>> entry = TopicMetricsMap.get(clusterId);
        if (entry == null) {
            return new ArrayList<>();
        }
        return entry.getValue();
    }

    public static Set<Long> getTopicMetricsClusterIdSet() {
        return TopicMetricsMap.keySet();
    }

    public static void putBrokerMetricsToCache(Long clusterId, List<BrokerMetrics> metricsList) {
        if (clusterId == null || metricsList == null) {
            return;
        }
        BrokerMetricsMap.put(clusterId, new AbstractMap.SimpleEntry<>(System.currentTimeMillis(), metricsList));
    }

    public static List<BrokerMetrics> getBrokerMetricsFromCache(Long clusterId) {
        Map.Entry<Long, List<BrokerMetrics>> entry = BrokerMetricsMap.get(clusterId);
        if (entry == null) {
            return new ArrayList<>();
        }
        return entry.getValue();
    }

    public static Set<Long> getBrokerMetricsClusterIdSet() {
        return BrokerMetricsMap.keySet();
    }

    public static void putConsumerMetricsToCache(Long clusterId, List<ConsumerMetrics> metricsList) {
        if (clusterId == null || metricsList == null) {
            return;
        }
        ConsumerMetricsMap.put(clusterId, new AbstractMap.SimpleEntry<>(System.currentTimeMillis(), metricsList));
    }

    public static List<ConsumerMetrics> getConsumerMetricsFromCache(Long clusterId) {
        Map.Entry<Long, List<ConsumerMetrics>> entry = ConsumerMetricsMap.get(clusterId);
        if (entry == null) {
            return new ArrayList<>();
        }
        return entry.getValue();
    }

    public static Set<Long> getConsumerMetricsClusterIdSet() {
        return ConsumerMetricsMap.keySet();
    }
}
