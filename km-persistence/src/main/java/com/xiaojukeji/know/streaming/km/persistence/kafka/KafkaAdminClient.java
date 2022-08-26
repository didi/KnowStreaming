package com.xiaojukeji.know.streaming.km.persistence.kafka;

import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.persistence.AbstractClusterLoadedChangedHandler;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class KafkaAdminClient extends AbstractClusterLoadedChangedHandler {
    private static final Map<Long, AdminClient> KAFKA_ADMIN_CLIENT_MAP = new ConcurrentHashMap<>();

    public AdminClient getClient(Long clusterPhyId) throws NotExistException {
        AdminClient adminClient = KAFKA_ADMIN_CLIENT_MAP.get(clusterPhyId);
        if (adminClient != null) {
            return adminClient;
        }

        adminClient = this.createKafkaAdminClient(clusterPhyId);
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

            AdminClient adminClient = KAFKA_ADMIN_CLIENT_MAP.remove(clusterPhyId);
            if (adminClient == null) {
                return;
            }

            log.info("close kafka AdminClient starting, clusterPhyId:{}", clusterPhyId);

            adminClient.close();

            log.info("close kafka AdminClient success, clusterPhyId:{}", clusterPhyId);
        } catch (Exception e) {
            log.error("close kafka AdminClient failed, clusterPhyId:{}", clusterPhyId, e);
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
        try {
            modifyClientMapLock.lock();

            AdminClient adminClient = KAFKA_ADMIN_CLIENT_MAP.get(clusterPhyId);
            if (adminClient != null) {
                return adminClient;
            }

            log.debug("create kafka AdminClient starting, clusterPhyId:{} props:{}", clusterPhyId, props);

            if (props == null) {
                props = new Properties();
            }
            props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            KAFKA_ADMIN_CLIENT_MAP.put(clusterPhyId, AdminClient.create(props));

            log.info("create kafka AdminClient success, clusterPhyId:{}", clusterPhyId);
        } catch (Exception e) {
            log.error("create kafka AdminClient failed, clusterPhyId:{} props:{}", clusterPhyId, props, e);
        } finally {
            modifyClientMapLock.unlock();
        }

        return KAFKA_ADMIN_CLIENT_MAP.get(clusterPhyId);
    }
}
