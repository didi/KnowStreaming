package com.xiaojukeji.kafka.manager.task.dispatch.metrics.delete;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.dao.*;
import com.xiaojukeji.kafka.manager.service.utils.ConfigUtils;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import com.xiaojukeji.kafka.manager.task.component.EmptyEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 定期删除Metrics信息
 * @author zengqiao
 * @date 20/1/8
 */
@CustomScheduled(name = "deleteMetrics", cron = "0 0/2 * * * ?", threadNum = 1)
public class DeleteMetrics extends AbstractScheduledTask<EmptyEntry> {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private ConfigUtils                 configUtils;

    @Autowired
    private TopicMetricsDao             topicMetricsDao;

    @Autowired
    private TopicAppMetricsDao          topicAppMetricsDao;

    @Autowired
    private TopicRequestMetricsDao      topicRequestMetricsDao;

    @Autowired
    private BrokerMetricsDao            brokerMetricsDao;

    @Autowired
    private ClusterMetricsDao           clusterMetricsDao;

    @Autowired
    private TopicThrottledMetricsDao    topicThrottledMetricsDao;

    @Override
    public List<EmptyEntry> listAllTasks() {
        EmptyEntry emptyEntry = new EmptyEntry();
        emptyEntry.setId(System.currentTimeMillis() / 1000);
        return Arrays.asList(emptyEntry);
    }

    @Override
    public void processTask(EmptyEntry entryEntry) {
        if (Constant.INVALID_CODE.equals(configUtils.getMaxMetricsSaveDays())) {
            // 无需数据删除
            return;
        }

        long startTime = System.currentTimeMillis();
        LOGGER.info("start delete metrics");
        try {
            deleteTopicMetrics();
        } catch (Exception e) {
            LOGGER.error("delete topic metrics failed.", e);
        }

        try {
            deleteTopicAppMetrics();
        } catch (Exception e) {
            LOGGER.error("delete topic app metrics failed.", e);
        }

        try {
            deleteTopicRequestMetrics();
        } catch (Exception e) {
            LOGGER.error("delete topic request metrics failed.", e);
        }

        try {
            deleteThrottledMetrics();
        } catch (Exception e) {
            LOGGER.error("delete topic throttled metrics failed.", e);
        }

        try {
            deleteBrokerMetrics();
        } catch (Exception e) {
            LOGGER.error("delete broker metrics failed.", e);
        }

        try {
            deleteClusterMetrics();
        } catch (Exception e) {
            LOGGER.error("delete cluster metrics failed.", e);
        }
        LOGGER.info("finish delete metrics, costTime:{}ms.", System.currentTimeMillis() - startTime);
    }

    private void deleteTopicMetrics() {
        Date endTime = new Date(System.currentTimeMillis() - configUtils.getMaxMetricsSaveDays() * 24 * 60 * 60 * 1000);
        topicMetricsDao.deleteBeforeTime(endTime);
    }

    private void deleteTopicAppMetrics() {
        Date endTime = new Date(System.currentTimeMillis() - configUtils.getMaxMetricsSaveDays() * 24 * 60 * 60 * 1000);
        topicAppMetricsDao.deleteBeforeTime(endTime);
    }

    private void deleteTopicRequestMetrics() {
        Date endTime = new Date(System.currentTimeMillis() - configUtils.getMaxMetricsSaveDays() * 24 * 60 * 60 * 1000);
        topicRequestMetricsDao.deleteBeforeTime(endTime);
    }

    private void deleteThrottledMetrics() {
        Date endTime = new Date(System.currentTimeMillis() - configUtils.getMaxMetricsSaveDays() * 24 * 60 * 60 * 1000);
        topicThrottledMetricsDao.deleteBeforeTime(endTime);
    }

    private void deleteBrokerMetrics() {
        Date endTime = new Date(System.currentTimeMillis() - configUtils.getMaxMetricsSaveDays() * 24 * 60 * 60 * 1000);
        brokerMetricsDao.deleteBeforeTime(endTime);
    }

    private void deleteClusterMetrics() {
        Date endTime = new Date(System.currentTimeMillis() - configUtils.getMaxMetricsSaveDays() * 24 * 60 * 60 * 1000);
        clusterMetricsDao.deleteBeforeTime(endTime);
    }
}