package com.xiaojukeji.know.streaming.km.task.kafka.metrics;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.xiaojukeji.know.streaming.km.collector.metric.kafka.ClusterMetricCollector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author didi
 */
@Task(name = "ClusterMetricCollectorTask",
        description = "Cluster指标采集任务",
        cron = "30 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class ClusterMetricCollectorTask extends AbstractAsyncMetricsDispatchTask {
    @Autowired
    private ClusterMetricCollector clusterMetricCollector;

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) throws Exception {
        clusterMetricCollector.collectMetrics(clusterPhy);

        return TaskResult.SUCCESS;
    }
}
