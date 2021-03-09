package com.xiaojukeji.kafka.manager.task.listener;

import com.xiaojukeji.kafka.manager.monitor.common.entry.bizenum.MonitorMetricNameEnum;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.monitor.common.MonitorSinkConstant;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.monitor.common.entry.sink.MonitorTopicSinkTag;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.monitor.component.AbstractMonitorService;
import com.xiaojukeji.kafka.manager.monitor.common.entry.MetricSinkPoint;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/8/10
 */
@ConditionalOnProperty(prefix = "monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
@CustomScheduled(name = "sinkCommunityTopicMetrics2Monitor", cron = "1 0/1 * * * ?", threadNum = 5)
public class SinkCommunityTopicMetrics2Monitor extends AbstractScheduledTask<ClusterDO> {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private AbstractMonitorService abstractMonitor;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Override
    protected List<ClusterDO> listAllTasks() {
        return clusterService.list();
    }

    @Override
    public void processTask(ClusterDO clusterDO) {
        long startTime = System.currentTimeMillis();

        try {
            sink2Monitor(clusterDO.getId(), startTime / 1000);
        } catch (Throwable t) {
            LOGGER.error("sink topic metrics failed, clusterId:{}.", clusterDO.getId(), t);
        }
    }

    private void sink2Monitor(Long clusterId, Long now) throws Exception {
        // 上报Topic指标
        List<MetricSinkPoint> metricSinkPoints = new ArrayList<>();
        for (String topicName: PhysicalClusterMetadataManager.getTopicNameList(clusterId)) {
            LogicalClusterDO logicalClusterDO =
                    logicalClusterMetadataManager.getTopicLogicalCluster(clusterId, topicName);
            if (ValidateUtils.isNull(logicalClusterDO)) {
                continue;
            }
            TopicMetrics metrics = KafkaMetricsCache.getTopicMetricsFromCache(clusterId, topicName);
            if (ValidateUtils.isNull(metrics)) {
                continue;
            }

            metricSinkPoints.addAll(recordTopics(now, logicalClusterDO.getIdentification(), metrics));
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

    private static List<MetricSinkPoint> recordTopics(long timestamp,
                                                      String logicalClusterName,
                                                      TopicMetrics metrics) {
        if (ValidateUtils.isNull(logicalClusterName) || ValidateUtils.isNull(metrics)) {
            return new ArrayList<>();
        }

        return Arrays.asList(
                new MetricSinkPoint(
                        MonitorMetricNameEnum.TOPIC_MSG_IN.getMetricName(),
                        metrics.getMessagesInPerSecOneMinuteRate(0.0),
                        MonitorSinkConstant.MONITOR_SYSTEM_SINK_STEP,
                        timestamp,
                        new MonitorTopicSinkTag(
                                logicalClusterName,
                                metrics.getTopicName()
                        )
                ),

                new MetricSinkPoint(
                        MonitorMetricNameEnum.TOPIC_BYTES_IN.getMetricName(),
                        metrics.getBytesInPerSecOneMinuteRate(0.0),
                        MonitorSinkConstant.MONITOR_SYSTEM_SINK_STEP,
                        timestamp,
                        new MonitorTopicSinkTag(
                                logicalClusterName,
                                metrics.getTopicName()
                        )
                ),

                new MetricSinkPoint(
                        MonitorMetricNameEnum.TOPIC_BYTES_REJECTED.getMetricName(),
                        metrics.getBytesRejectedPerSecOneMinuteRate(0.0),
                        MonitorSinkConstant.MONITOR_SYSTEM_SINK_STEP,
                        timestamp,
                        new MonitorTopicSinkTag(
                                logicalClusterName,
                                metrics.getTopicName()
                        )
                )
        );
    }
}