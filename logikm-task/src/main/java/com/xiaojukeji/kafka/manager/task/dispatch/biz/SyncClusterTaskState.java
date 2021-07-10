package com.xiaojukeji.kafka.manager.task.dispatch.biz;

import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterTaskDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.kcm.ClusterTaskService;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskStateEnum;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import com.xiaojukeji.kafka.manager.task.component.EmptyEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Arrays;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/9/7
 */
@CustomScheduled(name = "syncClusterTaskState", cron = "0 0/1 * * * ?", threadNum = 1)
@ConditionalOnProperty(prefix = "kcm", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SyncClusterTaskState extends AbstractScheduledTask<EmptyEntry> {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private ClusterTaskService clusterTaskService;

    @Override
    public List<EmptyEntry> listAllTasks() {
        EmptyEntry emptyEntry = new EmptyEntry();
        emptyEntry.setId(System.currentTimeMillis() / 1000);
        return Arrays.asList(emptyEntry);
    }

    @Override
    public void processTask(EmptyEntry entryEntry) {
        List<ClusterTaskDO> doList = clusterTaskService.listAll();
        if (ValidateUtils.isEmptyList(doList)) {
            return;
        }

        for (ClusterTaskDO clusterTaskDO: doList) {
            if (ClusterTaskStateEnum.FINISHED.getCode().equals(clusterTaskDO.getTaskStatus())) {
                continue;
            }
            try {
                syncAndUpdateClusterTaskState(clusterTaskDO);
            } catch (Exception e) {
                LOGGER.error("sync cluster task state failed, clusterTask:{}.", clusterTaskDO, e);
            }
        }
    }

    private void syncAndUpdateClusterTaskState(ClusterTaskDO clusterTaskDO) {
        ClusterTaskStateEnum stateEnum = clusterTaskService.getTaskState(clusterTaskDO.getAgentTaskId());
        if (ValidateUtils.isNull(stateEnum)) {
            return;
        }
        clusterTaskService.updateTaskState(clusterTaskDO.getId(), stateEnum.getCode());
    }
}