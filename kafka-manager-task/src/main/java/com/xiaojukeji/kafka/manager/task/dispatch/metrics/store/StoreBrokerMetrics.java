package com.xiaojukeji.kafka.manager.task.dispatch.metrics.store;

import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BaseMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.ClusterMetrics;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxConstant;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.dao.BrokerMetricsDao;
import com.xiaojukeji.kafka.manager.dao.ClusterMetricsDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BrokerMetricsDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterMetricsDO;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import com.xiaojukeji.kafka.manager.service.strategy.AbstractHealthScoreStrategy;
import com.xiaojukeji.kafka.manager.service.utils.MetricsConvertUtils;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Broker指标信息存DB, Broker流量, 集群流量
 * @author zengqiao
 * @date 20/5/7
 */
@CustomScheduled(name = "storeBrokerMetrics", cron = "21 0/1 * * * ?", threadNum = 2)
@ConditionalOnProperty(prefix = "custom.store-metrics-task.community", name = "broker-metrics-enabled", havingValue = "true", matchIfMissing = true)
public class StoreBrokerMetrics extends AbstractScheduledTask<ClusterDO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private JmxService jmxService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private BrokerMetricsDao brokerMetricsDao;

    @Autowired
    private ClusterMetricsDao clusterMetricsDao;

    @Autowired
    private AbstractHealthScoreStrategy healthScoreStrategy;

    private static final Integer INSERT_BATCH_SIZE = 100;

    @Override
    protected List<ClusterDO> listAllTasks() {
        return clusterService.list();
    }

    @Override
    public void processTask(ClusterDO clusterDO) {
        long startTime = System.currentTimeMillis();
        List<ClusterMetrics> clusterMetricsList = new ArrayList<>();

        try {
            List<BrokerMetrics> brokerMetricsList = getAndBatchAddMetrics(startTime, clusterDO.getId());
            clusterMetricsList.add(supplyAndConvert2ClusterMetrics(
                    clusterDO.getId(),
                    MetricsConvertUtils.merge2BaseMetricsByAdd(brokerMetricsList))
            );
        } catch (Throwable t) {
            LOGGER.error("collect failed, clusterId:{}.", clusterDO.getId(), t);
        }
        long endTime = System.currentTimeMillis();
        LOGGER.info("collect finish, clusterId:{} costTime:{}", clusterDO.getId(), endTime - startTime);

        List<ClusterMetricsDO> doList = MetricsConvertUtils.convertAndUpdateCreateTime2ClusterMetricsDOList(
                startTime,
                clusterMetricsList
        );
        clusterMetricsDao.batchAdd(doList);
    }

    private List<BrokerMetrics> getAndBatchAddMetrics(Long startTime, Long clusterId) {
        List<BrokerMetrics> metricsList = new ArrayList<>();
        for (Integer brokerId: PhysicalClusterMetadataManager.getBrokerIdList(clusterId)) {
            BrokerMetrics metrics = jmxService.getBrokerMetrics(
                    clusterId,
                    brokerId,
                    KafkaMetricsCollections.BROKER_TO_DB_METRICS
            );
            if (ValidateUtils.isNull(metrics)) {
                continue;
            }
            metrics.getMetricsMap().put(
                    JmxConstant.HEALTH_SCORE,
                    healthScoreStrategy.calBrokerHealthScore(clusterId, brokerId, metrics)
            );
            metricsList.add(metrics);
        }
        if (ValidateUtils.isEmptyList(metricsList)) {
            return new ArrayList<>();
        }

        List<BrokerMetricsDO> doList =
                MetricsConvertUtils.convertAndUpdateCreateTime2BrokerMetricsDOList(startTime, metricsList);
        int i = 0;
        do {
            brokerMetricsDao.batchAdd(doList.subList(i, Math.min(i + INSERT_BATCH_SIZE, doList.size())));
            i += INSERT_BATCH_SIZE;
        } while (i < doList.size());
        return metricsList;
    }

    private ClusterMetrics supplyAndConvert2ClusterMetrics(Long clusterId, BaseMetrics baseMetrics) {
        ClusterMetrics metrics = new ClusterMetrics(clusterId);
        Map<String, Object> metricsMap = metrics.getMetricsMap();
        metricsMap.putAll(baseMetrics.getMetricsMap());
        metricsMap.put(JmxConstant.TOPIC_NUM, PhysicalClusterMetadataManager.getTopicNameList(clusterId).size());
        metricsMap.put(JmxConstant.BROKER_NUM, PhysicalClusterMetadataManager.getBrokerIdList(clusterId).size());
        Integer partitionNum = 0;
        for (String topicName : PhysicalClusterMetadataManager.getTopicNameList(clusterId)) {
            TopicMetadata topicMetaData = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
            if (ValidateUtils.isNull(topicMetaData)) {
                continue;
            }
            partitionNum += topicMetaData.getPartitionNum();
        }
        metricsMap.put(JmxConstant.PARTITION_NUM, partitionNum);
        return metrics;
    }
}