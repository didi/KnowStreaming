package com.xiaojukeji.know.streaming.km.task.connect.metrics;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.xiaojukeji.know.streaming.km.collector.metric.connect.ConnectClusterMetricCollector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wyb
 * @date 2022/11/7
 */
@Task(name = "ConnectClusterMetricCollectorTask",
        description = "ConnectCluster指标采集任务",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class ConnectClusterMetricCollectorTask extends AbstractAsyncMetricsDispatchTask {

    @Autowired
    private ConnectClusterMetricCollector connectClusterMetricCollector;

    @Override
    public TaskResult processClusterTask(ConnectCluster connectCluster, long triggerTimeUnitMs) throws Exception {
       connectClusterMetricCollector.collectMetrics(connectCluster);

        return TaskResult.SUCCESS;
    }
}
