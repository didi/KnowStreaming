package com.xiaojukeji.kafka.manager.kcm.component.agent.n9e;

import com.alibaba.fastjson.JSON;
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
    private String baseUrl;

    @Value("${kcm.n9e.username}")
    private String username;

    @Value("${kcm.n9e.user-token}")
    private String userToken;

    @Value("${kcm.n9e.tpl-id}")
    private Integer tplId;

    @Value("${kcm.n9e.timeout}")
    private Integer timeout;

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

    @Override
    public Long createTask(CreationTaskData dto) {
        StringBuilder sb = new StringBuilder();
        sb.append(dto.getKafkaPackageName()).append(",,").append(dto.getKafkaPackageMd5()).append(",,");
        sb.append(dto.getServerPropertiesName()).append(",,").append(dto.getServerPropertiesMd5());

        Map<String, Object> param = new HashMap<>();
        param.put("tpl_id", tplId);
        param.put("batch", BATCH);
        param.put("tolerance", TOLERANCE);
        param.put("timeout", timeout);
        param.put("hosts", dto.getHostList());
        param.put("pause", ListUtils.strList2String(dto.getPauseList()));
        param.put("action", "pause");
        param.put("args", sb.toString());

        String response = null;
        try {
            response = HttpUtils.postForString(
                    baseUrl + CREATE_TASK_URI,
                    JsonUtils.toJSONString(param),
                    buildHeader()
            );
            N9eResult zr = JSON.parseObject(response, N9eResult.class);
            if (!ValidateUtils.isBlank(zr.getErr())) {
                return null;
            }
            return Long.valueOf(zr.getDat().toString());
        } catch (Exception e) {
            LOGGER.error("create task failed, dto:{}.", dto, e);
        }
        return null;
    }

    @Override
    public Boolean actionTask(Long taskId, String action) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("action", action);

        String response = null;
        try {
            response = HttpUtils.postForString(
                    baseUrl + ACTION_TASK_URI.replace("{taskId}", taskId.toString()),
                    JSON.toJSONString(param),
                    buildHeader()
            );
            N9eResult zr = JSON.parseObject(response, N9eResult.class);
            if (ValidateUtils.isBlank(zr.getErr())) {
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.error("action task failed, taskId:{}, action:{}.", taskId, action, e);
        }
        return false;
    }

    @Override
    public Boolean actionHostTask(Long taskId, String action, String hostname) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("action", action);
        param.put("hostname", hostname);

        String response = null;
        try {
            response = HttpUtils.postForString(
                    baseUrl + ACTION_HOST_TASK_URI.replace("{taskId}", taskId.toString()),
                    JSON.toJSONString(param),
                    buildHeader()
            );
            N9eResult zr = JSON.parseObject(response, N9eResult.class);
            if (ValidateUtils.isBlank(zr.getErr())) {
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.error("action task failed, taskId:{}, action:{}, hostname:{}.", taskId, action, hostname, e);
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
            LOGGER.error("get task status failed, agentTaskId:{}.", agentTaskId, e);
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
        Map<String,String> headers = new HashMap<>(1);
        headers.put("Content-Type", "application/json;charset=UTF-8");
        headers.put("X-User-Token", userToken);
        return headers;
    }
}