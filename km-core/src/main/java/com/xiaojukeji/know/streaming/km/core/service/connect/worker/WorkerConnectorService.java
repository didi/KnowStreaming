package com.xiaojukeji.know.streaming.km.core.service.connect.worker;

import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.task.TaskActionDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.WorkerConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;

import java.util.List;

/**
 * Worker
 */
public interface WorkerConnectorService {
    void batchReplaceInDB(Long connectClusterId, List<WorkerConnector> workerList);

    List<WorkerConnector> listFromDB(Long connectClusterId);

    List<WorkerConnector> listByKafkaClusterIdFromDB(Long kafkaClusterPhyId);

    Result<Void> actionTask(TaskActionDTO dto);

    List<WorkerConnector> getWorkerConnectorListFromCluster(ConnectCluster connectCluster, String connectorName);
}
