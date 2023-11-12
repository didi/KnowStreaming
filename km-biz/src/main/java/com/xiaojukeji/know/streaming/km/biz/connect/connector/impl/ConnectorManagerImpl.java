package com.xiaojukeji.know.streaming.km.biz.connect.connector.impl;

import com.xiaojukeji.know.streaming.km.biz.connect.connector.ConnectorManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.connector.ConnectorCreateDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.WorkerConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.config.ConnectConfigInfos;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnectorInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectorPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.connect.connector.ConnectorStateVO;
import com.xiaojukeji.know.streaming.km.common.constant.connect.KafkaConnectConstant;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.OpConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.plugin.PluginService;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerConnectorService;
import org.apache.kafka.connect.runtime.AbstractStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class ConnectorManagerImpl implements ConnectorManager {
    @Autowired
    private PluginService pluginService;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private OpConnectorService opConnectorService;

    @Autowired
    private WorkerConnectorService workerConnectorService;

    @Override
    public Result<Void> updateConnectorConfig(Long connectClusterId, String connectorName, Properties configs, String operator) {
        Result<ConnectConfigInfos> infosResult = pluginService.validateConfig(connectClusterId, configs);
        if (infosResult.failed()) {
            return Result.buildFromIgnoreData(infosResult);
        }

        if (infosResult.getData().getErrorCount() > 0) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "Connector参数错误");
        }

        return opConnectorService.updateConnectorConfig(connectClusterId, connectorName, configs, operator);
    }

    @Override
    public Result<Void> createConnector(ConnectorCreateDTO dto, String operator) {
        dto.getSuitableConfig().put(KafkaConnectConstant.MIRROR_MAKER_NAME_FIELD_NAME, dto.getConnectorName());

        Result<KSConnectorInfo> createResult = opConnectorService.createConnector(dto.getConnectClusterId(), dto.getConnectorName(), dto.getSuitableConfig(), operator);
        if (createResult.failed()) {
            return Result.buildFromIgnoreData(createResult);
        }

        Result<KSConnector> ksConnectorResult = connectorService.getConnectorFromKafka(dto.getConnectClusterId(), dto.getConnectorName());
        if (ksConnectorResult.failed()) {
            return Result.buildFromRSAndMsg(ResultStatus.SUCCESS, "创建成功，但是获取元信息失败，页面元信息会存在1分钟延迟");
        }

        opConnectorService.addNewToDB(ksConnectorResult.getData());
        return Result.buildSuc();
    }

    @Override
    public Result<Void> createConnector(ConnectorCreateDTO dto, String heartbeatName, String checkpointName, String operator) {
        dto.getSuitableConfig().put(KafkaConnectConstant.MIRROR_MAKER_NAME_FIELD_NAME, dto.getConnectorName());

        Result<KSConnectorInfo> createResult = opConnectorService.createConnector(dto.getConnectClusterId(), dto.getConnectorName(), dto.getSuitableConfig(), operator);
        if (createResult.failed()) {
            return Result.buildFromIgnoreData(createResult);
        }

        Result<KSConnector> ksConnectorResult = connectorService.getConnectorFromKafka(dto.getConnectClusterId(), dto.getConnectorName());
        if (ksConnectorResult.failed()) {
            return Result.buildFromRSAndMsg(ResultStatus.SUCCESS, "创建成功，但是获取元信息失败，页面元信息会存在1分钟延迟");
        }

        KSConnector connector = ksConnectorResult.getData();
        connector.setCheckpointConnectorName(checkpointName);
        connector.setHeartbeatConnectorName(heartbeatName);

        opConnectorService.addNewToDB(connector);
        return Result.buildSuc();
    }


    @Override
    public Result<ConnectorStateVO> getConnectorStateVO(Long connectClusterId, String connectorName) {
        ConnectorPO connectorPO = connectorService.getConnectorFromDB(connectClusterId, connectorName);

        if (connectorPO == null) {
            return Result.buildFailure(ResultStatus.NOT_EXIST);
        }

        List<WorkerConnector> workerConnectorList = workerConnectorService.listFromDB(connectClusterId).stream().filter(elem -> elem.getConnectorName().equals(connectorName)).collect(Collectors.toList());

        return Result.buildSuc(convert2ConnectorOverviewVO(connectorPO, workerConnectorList));
    }

    private ConnectorStateVO convert2ConnectorOverviewVO(ConnectorPO connectorPO, List<WorkerConnector> workerConnectorList) {
        ConnectorStateVO connectorStateVO = new ConnectorStateVO();
        connectorStateVO.setConnectClusterId(connectorPO.getConnectClusterId());
        connectorStateVO.setName(connectorPO.getConnectorName());
        connectorStateVO.setType(connectorPO.getConnectorType());
        connectorStateVO.setState(connectorPO.getState());
        connectorStateVO.setTotalTaskCount(workerConnectorList.size());
        connectorStateVO.setAliveTaskCount(workerConnectorList.stream().filter(elem -> elem.getState().equals(AbstractStatus.State.RUNNING.name())).collect(Collectors.toList()).size());
        connectorStateVO.setTotalWorkerCount(workerConnectorList.stream().map(elem -> elem.getWorkerId()).collect(Collectors.toSet()).size());
        return connectorStateVO;
    }
}
