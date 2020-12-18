package com.xiaojukeji.kafka.manager.task.listener;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaClientEnum;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicThrottledMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicThrottledMetricsDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.ThrottleService;
import com.xiaojukeji.kafka.manager.task.common.TopicThrottledMetricsCollectedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author zengqiao
 * @date 20/9/24
 */
@Component("storeTopicThrottledMetrics2DB")
@ConditionalOnProperty(prefix = "custom.store-metrics-task.didi", name = "topic-throttled-metrics", havingValue = "true", matchIfMissing = true)
public class StoreTopicThrottledMetrics2DB implements ApplicationListener<TopicThrottledMetricsCollectedEvent> {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private ThrottleService throttleService;

    @Override
    public void onApplicationEvent(TopicThrottledMetricsCollectedEvent event) {
        List<TopicThrottledMetrics> metrics = event.getMetricsList();
        if (ValidateUtils.isEmptyList(metrics)) {
            return;
        }
        Long clusterId = metrics.get(0).getClusterId();
        store2DB(clusterId, convert2TopicThrottledMetricsDO(metrics));
    }

    private List<TopicThrottledMetricsDO> convert2TopicThrottledMetricsDO(List<TopicThrottledMetrics> metricsList) {
        Map<String, TopicThrottledMetricsDO> doMap = new HashMap<>();
        for (TopicThrottledMetrics metrics: metricsList) {
            String key = new StringBuilder()
                    .append(metrics.getClusterId())
                    .append(metrics.getAppId())
                    .append(metrics.getTopicName())
                    .toString();
            TopicThrottledMetricsDO metricsDO = doMap.get(key);
            if (ValidateUtils.isNull(metricsDO)) {
                metricsDO = new TopicThrottledMetricsDO();
                metricsDO.setAppId(metrics.getAppId());
                metricsDO.setClusterId(metrics.getClusterId());
                metricsDO.setTopicName(metrics.getTopicName());
                metricsDO.setFetchThrottled(0);
                metricsDO.setProduceThrottled(0);
            }
            if (KafkaClientEnum.PRODUCE_CLIENT.equals(metrics.getClientType())) {
                metricsDO.setProduceThrottled(1);
            } else {
                metricsDO.setFetchThrottled(1);
            }
            doMap.put(key, metricsDO);
        }
        return new ArrayList<>(doMap.values());
    }

    private void store2DB(Long clusterId,
                          List<TopicThrottledMetricsDO> doList) {
        if (ValidateUtils.isEmptyList(doList)) {
            return;
        }
        int i = 0;
        do {
            try {
                throttleService.insertBatch(doList.subList(
                        i,
                        Math.min(i + Constant.BATCH_INSERT_SIZE, doList.size()))
                );
            } catch (Exception e) {
                LOGGER.error("store topic throttled metrics failed, clusterId:{}.", clusterId);
            }

            i += Constant.BATCH_INSERT_SIZE;
        } while (i < doList.size());
    }
}