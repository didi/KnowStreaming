package com.xiaojukeji.kafka.manager.task.schedule.metadata;

import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.entity.ConsumerMetadata;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.ConsumerMetadataCache;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkPathUtil;
import com.xiaojukeji.kafka.manager.service.cache.ThreadPool;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

/**
 * @author zengqiao
 * @date 19/12/25
 */
@Component
public class FlushZKConsumerGroupMetadata {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private ClusterService clusterService;

    @Scheduled(cron="35 0/1 * * * ?")
    public void schedule() {
        List<ClusterDO> doList = clusterService.list();

        for (ClusterDO clusterDO: doList) {
            LOGGER.info("flush zookeeper cg start, clusterId:{}.", clusterDO.getId());
            long startTime = System.currentTimeMillis();
            try {
                flush(clusterDO.getId());
            } catch (Throwable t) {
                LOGGER.error("flush zookeeper cg failed, clusterId:{}.", clusterDO.getId(), t);
            }
            LOGGER.info("flush zookeeper cg finished, clusterId:{} costTime:{}.",
                    clusterDO.getId(), System.currentTimeMillis() - startTime);
        }
    }

    private void flush(Long clusterId) {
        Set<String> consumerGroupSet = collectConsumerGroup(clusterId);
        Map<String, Set<String>> topicNameConsumerGroupMap =
                collectTopicAndConsumerGroupMap(clusterId, new ArrayList<>(consumerGroupSet));
        ConsumerMetadataCache.putConsumerMetadataInZK(
                clusterId,
                new ConsumerMetadata(consumerGroupSet, topicNameConsumerGroupMap, new HashMap<>(0))
        );
    }

    private Set<String> collectConsumerGroup(Long clusterId) {
        try {
            ZkConfigImpl zkConfigImpl = PhysicalClusterMetadataManager.getZKConfig(clusterId);
            List<String> consumerGroupList = zkConfigImpl.getChildren(ZkPathUtil.CONSUMER_ROOT_NODE);
            if (consumerGroupList == null) {
                return new HashSet<>();
            }
            return consumerGroupList
                    .stream()
                    .filter(elem -> !elem.startsWith("console-consumer"))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            LOGGER.error("collect consumerGroup failed, clusterId:{}.", clusterId, e);
        }
        return new HashSet<>();
    }

    private Map<String, Set<String>> collectTopicAndConsumerGroupMap(Long clusterId,
                                                                     List<String> consumerGroupList) {
        ZkConfigImpl zkConfigImpl = PhysicalClusterMetadataManager.getZKConfig(clusterId);

        FutureTask<List<String>>[] taskList = new FutureTask[consumerGroupList.size()];
        for (int i = 0; i < consumerGroupList.size(); i++) {
            final String consumerGroup = consumerGroupList.get(i);
            taskList[i] = new FutureTask<List<String>>(new Callable<List<String>>() {
                @Override
                public List<String> call() throws Exception {
                    try {
                        return zkConfigImpl.getChildren(ZkPathUtil.getConsumerGroupOffsetRoot(consumerGroup));
                    } catch (Exception e) {
                        LOGGER.error("collect topicName and consumerGroup failed, clusterId:{} consumerGroup:{}.",
                                clusterId, consumerGroup, e);
                    }
                    return new ArrayList<>();
                }
            });
            ThreadPool.submitCollectMetricsTask(taskList[i]);
        }

        Map<String, Set<String>> topicNameConsumerGroupMap = new HashMap<>();
        for (int i = 0; i < taskList.length; ++i) {
            try {
                List<String> topicNameList = taskList[i].get();
                if (ValidateUtils.isEmptyList(topicNameList)) {
                    continue;
                }
                for (String topicName: topicNameList) {
                    Set<String> subConsumerGroupSet =
                            topicNameConsumerGroupMap.getOrDefault(topicName, new HashSet<>());
                    subConsumerGroupSet.add(consumerGroupList.get(i));
                    topicNameConsumerGroupMap.put(topicName, subConsumerGroupSet);
                }
            } catch (Exception e) {
                LOGGER.error("get topic list failed, clusterId:{} consumerGroup:{}.",
                        clusterId, consumerGroupList.get(i), e);
            }
        }
        return topicNameConsumerGroupMap;
    }
}