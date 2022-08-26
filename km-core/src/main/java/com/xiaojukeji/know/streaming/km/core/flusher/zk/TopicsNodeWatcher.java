package com.xiaojukeji.know.streaming.km.core.flusher.zk;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.core.flusher.zk.handler.TopicsNodeChangeHandler;
import com.xiaojukeji.know.streaming.km.core.service.change.record.KafkaChangeRecordService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import kafka.zk.KafkaZkClient;
import kafka.zk.TopicsZNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TopicsNodeWatcher extends AbstractZKWatcher {
    private static final ILog logger = LogFactory.getLog(TopicsNodeWatcher.class);

    @Autowired
    private TopicService topicService;

    @Autowired
    private KafkaChangeRecordService kafkaChangeRecordService;

    @Override
    protected boolean addWatch(ClusterPhy clusterPhy, KafkaZkClient kafkaZkClient) {
        return this.loadMetadataByWatchZK(clusterPhy, kafkaZkClient);
    }

    @Override
    protected void handleZKClientNotExist(ClusterPhy clusterPhy) {
        // 无需对ZK客户端不存在时进行处理
    }

    /**************************************************** private method ****************************************************/

    private boolean loadMetadataByWatchZK(ClusterPhy clusterPhy, KafkaZkClient kafkaZkClient) {
        try {
            if (kafkaZkClient.currentZooKeeper().exists(TopicsZNode.path(), false) == null) {
                logger.info("ignore add physicalCluster, zk path={} not exist, physicalClusterName:{}.", TopicsZNode.path(), clusterPhy.getName());
                return false;
            }

            //增加Topic监控
            TopicsNodeChangeHandler changeHandler = new TopicsNodeChangeHandler(clusterPhy.getId(), kafkaZKDAO, kafkaChangeRecordService, topicService);
            kafkaAdminZKClient.getClient(clusterPhy.getId()).registerZNodeChildChangeHandler(changeHandler);
            changeHandler.init();
            return true;
        } catch (Exception e) {
            logger.error("load physicalCluster failed, clusterPhy:{}.", clusterPhy, e);
        }

        return false;
    }
}
