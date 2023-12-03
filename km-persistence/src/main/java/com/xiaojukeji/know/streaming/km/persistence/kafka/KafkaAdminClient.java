package com.xiaojukeji.know.streaming.km.persistence.kafka;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.persistence.AbstractClusterLoadedChangedHandler;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class KafkaAdminClient extends AbstractClusterLoadedChangedHandler {
    private static final ILog LOGGER = LogFactory.getLog(KafkaAdminClient.class);

    @Value("${client-pool.kafka-admin.client-cnt:1}")
    private Integer clientCnt;

    private static final Map<Long, List<AdminClient>> KAFKA_ADMIN_CLIENT_MAP = new ConcurrentHashMap<>();

    public AdminClient getClient(Long clusterPhyId) throws NotExistException {
        List<AdminClient> adminClientList = KAFKA_ADMIN_CLIENT_MAP.get(clusterPhyId);
        if (adminClientList != null) {
            return adminClientList.get((int)(System.currentTimeMillis() % clientCnt));
        }

        AdminClient adminClient = this.createKafkaAdminClient(clusterPhyId);
        if (adminClient == null) {
            throw new NotExistException("kafka admin-client not exist due to create failed");
        }

        return adminClient;
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
        this.closeKafkaAdminClient(clusterPhy.getId());
    }

    private void closeKafkaAdminClient(Long clusterPhyId) {
        try {
            modifyClientMapLock.lock();

            List<AdminClient> adminClientList = KAFKA_ADMIN_CLIENT_MAP.remove(clusterPhyId);
            if (adminClientList == null) {
                return;
            }

            LOGGER.info("close kafka AdminClient starting, clusterPhyId:{}", clusterPhyId);

            boolean allSuccess = this.closeAdminClientList(clusterPhyId, adminClientList);

            if (allSuccess) {
                LOGGER.info("close kafka AdminClient success, clusterPhyId:{}", clusterPhyId);
            } else {
                LOGGER.error("close kafka AdminClient exist failed and can ignore this error, clusterPhyId:{}", clusterPhyId);
            }
        } catch (Exception e) {
            LOGGER.error("close kafka AdminClient failed, clusterPhyId:{}", clusterPhyId, e);
        } finally {
            modifyClientMapLock.unlock();
        }
    }

    private AdminClient createKafkaAdminClient(Long clusterPhyId) throws NotExistException {
        ClusterPhy clusterPhy = LoadedClusterPhyCache.getByPhyId(clusterPhyId);
        if (clusterPhy == null) {
            throw new NotExistException(String.format("clusterPhyId:%d not exist", clusterPhyId));
        }

        return this.createKafkaAdminClient(clusterPhyId, clusterPhy.getBootstrapServers(), ConvertUtil.str2ObjByJson(clusterPhy.getClientProperties(), Properties.class));
    }

    private AdminClient createKafkaAdminClient(Long clusterPhyId, String bootstrapServers, Properties props) {
        List<AdminClient> adminClientList = null;
        try {
            modifyClientMapLock.lock();

            adminClientList = KAFKA_ADMIN_CLIENT_MAP.get(clusterPhyId);
            if (adminClientList != null) {
                return adminClientList.get((int)(System.currentTimeMillis() % clientCnt));
            }

            LOGGER.debug("create kafka AdminClient starting, clusterPhyId:{} props:{}", clusterPhyId, props);

            if (props == null) {
                props = new Properties();
            }
            props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

            adminClientList = new ArrayList<>();
            for (int i = 0; i < clientCnt; ++i) {
                props.put(AdminClientConfig.CLIENT_ID_CONFIG, String.format("ApacheAdminClient||clusterPhyId=%d||Cnt=%d", clusterPhyId, i));
                adminClientList.add(AdminClient.create(props));
            }

            KAFKA_ADMIN_CLIENT_MAP.put(clusterPhyId, adminClientList);

            LOGGER.info("create kafka AdminClient success, clusterPhyId:{}", clusterPhyId);
        } catch (Exception e) {
            LOGGER.error("create kafka AdminClient failed, clusterPhyId:{} props:{}", clusterPhyId, props, e);

            this.closeAdminClientList(clusterPhyId, adminClientList);
        } finally {
            modifyClientMapLock.unlock();
        }

        return KAFKA_ADMIN_CLIENT_MAP.get(clusterPhyId).get((int)(System.currentTimeMillis() % clientCnt));
    }

    private boolean closeAdminClientList(Long clusterPhyId, List<AdminClient> adminClientList) {
        if (adminClientList == null) {
            return true;
        }

        boolean allSuccess = true;
        for (AdminClient adminClient: adminClientList) {
            try {
                // 关闭客户端，超时时间为30秒
                adminClient.close(Duration.ofSeconds(30));
            } catch (Exception e) {
                // ignore
                LOGGER.error("close kafka AdminClient exist failed, clusterPhyId:{}", clusterPhyId, e);
                allSuccess = false;
            }
        }

        return allSuccess;
    }
}
