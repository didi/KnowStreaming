package com.xiaojukeji.kafka.manager.service.cache;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
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

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Cache Kafka客户端
 * @author zengqiao
 * @date 19/12/24
 */
public class KafkaClientPool {
    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaClientPool.class);

    /**
     * AdminClient
     */
    private static Map<Long, AdminClient> AdminClientMap = new ConcurrentHashMap<>();

    private static Map<Long, KafkaProducer<String, String>> KAFKA_PRODUCER_MAP = new ConcurrentHashMap<>();

    private static Map<Long, GenericObjectPool<KafkaConsumer>> KAFKA_CONSUMER_POOL = new ConcurrentHashMap<>();

    private static ReentrantLock lock = new ReentrantLock();

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
            KAFKA_PRODUCER_MAP.put(clusterId, new KafkaProducer<String, String>(properties));
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
        kafkaProducer.send(new ProducerRecord<String, String>(topicName, data));
        return true;
    }

    private static void initKafkaConsumerPool(ClusterDO clusterDO) {
        lock.lock();
        try {
            GenericObjectPool<KafkaConsumer> objectPool = KAFKA_CONSUMER_POOL.get(clusterDO.getId());
            if (objectPool != null) {
                return;
            }
            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            config.setMaxIdle(24);
            config.setMinIdle(24);
            config.setMaxTotal(24);
            KAFKA_CONSUMER_POOL.put(
                    clusterDO.getId(),
                    new GenericObjectPool<KafkaConsumer>(new KafkaConsumerFactory(clusterDO), config)
            );
        } catch (Exception e) {
            LOGGER.error("create kafka consumer pool failed, clusterDO:{}.", clusterDO, e);
        } finally {
            lock.unlock();
        }
    }

    public static void closeKafkaConsumerPool(Long clusterId) {
        lock.lock();
        try {
            GenericObjectPool<KafkaConsumer> objectPool = KAFKA_CONSUMER_POOL.remove(clusterId);
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

    public static KafkaConsumer borrowKafkaConsumerClient(ClusterDO clusterDO) {
        if (ValidateUtils.isNull(clusterDO)) {
            return null;
        }
        GenericObjectPool<KafkaConsumer> objectPool = KAFKA_CONSUMER_POOL.get(clusterDO.getId());
        if (ValidateUtils.isNull(objectPool)) {
            initKafkaConsumerPool(clusterDO);
            objectPool = KAFKA_CONSUMER_POOL.get(clusterDO.getId());
        }
        if (ValidateUtils.isNull(objectPool)) {
            return null;
        }

        try {
            return objectPool.borrowObject(3000);
        } catch (Exception e) {
            LOGGER.error("borrow kafka consumer client failed, clusterDO:{}.", clusterDO, e);
        }
        return null;
    }

    public static void returnKafkaConsumerClient(Long physicalClusterId, KafkaConsumer kafkaConsumer) {
        if (ValidateUtils.isNull(physicalClusterId) || ValidateUtils.isNull(kafkaConsumer)) {
            return;
        }
        GenericObjectPool<KafkaConsumer> objectPool = KAFKA_CONSUMER_POOL.get(physicalClusterId);
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
        AdminClient adminClient = AdminClientMap.get(clusterId);
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
            adminClient = AdminClientMap.get(clusterId);
            if (adminClient != null) {
                return adminClient;
            }
            AdminClientMap.put(clusterId, AdminClient.create(properties));
        } catch (Exception e) {
            LOGGER.error("create kafka admin client failed, clusterId:{}.", clusterId, e);
        } finally {
            lock.unlock();
        }
        return AdminClientMap.get(clusterId);
    }

    public static void closeAdminClient(ClusterDO cluster) {
        if (AdminClientMap.containsKey(cluster.getId())) {
            AdminClientMap.get(cluster.getId()).close();
        }
    }

    public static Properties createProperties(ClusterDO clusterDO, Boolean serialize) {
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
        Properties securityProperties = JSONObject.parseObject(clusterDO.getSecurityProperties(), Properties.class);
        properties.putAll(securityProperties);
        return properties;
    }
}