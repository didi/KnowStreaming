package com.xiaojukeji.kafka.manager.kcm.component.agent;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskActionEnum;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskStateEnum;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskSubStateEnum;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.ClusterTaskLog;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.CreationTaskData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;


/**
 * Agent 抽象类
 * @author zengqiao
 * @date 20/4/26
 */
public abstract class AbstractAgent {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAgent.class);

    /**
     * 创建任务
     * @param creationTaskData 创建任务参数
     * @return 任务ID
     */
    public abstract Result<Long> createTask(CreationTaskData creationTaskData);

    /**
     * 执行任务
     * @param taskId 任务ID
     * @param actionEnum 执行动作
     * @return true:触发成功, false:触发失败
     */
    public abstract boolean actionTask(Long taskId, ClusterTaskActionEnum actionEnum);

    /**
     * 执行任务
     * @param taskId 任务ID
     * @param actionEnum 执行动作
     * @param hostname 具体主机
     * @return true:触发成功, false:触发失败
     */
    public abstract boolean actionHostTask(Long taskId, ClusterTaskActionEnum actionEnum, String hostname);

    /**
     * 获取任务运行的状态[阻塞, 执行中, 完成等]
     * @param taskId 任务ID
     * @return 任务状态
     */
    public abstract Result<ClusterTaskStateEnum> getTaskExecuteState(Long taskId);

    /**
     * 获取任务结果
     * @param taskId 任务ID
     * @return 任务结果
     */
    public abstract Result<Map<String, ClusterTaskSubStateEnum>> getTaskResult(Long taskId);

    /**
     * 获取任务执行日志
     * @param taskId 任务ID
     * @param hostname 具体主机
     * @return 机器运行日志
     */
    public abstract Result<ClusterTaskLog> getTaskLog(Long taskId, String hostname);

    protected static String readScriptInJarFile(String fileName) {
        InputStream inputStream = AbstractAgent.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            LOGGER.error("class=AbstractAgent||method=readScriptInJarFile||fileName={}||msg=read script failed", fileName);
            return "";
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            LOGGER.error("class=AbstractAgent||method=readScriptInJarFile||fileName={}||errMsg={}||msg=read script failed", fileName, e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.error("class=AbstractAgent||method=readScriptInJarFile||fileName={}||errMsg={}||msg=close reading script failed", fileName, e.getMessage());
            }
        }
        return "";
    }
}