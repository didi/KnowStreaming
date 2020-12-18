package com.xiaojukeji.kafka.manager.task.listener;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicMetricsDO;
import com.xiaojukeji.kafka.manager.common.events.TopicMetricsCollectedEvent;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.TopicMetricsDao;
import com.xiaojukeji.kafka.manager.service.utils.MetricsConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据存储DB
 * @author zengqiao
 * @date 20/9/1
 */
@Component("storeCommunityTopicMetrics2DB")
@ConditionalOnProperty(prefix = "custom.store-metrics-task.community", name = "topic-metrics-enabled", havingValue = "true", matchIfMissing = true)
public class StoreCommunityTopicMetrics2DB implements ApplicationListener<TopicMetricsCollectedEvent> {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private TopicMetricsDao topicMetricsDao;

    @Override
    public void onApplicationEvent(TopicMetricsCollectedEvent event) {
        List<TopicMetrics> metricsList = event.getMetricsList();
        if (ValidateUtils.isEmptyList(metricsList)) {
            LOGGER.warn("store topic metrics failed, data is empty.");
            return;
        }

        try {
            store2DB(System.currentTimeMillis(), metricsList);
        } catch (Throwable t) {
            LOGGER.error("save topic metrics failed, clusterId:{}.", event.getClusterId(), t);
        }
    }

    private void store2DB(Long startTime, List<TopicMetrics> metricsList) throws Exception {
        List<TopicMetricsDO> doList =
                MetricsConvertUtils.convertAndUpdateCreateTime2TopicMetricsDOList(startTime, metricsList);
        int i = 0;
        do {
            topicMetricsDao.batchAdd(doList.subList(i, Math.min(i + Constant.BATCH_INSERT_SIZE, doList.size())));
            i += Constant.BATCH_INSERT_SIZE;
        } while (i < doList.size());
    }
}