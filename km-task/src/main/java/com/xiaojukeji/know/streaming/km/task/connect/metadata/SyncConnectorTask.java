package com.xiaojukeji.know.streaming.km.task.connect.metadata;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;


@Task(name = "SyncConnectorTask",
        description = "Connector信息同步到DB",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class SyncConnectorTask extends AbstractAsyncMetadataDispatchTask {
    @Autowired
    private ConnectorService connectorService;

    @Override
    public TaskResult processClusterTask(ConnectCluster connectCluster, long triggerTimeUnitMs) {
        // 获取信息
        Result<Tuple<Set<String>, List<KSConnector>>> dataResult = connectorService.getDataFromKafka(connectCluster);
        if (dataResult.failed()) {
            return new TaskResult(TaskResult.FAIL_CODE, dataResult.getMessage());
        }

        // 更新到DB
        connectorService.writeToDB( connectCluster.getId(), dataResult.getData().v1(), dataResult.getData().v2());

        // 返回结果
        return dataResult.getData().v1().size() == dataResult.getData().v2().size()? TaskResult.SUCCESS: TaskResult.FAIL;
    }
}
