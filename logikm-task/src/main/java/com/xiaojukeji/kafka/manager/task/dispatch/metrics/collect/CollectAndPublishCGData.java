package com.xiaojukeji.kafka.manager.task.dispatch.metrics.collect;

import com.xiaojukeji.kafka.manager.common.bizenum.OffsetPosEnum;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumerGroup;
import com.xiaojukeji.kafka.manager.common.entity.metrics.ConsumerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.events.ConsumerMetricsCollectedEvent;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.ThreadPool;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ConsumerService;
import com.xiaojukeji.kafka.manager.service.service.TopicService;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author zengqiao
 * @date 20/9/14
 */
@CustomScheduled(name = "newCollectAndPublishCGData", cron = "30 0/1 * * * *", threadNum = 10)
public class CollectAndPublishCGData extends AbstractScheduledTask<ClusterDO> {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private TopicService topicService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ConsumerService consumerService;

    @Override
    protected List<ClusterDO> listAllTasks() {
        return clusterService.list();
    }

    @Override
    public void processTask(ClusterDO clusterDO) {
        LOGGER.info("collect consumer-group metrics start, clusterId:{}.", clusterDO.getId());

        long startTime = System.currentTimeMillis();
        try {
            collectData(clusterDO, startTime);
        } catch (Throwable t) {
            LOGGER.error("collect consumer-group metrics failed, clusterId:{}.", clusterDO.getId(), t);
        }
        LOGGER.info("collect consumer-group metrics finish, clusterId:{} costTime:{}."
                , clusterDO.getId(), System.currentTimeMillis() - startTime);
    }

    private void collectData(ClusterDO clusterDO, long startTimeUnitMs) {
        List<String> topicNameList = PhysicalClusterMetadataManager.getTopicNameList(clusterDO.getId());
        if (ValidateUtils.isEmptyList(topicNameList)) {
            // 重试
            topicNameList = PhysicalClusterMetadataManager.getTopicNameList(clusterDO.getId());
        }
        if (ValidateUtils.isEmptyList(topicNameList)) {
            return;
        }

        FutureTask<List<ConsumerMetrics>>[] taskList = new FutureTask[topicNameList.size()];
        for (int i = 0; i < topicNameList.size(); ++i) {
            final String topicName = topicNameList.get(i);
            taskList[i] = new FutureTask<List<ConsumerMetrics>>(new Callable<List<ConsumerMetrics>>() {
                @Override
                public List<ConsumerMetrics> call() throws Exception {
                    return getTopicConsumerMetrics(clusterDO, topicName, startTimeUnitMs);
                }
            });
            ThreadPool.submitCollectMetricsTask(taskList[i]);
        }

        List<ConsumerMetrics> consumerMetricsList = new ArrayList<>();
        for (int i = 0; i < taskList.length; ++i) {
            try {
                List<ConsumerMetrics> metricsList = taskList[i].get();
                if (ValidateUtils.isEmptyList(metricsList)) {
                    continue;
                }
                consumerMetricsList.addAll(metricsList);
            } catch (Exception e) {
                LOGGER.error("collect consumer-group metrics failed, clusterId:{} topicName:{}.",
                        clusterDO.getId(), topicNameList.get(i), e
                );
            }
        }
        SpringTool.publish(new ConsumerMetricsCollectedEvent(this, consumerMetricsList));
    }

    private List<ConsumerMetrics> getTopicConsumerMetrics(ClusterDO clusterDO,
                                                          String topicName,
                                                          long startTimeUnitMs) {
        List<ConsumerGroup> consumerGroupDTOList = consumerService.getConsumerGroupList(clusterDO.getId(), topicName);
        if (ValidateUtils.isEmptyList(consumerGroupDTOList)) {
            // 重试
            consumerGroupDTOList = consumerService.getConsumerGroupList(clusterDO.getId(), topicName);
        }
        if (ValidateUtils.isEmptyList(consumerGroupDTOList)) {
            return new ArrayList<>();
        }
        List<ConsumerMetrics> metricsList = new ArrayList<>();

        Map<TopicPartition, Long> offsetMap = topicService.getPartitionOffset(clusterDO, topicName, OffsetPosEnum.END);
        if (ValidateUtils.isEmptyMap(offsetMap)) {
            // 重试
            offsetMap = topicService.getPartitionOffset(clusterDO, topicName, OffsetPosEnum.END);
        }
        if (ValidateUtils.isEmptyMap(offsetMap)) {
            LOGGER.error("collect consumer-group metrics failed, partition offset is empty, clusterId:{} topicName:{}.",
                    clusterDO.getId(), topicName
            );
            return new ArrayList<>();
        }
        Map<Integer, Long> partitionOffsetMap = new HashMap<>();
        for (Map.Entry<TopicPartition, Long> entry: offsetMap.entrySet()) {
            partitionOffsetMap.put(entry.getKey().partition(), entry.getValue());
        }

        for (ConsumerGroup consumerGroupDTO: consumerGroupDTOList) {
            try {
                ConsumerMetrics consumerMetrics =
                        getTopicConsumerMetrics(clusterDO, topicName, consumerGroupDTO, partitionOffsetMap, startTimeUnitMs);
                if (ValidateUtils.isNull(consumerMetrics)) {
                    continue;
                }
                metricsList.add(consumerMetrics);
            } catch (Exception e) {
                LOGGER.error("collect consumer-group metrics failed, clusterId:{} topicName:{} consumerGroup:{}.",
                        clusterDO.getId(), topicName, consumerGroupDTO.getConsumerGroup(), e
                );
            }
        }
        return metricsList;
    }

    private ConsumerMetrics getTopicConsumerMetrics(ClusterDO clusterDO,
                                                    String topicName,
                                                    ConsumerGroup consumerGroup,
                                                    Map<Integer, Long> partitionOffsetMap,
                                                    long startTimeUnitMs) {

        Map<Integer, Long> consumerOffsetMap =
                consumerService.getConsumerOffset(clusterDO, topicName, consumerGroup);
        if (ValidateUtils.isEmptyMap(consumerOffsetMap)) {
            return null;
        }
        ConsumerMetrics metrics = new ConsumerMetrics();
        metrics.setClusterId(clusterDO.getId());
        metrics.setTopicName(topicName);
        metrics.setConsumerGroup(consumerGroup.getConsumerGroup());
        metrics.setLocation(consumerGroup.getOffsetStoreLocation().location);
        metrics.setPartitionOffsetMap(partitionOffsetMap);
        metrics.setConsumeOffsetMap(consumerOffsetMap);
        metrics.setTimestampUnitMs(startTimeUnitMs);
        return metrics;
    }
}