package com.xiaojukeji.kafka.manager.task.listener;

import com.xiaojukeji.kafka.manager.common.constant.ConfigConstant;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.entity.ao.config.TopicNameConfig;
import com.xiaojukeji.kafka.manager.common.entity.ao.remote.KafkaConsumerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.ao.remote.KafkaConsumerMetricsElem;
import com.xiaojukeji.kafka.manager.common.entity.metrics.ConsumerMetrics;
import com.xiaojukeji.kafka.manager.common.events.ConsumerMetricsCollectedEvent;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.cache.KafkaClientPool;
import com.xiaojukeji.kafka.manager.service.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/8/31
 */
@Component("produceConsumerMetrics")
public class SinkConsumerMetrics2Kafka implements ApplicationListener<ConsumerMetricsCollectedEvent> {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private ConfigService configService;

    @Override
    public void onApplicationEvent(ConsumerMetricsCollectedEvent event) {
        List<ConsumerMetrics> metricsList = event.getMetricsList();
        if (ValidateUtils.isEmptyList(metricsList)) {
            LOGGER.warn("produce consumer metrics failed, data is empty.");
            return;
        }

        TopicNameConfig config = configService.getByKey(
                ConfigConstant.PRODUCE_CONSUMER_METRICS_CONFIG_KEY,
                TopicNameConfig.class
        );
        if (ValidateUtils.isNull(config) || !config.legal()) {
            LOGGER.warn("produce consumer metrics failed, config illegal, config:{}.", config);
            return;
        }

        Long now = System.currentTimeMillis();
        for (ConsumerMetrics consumerMetrics: metricsList) {
            try {
                convertAndProduceMetrics(consumerMetrics, config, now);
            } catch (Exception e) {
                LOGGER.error("convert and produce failed, metrics:{}.", consumerMetrics);
            }
        }
    }

    private void convertAndProduceMetrics(ConsumerMetrics consumerMetrics,
                                          TopicNameConfig config,
                                          Long now) {
        KafkaConsumerMetrics kafkaConsumerMetrics = new KafkaConsumerMetrics();
        kafkaConsumerMetrics.setClusterId(consumerMetrics.getClusterId());
        kafkaConsumerMetrics.setTopicName(consumerMetrics.getTopicName());
        kafkaConsumerMetrics.setConsumerGroup(consumerMetrics.getConsumerGroup());
        kafkaConsumerMetrics.setLocation(consumerMetrics.getLocation().toLowerCase());
        kafkaConsumerMetrics.setPartitionNum(consumerMetrics.getPartitionOffsetMap().size());
        kafkaConsumerMetrics.setCreateTime(now);

        List<KafkaConsumerMetricsElem> elemList = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry: consumerMetrics.getPartitionOffsetMap().entrySet()) {
            KafkaConsumerMetricsElem elem = new KafkaConsumerMetricsElem();
            if (ValidateUtils.isNull(entry.getKey()) || ValidateUtils.isNull(entry.getValue())) {
                LOGGER.error("sink consumer metrics 2 kafka failed, exist null data, consumer-metrics:{}."
                        , consumerMetrics);
                return;
            }
            elem.setPartitionId(entry.getKey());
            elem.setPartitionOffset(entry.getValue());
            elem.setConsumeOffset(consumerMetrics.getConsumeOffsetMap().get(entry.getKey()));
            elemList.add(elem);
        }
        kafkaConsumerMetrics.setConsumeDetailList(elemList);
        KafkaClientPool.produceData2Kafka(
                config.getClusterId(),
                config.getTopicName(),
                JsonUtils.toJSONString(kafkaConsumerMetrics)
        );
    }

}