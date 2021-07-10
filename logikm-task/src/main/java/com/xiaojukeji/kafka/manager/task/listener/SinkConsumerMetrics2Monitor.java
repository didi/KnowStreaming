package com.xiaojukeji.kafka.manager.task.listener;

import com.xiaojukeji.kafka.manager.monitor.common.entry.bizenum.MonitorMetricNameEnum;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.monitor.common.MonitorSinkConstant;
import com.xiaojukeji.kafka.manager.common.entity.metrics.ConsumerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.common.events.ConsumerMetricsCollectedEvent;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.monitor.common.entry.MetricSinkPoint;
import com.xiaojukeji.kafka.manager.monitor.common.entry.sink.MonitorConsumePartitionSinkTag;
import com.xiaojukeji.kafka.manager.monitor.common.entry.sink.MonitorConsumeTopicSinkTag;
import com.xiaojukeji.kafka.manager.monitor.component.AbstractMonitorService;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author zengqiao
 * @date 20/9/2
 */
@Component("sinkConsumerMetrics2Monitor")
@ConditionalOnProperty(prefix = "monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SinkConsumerMetrics2Monitor implements ApplicationListener<ConsumerMetricsCollectedEvent> {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private AbstractMonitorService abstractMonitor;

    @Override
    public void onApplicationEvent(ConsumerMetricsCollectedEvent event) {
        LOGGER.warn("sink consumer metrics start.");
        List<ConsumerMetrics> metricsList = event.getMetricsList();
        if (ValidateUtils.isEmptyList(metricsList)) {
            LOGGER.warn("sink consumer metrics failed, data is empty.");
            return;
        }
        long startTime = System.currentTimeMillis();

        sinkConsumerGroup(metricsList);
        LOGGER.info("sink consumer metrics to monitor-system finish, clusterId:{} costTime:{}"
                , metricsList.get(0).getClusterId(), System.currentTimeMillis() - startTime);
    }

    private void sinkConsumerGroup(List<ConsumerMetrics> metricsList) {
        List<MetricSinkPoint> metricSinkPoints = new ArrayList<>();
        for (ConsumerMetrics elem: metricsList) {
            LogicalClusterDO logicalClusterDO =
                    logicalClusterMetadataManager.getTopicLogicalCluster(elem.getClusterId(), elem.getTopicName());
            if (ValidateUtils.isNull(logicalClusterDO)) {
                continue;
            }

            metricSinkPoints.addAll(recordConsumer(elem.getTimestampUnitMs() / 1000, logicalClusterDO.getIdentification(), elem));
            if (metricSinkPoints.size() > MonitorSinkConstant.MONITOR_SYSTEM_SINK_THRESHOLD) {
                abstractMonitor.sinkMetrics(metricSinkPoints);
                metricSinkPoints.clear();
            }
        }
        if (metricSinkPoints.isEmpty()) {
            return;
        }
        abstractMonitor.sinkMetrics(metricSinkPoints);
    }

    private static List<MetricSinkPoint> recordConsumer(long timestamp,
                                                        String logicalClusterName,
                                                        ConsumerMetrics metrics) {
        if (ValidateUtils.isNull(logicalClusterName) || ValidateUtils.isNull(metrics)) {
            return new ArrayList<>();
        }
        Long maxLag = 0L;
        List<MetricSinkPoint> pointList = new ArrayList<>();

        for (Integer partitionId: metrics.getPartitionOffsetMap().keySet()) {
            Long partitionOffset = metrics.getPartitionOffsetMap().get(partitionId);
            Long consumerOffset = metrics.getConsumeOffsetMap().get(partitionId);
            if (ValidateUtils.isNull(partitionOffset) || ValidateUtils.isNull(consumerOffset)) {
                continue;
            }

            Long lag = Math.max(partitionOffset - consumerOffset, 0L);
            pointList.add(new MetricSinkPoint(
                    MonitorMetricNameEnum.CONSUMER_PARTITION_LAG.getMetricName(),
                    lag,
                    MonitorSinkConstant.MONITOR_SYSTEM_SINK_STEP,
                    timestamp,
                    new MonitorConsumePartitionSinkTag(
                            logicalClusterName,
                            metrics.getTopicName(),
                            partitionId,
                            metrics.getConsumerGroup()
                    )
            ));

            maxLag = Math.max(maxLag, lag);
        }

        pointList.add(new MetricSinkPoint(
                MonitorMetricNameEnum.CONSUMER_MAX_LAG.getMetricName(),
                maxLag,
                MonitorSinkConstant.MONITOR_SYSTEM_SINK_STEP,
                timestamp,
                new MonitorConsumeTopicSinkTag(
                        logicalClusterName,
                        metrics.getTopicName(),
                        metrics.getConsumerGroup()
                )
        ));

        Long maxDelayTime = calMaxDelayTime(
                metrics.getClusterId(),
                metrics.getTopicName(),
                metrics.getConsumerGroup(),
                maxLag
        );
        if (ValidateUtils.isNull(maxDelayTime)) {
            LOGGER.error("cal maxDelayTime failed, clusterId:{} topicName:{} consumerGroup:{} maxLag:{}."
                    , metrics.getClusterId(), metrics.getTopicName(), metrics.getConsumerGroup(), maxLag);
            return pointList;
        }
        pointList.add(new MetricSinkPoint(
                MonitorMetricNameEnum.CONSUMER_MAX_DELAY_TIME.getMetricName(),
                maxDelayTime,
                MonitorSinkConstant.MONITOR_SYSTEM_SINK_STEP,
                timestamp,
                new MonitorConsumeTopicSinkTag(
                        logicalClusterName,
                        metrics.getTopicName(),
                        metrics.getConsumerGroup()
                )
        ));
        return pointList;
    }

    private static Long calMaxDelayTime(Long clusterId, String topicName, String consumerGroup, Long maxLag) {
        try {
            TopicMetrics metrics = KafkaMetricsCache.getTopicMetricsFromCache(clusterId, topicName);
            if (ValidateUtils.isNull(metrics)) {
                return null;
            }
            Double messageIn = metrics.getMessagesInPerSecOneMinuteRate(-1.0);
            if (messageIn.equals(-1.0) || messageIn.equals(0.0)) {
                return null;
            }
            TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
            if (ValidateUtils.isNull(topicMetadata)) {
                return null;
            }
            return Math.round(maxLag / messageIn * topicMetadata.getPartitionNum());
        } catch (Exception e) {
            LOGGER.error("cal maxDelayTime failed, clusterId:{} topicName:{} consumerGroup:{} maxLag:{}."
                    , clusterId, topicName, consumerGroup, maxLag);
        }
        return null;
    }
}
