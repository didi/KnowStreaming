package com.xiaojukeji.kafka.manager.task.dispatch.ha;

import com.xiaojukeji.kafka.manager.service.biz.job.HaASSwitchJobManager;
import com.xiaojukeji.kafka.manager.service.service.ha.HaASSwitchJobService;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 主备切换任务
 */
@Component
@CustomScheduled(name = "HaFlushASSwitchJob",
        cron = "0 0/1 * * * ?",
        threadNum = 1,
        description = "刷新主备切换任务")
public class HaFlushASSwitchJob extends AbstractScheduledTask<Long> {
    @Autowired
    private HaASSwitchJobService haASSwitchJobService;

    @Autowired
    private HaASSwitchJobManager haASSwitchJobManager;

    @Override
    public List<Long> listAllTasks() {
        // 获取正在运行的任务ID列表, 忽略1分钟内的任务，尽量避免任务被重复执行
        return haASSwitchJobService.listRunningJobs(System.currentTimeMillis() - (60 * 1000L));
    }

    @Override
    public void processTask(Long jobId) {
        // 执行Job
        haASSwitchJobManager.executeJob(jobId, false, false);

        // 更新任务信息
        haASSwitchJobManager.flushExtendData(jobId);
    }
}
