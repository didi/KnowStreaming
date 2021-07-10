package com.xiaojukeji.kafka.manager.kcm;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterTaskDO;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.ClusterTaskStatus;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.kcm.common.entry.dto.AbstractClusterTaskDTO;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskStateEnum;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/19
 */
public interface ClusterTaskService {
    Result createTask(AbstractClusterTaskDTO dto, String operator);

    ClusterTaskDO getById(Long taskId);

    List<ClusterTaskDO> listAll();

    ResultStatus executeTask(Long taskId, String action, String hostname);

    Result<String> getTaskLog(Long taskId, String hostname);

    Result<ClusterTaskStatus> getTaskStatus(Long taskId);

    ClusterTaskStateEnum getTaskState(Long agentTaskId);

    int updateTaskState(Long taskId, Integer taskStatus);
}