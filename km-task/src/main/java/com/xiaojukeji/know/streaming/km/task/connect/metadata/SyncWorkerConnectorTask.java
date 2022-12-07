package com.xiaojukeji.know.streaming.km.task.connect.metadata;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.WorkerConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafka.KSGroupDescription;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectorPO;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupService;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
/**
 * @author wyb
 * @date 2022/11/14
 */
@Task(name = "SyncWorkerConnectorTask",
        description = "WorkerConnector信息同步到DB",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class SyncWorkerConnectorTask extends AbstractAsyncMetadataDispatchTask{

    private static final ILog LOGGER = LogFactory.getLog(SyncWorkerConnectorTask.class);

    @Autowired
    ConnectorService connectorService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private WorkerConnectorService workerConnectorService;

    @Override
    public TaskResult processClusterTask(ConnectCluster connectCluster, long triggerTimeUnitMs) throws Exception {
        List<ConnectorPO> connectorPOList = connectorService.listByConnectClusterIdFromDB(connectCluster.getId());
        if (connectorPOList.isEmpty()) {
            return TaskResult.SUCCESS;
        }

        //获取集群信息
        ClusterPhy clusterPhy = LoadedClusterPhyCache.getByPhyId(connectCluster.getKafkaClusterPhyId());
        if (clusterPhy == null) {
            LOGGER.error(
                    "class=SyncWorkerConnectorTask||method=processClusterTask||connectClusterId={}||clusterPhyId={}||errMsg=clusterPhy not exist!.",
                    connectCluster.getId(), connectCluster.getKafkaClusterPhyId()
            );
            return TaskResult.FAIL;

        }
        KSGroupDescription ksGroupDescription = groupService.getGroupDescriptionFromKafka(clusterPhy, connectCluster.getGroupName());

        //获取workerConnector列表
        try {
            List<WorkerConnector> workerConnectorList = new ArrayList<>();
            for (ConnectorPO connectorPO : connectorPOList) {
                workerConnectorList.addAll(workerConnectorService.getWorkerConnectorListFromCluster(connectCluster, connectorPO.getConnectorName()));
            }
            workerConnectorService.batchReplaceInDB(connectCluster.getId(), workerConnectorList);
        } catch (Exception e) {
            LOGGER.error(
                    "class=SyncWorkerConnectorTask||method=processClusterTask||connectClusterId={}||ksGroupDescription={}||errMsg=exception.",
                    connectCluster.getId(), ksGroupDescription, e
            );

            return TaskResult.FAIL;
        }

        return TaskResult.SUCCESS;
    }


}
