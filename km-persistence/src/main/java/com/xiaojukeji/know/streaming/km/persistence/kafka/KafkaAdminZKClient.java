package com.xiaojukeji.know.streaming.km.persistence.kafka;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.persistence.AbstractClusterLoadedChangedHandler;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import kafka.zk.AdminZkClient;
import kafka.zk.KafkaZkClient;
import org.apache.kafka.common.utils.Time;
import org.apache.zookeeper.client.ZKClientConfig;
import org.springframework.stereotype.Component;
import scala.Option;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class KafkaAdminZKClient extends AbstractClusterLoadedChangedHandler implements KafkaClient<KafkaZkClient> {
    private static final ILog log = LogFactory.getLog(KafkaAdminZKClient.class);

    /**
     * Kafka提供的KafkaZkClient
     */
    private static final Map<Long, KafkaZkClient> KAFKA_ZK_CLIENT_MAP = new ConcurrentHashMap<>();

    /**
     * zk-client最近创建时间
     */
    private static final Map<Long, Long> KAFKA_ZK_CLIENT_CREATE_TIME = new ConcurrentHashMap<>();

    @Override
    public KafkaZkClient getClient(Long clusterPhyId) throws NotExistException {
        KafkaZkClient kafkaZkClient = KAFKA_ZK_CLIENT_MAP.get(clusterPhyId);
        if (kafkaZkClient != null) {
            return kafkaZkClient;
        }

        kafkaZkClient = this.createZKClient(clusterPhyId);
        if (kafkaZkClient == null) {
            throw new NotExistException("kafka kafka-zk-client not exist due to create failed");
        }

        return kafkaZkClient;
    }

    public AdminZkClient getKafkaZKWrapClient(Long clusterPhyId) throws NotExistException {
        return new AdminZkClient(this.getClient(clusterPhyId));
    }

    public Long getZKClientCreateTime(Long clusterPhyId) {
        return KAFKA_ZK_CLIENT_CREATE_TIME.get(clusterPhyId);
    }

    /**************************************************** private method ****************************************************/

    @Override
    protected void add(ClusterPhy clusterPhy) {
        // ignore 后续按需创建，因此这里的操作直接忽略
    }

    @Override
    protected void modify(ClusterPhy newClusterPhy, ClusterPhy oldClusterPhy) {
        if (newClusterPhy.getZookeeper().equals(oldClusterPhy.getZookeeper())) {
            // 集群的ZK地址并未编辑，直接返回
            return;
        }

        // 去除历史的，新的继续按需创建
        this.remove(newClusterPhy);
    }

    @Override
    protected void remove(ClusterPhy clusterPhy) {
        // 关闭ZK客户端
        this.closeZKClient(clusterPhy.getId());

        // 移除该时间
        KAFKA_ZK_CLIENT_CREATE_TIME.remove(clusterPhy.getId());
    }

    private void closeZKClient(Long clusterPhyId) {
        try {
            modifyClientMapLock.lock();

            KafkaZkClient kafkaZkClient = KAFKA_ZK_CLIENT_MAP.remove(clusterPhyId);
            if (kafkaZkClient == null) {
                return;
            }

            log.info("close ZK Client starting, clusterPhyId:{}", clusterPhyId);

            kafkaZkClient.close();

            log.info("close ZK Client success, clusterPhyId:{}", clusterPhyId);
        } catch (Exception e) {
            log.error("close ZK Client failed, clusterPhyId:{}", clusterPhyId, e);
        } finally {
            modifyClientMapLock.unlock();
        }
    }

    private KafkaZkClient createZKClient(Long clusterPhyId) throws NotExistException {
        ClusterPhy clusterPhy = LoadedClusterPhyCache.getByPhyId(clusterPhyId);
        if (clusterPhy == null) {
            log.warn("create ZK Client failed, cluster not exist, clusterPhyId:{}", clusterPhyId);
            throw new NotExistException(MsgConstant.getClusterPhyNotExist(clusterPhyId));
        }

        if (ValidateUtils.isBlank(clusterPhy.getZookeeper())) {
            log.warn("create ZK Client failed, zookeeper not exist, clusterPhyId:{}", clusterPhyId);
            return null;
        }

        return this.createZKClient(clusterPhyId, clusterPhy.getZookeeper());
    }

    private KafkaZkClient createZKClient(Long clusterPhyId, String zookeeperAddress) {
        try {
            modifyClientMapLock.lock();

            KafkaZkClient kafkaZkClient = KAFKA_ZK_CLIENT_MAP.get(clusterPhyId);
            if (kafkaZkClient != null) {
                return kafkaZkClient;
            }

            log.debug("create ZK Client starting, clusterPhyId:{} zookeeperAddress:{}", clusterPhyId, zookeeperAddress);

            kafkaZkClient = KafkaZkClient.apply(
                    zookeeperAddress,
//                    false,
//                    添加支持zk的Kerberos认证
                    true,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    5,
                    Time.SYSTEM,
                    "KnowStreaming-clusterPhyId-" + clusterPhyId,
                    "SessionExpireListener",
                    Option.apply("KnowStreaming-clusterPhyId-" + clusterPhyId),
                    Option.apply(new ZKClientConfig())
            );

            KAFKA_ZK_CLIENT_MAP.put(clusterPhyId, kafkaZkClient);
            KAFKA_ZK_CLIENT_CREATE_TIME.put(clusterPhyId, System.currentTimeMillis());

            log.info("create ZK Client success, clusterPhyId:{}", clusterPhyId);
        } catch (Exception e) {
            log.error("create ZK Client failed, clusterPhyId:{} zookeeperAddress:{}", clusterPhyId, zookeeperAddress, e);
        } finally {
            modifyClientMapLock.unlock();
        }

        return KAFKA_ZK_CLIENT_MAP.get(clusterPhyId);
    }
}
