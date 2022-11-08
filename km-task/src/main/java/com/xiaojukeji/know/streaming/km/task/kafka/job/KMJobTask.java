package com.xiaojukeji.know.streaming.km.task.kafka.job;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.core.service.job.JobService;
import com.xiaojukeji.know.streaming.km.task.kafka.AbstractAsyncCommonDispatchTask;
import org.springframework.beans.factory.annotation.Autowired;

@Task(name = "kmJobTask",
        description = "km job 模块调度执行任务",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class KMJobTask extends AbstractAsyncCommonDispatchTask {

    @Autowired
    private JobService jobService;

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) throws Exception {
        jobService.scheduleJobByClusterId(clusterPhy.getId());
        return TaskResult.SUCCESS;
    }
}
