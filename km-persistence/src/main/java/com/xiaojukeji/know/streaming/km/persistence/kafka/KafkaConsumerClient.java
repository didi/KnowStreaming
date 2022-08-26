package com.xiaojukeji.know.streaming.km.persistence.kafka;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.KafkaConsumerFactory;
import com.xiaojukeji.know.streaming.km.persistence.AbstractClusterLoadedChangedHandler;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class KafkaConsumerClient extends AbstractClusterLoadedChangedHandler implements KafkaClient<KafkaConsumer<String, String>> {
    private static final ILog log = LogFactory.getLog(KafkaConsumerClient.class);

    @Value(value = "${client-pool.kafka-consumer.min-idle-client-num:24}")
    private Integer kafkaConsumerMinIdleClientNum;

    @Value(value = "${client-pool.kafka-consumer.max-idle-client-num:24}")
    private Integer kafkaConsumerMaxIdleClientNum;

    @Value(value = "${client-pool.kafka-consumer.max-total-client-num:24}")
    private Integer kafkaConsumerMaxTotalClientNum;

    @Value(value = "${client-pool.kafka-consumer.borrow-timeout-unit-ms:3000}")
    private Integer kafkaConsumerBorrowTimeoutUnitMs;

    private static final Map<Long, GenericObjectPool<KafkaConsumer<String, String>>> KAFKA_CONSUMER_POOL = new ConcurrentHashMap<>();

    private static final ReentrantLock modifyPoolLock = new ReentrantLock();

    @Override
    public KafkaConsumer<String, String> getClient(Long clusterPhyId) throws NotExistException {
        KafkaConsumer<String, String> kafkaConsumer =  this.borrowClientTryInitIfNotExist(clusterPhyId);
        if (kafkaConsumer == null) {
            throw new NotExistException(String.format("clusterPhyId:%d kafkaConsumer is null", clusterPhyId));
        }

        return kafkaConsumer;
    }

    public void returnClient(Long clusterPhyId, KafkaConsumer<String, String> kafkaConsumer) {
        GenericObjectPool<KafkaConsumer<String, String>> objectPool = KAFKA_CONSUMER_POOL.get(clusterPhyId);
        if (objectPool == null) {
            return;
        }

        try {
            objectPool.returnObject(kafkaConsumer);
        } catch (Exception e) {
            log.error("method=returnClient||clusterPhyId={}||errMsg=exception!", clusterPhyId, e);
        }
    }

    /**************************************************** private method ****************************************************/

    @Override
    protected void add(ClusterPhy clusterPhy) {
        // ignore 后续按需创建，因此这里的操作直接忽略
    }

    @Override
    protected void modify(ClusterPhy newClusterPhy, ClusterPhy oldClusterPhy) {
        if (newClusterPhy.getBootstrapServers().equals(oldClusterPhy.getBootstrapServers())
                && newClusterPhy.getClientProperties().equals(oldClusterPhy.getClientProperties())) {
            // 集群信息虽然变化，但是服务地址和client配置没有变化，则直接返回
            return;
        }

        // 去除历史的，新的继续按需创建
        this.remove(newClusterPhy);
    }

    @Override
    protected void remove(ClusterPhy clusterPhy) {
        GenericObjectPool<KafkaConsumer<String, String>> genericObjectPool = KAFKA_CONSUMER_POOL.remove(clusterPhy.getId());
        if (genericObjectPool == null) {
            return;
        }

        genericObjectPool.close();
    }

    private KafkaConsumer<String, String> borrowClientTryInitIfNotExist(Long clusterPhyId) {
        GenericObjectPool<KafkaConsumer<String, String>> objectPool = KAFKA_CONSUMER_POOL.get(clusterPhyId);
        if (objectPool == null) {
            initKafkaConsumerPool(LoadedClusterPhyCache.getByPhyId(clusterPhyId));
            objectPool = KAFKA_CONSUMER_POOL.get(clusterPhyId);
        }

        if (objectPool == null) {
            return null;
        }

        try {
            return objectPool.borrowObject(kafkaConsumerBorrowTimeoutUnitMs);
        } catch (Exception e) {
            log.error("method=borrowClientTryInitIfNotExist||clusterPhyId={}||errMsg=exception!", clusterPhyId, e);
        }
        return null;
    }

    private void initKafkaConsumerPool(ClusterPhy clusterPhy) {
        modifyPoolLock.lock();
        try {
            if (clusterPhy == null) {
                return;
            }

            GenericObjectPool<KafkaConsumer<String, String>> objectPool = KAFKA_CONSUMER_POOL.get(clusterPhy.getId());
            if (objectPool != null) {
                return;
            }

            GenericObjectPoolConfig<KafkaConsumer<String, String>> config = new GenericObjectPoolConfig<>();
            config.setMaxIdle(kafkaConsumerMaxIdleClientNum);
            config.setMinIdle(kafkaConsumerMinIdleClientNum);
            config.setMaxTotal(kafkaConsumerMaxTotalClientNum);

            KAFKA_CONSUMER_POOL.put(clusterPhy.getId(), new GenericObjectPool<>(new KafkaConsumerFactory(clusterPhy), config));
        } catch (Exception e) {
            log.error("method=initKafkaConsumerPool||clusterPhy={}||errMsg=exception!", clusterPhy, e);
        } finally {
            modifyPoolLock.unlock();
        }
    }
}
