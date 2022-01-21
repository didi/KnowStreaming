package com.xiaojukeji.kafka.manager.task.dispatch.metrics.store;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.TopicAppMetricsDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicMetricsDO;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import com.xiaojukeji.kafka.manager.service.utils.MetricsConvertUtils;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.*;

/**
 * JMX中获取appId维度的流量信息存DB
 * @author zengqiao
 * @date 20/7/21
 */
@CustomScheduled(name = "storeDiDiAppTopicMetrics", cron = "41 0/1 * * * ?", threadNum = 5, description = "JMX中获取appId维度的流量信息存DB")
@ConditionalOnProperty(prefix = "custom.store-metrics-task.didi", name = "app-topic-metrics-enabled", havingValue = "true", matchIfMissing = true)
public class StoreDiDiAppTopicMetrics extends AbstractScheduledTask<ClusterDO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreDiDiAppTopicMetrics.class);

    @Autowired
    private JmxService jmxService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private TopicAppMetricsDao topicAppMetricsDao;

    @Override
    protected List<ClusterDO> listAllTasks() {
        return clusterService.list();
    }

    @Override
    public void processTask(ClusterDO clusterDO) {
        long startTime = System.currentTimeMillis();

        try {
            getAndBatchAddTopicAppMetrics(startTime, clusterDO.getId());
        } catch (Exception t) {
            LOGGER.error("save topic metrics failed, clusterId:{}.", clusterDO.getId(), t);
        }
    }

    private void getAndBatchAddTopicAppMetrics(Long startTime, Long clusterId) {
        List<TopicMetrics> metricsList =
                jmxService.getTopicAppMetrics(clusterId, KafkaMetricsCollections.APP_TOPIC_METRICS_TO_DB);
        if (ValidateUtils.isEmptyList(metricsList)) {
            return;
        }
        List<TopicMetricsDO> doList =
                MetricsConvertUtils.convertAndUpdateCreateTime2TopicMetricsDOList(startTime, metricsList);
        int i = 0;
        do {
            List<TopicMetricsDO> subDOList = doList.subList(i, Math.min(i + Constant.BATCH_INSERT_SIZE, doList.size()));
            if (ValidateUtils.isEmptyList(subDOList)) {
                return;
            }

            topicAppMetricsDao.batchAdd(subDOList);
            i += Constant.BATCH_INSERT_SIZE;
        } while (i < doList.size());
    }
}