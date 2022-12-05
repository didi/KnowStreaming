package com.xiaojukeji.know.streaming.km.task.kafka.metrics;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.collector.metric.kafka.GroupMetricCollector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author didi
 */
@Task(name = "GroupMetricCollectorTask",
        description = "Group指标采集任务",
        cron = "40 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class GroupMetricCollectorTask extends AbstractAsyncMetricsDispatchTask {
    private static final ILog log = LogFactory.getLog(GroupMetricCollectorTask.class);

    @Autowired
    private GroupMetricCollector groupMetricCollector;

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) throws Exception {
        groupMetricCollector.collectMetrics(clusterPhy);

        return TaskResult.SUCCESS;
    }
}
