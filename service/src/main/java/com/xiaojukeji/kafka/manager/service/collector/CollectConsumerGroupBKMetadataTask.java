package com.xiaojukeji.kafka.manager.service.collector;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.ConsumerMetadata;
import com.xiaojukeji.kafka.manager.service.cache.ConsumerMetadataCache;
import com.xiaojukeji.kafka.manager.service.cache.KafkaClientCache;
import kafka.admin.AdminClient;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.types.SchemaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConversions;

import java.util.*;

/**
 * @author zengqiao
 * @date 19/12/25
 */
public class CollectConsumerGroupBKMetadataTask extends BaseCollectTask {
    private final static Logger logger = LoggerFactory.getLogger(Constant.COLLECTOR_METRICS_LOGGER);

    public CollectConsumerGroupBKMetadataTask(Long clusterId) {
        super(logger, clusterId);
    }

    @Override
    public void collect() {
        // 获取消费组列表
        Set<String> consumerGroupSet = collectConsumerGroup();

        // 获取消费组summary信息
        Map<String, Set<String>> topicNameConsumerGroupMap = new HashMap<>();
        Map<String, AdminClient.ConsumerGroupSummary> consumerGroupSummary = collectConsumerGroupSummary(consumerGroupSet, topicNameConsumerGroupMap);

        // 获取Topic下的消费组
        topicNameConsumerGroupMap = collectTopicAndConsumerGroupMap(consumerGroupSet, topicNameConsumerGroupMap);
        ConsumerMetadataCache.putConsumerMetadataInBK(clusterId, new ConsumerMetadata(consumerGroupSet, topicNameConsumerGroupMap, consumerGroupSummary));
    }

    private Set<String> collectConsumerGroup() {
        try {
            AdminClient adminClient = KafkaClientCache.getAdminClient(clusterId);
            Set<String> consumerGroupSet = new HashSet<>();
            scala.collection.immutable.Map<org.apache.kafka.common.Node, scala.collection.immutable.List<kafka.coordinator.GroupOverview>> brokerGroupMap = adminClient.listAllGroups();
            for (scala.collection.immutable.List<kafka.coordinator.GroupOverview> brokerGroup : JavaConversions.asJavaMap(brokerGroupMap).values()) {
                List<kafka.coordinator.GroupOverview> lists = JavaConversions.asJavaList(brokerGroup);
                for (kafka.coordinator.GroupOverview groupOverview : lists) {
                    String consumerGroup = groupOverview.groupId();
                    if (consumerGroup != null && consumerGroup.contains("#")) {
                        consumerGroup = consumerGroup.split("#", 2)[1];
                    }
                    consumerGroupSet.add(consumerGroup);
                }
            }
            return consumerGroupSet;
        } catch (Exception e) {
            logger.error("collect consumerGroup failed, clusterId:{}.", clusterId, e);
        }
        return new HashSet<>();
    }

    private Map<String, AdminClient.ConsumerGroupSummary> collectConsumerGroupSummary(Set<String> consumerGroupSet, Map<String, Set<String>> topicNameConsumerGroupMap) {
        if (consumerGroupSet == null || consumerGroupSet.isEmpty()) {
            return new HashMap<>();
        }
        AdminClient adminClient = KafkaClientCache.getAdminClient(clusterId);

        Map<String, AdminClient.ConsumerGroupSummary> consumerGroupSummaryMap = new HashMap<>();
        for (String consumerGroup : consumerGroupSet) {
            try {
                AdminClient.ConsumerGroupSummary consumerGroupSummary = adminClient.describeConsumerGroup(consumerGroup);
                if (consumerGroupSummary == null) {
                    continue;
                }
                consumerGroupSummaryMap.put(consumerGroup, consumerGroupSummary);

                java.util.Iterator<scala.collection.immutable.List<AdminClient.ConsumerSummary>> it = JavaConversions.asJavaIterator(consumerGroupSummary.consumers().iterator());
                while (it.hasNext()) {
                    List<AdminClient.ConsumerSummary> consumerSummaryList = JavaConversions.asJavaList(it.next());
                    for (AdminClient.ConsumerSummary consumerSummary: consumerSummaryList) {
                        List<TopicPartition> topicPartitionList = JavaConversions.asJavaList(consumerSummary.assignment());
                        if (topicPartitionList == null) {
                            continue;
                        }
                        for (TopicPartition topicPartition: topicPartitionList) {
                            Set<String> groupSet = topicNameConsumerGroupMap.getOrDefault(topicPartition.topic(), new HashSet<>());
                            groupSet.add(consumerGroup);
                            topicNameConsumerGroupMap.put(topicPartition.topic(), groupSet);
                        }
                    }
                }
            } catch (SchemaException e) {
                logger.error("schemaException exception, clusterId:{} consumerGroup:{}.", clusterId, consumerGroup, e);
            } catch (Exception e) {
                logger.error("collect consumerGroupSummary failed, clusterId:{} consumerGroup:{}.", clusterId, consumerGroup, e);
            }
        }
        return consumerGroupSummaryMap;
    }

    private Map<String, Set<String>> collectTopicAndConsumerGroupMap(Set<String> consumerGroupSet, Map<String, Set<String>> topicNameConsumerGroupMap) {
        if (consumerGroupSet == null || consumerGroupSet.isEmpty()) {
            return new HashMap<>();
        }
        AdminClient adminClient = KafkaClientCache.getAdminClient(clusterId);

        for (String consumerGroup: consumerGroupSet) {
            try {
                Map<TopicPartition, Object> topicPartitionAndOffsetMap = JavaConversions.asJavaMap(adminClient.listGroupOffsets(consumerGroup));
                for (Map.Entry<TopicPartition, Object> entry : topicPartitionAndOffsetMap.entrySet()) {
                    TopicPartition tp = entry.getKey();
                    Set<String> subConsumerGroupSet = topicNameConsumerGroupMap.getOrDefault(tp.topic(), new HashSet<>());
                    subConsumerGroupSet.add(consumerGroup);
                    topicNameConsumerGroupMap.put(tp.topic(), subConsumerGroupSet);
                }
            } catch (Exception e) {
                logger.error("collectTopicAndConsumerGroupMap@ConsumerMetaDataManager, update consumer group failed, clusterId:{} consumerGroup:{}.", clusterId, consumerGroup, e);
            }
        }
        return topicNameConsumerGroupMap;
    }
}