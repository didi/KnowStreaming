package com.xiaojukeji.kafka.manager.kcm.component.agent.n9e;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaFileEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.kcm.common.Constant;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskActionEnum;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskTypeEnum;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.ClusterTaskLog;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.CreationTaskData;
import com.xiaojukeji.kafka.manager.common.utils.HttpUtils;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskStateEnum;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskSubStateEnum;
import com.xiaojukeji.kafka.manager.kcm.component.agent.AbstractAgent;
import com.xiaojukeji.kafka.manager.kcm.component.agent.n9e.entry.N9eCreationTask;
import com.xiaojukeji.kafka.manager.kcm.component.agent.n9e.entry.N9eResult;
import com.xiaojukeji.kafka.manager.kcm.component.agent.n9e.entry.N9eTaskResult;
import com.xiaojukeji.kafka.manager.kcm.component.agent.n9e.entry.N9eTaskStdoutLog;
import com.xiaojukeji.kafka.manager.kcm.component.agent.n9e.entry.bizenum.N9eTaskStatusEnum;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/9/3
 */
@Service("abstractAgent")
public class N9e extends AbstractAgent {
    private static final Logger LOGGER = LoggerFactory.getLogger(N9e.class);

    @Value("${kcm.n9e.base-url}")
    private String      baseUrl;

    @Value("${kcm.n9e.user-token}")
    private String      userToken;

    @Value("${kcm.n9e.account}")
    private String      account;

    @Value("${kcm.n9e.timeout}")
    private Integer     timeout;

    @Value("${kcm.n9e.script-file}")
    private String      scriptFile;

    private String      script;

    private static final String CREATE_TASK_URI = "/api/job-ce/tasks";

    private static final String ACTION_TASK_URI = "/api/job-ce/task/{taskId}/action";

    private static final String ACTION_HOST_TASK_URI = "/api/job-ce/task/{taskId}/host";

    private static final String TASK_STATE_URI = "/api/job-ce/task/{taskId}/state";

    private static final String TASK_SUB_STATE_URI = "/api/job-ce/task/{taskId}/result";

    private static final String TASK_STD_LOG_URI = "/api/job-ce/task/{taskId}/stdout.json";

    @PostConstruct
    public void init() {
        this.script = readScriptInJarFile(scriptFile);
    }

    @Override
    public Result<Long> createTask(CreationTaskData creationTaskData) {
        String content = JsonUtils.toJSONString(buildCreateTaskParam(creationTaskData));

        String response = null;
        try {
            response = HttpUtils.postForString(baseUrl + CREATE_TASK_URI, content, buildHeader());
            N9eResult nr = JsonUtils.stringToObj(response, N9eResult.class);
            if (!ValidateUtils.isBlank(nr.getErr())) {
                LOGGER.error("class=N9e||method=createTask||param={}||response={}||msg=call create task failed", content, response);
                return Result.buildFailure(nr.getErr());
            }
            return Result.buildSuc(Long.valueOf(nr.getDat().toString()));
        } catch (Exception e) {
            LOGGER.error("class=N9e||method=createTask||param={}||response={}||errMsg={}||msg=call create task failed", content, response, e.getMessage());
        }
        return Result.buildFailure("create n9e task failed");
    }

    @Override
    public boolean actionTask(Long taskId, ClusterTaskActionEnum actionEnum) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("action", actionEnum.getAction());

        String response = null;
        try {
            response = HttpUtils.putForString(baseUrl + ACTION_TASK_URI.replace("{taskId}", String.valueOf(taskId)), JsonUtils.toJSONString(param), buildHeader());
            N9eResult nr = JsonUtils.stringToObj(response, N9eResult.class);
            if (ValidateUtils.isBlank(nr.getErr())) {
                return true;
            }

            LOGGER.error("class=N9e||method=actionTask||param={}||response={}||msg=call action task fail", JsonUtils.toJSONString(param), response);
            return false;
        } catch (Exception e) {
            LOGGER.error("class=N9e||method=actionTask||param={}||response={}||errMsg={}||msg=call action task fail", JsonUtils.toJSONString(param), response, e.getMessage());
        }
        return false;
    }

    @Override
    public boolean actionHostTask(Long taskId, ClusterTaskActionEnum actionEnum, String hostname) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("action", actionEnum.getAction());
        params.put("hostname", hostname);

        String response = null;
        try {
            response = HttpUtils.putForString(baseUrl + ACTION_HOST_TASK_URI.replace("{taskId}", String.valueOf(taskId)), JsonUtils.toJSONString(params), buildHeader());
            N9eResult nr = JsonUtils.stringToObj(response, N9eResult.class);
            if (ValidateUtils.isBlank(nr.getErr())) {
                return true;
            }

            LOGGER.error("class=N9e||method=actionHostTask||params={}||response={}||msg=call action host task fail", JsonUtils.toJSONString(params), response);
            return false;
        } catch (Exception e) {
            LOGGER.error("class=N9e||method=actionHostTask||params={}||response={}||errMsg={}||msg=call action host task fail", JsonUtils.toJSONString(params), response, e.getMessage());
        }
        return false;
    }

    @Override
    public Result<ClusterTaskStateEnum> getTaskExecuteState(Long taskId) {
        String response = null;
        try {
            // 获取任务的state
            response = HttpUtils.get(baseUrl + TASK_STATE_URI.replace("{taskId}", String.valueOf(taskId)), null);
            N9eResult nr = JsonUtils.stringToObj(response, N9eResult.class);
            if (!ValidateUtils.isBlank(nr.getErr())) {
                return Result.buildFailure(nr.getErr());
            }

            String state = JsonUtils.stringToObj(JsonUtils.toJSONString(nr.getDat()), String.class);

            N9eTaskStatusEnum n9eTaskStatusEnum = N9eTaskStatusEnum.getByMessage(state);
            if (ValidateUtils.isNull(n9eTaskStatusEnum)) {
                LOGGER.error("class=N9e||method=getTaskExecuteState||taskId={}||response={}||msg=get task state failed", taskId, response);
                return Result.buildFailure("unknown state, state:" + state);
            }
            return Result.buildSuc(n9eTaskStatusEnum.getStatus());
        } catch (Exception e) {
            LOGGER.error("class=N9e||method=getTaskExecuteState||taskId={}||response={}||errMsg={}||msg=get task state failed", taskId, response, e.getMessage());
        }
        return Result.buildFailure("get task state failed");
    }

    @Override
    public Result<Map<String, ClusterTaskSubStateEnum>> getTaskResult(Long taskId) {
        String response = null;
        try {
            // 获取子任务的state
            response = HttpUtils.get(baseUrl + TASK_SUB_STATE_URI.replace("{taskId}", String.valueOf(taskId)), null);
            N9eResult nr = JsonUtils.stringToObj(response, N9eResult.class);
            if (!ValidateUtils.isBlank(nr.getErr())) {
                LOGGER.error("class=N9e||method=getTaskResult||taskId={}||response={}||msg=get task result failed", taskId, response);
                return Result.buildFailure(nr.getErr());
            }

            return Result.buildSuc(JsonUtils.stringToObj(JsonUtils.toJSONString(nr.getDat()), N9eTaskResult.class).convert2HostnameStatusMap());
        } catch (Exception e) {
            LOGGER.error("class=N9e||method=getTaskResult||taskId={}||response={}||errMsg={}||msg=get task result failed", taskId, response, e.getMessage());
        }
        return Result.buildFailure("get task result failed");
    }

    @Override
    public Result<ClusterTaskLog> getTaskLog(Long taskId, String hostname) {
        Map<String, String> params = new HashMap<>(1);
        params.put("hostname", hostname);

        String response = null;
        try {
            response = HttpUtils.get(baseUrl + TASK_STD_LOG_URI.replace("{taskId}", String.valueOf(taskId)), params);
            N9eResult nr = JsonUtils.stringToObj(response, N9eResult.class);
            if (!ValidateUtils.isBlank(nr.getErr())) {
                LOGGER.error("class=N9e||method=getTaskLog||taskId={}||response={}||msg=get task log failed", taskId, response);
                return Result.buildFailure(nr.getErr());
            }

            List<N9eTaskStdoutLog> dtoList = JsonUtils.stringToArrObj(JsonUtils.toJSONString(nr.getDat()), N9eTaskStdoutLog.class);
            if (ValidateUtils.isEmptyList(dtoList)) {
                return Result.buildSuc(new ClusterTaskLog(""));
            }
            return Result.buildSuc(new ClusterTaskLog(dtoList.get(0).getStdout()));
        } catch (Exception e) {
            LOGGER.error("class=N9e||method=getTaskLog||taskId={}||response={}||errMsg={}||msg=get task log failed", taskId, response, e.getMessage());
        }
        return Result.buildFailure("get task log failed");
    }

    private Map<String, String> buildHeader() {
        Map<String,String> headers = new HashMap<>(2);
        headers.put("Content-Type", "application/json;charset=UTF-8");
        headers.put("X-User-Token", userToken);
        return headers;
    }

    private N9eCreationTask buildCreateTaskParam(CreationTaskData creationTaskData) {
        StringBuilder sb = new StringBuilder();
        sb.append(creationTaskData.getUuid()).append(",,");
        sb.append(creationTaskData.getClusterId()).append(",,");
        sb.append(ClusterTaskTypeEnum.getByName(creationTaskData.getTaskType()).getWay()).append(",,");
        sb.append(creationTaskData.getKafkaPackageName().replace(KafkaFileEnum.PACKAGE.getSuffix(), "")).append(",,");
        sb.append(creationTaskData.getKafkaPackageMd5()).append(",,");
        sb.append(creationTaskData.getKafkaPackageUrl()).append(",,");
        sb.append(creationTaskData.getServerPropertiesName().replace(KafkaFileEnum.SERVER_CONFIG.getSuffix(), "")).append(",,");
        sb.append(creationTaskData.getServerPropertiesMd5()).append(",,");
        sb.append(creationTaskData.getServerPropertiesUrl());

        N9eCreationTask n9eCreationTask = new N9eCreationTask();
        n9eCreationTask.setTitle(Constant.TASK_TITLE_PREFIX + "-集群ID:" + creationTaskData.getClusterId());
        n9eCreationTask.setBatch(Constant.AGENT_TASK_BATCH);
        n9eCreationTask.setTolerance(Constant.AGENT_TASK_TOLERANCE);
        n9eCreationTask.setTimeout(this.timeout);
        n9eCreationTask.setPause(ListUtils.strList2String(creationTaskData.getPauseList()));
        n9eCreationTask.setScript(this.script);
        n9eCreationTask.setArgs(sb.toString());
        n9eCreationTask.setAccount(this.account);
        n9eCreationTask.setAction(ClusterTaskActionEnum.PAUSE.getAction());
        n9eCreationTask.setHosts(creationTaskData.getHostList());
        return n9eCreationTask;
    }
}