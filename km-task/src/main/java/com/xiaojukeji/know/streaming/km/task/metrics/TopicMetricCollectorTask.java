package com.xiaojukeji.know.streaming.km.task.metrics;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.collector.metric.kafka.TopicMetricCollector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author didi
 */
@Task(name = "TopicMetricCollectorTask",
        description = "Topic指标采集任务",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class TopicMetricCollectorTask extends AbstractAsyncMetricsDispatchTask {
    private static final ILog log = LogFactory.getLog(TopicMetricCollectorTask.class);

    @Autowired
    private TopicMetricCollector topicMetricCollector;

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) throws Exception {
        topicMetricCollector.collectMetrics(clusterPhy);

        return TaskResult.SUCCESS;
    }
}
