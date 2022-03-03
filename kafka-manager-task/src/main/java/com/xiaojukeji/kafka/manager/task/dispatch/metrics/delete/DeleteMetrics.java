package com.xiaojukeji.kafka.manager.task.dispatch.metrics.delete;

import com.xiaojukeji.kafka.manager.common.utils.BackoffUtils;
import com.xiaojukeji.kafka.manager.dao.*;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import com.xiaojukeji.kafka.manager.task.component.EmptyEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 定期删除Metrics信息
 * @author zengqiao
 * @date 20/1/8
 */
@CustomScheduled(name = "deleteMetrics", cron = "0 0/2 * * * ?", threadNum = 1, description = "定期删除Metrics信息")
public class DeleteMetrics extends AbstractScheduledTask<EmptyEntry> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteMetrics.class);

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

    @Value(value = "${task.metrics.delete.delete-limit-size:1000}")
    private Integer deleteLimitSize;

    @Value(value = "${task.metrics.delete.cluster-metrics-save-days:14}")
    private Integer clusterMetricsSaveDays;

    @Value(value = "${task.metrics.delete.broker-metrics-save-days:14}")
    private Integer brokerMetricsSaveDays;

    @Value(value = "${task.metrics.delete.topic-metrics-save-days:7}")
    private Integer topicMetricsSaveDays;

    @Value(value = "${task.metrics.delete.topic-request-time-metrics-save-days:7}")
    private Integer topicRequestTimeMetricsSaveDays;

    @Value(value = "${task.metrics.delete.topic-throttled-metrics-save-days:7}")
    private Integer topicThrottledMetricsSaveDays;

    @Value(value = "${task.metrics.delete.app-topic-metrics-save-days:7}")
    private Integer appTopicMetricsSaveDays;

    @Override
    public List<EmptyEntry> listAllTasks() {
        EmptyEntry emptyEntry = new EmptyEntry();
        emptyEntry.setId(System.currentTimeMillis() / 1000);
        return Arrays.asList(emptyEntry);
    }

    @Override
    public void processTask(EmptyEntry entryEntry) {
        long startTime = System.currentTimeMillis();
        LOGGER.info("start delete metrics");

        // 数据量可能比较大，一次触发多删除几次
        for (int i = 0; i < 10; ++i) {
            try {
                boolean needReDelete = this.deleteCommunityTopicMetrics();
                if (!needReDelete) {
                    break;
                }

                // 暂停1000毫秒，避免删除太快导致DB出现问题
                BackoffUtils.backoff(1000);
            } catch (Exception e) {
                LOGGER.error("delete community topic metrics failed.", e);
            }
        }

        // 数据量可能比较大，一次触发多删除几次
        for (int i = 0; i < 10; ++i) {
            try {
                boolean needReDelete = this.deleteDiDiTopicMetrics();
                if (!needReDelete) {
                    break;
                }

                // 暂停1000毫秒，避免删除太快导致DB出现问题
                BackoffUtils.backoff(1000);
            } catch (Exception e) {
                LOGGER.error("delete didi topic metrics failed.", e);
            }
        }

        try {
            this.deleteClusterBrokerMetrics();
        } catch (Exception e) {
            LOGGER.error("delete cluster and broker metrics failed.", e);
        }

        LOGGER.info("finish delete metrics, costTime:{}ms.", System.currentTimeMillis() - startTime);
    }

    private boolean deleteCommunityTopicMetrics() {
        return topicMetricsDao.deleteBeforeTime(new Date(System.currentTimeMillis() - this.topicMetricsSaveDays * 24L * 60L* 60L * 1000L), this.deleteLimitSize) >= this.deleteLimitSize;
    }

    private boolean deleteDiDiTopicMetrics() {
        boolean needReDelete = false;

        if (topicAppMetricsDao.deleteBeforeTime(new Date(System.currentTimeMillis() - this.appTopicMetricsSaveDays * 24L * 60L* 60L * 1000L), this.deleteLimitSize) >= this.deleteLimitSize) {
            needReDelete = true;
        }

        if (topicRequestMetricsDao.deleteBeforeTime(new Date(System.currentTimeMillis() - this.topicRequestTimeMetricsSaveDays * 24L * 60L* 60L * 1000L), this.deleteLimitSize) >= this.deleteLimitSize) {
            needReDelete = true;
        }

        if (topicThrottledMetricsDao.deleteBeforeTime(new Date(System.currentTimeMillis() - this.topicThrottledMetricsSaveDays * 24L * 60L* 60L * 1000L), this.deleteLimitSize) >= this.deleteLimitSize) {
            needReDelete = true;
        }

        return needReDelete;
    }

    private void deleteClusterBrokerMetrics() {
        brokerMetricsDao.deleteBeforeTime(new Date(System.currentTimeMillis() - this.brokerMetricsSaveDays * 24L * 60L* 60L * 1000L), this.deleteLimitSize);

        clusterMetricsDao.deleteBeforeTime(new Date(System.currentTimeMillis() - this.clusterMetricsSaveDays * 24L * 60L * 60L * 1000L), this.deleteLimitSize);
    }
}