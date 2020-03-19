package com.xiaojukeji.kafka.manager.service.cache;

import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import kafka.admin.AdminClient;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Cache Kafka客户端
 * @author zengqiao
 * @date 19/12/24
 */
public class KafkaClientCache {
    private final static Logger logger = LoggerFactory.getLogger(KafkaClientCache.class);

    /**
     * AdminClient
     */
    private static Map<Long, AdminClient> AdminClientMap = new ConcurrentHashMap<>();

    /**
     * API侧接口使用的Client
     */
    private static Map<Long, KafkaConsumer> ApiKafkaConsumerClientMap = new ConcurrentHashMap<>();

    /**
     * Common使用的Client
     */
    private static Map<Long, KafkaConsumer> CommonKafkaConsumerClientMap = new ConcurrentHashMap<>();

    /**
     * 公共Producer, 一个集群至多一个
     */
    private static Map<Long, KafkaProducer<String, String>> KafkaProducerMap = new ConcurrentHashMap<>();

    private static ReentrantLock lock = new ReentrantLock();

    public static KafkaProducer<String, String> getKafkaProducerClient(Long clusterId) {
        KafkaProducer<String, String> kafkaProducer = KafkaProducerMap.get(clusterId);
        if (kafkaProducer != null) {
            return kafkaProducer;
        }
        ClusterDO clusterDO = ClusterMetadataManager.getClusterFromCache(clusterId);
        if (clusterDO == null) {
            return null;
        }
        Properties properties = createProperties(clusterDO, true);
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "10");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
        lock.lock();
        try {
            kafkaProducer = KafkaProducerMap.get(clusterId);
            if (kafkaProducer != null) {
                return kafkaProducer;
            }
            KafkaProducerMap.put(clusterId, new KafkaProducer<String, String>(properties));
        } catch (Exception e) {
            logger.error("create kafka producer client failed, clusterId:{}.", clusterId, e);
        } finally {
            lock.unlock();
        }
        return KafkaProducerMap.get(clusterId);
    }

    public static KafkaConsumer getApiKafkaConsumerClient(ClusterDO clusterDO) {
        if (clusterDO == null) {
            return null;
        }
        KafkaConsumer kafkaConsumer = ApiKafkaConsumerClientMap.get(clusterDO.getId());
        if (kafkaConsumer != null) {
            return kafkaConsumer;
        }
        Properties properties = createProperties(clusterDO, false);
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "10000");
        properties.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, "20000");
        properties.put("enable.auto.commit", "false");
        lock.lock();
        try {
            kafkaConsumer = ApiKafkaConsumerClientMap.get(clusterDO.getId());
            if (kafkaConsumer != null) {
                return kafkaConsumer;
            }
            ApiKafkaConsumerClientMap.put(clusterDO.getId(), new KafkaConsumer(properties));
        } catch (Exception e) {
            logger.error("create kafka consumer client failed, clusterId:{}.", clusterDO.getId(), e);
        } finally {
            lock.unlock();
        }
        return ApiKafkaConsumerClientMap.get(clusterDO.getId());
    }

    public static KafkaConsumer getCommonKafkaConsumerClient(Long clusterId) {
        KafkaConsumer kafkaConsumer = CommonKafkaConsumerClientMap.get(clusterId);
        if (kafkaConsumer != null) {
            return kafkaConsumer;
        }
        ClusterDO clusterDO = ClusterMetadataManager.getClusterFromCache(clusterId);
        if (clusterDO == null) {
            return null;
        }
        Properties properties = createProperties(clusterDO, false);
        properties.put("enable.auto.commit", "false");
        lock.lock();
        try {
            kafkaConsumer = CommonKafkaConsumerClientMap.get(clusterId);
            if (kafkaConsumer != null) {
                return kafkaConsumer;
            }
            CommonKafkaConsumerClientMap.put(clusterId, new KafkaConsumer(properties));
        } catch (Exception e) {
            logger.error("create kafka consumer client failed, clusterId:{}.", clusterId, e);
        } finally {
            lock.unlock();
        }
        return CommonKafkaConsumerClientMap.get(clusterId);
    }

    public static synchronized void closeApiKafkaConsumerClient(Long clusterId) {
        KafkaConsumer kafkaConsumer = ApiKafkaConsumerClientMap.remove(clusterId);
        if (kafkaConsumer == null) {
            return;
        }
        try {
            kafkaConsumer.close();
        } catch (Exception e) {
            logger.error("close kafka consumer client error, clusterId:{}.", clusterId, e);
        }
    }

    public static synchronized void closeCommonKafkaConsumerClient(Long clusterId) {
        KafkaConsumer kafkaConsumer = CommonKafkaConsumerClientMap.remove(clusterId);
        if (kafkaConsumer == null) {
            return;
        }
        try {
            kafkaConsumer.close();
        } catch (Exception e) {
            logger.error("close kafka consumer client error, clusterId:{}.", clusterId, e);
        }
    }

    public static AdminClient getAdminClient(Long clusterId) {
        AdminClient adminClient = AdminClientMap.get(clusterId);
        if (adminClient != null) {
            return adminClient;
        }
        ClusterDO clusterDO = ClusterMetadataManager.getClusterFromCache(clusterId);
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
            logger.error("create kafka admin client failed, clusterId:{}.", clusterId, e);
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
        if (!StringUtils.isEmpty(clusterDO.getSecurityProtocol())) {
            properties.setProperty (CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, clusterDO.getSecurityProtocol());
        }
        if (!StringUtils.isEmpty(clusterDO.getSaslMechanism())) {
            properties.setProperty ("sasl.mechanism", clusterDO.getSaslMechanism());
        }
        if (!StringUtils.isEmpty(clusterDO.getSaslJaasConfig())) {
            properties.put("sasl.jaas.config", clusterDO.getSaslJaasConfig());
        }
        return properties;
    }

    private static String key(Long clusterId, String topicName) {
        if (StringUtils.isEmpty(topicName)) {
            return String.valueOf(clusterId);
        }
        return String.valueOf(clusterId) + '_' + topicName;
    }
}