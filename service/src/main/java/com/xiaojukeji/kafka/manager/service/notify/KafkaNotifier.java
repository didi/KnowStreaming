package com.xiaojukeji.kafka.manager.service.notify;

import com.xiaojukeji.kafka.manager.service.cache.KafkaClientCache;
import com.xiaojukeji.kafka.manager.service.monitor.AlarmScheduleCheckTask;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author zengqiao
 * @date 20/3/18
 */
@Component
public class KafkaNotifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmScheduleCheckTask.class);

    public void produce(Long clusterId, String topicName, String message) {
        KafkaProducer<String, String> kafkaProducer = KafkaClientCache.getKafkaProducerClient(clusterId);
        if (kafkaProducer == null) {
            LOGGER.error("param illegal, get kafka producer client failed, clusterId:{}.", clusterId);
            return;
        }

        kafkaProducer.send(new ProducerRecord<String, String>(topicName, message), new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception exception) {
                if (null != exception) {
                    LOGGER.info("produce failed, topicName:{} recordMetadata:{} exception:{}.", topicName, recordMetadata, exception);
                }
            }
        });
    }
}