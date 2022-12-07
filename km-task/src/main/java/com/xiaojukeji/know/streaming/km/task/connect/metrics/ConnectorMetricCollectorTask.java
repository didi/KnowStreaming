package com.xiaojukeji.know.streaming.km.task.connect.metrics;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.xiaojukeji.know.streaming.km.collector.metric.connect.ConnectConnectorMetricCollector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wyb
 * @date 2022/11/7
 */
@Task(name = "ConnectorMetricCollectorTask",
        description = "Connector指标采集任务",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class ConnectorMetricCollectorTask extends AbstractAsyncMetricsDispatchTask {

    @Autowired
    private ConnectConnectorMetricCollector connectConnectorMetricCollector;

    @Override
    public TaskResult processClusterTask(ConnectCluster connectCluster, long triggerTimeUnitMs) throws Exception {
       connectConnectorMetricCollector.collectMetrics(connectCluster);

        return TaskResult.SUCCESS;
    }
}
