package com.xiaojukeji.know.streaming.km.task.kafka.job;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.core.service.reassign.ReassignJobService;
import com.xiaojukeji.know.streaming.km.task.kafka.AbstractAsyncCommonDispatchTask;
import org.springframework.beans.factory.annotation.Autowired;

@Task(name = "CommunityReassignJobTask",
        description = "原生副本迁移调度执行任务",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class CommunityReassignJobTask extends AbstractAsyncCommonDispatchTask {
    private static final ILog log = LogFactory.getLog(CommunityReassignJobTask.class);

    @Autowired
    private ReassignJobService reassignJobService;

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) throws Exception {
        // 获取迁移中的任务
        Long jobId = reassignJobService.getOneRunningJobId(clusterPhy.getId());
        if (jobId == null) {
            // 当前无任务
            return TaskResult.SUCCESS;
        }

        // 更新任务的状态
        Result<Void> rv = reassignJobService.verifyAndUpdateStatue(jobId);
        if (rv != null && rv.failed()) {
            log.error("method=processSubTask||jobId={}||result={}||msg=verify and update task status failed", jobId, rv);
        }

        // 更新同步进度信息
        reassignJobService.getAndUpdateSubJobExtendData(jobId);

        return rv.failed()? TaskResult.FAIL: TaskResult.SUCCESS;
    }
}
