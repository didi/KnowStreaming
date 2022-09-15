package com.xiaojukeji.know.streaming.km.task.metadata;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Task(name = "SyncBrokerTask",
        description = "Broker信息同步到DB",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class SyncBrokerTask extends AbstractAsyncMetadataDispatchTask {
    private static final ILog log = LogFactory.getLog(SyncBrokerTask.class);

    @Autowired
    private BrokerService brokerService;

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) {
        Result<List<Broker>> brokersResult = brokerService.listBrokersFromKafka(clusterPhy);
        if (brokersResult.failed()) {
            return new TaskResult(TaskResult.FAIL_CODE, brokersResult.getMessage());
        }

        brokerService.updateAliveBrokers(clusterPhy.getId(), brokersResult.getData());
        return TaskResult.SUCCESS;
    }
}
