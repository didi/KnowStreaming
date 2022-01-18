package com.xiaojukeji.kafka.manager.service.cache;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.utils.factory.KafkaConsumerFactory;
import kafka.admin.AdminClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Cache Kafka客户端
 * @author zengqiao
 * @date 19/12/24
 */
@Service
public class KafkaClientPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaClientPool.class);

    @Value(value = "${client-pool.kafka-consumer.min-idle-client-num:24}")
    private Integer kafkaConsumerMinIdleClientNum;

    @Value(value = "${client-pool.kafka-consumer.max-idle-client-num:24}")
    private Integer kafkaConsumerMaxIdleClientNum;

    @Value(value = "${client-pool.kafka-consumer.max-total-client-num:24}")
    private Integer kafkaConsumerMaxTotalClientNum;

    @Value(value = "${client-pool.kafka-consumer.borrow-timeout-unit-ms:3000}")
    private Integer kafkaConsumerBorrowTimeoutUnitMs;

    /**
     * AdminClient
     */
    private static final Map<Long, AdminClient> ADMIN_CLIENT_MAP = new ConcurrentHashMap<>();

    private static final Map<Long, KafkaProducer<String, String>> KAFKA_PRODUCER_MAP = new ConcurrentHashMap<>();

    private static final Map<Long, GenericObjectPool<KafkaConsumer<String, String>>> KAFKA_CONSUMER_POOL = new ConcurrentHashMap<>();

    private static ReentrantLock lock = new ReentrantLock();

    private KafkaClientPool() {
    }

    private static void initKafkaProducerMap(Long clusterId) {
        ClusterDO clusterDO = PhysicalClusterMetadataManager.getClusterFromCache(clusterId);
        if (clusterDO == null) {
            return;
        }

        lock.lock();
        try {
            KafkaProducer<String, String> kafkaProducer = KAFKA_PRODUCER_MAP.get(clusterId);
            if (!ValidateUtils.isNull(kafkaProducer)) {
                return;
            }
            Properties properties = createProperties(clusterDO, true);
            properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
            properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "10");
            properties.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
            KAFKA_PRODUCER_MAP.put(clusterId, new KafkaProducer<>(properties));
        } catch (Exception e) {
            LOGGER.error("create kafka producer failed, clusterDO:{}.", clusterDO, e);
        } finally {
            lock.unlock();
        }
    }

    public static boolean produceData2Kafka(Long clusterId, String topicName, String data) {
        if (!PhysicalClusterMetadataManager.isTopicExist(clusterId, topicName)) {
            // Topic不存在
            return false;
        }

        KafkaProducer<String, String> kafkaProducer = KAFKA_PRODUCER_MAP.get(clusterId);
        if (ValidateUtils.isNull(kafkaProducer)) {
            initKafkaProducerMap(clusterId);
            kafkaProducer = KAFKA_PRODUCER_MAP.get(clusterId);
        }
        if (ValidateUtils.isNull(kafkaProducer)) {
            return false;
        }
        kafkaProducer.send(new ProducerRecord<>(topicName, data));
        return true;
    }

    private void initKafkaConsumerPool(ClusterDO clusterDO) {
        lock.lock();
        try {
            GenericObjectPool<KafkaConsumer<String, String>> objectPool = KAFKA_CONSUMER_POOL.get(clusterDO.getId());
            if (objectPool != null) {
                return;
            }
            GenericObjectPoolConfig<KafkaConsumer<String, String>> config = new GenericObjectPoolConfig<>();
            config.setMaxIdle(kafkaConsumerMaxIdleClientNum);
            config.setMinIdle(kafkaConsumerMinIdleClientNum);
            config.setMaxTotal(kafkaConsumerMaxTotalClientNum);
            KAFKA_CONSUMER_POOL.put(clusterDO.getId(), new GenericObjectPool<>(new KafkaConsumerFactory(clusterDO), config));
        } catch (Exception e) {
            LOGGER.error("create kafka consumer pool failed, clusterDO:{}.", clusterDO, e);
        } finally {
            lock.unlock();
        }
    }

    public static void closeKafkaConsumerPool(Long clusterId) {
        lock.lock();
        try {
            GenericObjectPool<KafkaConsumer<String, String>> objectPool = KAFKA_CONSUMER_POOL.remove(clusterId);
            if (objectPool == null) {
                return;
            }
            objectPool.close();
        } catch (Exception e) {
            LOGGER.error("close kafka consumer pool failed, clusterId:{}.", clusterId, e);
        } finally {
            lock.unlock();
        }
    }

    public KafkaConsumer<String, String> borrowKafkaConsumerClient(ClusterDO clusterDO) {
        if (ValidateUtils.isNull(clusterDO)) {
            return null;
        }
        GenericObjectPool<KafkaConsumer<String, String>> objectPool = KAFKA_CONSUMER_POOL.get(clusterDO.getId());
        if (ValidateUtils.isNull(objectPool)) {
            initKafkaConsumerPool(clusterDO);
            objectPool = KAFKA_CONSUMER_POOL.get(clusterDO.getId());
        }
        if (ValidateUtils.isNull(objectPool)) {
            return null;
        }

        try {
            return objectPool.borrowObject(kafkaConsumerBorrowTimeoutUnitMs);
        } catch (Exception e) {
            LOGGER.error("borrow kafka consumer client failed, clusterDO:{}.", clusterDO, e);
        }
        return null;
    }

    public static void returnKafkaConsumerClient(Long physicalClusterId, KafkaConsumer<String, String> kafkaConsumer) {
        if (ValidateUtils.isNull(physicalClusterId) || ValidateUtils.isNull(kafkaConsumer)) {
            return;
        }
        GenericObjectPool<KafkaConsumer<String, String>> objectPool = KAFKA_CONSUMER_POOL.get(physicalClusterId);
        if (ValidateUtils.isNull(objectPool)) {
            return;
        }
        try {
            objectPool.returnObject(kafkaConsumer);
        } catch (Exception e) {
            LOGGER.error("return kafka consumer client failed, clusterId:{}", physicalClusterId, e);
        }
    }

    public static AdminClient getAdminClient(Long clusterId) {
        AdminClient adminClient = ADMIN_CLIENT_MAP.get(clusterId);
        if (adminClient != null) {
            return adminClient;
        }
        ClusterDO clusterDO = PhysicalClusterMetadataManager.getClusterFromCache(clusterId);
        if (clusterDO == null) {
            return null;
        }
        Properties properties = createProperties(clusterDO, false);
        lock.lock();
        try {
            adminClient = ADMIN_CLIENT_MAP.get(clusterId);
            if (adminClient != null) {
                return adminClient;
            }
            ADMIN_CLIENT_MAP.put(clusterId, AdminClient.create(properties));
        } catch (Exception e) {
            LOGGER.error("create kafka admin client failed, clusterId:{}.", clusterId, e);
        } finally {
            lock.unlock();
        }
        return ADMIN_CLIENT_MAP.get(clusterId);
    }

    public static void closeAdminClient(ClusterDO cluster) {
        if (ADMIN_CLIENT_MAP.containsKey(cluster.getId())) {
            ADMIN_CLIENT_MAP.get(cluster.getId()).close();
        }
    }

    public static Properties createProperties(ClusterDO clusterDO, boolean serialize) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, clusterDO.getBootstrapServers());
        if (serialize) {
            properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        } else {
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        }
        if (ValidateUtils.isBlank(clusterDO.getSecurityProperties())) {
            return properties;
        }
        properties.putAll(JsonUtils.stringToObj(clusterDO.getSecurityProperties(), Properties.class));
        return properties;
    }
}