package com.xiaojukeji.know.streaming.km.task.kafka;

import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.task.AbstractDispatchTask;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class AbstractClusterPhyDispatchTask extends AbstractDispatchTask<ClusterPhy> {
    private static final ILog log = LogFactory.getLog(AbstractClusterPhyDispatchTask.class);

    @Autowired
    private ClusterPhyService clusterPhyService;

    /**
     * 执行被分配的任务
     * @param clusterPhy 任务
     * @param triggerTimeUnitMs 任务触发时间
     * @return
     */
    protected abstract TaskResult processSubTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) throws Exception;

    @Override
    public List<ClusterPhy> listAllTasks() {
        return clusterPhyService.listAllClusters();
    }

    @Override
    public TaskResult processTask(List<ClusterPhy> subTaskList, long triggerTimeUnitMs) {
        boolean allSuccess = true;
        for (ClusterPhy elem: subTaskList) {
            try {
                log.debug("method=processTask||taskName={}||clusterPhyId={}||msg=start", this.taskName, elem.getId());

                TaskResult tr = this.processSubTask(elem, triggerTimeUnitMs);
                if (TaskResult.SUCCESS.getCode() != tr.getCode()) {
                    log.warn("method=processTask||taskName={}||clusterPhyId={}||msg=process failed", this.taskName, elem.getId());
                    allSuccess = false;
                    continue;
                }

                log.debug("method=processTask||taskName={}||clusterPhyId={}||msg=finished", this.taskName, elem.getId());
            } catch (Exception e) {
                log.error("method=processTask||taskName={}||clusterPhyId={}||errMsg=throw exception", this.taskName, elem.getId(), e);
            }
        }

        return allSuccess? TaskResult.SUCCESS: TaskResult.FAIL;
    }
}
