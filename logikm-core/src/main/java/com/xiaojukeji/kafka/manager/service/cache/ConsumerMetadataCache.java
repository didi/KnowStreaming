package com.xiaojukeji.kafka.manager.service.cache;

import com.xiaojukeji.kafka.manager.common.bizenum.OffsetLocationEnum;
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
    private static final Map<Long, ConsumerMetadata> CG_METADATA_IN_ZK_MAP = new ConcurrentHashMap<>();

    private static final Map<Long, ConsumerMetadata> CG_METADATA_IN_BK_MAP = new ConcurrentHashMap<>();

    public static void putConsumerMetadataInZK(Long clusterId, ConsumerMetadata consumerMetadata) {
        if (clusterId == null || consumerMetadata == null) {
            return;
        }
        CG_METADATA_IN_ZK_MAP.put(clusterId, consumerMetadata);
    }

    public static void putConsumerMetadataInBK(Long clusterId, ConsumerMetadata consumerMetadata) {
        if (clusterId == null || consumerMetadata == null) {
            return;
        }
        CG_METADATA_IN_BK_MAP.put(clusterId, consumerMetadata);
    }

    public static Set<String> getGroupInZkMap(Long clusterId) {
        ConsumerMetadata consumerMetadata = CG_METADATA_IN_ZK_MAP.get(clusterId);
        if (consumerMetadata == null) {
            return new HashSet<>();
        }
        return consumerMetadata.getConsumerGroupSet();
    }

    public static Set<String> getGroupInBrokerMap(Long clusterId) {
        ConsumerMetadata consumerMetadata = CG_METADATA_IN_BK_MAP.get(clusterId);
        if (consumerMetadata == null) {
            return new HashSet<>();
        }
        return consumerMetadata.getConsumerGroupSet();
    }

    public static AdminClient.ConsumerGroupSummary getConsumerGroupSummary(Long clusterId, String consumerGroup) {
        ConsumerMetadata consumerMetadata = CG_METADATA_IN_BK_MAP.get(clusterId);
        if (consumerMetadata == null) {
            return null;
        }
        return consumerMetadata.getConsumerGroupSummaryMap().get(consumerGroup);
    }

    public static List<String> getConsumerGroupConsumedTopicList(Long clusterId,
                                                                 String consumerGroup,
                                                                 String location) {
        ConsumerMetadata consumerMetadata = null;
        if(OffsetLocationEnum.ZOOKEEPER.location.equals(location)){
            consumerMetadata = CG_METADATA_IN_ZK_MAP.get(clusterId);
        } else if (OffsetLocationEnum.BROKER.location.equals(location)) {
            consumerMetadata = CG_METADATA_IN_BK_MAP.get(clusterId);
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
        ConsumerMetadata consumerMetadata = CG_METADATA_IN_ZK_MAP.get(clusterId);
        if(consumerMetadata == null){
            return new HashSet<>();
        }
        return consumerMetadata.getTopicNameConsumerGroupMap().getOrDefault(topicName, new HashSet<>());
    }

    public static Set<String> getTopicConsumerGroupInBroker(Long clusterId, String topicName) {
        ConsumerMetadata consumerMetadata = CG_METADATA_IN_BK_MAP.get(clusterId);
        if(consumerMetadata == null){
            return new HashSet<>();
        }
        return consumerMetadata.getTopicNameConsumerGroupMap().getOrDefault(topicName, new HashSet<>());
    }
}
