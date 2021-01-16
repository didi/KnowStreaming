package com.xiaojukeji.kafka.manager.service.strategy.healthscore;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.ThreadPool;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import com.xiaojukeji.kafka.manager.service.strategy.AbstractHealthScoreStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author zengqiao
 * @date 20/9/23
 */
@Service("healthScoreStrategy")
public class DidiHealthScoreStrategy extends AbstractHealthScoreStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(DidiHealthScoreStrategy.class);

    private static final Integer QUEUED_MAX_REQUESTS = 500;

    private static final Integer KAFKA_REQUEST_HANDLER_POOL_SIZE = 3;

    private static final Double MIN_IDLE = 0.8;

    private static final Integer HEALTH_SCORE_HEALTHY = 100;

    private static final Integer HEALTH_SCORE_NORMAL = 90;

    private static final Integer HEALTH_SCORE_BAD = 60;

    private static final Integer HEALTH_SCORE_VERY_BAD = 30;

    @Autowired
    private JmxService jmxService;

    @Override
    public Integer calBrokerHealthScore(Long clusterId, Integer brokerId) {
        BrokerMetadata brokerMetadata = PhysicalClusterMetadataManager.getBrokerMetadata(clusterId, brokerId);
        if (ValidateUtils.isNull(brokerMetadata)) {
            return HEALTH_SCORE_HEALTHY;
        }

        BrokerMetrics metrics = jmxService.getBrokerMetrics(clusterId, brokerId, KafkaMetricsCollections.BROKER_HEALTH_SCORE_METRICS);
        if (ValidateUtils.isNull(metrics)) {
            return Constant.INVALID_CODE;
        }
        return calBrokerHealthScore(clusterId, brokerId, metrics);
    }

    @Override
    public Integer calBrokerHealthScore(Long clusterId, Integer brokerId, BrokerMetrics metrics) {
        try {
            if (ValidateUtils.isNull(metrics)) {
                return Constant.INVALID_CODE;
            }

            Object failedFetchRequestsPerSecOneMinuteRate = metrics.getSpecifiedMetrics("FailedFetchRequestsPerSecOneMinuteRate");
            Object failedProduceRequestsPerSecOneMinuteRate = metrics.getSpecifiedMetrics("FailedProduceRequestsPerSecOneMinuteRate");
            if (ValidateUtils.isNull(failedFetchRequestsPerSecOneMinuteRate) || ValidateUtils.isNull(failedProduceRequestsPerSecOneMinuteRate)) {
                // 数据获取失败
                return Constant.INVALID_CODE;
            }
            if (((Double) failedFetchRequestsPerSecOneMinuteRate) > 0.01
                    || ((Double) failedProduceRequestsPerSecOneMinuteRate) > 0.01) {
                return HEALTH_SCORE_VERY_BAD;
            }

            Object requestQueueSizeValue = metrics.getMetricsMap().get("RequestQueueSizeValue");
            Object responseQueueSizeValue = metrics.getMetricsMap().get("ResponseQueueSizeValue");
            if (ValidateUtils.isNull(requestQueueSizeValue)
                    || ValidateUtils.isNull(responseQueueSizeValue)) {
                // 数据获取失败
                return Constant.INVALID_CODE;
            }
            if (((Integer) requestQueueSizeValue) >= QUEUED_MAX_REQUESTS
                    || ((Integer) responseQueueSizeValue) >= QUEUED_MAX_REQUESTS) {
                return HEALTH_SCORE_BAD;
            }

            Object RequestHandlerAvgIdlePercentOneMinuteRate = metrics.getMetricsMap().get("RequestHandlerAvgIdlePercentOneMinuteRate");
            Object NetworkProcessorAvgIdlePercentValue = metrics.getMetricsMap().get("NetworkProcessorAvgIdlePercentValue");
            if (ValidateUtils.isNull(RequestHandlerAvgIdlePercentOneMinuteRate)
                    || ValidateUtils.isNull(NetworkProcessorAvgIdlePercentValue)) {
                // 数据获取失败
                return Constant.INVALID_CODE;
            }
            if (((Double) RequestHandlerAvgIdlePercentOneMinuteRate) < MIN_IDLE * KAFKA_REQUEST_HANDLER_POOL_SIZE
                    || ((Double) NetworkProcessorAvgIdlePercentValue) < MIN_IDLE) {
                return HEALTH_SCORE_NORMAL;
            }
            return HEALTH_SCORE_HEALTHY;
        } catch (Exception e) {
            LOGGER.error("cal broker health score failed, clusterId:{} brokerId:{}.", clusterId, brokerId, e);
        }
        return Constant.INVALID_CODE;
    }

    @Override
    public Integer calTopicHealthScore(Long clusterId, String topicName) {
        TopicMetadata metadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
        if (ValidateUtils.isNull(metadata)) {
            return Constant.INVALID_CODE;
        }

        List<Integer> brokerIdList = new ArrayList<>(metadata.getBrokerIdSet().size());

        FutureTask<Integer>[] taskList = new FutureTask[brokerIdList.size()];
        for (int i = 0; i < brokerIdList.size(); ++i) {
            final Integer brokerId = brokerIdList.get(i);
            taskList[i] = new FutureTask<Integer>(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return calBrokerHealthScore(clusterId, brokerId);
                }
            });
            ThreadPool.submitApiCallTask(taskList[i]);
        }

        Integer topicHealthScore = HEALTH_SCORE_HEALTHY;
        for (int i = 0; i < taskList.length; ++i) {
            try {
                Integer brokerHealthScore = taskList[i].get();
                if (ValidateUtils.isNull(brokerHealthScore)) {
                    // 如果某台broker健康分计算失败, 则直接返回计算失败
                    return Constant.INVALID_CODE;
                }
                topicHealthScore = Math.min(topicHealthScore, brokerHealthScore);
            } catch (Exception e) {
                LOGGER.error("cal broker health failed, clusterId:{} brokerId:{}.",
                        clusterId, brokerIdList.get(i), e
                );
                return Constant.INVALID_CODE;
            }
        }
        return topicHealthScore;
    }
}