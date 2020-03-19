package com.xiaojukeji.kafka.manager.service.collector;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.ConsumerMetadata;
import com.xiaojukeji.kafka.manager.common.utils.zk.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.service.cache.ConsumerMetadataCache;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.common.utils.zk.ZkPathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author zengqiao
 * @date 19/12/25
 */
public class CollectConsumerGroupZKMetadataTask extends BaseCollectTask {
    private final static Logger logger = LoggerFactory.getLogger(Constant.COLLECTOR_METRICS_LOGGER);

    public CollectConsumerGroupZKMetadataTask(Long clusterId) {
        super(logger, clusterId);
    }

    @Override
    public void collect() {
        Set<String> consumerGroupSet = collectConsumerGroup();
        Map<String, Set<String>> topicNameConsumerGroupMap = collectTopicAndConsumerGroupMap(consumerGroupSet);
        ConsumerMetadataCache.putConsumerMetadataInZK(clusterId, new ConsumerMetadata(consumerGroupSet, topicNameConsumerGroupMap, new HashMap<>()));
    }

    private Set<String> collectConsumerGroup() {
        try {
            ZkConfigImpl zkConfigImpl = ClusterMetadataManager.getZKConfig(clusterId);
            List<String> consumerGroupList = zkConfigImpl.getChildren(ZkPathUtil.CONSUMER_ROOT_NODE);
            if (consumerGroupList == null) {
                return new HashSet<>();
            }
            return new HashSet<>(consumerGroupList);
        } catch (Exception e) {
            logger.error("collect consumerGroup failed, clusterId:{}.", clusterId, e);
        }
        return new HashSet<>();
    }

    private Map<String, Set<String>> collectTopicAndConsumerGroupMap(Set<String> consumerGroupSet) {
        ZkConfigImpl zkConfigImpl = ClusterMetadataManager.getZKConfig(clusterId);
        Map<String, Set<String>> topicNameConsumerGroupMap = new HashMap<>();
        for (String consumerGroup: consumerGroupSet) {
            try {
                List<String> topicNameList = zkConfigImpl.getChildren(ZkPathUtil.getConsumerGroupOffsetRoot(consumerGroup));
                if (topicNameList == null || topicNameList.isEmpty()) {
                    continue;
                }

                for (String topicName: topicNameList) {
                    Set<String> subConsumerGroupSet = topicNameConsumerGroupMap.getOrDefault(topicName, new HashSet<>());
                    subConsumerGroupSet.add(consumerGroup);
                    topicNameConsumerGroupMap.put(topicName, subConsumerGroupSet);
                }
            } catch (Exception e) {
                logger.error("collect topicName and consumerGroup failed, clusterId:{} consumerGroup:{}.", clusterId, consumerGroup, e);
            }
        }
        return topicNameConsumerGroupMap;
    }
}