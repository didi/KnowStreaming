package com.xiaojukeji.know.streaming.km.core.flusher.zk;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.core.flusher.zk.handler.ConfigNotificationNodeChangeHandler;
import com.xiaojukeji.know.streaming.km.core.service.change.record.KafkaChangeRecordService;
import kafka.zk.KafkaZkClient;
import kafka.zk.TopicsZNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ConfigChangeNodeWatcher extends AbstractZKWatcher {
    private static final ILog logger = LogFactory.getLog(ConfigChangeNodeWatcher.class);

    @Autowired
    private KafkaChangeRecordService kafkaChangeRecordService;

    @Override
    protected boolean addWatch(ClusterPhy clusterPhy, KafkaZkClient kafkaZkClient) {
        return this.loadChangeNotificationByWatchZK(clusterPhy, kafkaZkClient);
    }

    @Override
    protected void handleZKClientNotExist(ClusterPhy clusterPhy) {
        // ignore
    }

    /**************************************************** private method ****************************************************/

    private boolean loadChangeNotificationByWatchZK(ClusterPhy clusterPhy, KafkaZkClient kafkaZkClient) {
        try {
            if (kafkaZkClient.currentZooKeeper().exists(TopicsZNode.path(), false) == null) {
                logger.info("ignore add physicalCluster, zk path={} not exist, physicalClusterName:{}.", TopicsZNode.path(), clusterPhy.getName());
                return false;
            }

            //增加config-change监控
            ConfigNotificationNodeChangeHandler changeHandler = new ConfigNotificationNodeChangeHandler(clusterPhy.getId(), kafkaZKDAO, kafkaChangeRecordService);
            kafkaAdminZKClient.getClient(clusterPhy.getId()).registerZNodeChildChangeHandler(changeHandler);
            changeHandler.init();
            return true;
        } catch (Exception e) {
            logger.error("method=loadChangeNotificationByWatchZK||clusterPhyPO={}||errMsg=exception!", clusterPhy, e);
        }

        return false;
    }
}
