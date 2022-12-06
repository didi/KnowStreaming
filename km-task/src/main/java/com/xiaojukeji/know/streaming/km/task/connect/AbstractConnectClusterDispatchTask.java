package com.xiaojukeji.know.streaming.km.task.connect;

import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.task.AbstractDispatchTask;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author wyb
 * @date 2022/11/7
 */
public abstract class AbstractConnectClusterDispatchTask extends AbstractDispatchTask<ConnectCluster> {
    private static final ILog log = LogFactory.getLog(AbstractConnectClusterDispatchTask.class);

    @Autowired
    private ConnectClusterService connectClusterService;

    protected abstract TaskResult processSubTask(ConnectCluster connectCluster, long triggerTimeUnitMs) throws Exception;
    @Override
    protected List<ConnectCluster> listAllTasks() {
        return connectClusterService.listAllClusters();
    }

    @Override
    protected TaskResult processTask(List<ConnectCluster> subTaskList, long triggerTimeUnitMs) {
        boolean allSuccess = true;
        for (ConnectCluster elem : subTaskList) {
            try {
                log.debug("method=processTask||taskName={}||connectClusterId={}||msg=start", this.taskName, elem.getId());

                TaskResult tr = this.processSubTask(elem, triggerTimeUnitMs);
                if (TaskResult.SUCCESS.getCode() != tr.getCode()) {
                    log.warn("method=processTask||taskName={}||connectClusterId={}||msg=process failed", this.taskName, elem.getId());
                    allSuccess = false;
                    continue;
                }

                log.debug("method=processTask||taskName={}||connectClusterId={}||msg=finished", this.taskName, elem.getId());
            } catch (Exception e) {
                log.error("method=processTask||taskName={}||connectClusterId={}||errMsg=throw exception", this.taskName, elem.getId(), e);
            }
        }

        return allSuccess ? TaskResult.SUCCESS : TaskResult.FAIL;
    }
}
