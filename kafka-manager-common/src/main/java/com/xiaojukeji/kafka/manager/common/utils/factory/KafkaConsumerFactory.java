package com.xiaojukeji.kafka.manager.common.utils.factory;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
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
    private ClusterDO clusterDO;

    public KafkaConsumerFactory(ClusterDO clusterDO) {
        this.clusterDO = clusterDO;
    }

    @Override
    public KafkaConsumer create() {
        return new KafkaConsumer<String, String>(createKafkaConsumerProperties(clusterDO));
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

    private static Properties createKafkaConsumerProperties(ClusterDO clusterDO) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, clusterDO.getBootstrapServers());
        properties.setProperty(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);
        properties.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 15000);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        if (ValidateUtils.isBlank(clusterDO.getSecurityProperties())) {
            return properties;
        }
        properties.putAll(JsonUtils.stringToObj(clusterDO.getSecurityProperties(), Properties.class));
        return properties;
    }
}