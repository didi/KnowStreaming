package com.xiaojukeji.kafka.manager.task.dispatch.metrics.store;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.TopicRequestMetricsDao;
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
 * JMX中获取的TopicRequestTimeMetrics信息存DB
 * @author zengqiao
 * @date 20/7/21
 */
@CustomScheduled(name = "storeDiDiTopicRequestTimeMetrics", cron = "51 0/1 * * * ?", threadNum = 5, description = "JMX中获取的TopicRequestTimeMetrics信息存DB")
@ConditionalOnProperty(prefix = "custom.store-metrics-task.didi", name = "topic-request-time-metrics-enabled", havingValue = "true", matchIfMissing = true)
public class StoreDiDiTopicRequestTimeMetrics extends AbstractScheduledTask<ClusterDO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreDiDiTopicRequestTimeMetrics.class);

    @Autowired
    private JmxService jmxService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private TopicRequestMetricsDao topicRequestMetricsDao;

    @Override
    protected List<ClusterDO> listAllTasks() {
        return clusterService.list();
    }

    @Override
    public void processTask(ClusterDO clusterDO) {
        long startTime = System.currentTimeMillis();

        try {
            LOGGER.info("save topic metrics, clusterId:{}, start.", clusterDO.getId());
            getAndBatchAddTopicRequestTimeMetrics(startTime, clusterDO.getId());
            LOGGER.info("save topic metrics, clusterId:{}, end costTime:{}.", clusterDO.getId(), System.currentTimeMillis() - startTime);
        } catch (Exception t) {
            LOGGER.error("save topic metrics failed, clusterId:{}.", clusterDO.getId(), t);
        }
    }

    private void getAndBatchAddTopicRequestTimeMetrics(Long startTime, Long clusterId) {
        LOGGER.info("save topic metrics, clusterId:{}, collect start.", clusterId);
        List<TopicMetrics> metricsList =
                jmxService.getTopicMetrics(clusterId, KafkaMetricsCollections.TOPIC_REQUEST_TIME_METRICS_TO_DB, false);
        LOGGER.info("save topic metrics, clusterId:{}, collect end costTime:{} .", clusterId, System.currentTimeMillis() - startTime);
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

            topicRequestMetricsDao.batchAdd(subDOList);
            i += Constant.BATCH_INSERT_SIZE;
        } while (i < doList.size());
    }
}