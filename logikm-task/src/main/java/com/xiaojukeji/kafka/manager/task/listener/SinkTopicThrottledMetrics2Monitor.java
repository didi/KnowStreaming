package com.xiaojukeji.kafka.manager.task.listener;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaClientEnum;
import com.xiaojukeji.kafka.manager.monitor.common.MonitorSinkConstant;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicThrottledMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.monitor.common.entry.MetricSinkPoint;
import com.xiaojukeji.kafka.manager.monitor.common.entry.bizenum.MonitorMetricNameEnum;
import com.xiaojukeji.kafka.manager.monitor.common.entry.sink.MonitorTopicThrottledSinkTag;
import com.xiaojukeji.kafka.manager.monitor.component.AbstractMonitorService;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.task.common.TopicThrottledMetricsCollectedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/9/24
 */
@Component("sinkTopicThrottledMetrics2Monitor")
@ConditionalOnProperty(prefix = "monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SinkTopicThrottledMetrics2Monitor implements ApplicationListener<TopicThrottledMetricsCollectedEvent> {
    @Autowired
    private AbstractMonitorService abstractMonitor;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Override
    public void onApplicationEvent(TopicThrottledMetricsCollectedEvent event) {
        List<TopicThrottledMetrics> metrics = event.getMetricsList();
        if (ValidateUtils.isEmptyList(metrics)) {
            return;
        }
        Long clusterId = metrics.get(0).getClusterId();
        sink2MonitorSystem(clusterId, event.getStartTime(), metrics);
    }

    private void sink2MonitorSystem(Long clusterId,
                                    Long startTime,
                                    List<TopicThrottledMetrics> metricsList) {
        if (ValidateUtils.isEmptyList(metricsList)) {
            return;
        }

        List<MetricSinkPoint> metricSinkPoints = new ArrayList<>();
        for (TopicThrottledMetrics elem: metricsList) {
            LogicalClusterDO logicalClusterDO =
                    logicalClusterMetadataManager.getTopicLogicalCluster(clusterId, elem.getTopicName());
            if (ValidateUtils.isNull(logicalClusterDO)) {
                continue;
            }

            MetricSinkPoint point = recordTopicThrottled(startTime, logicalClusterDO.getIdentification(), elem);
            if (ValidateUtils.isNull(point)) {
                continue;
            }

            metricSinkPoints.add(point);
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

    private static MetricSinkPoint recordTopicThrottled(long startTime,
                                                        String logicalClusterName,
                                                        TopicThrottledMetrics metrics) {
        if (metrics.getClientType().equals(KafkaClientEnum.FETCH_CLIENT)) {
            return new MetricSinkPoint(
                    MonitorMetricNameEnum.TOPIC_APP_FETCH_THROTTLE.getMetricName(),
                    MonitorSinkConstant.MONITOR_SYSTEM_METRIC_VALUE_EFFECTIVE,
                    MonitorSinkConstant.MONITOR_SYSTEM_SINK_STEP,
                    startTime / 1000,
                    new MonitorTopicThrottledSinkTag(
                            logicalClusterName,
                            metrics.getTopicName(),
                            metrics.getAppId()
                    )
            );
        }

        if (metrics.getClientType().equals(KafkaClientEnum.PRODUCE_CLIENT)) {
            return new MetricSinkPoint(
                    MonitorMetricNameEnum.TOPIC_APP_PRODUCE_THROTTLE.getMetricName(),
                    MonitorSinkConstant.MONITOR_SYSTEM_METRIC_VALUE_EFFECTIVE,
                    MonitorSinkConstant.MONITOR_SYSTEM_SINK_STEP,
                    startTime / 1000,
                    new MonitorTopicThrottledSinkTag(
                            logicalClusterName,
                            metrics.getTopicName(),
                            metrics.getAppId()
                    )
            );
        }
        return null;
    }
}