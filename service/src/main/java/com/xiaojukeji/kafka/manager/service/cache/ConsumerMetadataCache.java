package com.xiaojukeji.kafka.manager.service.cache;

import com.xiaojukeji.kafka.manager.common.constant.OffsetStoreLocation;
import com.xiaojukeji.kafka.manager.common.entity.ConsumerMetadata;
import kafka.admin.AdminClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定时任务, 缓存消费组相关信息
 * @author zengqiao
 * @date 2019/05/01
 */
public class ConsumerMetadataCache {
    private static final Map<Long, ConsumerMetadata> ConsumerGroupMetadataInZKMap = new ConcurrentHashMap<>();

    private static final Map<Long, ConsumerMetadata> ConsumerGroupMetadataInBKMap = new ConcurrentHashMap<>();

    public static void putConsumerMetadataInZK(Long clusterId, ConsumerMetadata consumerMetadata) {
        if (clusterId == null || consumerMetadata == null) {
            return;
        }
        ConsumerGroupMetadataInZKMap.put(clusterId, consumerMetadata);
    }

    public static void putConsumerMetadataInBK(Long clusterId, ConsumerMetadata consumerMetadata) {
        if (clusterId == null || consumerMetadata == null) {
            return;
        }
        ConsumerGroupMetadataInBKMap.put(clusterId, consumerMetadata);
    }

    public static Set<String> getGroupInZkMap(Long clusterId) {
        ConsumerMetadata consumerMetadata = ConsumerGroupMetadataInZKMap.get(clusterId);
        if (consumerMetadata == null) {
            return new HashSet<>();
        }
        return consumerMetadata.getConsumerGroupSet();
    }

    public static Set<String> getGroupInBrokerMap(Long clusterId) {
        ConsumerMetadata consumerMetadata = ConsumerGroupMetadataInBKMap.get(clusterId);
        if (consumerMetadata == null) {
            return new HashSet<>();
        }
        return consumerMetadata.getConsumerGroupSet();
    }

    public static AdminClient.ConsumerGroupSummary getConsumerGroupSummary(Long clusterId, String consumerGroup) {
        ConsumerMetadata consumerMetadata = ConsumerGroupMetadataInBKMap.get(clusterId);
        if (consumerMetadata == null) {
            return null;
        }
        return consumerMetadata.getConsumerGroupSummaryMap().get(consumerGroup);
    }

    public static List<String> getConsumerGroupConsumedTopicList(Long clusterId,
                                                                 String location,
                                                                 String consumerGroup) {
        ConsumerMetadata consumerMetadata = null;
        if(OffsetStoreLocation.ZOOKEEPER.getLocation().equals(location)){
            consumerMetadata = ConsumerGroupMetadataInZKMap.get(clusterId);
        } else if (OffsetStoreLocation.BROKER.getLocation().equals(location)) {
            consumerMetadata = ConsumerGroupMetadataInBKMap.get(clusterId);
        }
        if (consumerMetadata == null) {
            return new ArrayList<>();
        }

        List<String> topicNameList =  new ArrayList<>();
        for(Map.Entry<String, Set<String>> entry: consumerMetadata.getTopicNameConsumerGroupMap().entrySet()){
            if(entry.getValue().contains(consumerGroup)){
                topicNameList.add(entry.getKey());
            }
        }
        return topicNameList;
    }

    public static Set<String> getTopicConsumerGroupInZk(Long clusterId, String topicName) {
        ConsumerMetadata consumerMetadata = ConsumerGroupMetadataInZKMap.get(clusterId);
        if(consumerMetadata == null){
            return new HashSet<>();
        }
        return consumerMetadata.getTopicNameConsumerGroupMap().getOrDefault(topicName, new HashSet<>());
    }

    public static Set<String> getTopicConsumerGroupInBroker(Long clusterId, String topicName) {
        ConsumerMetadata consumerMetadata = ConsumerGroupMetadataInBKMap.get(clusterId);
        if(consumerMetadata == null){
            return new HashSet<>();
        }
        return consumerMetadata.getTopicNameConsumerGroupMap().getOrDefault(topicName, new HashSet<>());
    }
}
