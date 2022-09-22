package com.xiaojukeji.know.streaming.km.core.flusher.zk.handler;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.BackoffUtils;
import com.xiaojukeji.know.streaming.km.core.service.change.record.KafkaChangeRecordService;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminZKClient;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.KafkaZKDAO;


public abstract class AbstractZKHandler {
    private static final ILog log = LogFactory.getLog(AbstractZKHandler.class);

    protected Long clusterPhyId;

    protected KafkaZKDAO kafkaZKDAO;

    protected KafkaAdminZKClient kafkaAdminZKClient;

    protected KafkaChangeRecordService kafkaChangeRecordService;

    public abstract void init();

    public void reInitDataIfException() {
        // 回退2秒，会阻塞处理线程，因此回退时间不可设置太长
        BackoffUtils.backoff(2000);

        if (!LoadedClusterPhyCache.containsByPhyId(this.clusterPhyId)) {
            // 集群不存在，则直接忽略重试
            log.info("method=reInitDataIfException||clusterPhyId={}||msg=not exist", this.clusterPhyId);
            return;
        }

        try {
            kafkaAdminZKClient.getClient(clusterPhyId);
        } catch (NotExistException nee) {
            // 客户端不存在，则也忽略重试
            log.info("method=reInitDataIfException||clusterPhyId={}||errMsg=exception", this.clusterPhyId, nee);
            return;
        }


        init();
    }

    protected AbstractZKHandler(Long clusterPhyId, KafkaZKDAO kafkaZKDAO, KafkaChangeRecordService kafkaChangeRecordService) {
        this.clusterPhyId = clusterPhyId;
        this.kafkaZKDAO = kafkaZKDAO;
        this.kafkaChangeRecordService = kafkaChangeRecordService;
    }
}
