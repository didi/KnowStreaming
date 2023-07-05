package com.xiaojukeji.know.streaming.km.task.connect.mm2.metrics;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.xiaojukeji.know.streaming.km.collector.metric.connect.mm2.MirrorMakerMetricCollector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.task.connect.metrics.AbstractAsyncMetricsDispatchTask;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wyb
 * @date 2022/12/21
 */
@Task(name = "MirrorMakerCollectorTask",
        description = "MirrorMaker指标采集任务",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class MirrorMakerCollectorTask extends AbstractAsyncMetricsDispatchTask {

    @Autowired
    private MirrorMakerMetricCollector mirrorMakerMetricCollector;

    @Override
    public TaskResult processClusterTask(ConnectCluster connectCluster, long triggerTimeUnitMs) throws Exception {
        mirrorMakerMetricCollector.collectConnectMetrics(connectCluster);
        return TaskResult.SUCCESS;
    }
}
