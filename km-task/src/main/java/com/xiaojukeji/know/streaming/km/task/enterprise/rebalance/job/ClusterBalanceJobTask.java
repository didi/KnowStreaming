package com.xiaojukeji.know.streaming.km.task.enterprise.rebalance.job;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.core.enterprise.rebalance.service.ClusterBalanceJobService;
import com.xiaojukeji.know.streaming.km.core.enterprise.rebalance.service.ClusterBalanceReassignService;
import com.xiaojukeji.know.streaming.km.core.enterprise.rebalance.service.ClusterBalanceService;
import com.xiaojukeji.know.streaming.km.task.AbstractAsyncCommonDispatchTask;
import org.springframework.beans.factory.annotation.Autowired;

@EnterpriseLoadReBalance
@Task(name = "clusterBalanceJobTask",
        description = "周期性校验是否到点能生成集群均衡任务，以及更新任务状态",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class ClusterBalanceJobTask extends AbstractAsyncCommonDispatchTask {

    @Autowired
    private ClusterBalanceService clusterBalanceService;

    @Autowired
    private ClusterBalanceJobService clusterBalanceJobService;

    @Autowired
    private ClusterBalanceReassignService clusterBalanceReassignService;

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) throws Exception {
        clusterBalanceService.createScheduleJob(clusterPhy.getId(), triggerTimeUnitMs);
        // 获取迁移中的任务
        Long jobId = clusterBalanceJobService.getOneRunningJob(clusterPhy.getId());
        if (jobId == null) {
            // 当前无任务
            return TaskResult.SUCCESS;
        }

        // 更新任务的状态
        Result<Void> rv = clusterBalanceJobService.verifyClusterBalanceAndUpdateStatue(jobId);

        // 更新同步进度信息
        clusterBalanceReassignService.getAndUpdateSubJobExtendData(jobId);

        //根据策略生成新的或更新迁移任务
        Result r = clusterBalanceJobService.generateReassignmentForStrategy(clusterPhy.getId(), jobId);

        return rv.failed()||r.failed() ? TaskResult.FAIL: TaskResult.SUCCESS;
    }
}
