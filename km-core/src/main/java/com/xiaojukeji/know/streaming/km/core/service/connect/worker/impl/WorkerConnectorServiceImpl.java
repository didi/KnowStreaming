package com.xiaojukeji.know.streaming.km.core.service.connect.worker.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.task.TaskActionDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectWorker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.WorkerConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnectorStateInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSTaskState;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.WorkerConnectorPO;
import com.xiaojukeji.know.streaming.km.common.component.RestTool;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerService;
import com.xiaojukeji.know.streaming.km.persistence.connect.cache.LoadedConnectClusterCache;
import com.xiaojukeji.know.streaming.km.persistence.mysql.connect.WorkerConnectorDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectActionEnum.RESTART;

@Service
public class WorkerConnectorServiceImpl implements WorkerConnectorService {

    protected static final ILog LOGGER = LogFactory.getLog(WorkerConnectorServiceImpl.class);
    @Autowired
    private WorkerConnectorDAO workerConnectorDAO;

    @Autowired
    private RestTool restTool;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private WorkerService workerService;


    private static final String RESTART_TASK_URI           = "%s/connectors/%s/tasks/%d/restart";

    @Override
    public void batchReplaceInDB(Long connectClusterId, List<WorkerConnector> workerList) {
        Map<String, WorkerConnectorPO> oldMap = new HashMap<>();
        for (WorkerConnectorPO oldPO : this.listPOSFromDB(connectClusterId)) {
            oldMap.put(oldPO.getConnectorName() + oldPO.getWorkerId() + oldPO.getTaskId() + oldPO.getState(), oldPO);
        }

        for (WorkerConnector workerConnector : workerList) {
            try {
                String key = workerConnector.getConnectorName() + workerConnector.getWorkerId() + workerConnector.getTaskId() + workerConnector.getState();

                WorkerConnectorPO oldPO = oldMap.remove(key);
                if (oldPO == null) {
                    workerConnectorDAO.insert(ConvertUtil.obj2Obj(workerConnector, WorkerConnectorPO.class));
                } else {
                    // 如果该数据已经存在，则不需要进行操作
                }
            } catch (DuplicateKeyException dke) {
                // ignore
            }
        }

        try {
            oldMap.values().forEach(elem -> workerConnectorDAO.deleteById(elem.getId()));
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public List<WorkerConnector> listFromDB(Long connectClusterId) {
        return ConvertUtil.list2List(this.listPOSFromDB(connectClusterId), WorkerConnector.class);
    }

    @Override
    public List<WorkerConnector> listByKafkaClusterIdFromDB(Long kafkaClusterPhyId) {
        LambdaQueryWrapper<WorkerConnectorPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WorkerConnectorPO::getKafkaClusterPhyId, kafkaClusterPhyId);
        return ConvertUtil.list2List(workerConnectorDAO.selectList(lambdaQueryWrapper), WorkerConnector.class);
    }


    @Override
    public Result<Void> actionTask(TaskActionDTO dto) {
        if (!dto.getAction().equals(RESTART.getValue())) {
            return Result.buildFailure(ResultStatus.OPERATION_FORBIDDEN);
        }

        ConnectCluster connectCluster = LoadedConnectClusterCache.getByPhyId(dto.getConnectClusterId());

        if (connectCluster == null) {
            return Result.buildFailure(ResultStatus.NOT_EXIST);
        }

        String url = String.format(RESTART_TASK_URI, connectCluster.getSuitableRequestUrl(), dto.getConnectorName(), dto.getTaskId());
        try {
            restTool.postObjectWithJsonContent(url, null, String.class);
        } catch (Exception e) {
            LOGGER.error("method=actionTask||connectClusterId={}||connectorName={}||taskId={}||restart failed||msg=exception",
                    dto.getConnectClusterId(), dto.getConnectorName(), dto.getTaskId(), e);
        }
        return Result.buildSuc();
    }

    @Override
    public List<WorkerConnector> getWorkerConnectorListFromCluster(ConnectCluster connectCluster, String connectorName) {
        Map<String, ConnectWorker> workerMap = workerService.listFromDB(connectCluster.getId()).stream().collect(Collectors.toMap(elem -> elem.getWorkerId(), Function.identity()));
        List<WorkerConnector> workerConnectorList = new ArrayList<>();
        Result<KSConnectorStateInfo> ret = connectorService.getConnectorStateInfoFromCluster(connectCluster.getId(), connectorName);
        if (!ret.hasData()) {
            return workerConnectorList;
        }

        KSConnectorStateInfo ksConnectorStateInfo = ret.getData();
        for (KSTaskState task : ksConnectorStateInfo.getTasks()) {
            WorkerConnector workerConnector = new WorkerConnector(connectCluster.getKafkaClusterPhyId(), connectCluster.getId(), ksConnectorStateInfo.getName(), workerMap.get(task.getWorkerId()).getMemberId(), task.getId(), task.getState(), task.getWorkerId(), task.getTrace());
            workerConnectorList.add(workerConnector);
        }
        return workerConnectorList;
    }



    private List<WorkerConnectorPO> listPOSFromDB(Long connectClusterId) {
        LambdaQueryWrapper<WorkerConnectorPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WorkerConnectorPO::getConnectClusterId, connectClusterId);

        return workerConnectorDAO.selectList(lambdaQueryWrapper);
    }
}
