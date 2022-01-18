package com.xiaojukeji.kafka.manager.task.listener.sink.db;

import com.xiaojukeji.kafka.manager.common.entity.metrics.BaseMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.ClusterMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterMetricsDO;
import com.xiaojukeji.kafka.manager.common.events.metrics.BatchBrokerMetricsCollectedEvent;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxConstant;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.dao.ClusterMetricsDao;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.utils.MetricsConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 22/01/17
 */
@Component
@ConditionalOnProperty(prefix = "task.metrics.sink.cluster-metrics", name = "sink-db-enabled", havingValue = "true", matchIfMissing = true)
public class SinkClusterMetrics2DB implements ApplicationListener<BatchBrokerMetricsCollectedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(SinkClusterMetrics2DB.class);

    @Autowired
    private ClusterMetricsDao clusterMetricsDao;

    @Override
    public void onApplicationEvent(BatchBrokerMetricsCollectedEvent event) {
        logger.debug("sink cluster-metrics to db start, event:{}.", event);

        List<BrokerMetrics> metricsList = event.getMetricsList();
        if (ValidateUtils.isEmptyList(metricsList)) {
            logger.warn("sink cluster-metrics to db finished, without need sink, event:{}.", event);
            return;
        }

        List<ClusterMetricsDO> doList = MetricsConvertUtils.convertAndUpdateCreateTime2ClusterMetricsDOList(
                event.getCollectTime(),
                // 合并broker-metrics为cluster-metrics
                Arrays.asList(supplyAndConvert2ClusterMetrics(event.getPhysicalClusterId(), MetricsConvertUtils.merge2BaseMetricsByAdd(event.getMetricsList())))
        );

        if (ValidateUtils.isEmptyList(doList)) {
            logger.warn("sink cluster-metrics to db finished, without need sink, event:{}.", event);
            return;
        }

        clusterMetricsDao.batchAdd(doList);

        logger.debug("sink cluster-metrics to db finished, event:{}.", event);
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