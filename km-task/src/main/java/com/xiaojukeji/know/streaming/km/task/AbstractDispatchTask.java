package com.xiaojukeji.know.streaming.km.task;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.job.core.job.Job;
import com.didiglobal.logi.job.core.job.JobContext;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.EntityIdInterface;
import com.xiaojukeji.know.streaming.km.common.exception.AdminTaskCodeException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractDispatchTask<E extends Comparable & EntityIdInterface> implements Job {
    private static final ILog LOGGER = LogFactory.getLog(AbstractDispatchTask.class);

    /**
     * 任务名称
     */
    protected String taskName;

    /**
     * 任务超时时间
     */
    protected Long timeoutUnitSec;

    /**
     * 返回所有的任务
     * @return
     */
    protected abstract List<E> listAllTasks();

    /**
     * 执行被分配的任务
     * @param subTaskList 子任务列表
     * @param triggerTimeUnitMs 任务触发时间
     * @return
     */
    protected abstract TaskResult processTask(List<E> subTaskList, long triggerTimeUnitMs);

    @PostConstruct
    void init() {
        Task customTask = this.getClass().getAnnotation(Task.class);
        if (customTask == null) {
            LOGGER.error("method=init||className={}||msg=extend AbstractDispatchTask must use @Task annotation.", getClass().getName());
            throw new AdminTaskCodeException("extend AbstractDispatchTask must use @Task annotation");
        }

        // 必须使用广播模式，否则会有任务丢失，因此如果采用了其他模式，则直接输出异常日志并退出
        if (!ConsensualEnum.BROADCAST.equals(customTask.consensual())) {
            LOGGER.error("method=init||className={}||msg=extend AbstractDispatchTask and @Task annotation must use BROADCAST.", getClass().getName());
            throw new AdminTaskCodeException("extend AbstractDispatchTask and @Task annotation must use BROADCAST");
        }

        this.taskName = customTask.name();
        this.timeoutUnitSec = customTask.timeout();
    }

    @Override
    public TaskResult execute(JobContext jobContext) {
        try {
            long triggerTimeUnitMs = System.currentTimeMillis();

            // 获取所有的任务
            List<E> allTaskList = this.listAllTasks();
            if (ValidateUtils.isEmptyList(allTaskList)) {
                LOGGER.debug("all-task is empty, finish process, taskName:{} jobContext:{}", taskName, jobContext);
                return TaskResult.SUCCESS;
            }

            // 计算当前机器需要执行的任务
            List<E> subTaskList = this.selectTask(allTaskList, jobContext.getAllWorkerCodes(), jobContext.getCurrentWorkerCode());

            if (ValidateUtils.isEmptyList(allTaskList)) {
                LOGGER.debug("sub-task is empty, finish process, taskName:{} jobContext:{}", taskName, jobContext);
                return TaskResult.SUCCESS;
            }

            // 进行任务处理
            TaskResult ret = this.processTask(subTaskList, triggerTimeUnitMs);

            //组装信息
            TaskResult taskResult = new TaskResult();
            taskResult.setCode(ret.getCode());
            taskResult.setMessage(ConvertUtil.list2String(subTaskList, ","));

            return taskResult;

        } catch (Exception e) {
            LOGGER.error("process task failed, taskName:{}", taskName, e);

            return new TaskResult(TaskResult.FAIL_CODE, e.toString());
        }
    }

    /**
     * 挑选当前机器需要执行的任务
     * @param allTaskList 所有的任务
     * @return 需要执行的任务
     */
    private List<E> selectTask(List<E> allTaskList, List<String> allWorkCodes, String currentWorkerCode) {
        if(ValidateUtils.isEmptyList(allTaskList)) {
            return new ArrayList<>();
        }

        if (ValidateUtils.isEmptyList(allWorkCodes) || ValidateUtils.isBlank(currentWorkerCode)) {
            LOGGER.warn("task running, but without registrant, and so scheduled tasks can't execute, taskName:{}.", taskName);
            return new ArrayList<>();
        }

        Collections.sort(allTaskList);
        Collections.sort(allWorkCodes);

        int idx = 0;
        while (idx < allWorkCodes.size()) {
            if (allWorkCodes.get(idx).equals(currentWorkerCode)) {
                break;
            }
            idx += 1;
        }

        if (idx == allWorkCodes.size()) {
            LOGGER.debug("task running, registrants not conclude present machine, taskName:{}.", taskName);
            return new ArrayList<>();
        }

        int count = allTaskList.size() / allWorkCodes.size();
        if (allTaskList.size() % allWorkCodes.size() != 0) {
            count += 1;
        }
        if (idx * count >= allTaskList.size()) {
            return new ArrayList<>();
        }
        return allTaskList.subList(idx * count, Math.min(idx * count + count, allTaskList.size()));
    }

    public String getTaskName() {
        return taskName;
    }

    public Long getTimeoutUnitSec() {
        return timeoutUnitSec;
    }
}
