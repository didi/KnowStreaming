package com.xiaojukeji.know.streaming.km.task.metrics;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.collector.metric.ClusterMetricCollector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.task.AbstractClusterPhyDispatchTask;
import com.xiaojukeji.know.streaming.km.task.service.TaskThreadPoolService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author didi
 */
@Task(name = "ClusterMetricCollectorTask",
        description = "Cluster指标采集任务,",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class ClusterMetricCollectorTask extends AbstractClusterPhyDispatchTask {
    private static final ILog log = LogFactory.getLog(ClusterMetricCollectorTask.class);

    @Autowired
    private ClusterMetricCollector clusterMetricCollector;

    @Autowired
    private TaskThreadPoolService taskThreadPoolService;

    @Override
    public TaskResult processSubTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) throws Exception {
        taskThreadPoolService.submitHeavenTask(
                String.format("TaskName=%s clusterPhyId=%d", this.taskName, clusterPhy.getId()),
                100000,
                () -> clusterMetricCollector.collectMetrics(clusterPhy)
        );

        return TaskResult.SUCCESS;
    }
}
