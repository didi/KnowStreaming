package com.xiaojukeji.know.streaming.km.core.flusher.zk;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.core.service.change.record.KafkaChangeRecordService;
import com.xiaojukeji.know.streaming.km.core.service.kafkacontroller.KafkaControllerService;
import com.xiaojukeji.know.streaming.km.core.flusher.zk.handler.ControllerNodeChangeHandler;
import kafka.zk.ControllerZNode;
import kafka.zk.KafkaZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ControllerNodeWatcher extends AbstractZKWatcher {
    private static final ILog log = LogFactory.getLog(ControllerNodeWatcher.class);

    @Autowired
    private KafkaControllerService kafkaControllerService;

    @Autowired
    private KafkaChangeRecordService kafkaChangeRecordService;

    @Override
    protected boolean addWatch(ClusterPhy clusterPhy, KafkaZkClient kafkaZkClient) {
        return this.loadMetadataByWatchZK(clusterPhy, kafkaZkClient);
    }

    @Override
    protected void handleZKClientNotExist(ClusterPhy clusterPhy) {
        kafkaControllerService.setNoKafkaController(clusterPhy.getId(), System.currentTimeMillis() / 1000L * 1000L);
    }

    /**************************************************** private method ****************************************************/

    private boolean loadMetadataByWatchZK(ClusterPhy clusterPhy, KafkaZkClient kafkaZkClient) {
        try {
            if (kafkaZkClient.currentZooKeeper().exists(ControllerZNode.path(), false) == null) {
                log.info("ignore add physicalCluster, zk path={} not exist, physicalClusterName:{}.", ControllerZNode.path(), clusterPhy.getName());
                return false;
            }

            ControllerNodeChangeHandler changeHandler = new ControllerNodeChangeHandler(clusterPhy.getId(), kafkaZKDAO, kafkaChangeRecordService, kafkaControllerService);
            changeHandler.init();
            kafkaAdminZKClient.getClient(clusterPhy.getId()).registerZNodeChangeHandlerAndCheckExistence(changeHandler);
            return true;
        } catch (Exception e) {
            log.error("load physicalCluster failed, clusterPhy:{}.", clusterPhy, e);
        }

        return false;
    }
}
