package com.xiaojukeji.kafka.manager.task.schedule;

import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author zengqiao
 * @date 20/7/2
 */
@Component
public class FlushTopicMetrics {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private JmxService jmxService;

    @Autowired
    private ClusterService clusterService;

    @Scheduled(cron="5 0/1 * * * ?")
    public void flushTopicMetrics() {
        long startTime = System.currentTimeMillis();
        LOGGER.info("flush topic-metrics start.");

        List<ClusterDO> clusterDOList = clusterService.list();
        for (ClusterDO clusterDO : clusterDOList) {
            try {
                flushTopicMetrics(clusterDO.getId());
            } catch (Exception e) {
                LOGGER.error("flush topic-metrics failed, clusterId:{}.", clusterDO.getId(), e);
            }
        }
        LOGGER.info("flush topic-metrics finished, costTime:{}.", System.currentTimeMillis() - startTime);
    }

    private void flushTopicMetrics(Long clusterId) {
        List<TopicMetrics> metricsList =
                jmxService.getTopicMetrics(clusterId, KafkaMetricsCollections.TOPIC_METRICS_TO_DB, true);
        if (ValidateUtils.isEmptyList(metricsList)) {
            KafkaMetricsCache.putTopicMetricsToCache(clusterId, new ArrayList<>());
            return;
        }
        KafkaMetricsCache.putTopicMetricsToCache(clusterId, metricsList);
    }
}