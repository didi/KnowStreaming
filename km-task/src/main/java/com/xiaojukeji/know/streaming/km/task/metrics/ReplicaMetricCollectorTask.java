package com.xiaojukeji.know.streaming.km.task.metrics;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.xiaojukeji.know.streaming.km.collector.metric.ReplicaMetricCollector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.task.AbstractClusterPhyDispatchTask;
import com.xiaojukeji.know.streaming.km.task.service.TaskThreadPoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author didi
 */
@Slf4j
@Task(name = "ReplicaMetricCollectorTask",
        description = "Replica指标采集任务,",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class ReplicaMetricCollectorTask extends AbstractClusterPhyDispatchTask {

    @Autowired
    private ReplicaMetricCollector replicaMetricCollector;

    @Autowired
    private TaskThreadPoolService taskThreadPoolService;

    @Override
    protected TaskResult processSubTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) throws Exception {
        taskThreadPoolService.submitHeavenTask(
                String.format("TaskName=%s clusterPhyId=%d", this.taskName, clusterPhy.getId()),
                100000,
                () -> replicaMetricCollector.collectMetrics(clusterPhy)
        );

        return TaskResult.SUCCESS;
    }
}
