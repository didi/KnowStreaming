package com.xiaojukeji.know.streaming.km.task.kafka.metrics;

import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.task.kafka.AbstractClusterPhyDispatchTask;
import com.xiaojukeji.know.streaming.km.task.service.TaskThreadPoolService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 指标采集相关任务
 */
public abstract class AbstractAsyncMetricsDispatchTask extends AbstractClusterPhyDispatchTask {
    private static final ILog log = LogFactory.getLog(AbstractAsyncMetricsDispatchTask.class);

    public abstract TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) throws Exception;

    @Autowired
    private TaskThreadPoolService taskThreadPoolService;

    @Override
    protected TaskResult processSubTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) throws Exception {
        return this.asyncProcessSubTask(clusterPhy, triggerTimeUnitMs);
    }

    public TaskResult asyncProcessSubTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) {
        taskThreadPoolService.submitMetricsTask(
                String.format("taskName=%s||clusterPhyId=%d", this.taskName, clusterPhy.getId()),
                this.timeoutUnitSec.intValue() * 1000,
                () -> {
                    try {
                        TaskResult tr = this.processClusterTask(clusterPhy, triggerTimeUnitMs);
                        if (TaskResult.SUCCESS_CODE != tr.getCode()) {
                            log.error("method=asyncProcessSubTask||taskName={}||clusterPhyId={}||taskResult={}||msg=failed", this.taskName, clusterPhy.getId(), tr);
                        } else {
                            log.debug("method=asyncProcessSubTask||taskName={}||clusterPhyId={}||msg=success", this.taskName, clusterPhy.getId());
                        }
                    } catch (Exception e) {
                        log.error("method=asyncProcessSubTask||taskName={}||clusterPhyId={}||errMsg=exception", this.taskName, clusterPhy.getId(), e);
                    }
                }
        );

        return TaskResult.SUCCESS;
    }
}
