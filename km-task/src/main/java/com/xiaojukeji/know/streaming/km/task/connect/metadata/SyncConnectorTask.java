package com.xiaojukeji.know.streaming.km.task.connect.metadata;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@Task(name = "SyncConnectorTask",
        description = "Connector信息同步到DB",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class SyncConnectorTask extends AbstractAsyncMetadataDispatchTask {
    private static final ILog LOGGER = LogFactory.getLog(SyncConnectorTask.class);

    @Autowired
    private ConnectorService connectorService;
    @Override
    public TaskResult processClusterTask(ConnectCluster connectCluster, long triggerTimeUnitMs) {
        Result<List<String>> nameListResult = connectorService.listConnectorsFromCluster(connectCluster.getId());
        if (nameListResult.failed()) {
            return TaskResult.FAIL;
        }

        boolean allSuccess = true;

        List<KSConnector> connectorList = new ArrayList<>();
        for (String connectorName: nameListResult.getData()) {
            Result<KSConnector> ksConnectorResult = connectorService.getAllConnectorInfoFromCluster(connectCluster.getId(), connectorName);
            if (ksConnectorResult.failed()) {
                LOGGER.error(
                        "method=processClusterTask||connectClusterId={}||connectorName={}||result={}",
                        connectCluster.getId(), connectorName, ksConnectorResult
                );

                allSuccess = false;
                continue;
            }

            connectorList.add(ksConnectorResult.getData());
        }

        //mm2相关信息的添加
        connectorService.completeMirrorMakerInfo(connectCluster, connectorList);

        connectorService.batchReplace(connectCluster.getKafkaClusterPhyId(), connectCluster.getId(), connectorList, new HashSet<>(nameListResult.getData()));

        return allSuccess? TaskResult.SUCCESS: TaskResult.FAIL;
    }
}
