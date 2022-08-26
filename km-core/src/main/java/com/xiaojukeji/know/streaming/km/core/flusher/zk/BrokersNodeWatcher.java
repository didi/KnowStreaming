package com.xiaojukeji.know.streaming.km.core.flusher.zk;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.core.flusher.zk.handler.BrokersNodeChangeHandler;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.change.record.KafkaChangeRecordService;
import kafka.zk.BrokerIdsZNode;
import kafka.zk.KafkaZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BrokersNodeWatcher extends AbstractZKWatcher {
    private static final ILog log = LogFactory.getLog(BrokersNodeWatcher.class);

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private KafkaChangeRecordService kafkaChangeRecordService;

    @Override
    protected boolean addWatch(ClusterPhy clusterPhy, KafkaZkClient kafkaZkClient) {
        return this.loadMetadataByWatchZK(clusterPhy, kafkaZkClient);
    }

    @Override
    protected void handleZKClientNotExist(ClusterPhy clusterPhy) {
        // ignore
    }

    /**************************************************** private method ****************************************************/

    private boolean loadMetadataByWatchZK(ClusterPhy clusterPhy, KafkaZkClient kafkaZkClient) {
        try {
            if (kafkaZkClient.currentZooKeeper().exists(BrokerIdsZNode.path(), false) == null) {
                log.info("ignore add physicalCluster, zk path={} not exist, physicalClusterName:{}.", BrokerIdsZNode.path(), clusterPhy.getName());
                return false;
            }

            //增加Broker监控
            BrokersNodeChangeHandler brokerChangeHandler = new BrokersNodeChangeHandler(clusterPhy.getId(), kafkaZKDAO, kafkaChangeRecordService, brokerService);
            kafkaAdminZKClient.getClient(clusterPhy.getId()).registerZNodeChildChangeHandler(brokerChangeHandler);
            brokerChangeHandler.init();
            return true;
        } catch (Exception e) {
            log.error("load physicalCluster failed, clusterPhy:{}.", clusterPhy, e);
        }

        return false;
    }
}
