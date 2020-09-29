package com.xiaojukeji.kafka.manager.kcm.impl;

import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.kcm.ClusterTaskService;
import com.xiaojukeji.kafka.manager.kcm.common.Converters;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskActionEnum;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ClusterTaskConstant;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.ClusterTaskSubStatus;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskStateEnum;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskSubStateEnum;
import com.xiaojukeji.kafka.manager.kcm.component.agent.AbstractAgent;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.CreationTaskData;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.ClusterTaskStatus;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskTypeEnum;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.kcm.common.entry.dto.AbstractClusterTaskDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.ClusterTaskDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterTaskDO;
import com.xiaojukeji.kafka.manager.kcm.tasks.AbstractClusterTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author zengqiao
 * @date 20/5/19
 */
@Service("clusterTaskService")
public class ClusterTaskServiceImpl implements ClusterTaskService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClusterTaskServiceImpl.class);

    @Autowired
    private AbstractAgent abstractAgent;

    @Autowired
    private ClusterTaskDao clusterTaskDao;

    @Override
    public Result createTask(AbstractClusterTaskDTO dto, String operator) {
        if (!dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        ClusterTaskTypeEnum taskTypeEnum = ClusterTaskTypeEnum.getByName(dto.getTaskType());
        if (ValidateUtils.isNull(taskTypeEnum)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        AbstractClusterTaskService abstractClusterTaskService =
                SpringTool.getBean(taskTypeEnum.getBeanName());

        // 构造参数
        Result<CreationTaskData> dtoResult = abstractClusterTaskService.getCreateTaskParamDTO(dto);
        if (!Constant.SUCCESS.equals(dtoResult.getCode())) {
            return dtoResult;
        }

        // 创建任务
        Long agentTaskId = abstractAgent.createTask(dtoResult.getData());
        if (ValidateUtils.isNull(agentTaskId)) {
            return Result.buildFrom(ResultStatus.CALL_CLUSTER_TASK_AGENT_FAILED);
        }

        try {
            if (clusterTaskDao.insert(Converters.convert2ClusterTaskDO(agentTaskId, dtoResult.getData(), operator)) > 0) {
                return Result.buildFrom(ResultStatus.SUCCESS);
            }
        } catch (Exception e) {
            LOGGER.error("create cluster task failed, clusterTask:{}.", dto, e);
        }
        return Result.buildFrom(ResultStatus.MYSQL_ERROR);
    }

    @Override
    public ResultStatus executeTask(Long taskId, String action, String hostname) {
        ClusterTaskDO clusterTaskDO = this.getById(taskId);
        if (ValidateUtils.isNull(clusterTaskDO)) {
            return ResultStatus.RESOURCE_NOT_EXIST;
        }
        Long agentTaskId = getActiveAgentTaskId(clusterTaskDO);
        Boolean rollback = inRollback(clusterTaskDO);

        ClusterTaskStateEnum stateEnum = abstractAgent.getTaskState(agentTaskId);
        if (ClusterTaskActionEnum.START.getMessage().equals(action)
                && ClusterTaskStateEnum.BLOCKED.equals(stateEnum)) {
            // 暂停状态, 可以执行开始
            return actionTaskExceptRollbackAction(agentTaskId, action, "");
        }
        if (ClusterTaskActionEnum.PAUSE.getMessage().equals(action)
                && ClusterTaskStateEnum.RUNNING.equals(stateEnum)) {
            // 运行状态, 可以执行暂停
            return actionTaskExceptRollbackAction(agentTaskId, action, "");
        }
        if (ClusterTaskActionEnum.IGNORE.getMessage().equals(action)
                || ClusterTaskActionEnum.CANCEL.getMessage().equals(action)) {
            // 忽略 & 取消随时都可以操作
            return actionTaskExceptRollbackAction(agentTaskId, action, hostname);
        }
        if ((!ClusterTaskStateEnum.FINISHED.equals(stateEnum) || !rollback)
                && ClusterTaskActionEnum.ROLLBACK.getMessage().equals(action)) {
            // 暂未操作完时可以回滚, 回滚所有操作过的机器到上一个版本
            return actionTaskRollback(clusterTaskDO);
        }
        return ResultStatus.OPERATION_FAILED;
    }

    private ResultStatus actionTaskExceptRollbackAction(Long agentId, String action, String hostname) {
        if (!ValidateUtils.isBlank(hostname)) {
            return actionHostTaskExceptRollbackAction(agentId, action, hostname);
        }
        if (abstractAgent.actionTask(agentId, action)) {
            return ResultStatus.SUCCESS;
        }
        return ResultStatus.OPERATION_FAILED;
    }

    private ResultStatus actionHostTaskExceptRollbackAction(Long agentId, String action, String hostname) {
        if (abstractAgent.actionHostTask(agentId, action, hostname)) {
            return ResultStatus.SUCCESS;
        }
        return ResultStatus.OPERATION_FAILED;
    }

    private ResultStatus actionTaskRollback(ClusterTaskDO clusterTaskDO) {
        if (!ClusterTaskConstant.INVALID_AGENT_TASK_ID.equals(clusterTaskDO.getAgentRollbackTaskId())) {
            return ResultStatus.OPERATION_FORBIDDEN;
        }

        Map<String, ClusterTaskSubStateEnum> subStatusEnumMap =
                abstractAgent.getTaskResult(clusterTaskDO.getAgentTaskId());
        if (ValidateUtils.isNull(subStatusEnumMap)) {
            return ResultStatus.CALL_CLUSTER_TASK_AGENT_FAILED;
        }

        // 回滚顺序和升级顺序一致, 仅回滚操作过的机器
        List<String> rollbackHostList = new ArrayList<>();
        List<String> rollbackPauseHostList = new ArrayList<>();
        for (String host: ListUtils.string2StrList(clusterTaskDO.getHostList())) {
            ClusterTaskSubStateEnum subStateEnum = subStatusEnumMap.get(host);
            if (ValidateUtils.isNull(subStateEnum)) {
                // 机器对应的任务查询失败
                return ResultStatus.OPERATION_FAILED;
            }
            if (ClusterTaskSubStateEnum.WAITING.equals(subStateEnum)) {
                break;
            }

            if (rollbackPauseHostList.isEmpty()) {
                rollbackPauseHostList.add(host);
            }
            rollbackHostList.add(host);
        }
        if (ValidateUtils.isEmptyList(rollbackHostList)) {
            // 不存在需要回滚的机器, 返回操作失败
            return ResultStatus.OPERATION_FAILED;
        }

        clusterTaskDO.setRollbackHostList(ListUtils.strList2String(rollbackHostList));
        clusterTaskDO.setRollbackPauseHostList(ListUtils.strList2String(rollbackPauseHostList));

        // 创建任务
        Long agentTaskId = abstractAgent.createTask(Converters.convert2CreationTaskData(clusterTaskDO));
        if (ValidateUtils.isNull(agentTaskId)) {
            return ResultStatus.CALL_CLUSTER_TASK_AGENT_FAILED;
        }

        try {
            clusterTaskDO.setAgentRollbackTaskId(agentTaskId);
            if (clusterTaskDao.updateRollback(clusterTaskDO) <= 0) {
                return ResultStatus.MYSQL_ERROR;
            }
            abstractAgent.actionTask(clusterTaskDO.getAgentTaskId(), ClusterTaskActionEnum.CANCEL.getMessage());
            return ResultStatus.SUCCESS;
        } catch (Exception e) {
            LOGGER.error("create cluster task failed, clusterTaskDO:{}.", clusterTaskDO, e);
        }
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public Result<String> getTaskLog(Long taskId, String hostname) {
        ClusterTaskDO clusterTaskDO = this.getById(taskId);
        if (ValidateUtils.isNull(clusterTaskDO)) {
            return Result.buildFrom(ResultStatus.TASK_NOT_EXIST);
        }

        String stdoutLog = abstractAgent.getTaskLog(getActiveAgentTaskId(clusterTaskDO, hostname), hostname);
        if (ValidateUtils.isNull(stdoutLog)) {
            return Result.buildFrom(ResultStatus.CALL_CLUSTER_TASK_AGENT_FAILED);
        }
        return new Result<>(stdoutLog);
    }

    @Override
    public Result<ClusterTaskStatus> getTaskStatus(Long taskId) {
        ClusterTaskDO clusterTaskDO = this.getById(taskId);
        if (ValidateUtils.isNull(clusterTaskDO)) {
            return Result.buildFrom(ResultStatus.TASK_NOT_EXIST);
        }

        return new Result<>(new ClusterTaskStatus(
                clusterTaskDO.getId(),
                clusterTaskDO.getClusterId(),
                inRollback(clusterTaskDO),
                abstractAgent.getTaskState(getActiveAgentTaskId(clusterTaskDO)),
                getTaskSubStatus(clusterTaskDO)
        ));
    }

    @Override
    public ClusterTaskStateEnum getTaskState(Long agentTaskId) {
        return abstractAgent.getTaskState(agentTaskId);
    }

    private List<ClusterTaskSubStatus> getTaskSubStatus(ClusterTaskDO clusterTaskDO) {
        Map<String, ClusterTaskSubStateEnum> statusMap = this.getClusterTaskSubState(clusterTaskDO);
        if (ValidateUtils.isNull(statusMap)) {
            return null;
        }
        List<String> pauseList = ListUtils.string2StrList(clusterTaskDO.getPauseHostList());

        int groupNum = 0;
        List<ClusterTaskSubStatus> subStatusList = new ArrayList<>();
        for (String host: ListUtils.string2StrList(clusterTaskDO.getHostList())) {
            ClusterTaskSubStatus subStatus = new ClusterTaskSubStatus();
            subStatus.setHostname(host);
            subStatus.setStatus(statusMap.get(host));
            subStatus.setGroupNum(groupNum);
            if (pauseList.size()> groupNum && pauseList.get(groupNum).equals(host)) {
                groupNum += 1;
            }
            subStatusList.add(subStatus);
        }
        return subStatusList;
    }

    private Map<String, ClusterTaskSubStateEnum> getClusterTaskSubState(ClusterTaskDO clusterTaskDO) {
        Map<String, ClusterTaskSubStateEnum> statusMap = abstractAgent.getTaskResult(clusterTaskDO.getAgentTaskId());
        if (ValidateUtils.isNull(statusMap)) {
            return null;
        }
        if (!inRollback(clusterTaskDO)) {
            return statusMap;
        }

        Map<String, ClusterTaskSubStateEnum> rollbackStatusMap =
                abstractAgent.getTaskResult(clusterTaskDO.getAgentRollbackTaskId());
        if (ValidateUtils.isNull(rollbackStatusMap)) {
            return null;
        }
        statusMap.putAll(rollbackStatusMap);
        return statusMap;
    }

    @Override
    public ClusterTaskDO getById(Long taskId) {
        try {
            return clusterTaskDao.getById(taskId);
        } catch (Exception e) {
            LOGGER.error("get cluster task failed, taskId:{}.", taskId);
        }
        return null;
    }

    @Override
    public List<ClusterTaskDO> listAll() {
        try {
            return clusterTaskDao.listAll();
        } catch (Exception e) {
            LOGGER.error("get all cluster task failed.");
        }
        return null;
    }

    @Override
    public int updateTaskState(Long taskId, Integer taskStatus) {
        return clusterTaskDao.updateTaskState(taskId, taskStatus);
    }

    private Long getActiveAgentTaskId(ClusterTaskDO clusterTaskDO) {
        if (ClusterTaskConstant.INVALID_AGENT_TASK_ID.equals(clusterTaskDO.getAgentRollbackTaskId())) {
            return clusterTaskDO.getAgentTaskId();
        }
        return clusterTaskDO.getAgentRollbackTaskId();
    }

    private Long getActiveAgentTaskId(ClusterTaskDO clusterTaskDO, String hostname) {
        if (ClusterTaskConstant.INVALID_AGENT_TASK_ID.equals(clusterTaskDO.getAgentRollbackTaskId())) {
            return clusterTaskDO.getAgentTaskId();
        }
        if (ListUtils.string2StrList(clusterTaskDO.getRollbackHostList()).contains(hostname)) {
            return clusterTaskDO.getAgentRollbackTaskId();
        }
        return clusterTaskDO.getAgentTaskId();
    }

    private boolean inRollback(ClusterTaskDO clusterTaskDO) {
        if (ClusterTaskConstant.INVALID_AGENT_TASK_ID.equals(clusterTaskDO.getAgentRollbackTaskId())) {
            return false;
        }
        return true;
    }
}