package com.xiaojukeji.kafka.manager.task.dispatch.metrics.collect;

import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.events.metrics.BatchBrokerMetricsCollectedEvent;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxConstant;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import com.xiaojukeji.kafka.manager.service.strategy.AbstractHealthScoreStrategy;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Broker指标信息收集
 * @author zengqiao
 * @date 20/5/7
 */
@CustomScheduled(name = "collectAndPublishBrokerMetrics", cron = "21 0/1 * * * ?", threadNum = 2)
@ConditionalOnProperty(prefix = "task.metrics.collect", name = "broker-metrics-enabled", havingValue = "true", matchIfMissing = true)
public class CollectAndPublishBrokerMetrics extends AbstractScheduledTask<ClusterDO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectAndPublishBrokerMetrics.class);

    @Autowired
    private JmxService jmxService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private AbstractHealthScoreStrategy healthScoreStrategy;

    @Override
    protected List<ClusterDO> listAllTasks() {
        return clusterService.list();
    }

    @Override
    public void processTask(ClusterDO clusterDO) {
        long startTime = System.currentTimeMillis();

        try {
            SpringTool.publish(new BatchBrokerMetricsCollectedEvent(
                    this,
                    clusterDO.getId(),
                    startTime,
                    this.getBrokerMetrics(clusterDO.getId()))
            );
        } catch (Exception e) {
            LOGGER.error("collect broker-metrics failed, physicalClusterId:{}.", clusterDO.getId(), e);
        }

        LOGGER.info("collect broker-metrics finished, physicalClusterId:{} costTime:{}", clusterDO.getId(), System.currentTimeMillis() - startTime);
    }

    private List<BrokerMetrics> getBrokerMetrics(Long clusterId) {
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

        return metricsList;
    }
}