package com.xiaojukeji.kafka.manager.task.listener;

import com.xiaojukeji.kafka.manager.common.constant.ConfigConstant;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.entity.ao.config.TopicNameConfig;
import com.xiaojukeji.kafka.manager.common.entity.ao.remote.KafkaTopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.events.TopicMetricsCollectedEvent;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.service.cache.KafkaClientPool;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据上报Kafka
 * @author zengqiao
 * @date 20/8/31
 */
@Component("sinkCommunityTopicMetrics2Kafka")
public class SinkCommunityTopicMetrics2Kafka implements ApplicationListener<TopicMetricsCollectedEvent> {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private ConfigService configService;

    @Override
    public void onApplicationEvent(TopicMetricsCollectedEvent event) {
        List<TopicMetrics> metricsList = event.getMetricsList();
        if (ValidateUtils.isEmptyList(metricsList)) {
            LOGGER.warn("produce topic metrics failed, data is empty.");
            return;
        }

        TopicNameConfig config = configService.getByKey(
                ConfigConstant.PRODUCE_TOPIC_METRICS_CONFIG_KEY,
                TopicNameConfig.class
        );
        if (ValidateUtils.isNull(config) || !config.legal()) {
            LOGGER.warn("produce consumer metrics failed, config illegal, config:{}.", config);
            return;
        }

        Long now = System.currentTimeMillis();
        for (TopicMetrics metrics: metricsList) {
            try {
                convertAndProduceMetrics(metrics, config, now);
            } catch (Exception e) {
                LOGGER.error("convert and produce failed, metrics:{}.", metrics);
            }
        }
    }

    private void convertAndProduceMetrics(TopicMetrics metrics,
                                          TopicNameConfig config,
                                          Long now) {
        KafkaTopicMetrics kafkaTopicMetrics = new KafkaTopicMetrics();
        kafkaTopicMetrics.setClusterId(metrics.getClusterId());
        kafkaTopicMetrics.setTopic(metrics.getTopicName());
        TopicMetadata topicMetadata =
                PhysicalClusterMetadataManager.getTopicMetadata(metrics.getClusterId(), metrics.getTopicName());
        if (!ValidateUtils.isNull(topicMetadata)) {
            kafkaTopicMetrics.setPartitionNum(topicMetadata.getPartitionNum());
        }
        kafkaTopicMetrics.setMessagesInPerSec(metrics.getMessagesInPerSecOneMinuteRate(null));
        kafkaTopicMetrics.setBytesInPerSec(metrics.getBytesInPerSecOneMinuteRate(null));
        kafkaTopicMetrics.setTimestamp(now);
        KafkaClientPool.produceData2Kafka(
                config.getClusterId(),
                config.getTopicName(),
                JsonUtils.toJSONString(kafkaTopicMetrics)
        );
    }
}