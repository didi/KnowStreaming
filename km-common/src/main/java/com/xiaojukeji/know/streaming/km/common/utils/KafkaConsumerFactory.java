package com.xiaojukeji.know.streaming.km.common.utils;

import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

/**
 * KafkaConsumer工厂
 * @author zengqiao
 * @date 20/8/24
 */
public class KafkaConsumerFactory extends BasePooledObjectFactory<KafkaConsumer<String, String>> {
    private final ClusterPhy clusterPhy;

    public KafkaConsumerFactory(ClusterPhy clusterPhy) {
        this.clusterPhy = clusterPhy;
    }

    @Override
    public KafkaConsumer<String, String> create() {
        return new KafkaConsumer<>(createClientProperties(clusterPhy));
    }

    @Override
    public PooledObject<KafkaConsumer<String, String>> wrap(KafkaConsumer<String, String> obj) {
        return new DefaultPooledObject<>(obj);
    }

    @Override
    public void destroyObject(final PooledObject<KafkaConsumer<String, String>> p) throws Exception  {
        KafkaConsumer<String, String> kafkaConsumer = p.getObject();
        if (ValidateUtils.isNull(kafkaConsumer)) {
            return;
        }

        kafkaConsumer.close();
    }

    private static Properties createClientProperties(ClusterPhy clusterPhy) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, clusterPhy.getBootstrapServers());
        properties.setProperty(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);
        properties.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 15000);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        if (ValidateUtils.isBlank(clusterPhy.getClientProperties())) {
            return properties;
        }

        properties.putAll(ConvertUtil.str2ObjByJson(clusterPhy.getClientProperties(), Properties.class));
        return properties;
    }
}