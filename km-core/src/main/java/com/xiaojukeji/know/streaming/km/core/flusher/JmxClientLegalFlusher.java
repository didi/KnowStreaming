package com.xiaojukeji.know.streaming.km.core.flusher;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.utils.FutureUtil;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaJMXClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * JMX连接检查
 */
@Service
public class JmxClientLegalFlusher {
    private static final ILog LOGGER = LogFactory.getLog(JmxClientLegalFlusher.class);

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private KafkaJMXClient kafkaJMXClient;

    @Scheduled(cron="0 0/1 * * * ?")
    public void checkJmxClient() {
        for (ClusterPhy clusterPhy: LoadedClusterPhyCache.listAll().values()) {
            FutureUtil.quickStartupFutureUtil.submitTask(
                    () -> {
                        try {
                            kafkaJMXClient.checkAndRemoveIfIllegal(
                                    clusterPhy.getId(),
                                    brokerService.listAliveBrokersFromDB(clusterPhy.getId())
                            );
                        } catch (Exception e) {
                            LOGGER.error("method=checkJmxClient||clusterPhyId={}||errMsg=exception", clusterPhy.getId(), e);
                        }
                    }
            );
        }
    }
}
