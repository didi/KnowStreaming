package com.xiaojukeji.kafka.manager.kcm.component.agent.n9e;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.bizenum.KafkaFileEnum;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskTypeEnum;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.CreationTaskData;
import com.xiaojukeji.kafka.manager.common.utils.HttpUtils;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskStateEnum;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskSubStateEnum;
import com.xiaojukeji.kafka.manager.kcm.component.agent.AbstractAgent;
import com.xiaojukeji.kafka.manager.kcm.component.agent.n9e.entry.N9eResult;
import com.xiaojukeji.kafka.manager.kcm.component.agent.n9e.entry.N9eTaskResultDTO;
import com.xiaojukeji.kafka.manager.kcm.component.agent.n9e.entry.N9eTaskStatusEnum;
import com.xiaojukeji.kafka.manager.kcm.component.agent.n9e.entry.N9eTaskStdoutDTO;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    /**
     * 并发度，顺序执行
     */
    private static final Integer BATCH = 1;

    /**
     * 失败的容忍度为0
     */
    private static final Integer TOLERANCE = 0;

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
    public Long createTask(CreationTaskData creationTaskData) {
        Map<String, Object> param = buildCreateTaskParam(creationTaskData);

        String response = null;
        try {
            response = HttpUtils.postForString(
                    baseUrl + CREATE_TASK_URI,
                    JsonUtils.toJSONString(param),
                    buildHeader()
            );
            N9eResult zr = JSON.parseObject(response, N9eResult.class);
            if (!ValidateUtils.isBlank(zr.getErr())) {
                LOGGER.warn("class=N9e||method=createTask||param={}||errMsg={}||msg=call create task fail", JsonUtils.toJSONString(param),zr.getErr());
                return null;
            }
            return Long.valueOf(zr.getDat().toString());
        } catch (Exception e) {
            LOGGER.error("create task failed, req:{}.", creationTaskData, e);
        }
        return null;
    }

    @Override
    public Boolean actionTask(Long taskId, String action) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("action", action);

        String response = null;
        try {
            response = HttpUtils.putForString(
                    baseUrl + ACTION_TASK_URI.replace("{taskId}", taskId.toString()),
                    JSON.toJSONString(param),
                    buildHeader()
            );
            N9eResult zr = JSON.parseObject(response, N9eResult.class);
            if (ValidateUtils.isBlank(zr.getErr())) {
                return true;
            }
            LOGGER.warn("class=N9e||method=actionTask||param={}||errMsg={}||msg=call action task fail", JSON.toJSONString(param),zr.getErr());
            return false;
        } catch (Exception e) {
            LOGGER.error("action task failed, taskId:{}, action:{}.", taskId, action, e);
        }
        return false;
    }

    @Override
    public Boolean actionHostTask(Long taskId, String action, String hostname) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("action", action);
        param.put("hostname", hostname);

        String response = null;
        try {
            response = HttpUtils.putForString(
                    baseUrl + ACTION_HOST_TASK_URI.replace("{taskId}", taskId.toString()),
                    JSON.toJSONString(param),
                    buildHeader()
            );
            N9eResult zr = JSON.parseObject(response, N9eResult.class);
            if (ValidateUtils.isBlank(zr.getErr())) {
                return true;
            }
            LOGGER.warn("class=N9e||method=actionHostTask||param={}||errMsg={}||msg=call action host task fail", JSON.toJSONString(param),zr.getErr());
            return false;
        } catch (Exception e) {
            LOGGER.error("action task failed, taskId:{} action:{} hostname:{}.", taskId, action, hostname, e);
        }
        return false;
    }

    @Override
    public ClusterTaskStateEnum getTaskState(Long agentTaskId) {
        String response = null;
        try {
            // 获取任务的state
            response = HttpUtils.get(
                    baseUrl + TASK_STATE_URI.replace("{taskId}", agentTaskId.toString()), null
            );
            N9eResult n9eResult = JSON.parseObject(response, N9eResult.class);
            if (!ValidateUtils.isBlank(n9eResult.getErr())) {
                LOGGER.error("get response result failed, agentTaskId:{} response:{}.", agentTaskId, response);
                return null;
            }
            String state = JSON.parseObject(JSON.toJSONString(n9eResult.getDat()), String.class);
            N9eTaskStatusEnum n9eTaskStatusEnum = N9eTaskStatusEnum.getByMessage(state);
            if (ValidateUtils.isNull(n9eTaskStatusEnum)) {
                LOGGER.error("get task status failed, agentTaskId:{} state:{}.", agentTaskId, state);
                return null;
            }
            return n9eTaskStatusEnum.getStatus();
        } catch (Exception e) {
            LOGGER.error("get task status failed, agentTaskId:{} response:{}.", agentTaskId, response, e);
        }
        return null;
    }

    @Override
    public Map<String, ClusterTaskSubStateEnum> getTaskResult(Long agentTaskId) {
        String response = null;
        try {
            // 获取子任务的state
            response = HttpUtils.get(baseUrl + TASK_SUB_STATE_URI.replace("{taskId}", agentTaskId.toString()), null);
            N9eResult n9eResult = JSON.parseObject(response, N9eResult.class);

            N9eTaskResultDTO n9eTaskResultDTO =
                    JSON.parseObject(JSON.toJSONString(n9eResult.getDat()), N9eTaskResultDTO.class);
            return n9eTaskResultDTO.convert2HostnameStatusMap();
        } catch (Exception e) {
            LOGGER.error("get task result failed, agentTaskId:{} response:{}.", agentTaskId, response, e);
        }
        return null;
    }

    @Override
    public String getTaskLog(Long agentTaskId, String hostname) {
        String response = null;
        try {
            Map<String, String> params = new HashMap<>(1);
            params.put("hostname", hostname);

            response = HttpUtils.get(baseUrl + TASK_STD_LOG_URI.replace("{taskId}", agentTaskId.toString()), params);
            N9eResult n9eResult = JSON.parseObject(response, N9eResult.class);
            if (!ValidateUtils.isBlank(n9eResult.getErr())) {
                LOGGER.error("get task log failed, agentTaskId:{} response:{}.", agentTaskId, response);
                return null;
            }
            List<N9eTaskStdoutDTO> dtoList =
                    JSON.parseArray(JSON.toJSONString(n9eResult.getDat()), N9eTaskStdoutDTO.class);
            if (ValidateUtils.isEmptyList(dtoList)) {
                return "";
            }
            return dtoList.get(0).getStdout();
        } catch (Exception e) {
            LOGGER.error("get task log failed, agentTaskId:{}.", agentTaskId, e);
        }
        return null;
    }

    private Map<String, String> buildHeader() {
        Map<String,String> headers = new HashMap<>(2);
        headers.put("Content-Type", "application/json;charset=UTF-8");
        headers.put("X-User-Token", userToken);
        return headers;
    }

    private Map<String, Object> buildCreateTaskParam(CreationTaskData creationTaskData) {
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

        Map<String, Object> params = new HashMap<>(10);
        params.put("title", String.format("集群ID=%d-升级部署", creationTaskData.getClusterId()));
        params.put("batch", BATCH);
        params.put("tolerance", TOLERANCE);
        params.put("timeout", timeout);
        params.put("pause", ListUtils.strList2String(creationTaskData.getPauseList()));
        params.put("script", this.script);
        params.put("args", sb.toString());
        params.put("account", account);
        params.put("action", "pause");
        params.put("hosts", creationTaskData.getHostList());
        return params;
    }

    private static String readScriptInJarFile(String fileName) {
        InputStream inputStream = N9e.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            LOGGER.error("read kcm script failed, filename:{}", fileName);
            return "";
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder("");

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            LOGGER.error("read kcm script failed, filename:{}", fileName, e);
            return "";
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.error("close reading kcm script failed, filename:{}", fileName, e);
            }
        }
    }
}