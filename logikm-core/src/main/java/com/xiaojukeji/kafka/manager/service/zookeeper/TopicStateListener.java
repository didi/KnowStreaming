package com.xiaojukeji.kafka.manager.service.zookeeper;

import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.PartitionMap;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.zookeeper.StateChangeListener;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkPathUtil;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.ThreadPool;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author zengqiao
 * @date 20/5/14
 */
public class TopicStateListener implements StateChangeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopicStateListener.class);

    private Long clusterId;

    private ZkConfigImpl zkConfig;

    public TopicStateListener(Long clusterId, ZkConfigImpl zkConfig) {
        this.clusterId = clusterId;
        this.zkConfig = zkConfig;
    }

    @Override
    public void init() {
        try {
            List<String> topicNameList = zkConfig.getChildren(ZkPathUtil.BROKER_TOPICS_ROOT);
            FutureTask[] taskList = new FutureTask[topicNameList.size()];
            for (int i = 0; i < topicNameList.size(); i++) {
                String topicName = topicNameList.get(i);
                taskList[i] = new FutureTask(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        processTopicAdded(topicName);
                        return null;
                    }
                });
                ThreadPool.submitCollectMetricsTask(taskList[i]);
            }
        } catch (Exception e) {
            LOGGER.error("init topics metadata failed, clusterId:{}.", clusterId, e);
        }
    }

    @Override
    public void onChange(State state, String path) {
        try {
            String topicName = ZkPathUtil.parseLastPartFromZkPath(path);
            switch (state) {
                case CHILD_ADDED:
                case CHILD_UPDATED:
                    processTopicAdded(topicName);
                    break;
                case CHILD_DELETED:
                    processTopicDelete(topicName);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("process topic state change failed, clusterId:{} state:{} path:{}.",
                    clusterId, state, path, e);
        }
    }

    private void processTopicDelete(String topicName) {
        LOGGER.warn("delete topic, clusterId:{} topicName:{}.", clusterId, topicName);
        PhysicalClusterMetadataManager.removeTopicMetadata(clusterId, topicName);
    }

    private void processTopicAdded(String topicName) {
        LOGGER.info("add topic, clusterId:{} topicName:{}.", clusterId, topicName);

        TopicMetadata topicMetadata = new TopicMetadata();
        try {
            topicMetadata.setTopic(topicName);
            Stat stat = zkConfig.getNodeStat(ZkPathUtil.getBrokerTopicRoot(topicName));
            topicMetadata.setCreateTime(stat.getCtime());
            topicMetadata.setModifyTime(stat.getMtime());

            PartitionMap partitionMap = zkConfig.get(ZkPathUtil.getBrokerTopicRoot(topicName), PartitionMap.class);
            topicMetadata.setPartitionMap(partitionMap);
            topicMetadata.setReplicaNum(partitionMap.getPartitions().values().iterator().next().size());
            topicMetadata.setPartitionNum(partitionMap.getPartitions().size());

            Set<Integer> brokerIdSet = new HashSet<>();
            Map<Integer, List<Integer>> topicBrokers = partitionMap.getPartitions();
            for (Map.Entry<Integer, List<Integer>> entry : topicBrokers.entrySet()) {
                brokerIdSet.addAll(entry.getValue());
            }
            topicMetadata.setBrokerIdSet(brokerIdSet);
            PhysicalClusterMetadataManager.putTopicMetadata(clusterId, topicName, topicMetadata);
        } catch (Exception e) {
            LOGGER.error("add topic failed, clusterId:{} topicMetadata:{}.", clusterId, topicMetadata, e);
        }
    }
}
