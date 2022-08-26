package com.xiaojukeji.know.streaming.km.task.client;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.job.core.job.Job;
import com.didiglobal.logi.job.core.job.JobContext;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaJMXClient;
import org.springframework.beans.factory.annotation.Autowired;

@Task(name = "CheckJmxClientTask",
        description = "检查Jmx客户端,",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        timeout = 2 * 60,
        consensual = ConsensualEnum.BROADCAST)
public class CheckJmxClientTask implements Job {
    private static final ILog log = LogFactory.getLog(CheckJmxClientTask.class);

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private KafkaJMXClient kafkaJMXClient;

    @Override
    public TaskResult execute(JobContext jobContext) {
        boolean status = true;
        for (ClusterPhy clusterPhy: LoadedClusterPhyCache.listAll().values()) {
            if (this.checkJmxClient(clusterPhy)) {
                continue;
            }

            status = false;
        }

        return status? TaskResult.SUCCESS: TaskResult.FAIL;
    }

    private boolean checkJmxClient(ClusterPhy clusterPhy) {
        try {
            kafkaJMXClient.checkAndRemoveIfIllegal(
                    clusterPhy.getId(),
                    brokerService.listAliveBrokersFromDB(clusterPhy.getId())
            );

            return true;
        } catch (Exception e) {
            log.error("method=checkJmxClient||clusterPhyId={}||errMsg=exception", clusterPhy.getId(), e);
        }

        return false;
    }

}
