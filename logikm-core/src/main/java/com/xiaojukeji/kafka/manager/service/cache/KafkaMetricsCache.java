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
    private static Map<Long, Map<String, TopicMetrics>> TopicMetricsMap = new ConcurrentHashMap<>();

    public static void putTopicMetricsToCache(Long clusterId, List<TopicMetrics> dataList) {
        if (clusterId == null || dataList == null) {
            return;
        }
        Map<String, TopicMetrics> subMetricsMap = new HashMap<>(dataList.size());
        for (TopicMetrics topicMetrics : dataList) {
            subMetricsMap.put(topicMetrics.getTopicName(), topicMetrics);
        }
        TopicMetricsMap.put(clusterId, subMetricsMap);
    }

    public static Map<String, TopicMetrics> getTopicMetricsFromCache(Long clusterId) {
        return TopicMetricsMap.getOrDefault(clusterId, Collections.emptyMap());
    }

    public static Map<Long, Map<String, TopicMetrics>> getAllTopicMetricsFromCache() {
        return TopicMetricsMap;
    }

    public static TopicMetrics getTopicMetricsFromCache(Long clusterId, String topicName) {
        if (clusterId == null || topicName == null) {
            return null;
        }
        Map<String, TopicMetrics> subMap = TopicMetricsMap.getOrDefault(clusterId, Collections.emptyMap());
        return subMap.get(topicName);
    }
}
