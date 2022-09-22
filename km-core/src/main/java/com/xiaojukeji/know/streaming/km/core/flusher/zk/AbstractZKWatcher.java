package com.xiaojukeji.know.streaming.km.core.flusher.zk;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminZKClient;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.KafkaZKDAO;
import kafka.zk.KafkaZkClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractZKWatcher {
    private static final ILog log = LogFactory.getLog(AbstractZKWatcher.class);

    @Autowired
    protected KafkaZKDAO kafkaZKDAO;

    @Autowired
    protected KafkaAdminZKClient kafkaAdminZKClient;

    private final Set<Long> watchedClusterPhyId = new CopyOnWriteArraySet<>();

    protected volatile Long recordedZKClientCreateTime;

    protected abstract boolean addWatch(ClusterPhy clusterPhy, KafkaZkClient kafkaZkClient);

    protected abstract void handleZKClientNotExist(ClusterPhy clusterPhy);

    /**
     * 检查并触发任务执行
     * @param clusterPhy 物理集群信息
     */
    public void flush(ClusterPhy clusterPhy) {
        Long newZKClientCreateTime = kafkaAdminZKClient.getZKClientCreateTime(clusterPhy.getId());
        if (newZKClientCreateTime == null || !newZKClientCreateTime.equals(recordedZKClientCreateTime)) {
            // 如果ZK客户端发生过变化，则移除已经watched的集群
            watchedClusterPhyId.remove(clusterPhy.getId());
        }

        if (watchedClusterPhyId.contains(clusterPhy.getId())) {
            // 已经加载
            return;
        }

        try {
            if (this.addWatch(clusterPhy, kafkaAdminZKClient.getClient(clusterPhy.getId()))) {
                watchedClusterPhyId.add(clusterPhy.getId());
                recordedZKClientCreateTime = newZKClientCreateTime;
            }
        } catch (NotExistException nee) {
            this.handleZKClientNotExist(clusterPhy);
        } catch (Exception e) {
            log.error("method=flush||clusterPhy={}||errMsg=exception.", clusterPhy, e);
        }
    }

    /**
     * 移除缓存
     * @param clusterPhyId 物理集群ID
     */
    public void remove(Long clusterPhyId) {
        watchedClusterPhyId.remove(clusterPhyId);
    }
}
