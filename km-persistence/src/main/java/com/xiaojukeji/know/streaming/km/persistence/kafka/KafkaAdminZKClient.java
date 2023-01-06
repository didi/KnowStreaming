package com.xiaojukeji.know.streaming.km.persistence.kafka;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.ZKConfig;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
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
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class KafkaAdminZKClient extends AbstractClusterLoadedChangedHandler implements KafkaClient<KafkaZkClient> {
    private static final ILog LOGGER = LogFactory.getLog(KafkaAdminZKClient.class);

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

            LOGGER.info("method=closeZKClient||clusterPhyId={}||msg=close ZK Client starting", clusterPhyId);

            kafkaZkClient.close();

            LOGGER.info("method=closeZKClient||clusterPhyId={}||msg=close ZK Client success", clusterPhyId);
        } catch (Exception e) {
            LOGGER.error("method=closeZKClient||clusterPhyId={}||msg=close ZK Client failed||errMsg=exception!", clusterPhyId, e);
        } finally {
            modifyClientMapLock.unlock();
        }
    }

    private KafkaZkClient createZKClient(Long clusterPhyId) throws NotExistException {
        ClusterPhy clusterPhy = LoadedClusterPhyCache.getByPhyId(clusterPhyId);
        if (clusterPhy == null) {
            LOGGER.warn("method=createZKClient||clusterPhyId={}||msg=create ZK Client failed, cluster not exist", clusterPhyId);
            throw new NotExistException(MsgConstant.getClusterPhyNotExist(clusterPhyId));
        }

        if (ValidateUtils.isBlank(clusterPhy.getZookeeper())) {
            LOGGER.warn("method=createZKClient||clusterPhyId={}||msg=create ZK Client failed, zookeeper not exist", clusterPhyId);
            return null;
        }

        return this.createZKClient(clusterPhyId, clusterPhy);
    }

    private KafkaZkClient createZKClient(Long clusterPhyId, ClusterPhy clusterPhy) {
        try {
            modifyClientMapLock.lock();

            KafkaZkClient kafkaZkClient = KAFKA_ZK_CLIENT_MAP.get(clusterPhyId);
            if (kafkaZkClient != null) {
                return kafkaZkClient;
            }

            ZKConfig zkConfig = this.getZKConfig(clusterPhy);

            LOGGER.debug("method=createZKClient||clusterPhyId={}||clusterPhy={}||msg=create ZK Client starting", clusterPhyId, clusterPhy);

            kafkaZkClient = KafkaZkClient.apply(
                    clusterPhy.getZookeeper(),
                    zkConfig.getOpenSecure(),
                    zkConfig.getSessionTimeoutUnitMs(),
                    zkConfig.getRequestTimeoutUnitMs(),
                    5,
                    Time.SYSTEM,
                    "KS-ZK-ClusterPhyId-" + clusterPhyId,
                    "KS-ZK-SessionExpireListener-clusterPhyId-" + clusterPhyId,
                    Option.apply("KS-ZK-ClusterPhyId-" + clusterPhyId),
                    Option.apply(this.getZKConfig(clusterPhyId, zkConfig.getOtherProps()))
            );

            KAFKA_ZK_CLIENT_MAP.put(clusterPhyId, kafkaZkClient);
            KAFKA_ZK_CLIENT_CREATE_TIME.put(clusterPhyId, System.currentTimeMillis());

            LOGGER.info("method=createZKClient||clusterPhyId={}||msg=create ZK Client success", clusterPhyId);
        } catch (Exception e) {
            LOGGER.error("method=createZKClient||clusterPhyId={}||clusterPhy={}||msg=create ZK Client failed||errMsg=exception", clusterPhyId, clusterPhy, e);
        } finally {
            modifyClientMapLock.unlock();
        }

        return KAFKA_ZK_CLIENT_MAP.get(clusterPhyId);
    }

    private ZKConfig getZKConfig(ClusterPhy clusterPhy) {
        ZKConfig zkConfig = ConvertUtil.str2ObjByJson(clusterPhy.getZkProperties(), ZKConfig.class);
        if (zkConfig == null) {
            return new ZKConfig();
        }

        return zkConfig;
    }

    private ZKClientConfig getZKConfig(Long clusterPhyId, Properties props) {
        ZKClientConfig zkClientConfig = new ZKClientConfig();

        try {
            props.entrySet().forEach(elem -> zkClientConfig.setProperty((String) elem.getKey(), (String) elem.getValue()));
        } catch (Exception e) {
            LOGGER.error("method=getZKConfig||clusterPhyId={}||props={}||errMsg=exception", clusterPhyId, props);
        }

        return zkClientConfig;
    }
}
