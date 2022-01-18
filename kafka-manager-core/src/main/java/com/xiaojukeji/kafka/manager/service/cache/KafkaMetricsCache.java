package com.xiaojukeji.kafka.manager.service.cache;

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
     * <clusterId, Metrics List>
     */
    private static final Map<Long, Map<String, TopicMetrics>> TOPIC_METRICS_MAP = new ConcurrentHashMap<>();

    private KafkaMetricsCache() {
    }

    public static void putTopicMetricsToCache(Long clusterId, List<TopicMetrics> dataList) {
        if (clusterId == null || dataList == null) {
            return;
        }
        Map<String, TopicMetrics> subMetricsMap = new HashMap<>(dataList.size());
        for (TopicMetrics topicMetrics : dataList) {
            subMetricsMap.put(topicMetrics.getTopicName(), topicMetrics);
        }
        TOPIC_METRICS_MAP.put(clusterId, subMetricsMap);
    }

    public static Map<String, TopicMetrics> getTopicMetricsFromCache(Long clusterId) {
        return TOPIC_METRICS_MAP.getOrDefault(clusterId, Collections.emptyMap());
    }

    public static Map<Long, Map<String, TopicMetrics>> getAllTopicMetricsFromCache() {
        return TOPIC_METRICS_MAP;
    }

    public static TopicMetrics getTopicMetricsFromCache(Long clusterId, String topicName) {
        if (clusterId == null || topicName == null) {
            return null;
        }
        Map<String, TopicMetrics> subMap = TOPIC_METRICS_MAP.getOrDefault(clusterId, Collections.emptyMap());
        return subMap.get(topicName);
    }
}
