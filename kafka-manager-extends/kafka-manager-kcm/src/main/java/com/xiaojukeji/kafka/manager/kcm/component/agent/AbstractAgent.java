package com.xiaojukeji.kafka.manager.kcm.component.agent;

import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskStateEnum;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskSubStateEnum;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.CreationTaskData;

import java.util.Map;


/**
 * Agent 抽象类
 * @author zengqiao
 * @date 20/4/26
 */
public abstract class AbstractAgent {
    /**
     * 创建任务
     */
    public abstract Long createTask(CreationTaskData dto);

    /**
     * 任务动作
     */
    public abstract Boolean actionTask(Long taskId, String action);

    /**
     * 任务动作
     */
    public abstract Boolean actionHostTask(Long taskId, String action, String hostname);

    /**
     * 获取任务状态
     */
    public abstract ClusterTaskStateEnum getTaskState(Long agentTaskId);

    /**
     * 获取任务结果
     */
    public abstract Map<String, ClusterTaskSubStateEnum> getTaskResult(Long taskId);

    /**
     * 获取任务日志
     */
    public abstract String getTaskLog(Long agentTaskId, String hostname);
}