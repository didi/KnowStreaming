package com.xiaojukeji.know.streaming.km.biz.connect.connector.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.biz.connect.connector.WorkerConnectorManager;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.WorkerConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.connect.task.KCTaskOverviewVO;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerConnectorService;
import com.xiaojukeji.know.streaming.km.persistence.connect.cache.LoadedConnectClusterCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wyb
 * @date 2022/11/14
 */
@Service
public class WorkerConnectorManageImpl implements WorkerConnectorManager {

    private static final ILog LOGGER = LogFactory.getLog(WorkerConnectorManageImpl.class);

    @Autowired
    private WorkerConnectorService workerConnectorService;

    @Override
    public Result<List<KCTaskOverviewVO>> getTaskOverview(Long connectClusterId, String connectorName) {
        ConnectCluster connectCluster = LoadedConnectClusterCache.getByPhyId(connectClusterId);
        List<WorkerConnector> workerConnectorList = workerConnectorService.getWorkerConnectorListFromCluster(connectCluster, connectorName);

        return Result.buildSuc(ConvertUtil.list2List(workerConnectorList, KCTaskOverviewVO.class));
    }
}
