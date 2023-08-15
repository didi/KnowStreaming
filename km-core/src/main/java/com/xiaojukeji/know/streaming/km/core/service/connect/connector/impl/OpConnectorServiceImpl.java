package com.xiaojukeji.know.streaming.km.core.service.connect.connector.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnectorInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnectorStateInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectorPO;
import com.xiaojukeji.know.streaming.km.common.component.RestTool;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.constant.connect.KafkaConnectConstant;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.BackoffUtils;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.OpConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.oprecord.OpLogWrapService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseVersionControlService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.connect.ConnectorDAO;
import org.apache.kafka.connect.runtime.rest.entities.ConnectorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.SERVICE_OP_CONNECT_CONNECTOR;

@Service
public class OpConnectorServiceImpl extends BaseVersionControlService implements OpConnectorService {
    private static final ILog LOGGER = LogFactory.getLog(OpConnectorServiceImpl.class);

    @Autowired
    private RestTool restTool;

    @Autowired
    private ConnectorDAO connectorDAO;

    @Autowired
    private ConnectClusterService connectClusterService;

    @Autowired
    private OpLogWrapService opLogWrapService;

    private static final String GET_CONNECTOR_STATUS_URI            = "/connectors/%s/status";

    private static final String CREATE_CONNECTOR_URI                = "/connectors";
    private static final String RESUME_CONNECTOR_URI                = "/connectors/%s/resume";
    private static final String RESTART_CONNECTOR_URI               = "/connectors/%s/restart";
    private static final String PAUSE_CONNECTOR_URI                 = "/connectors/%s/pause";
    private static final String DELETE_CONNECTOR_URI                = "/connectors/%s";
    private static final String UPDATE_CONNECTOR_CONFIG_URI         = "/connectors/%s/config";

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return SERVICE_OP_CONNECT_CONNECTOR;
    }

    @Override
    public Result<KSConnectorInfo> createConnector(Long connectClusterId, String connectorName, Properties configs, String operator) {
        try {
            ConnectCluster connectCluster = connectClusterService.getById(connectClusterId);
            if (ValidateUtils.isNull(connectCluster)) {
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
            }

            // 构造参数
            Properties props = new Properties();
            props.put(KafkaConnectConstant.MIRROR_MAKER_NAME_FIELD_NAME, connectorName);
            props.put("config", configs);

            ConnectorInfo connectorInfo = restTool.postObjectWithJsonContent(
                    connectCluster.getSuitableRequestUrl() + CREATE_CONNECTOR_URI,
                    props,
                    ConnectorInfo.class
            );

            opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                    operator,
                    OperationEnum.ADD.getDesc(),
                    ModuleEnum.KAFKA_CONNECT_CONNECTOR.getDesc(),
                    MsgConstant.getConnectorBizStr(connectClusterId, connectorName),
                    ConvertUtil.obj2Json(configs)
            ));

            KSConnectorInfo connector = new KSConnectorInfo();
            connector.setConnectClusterId(connectClusterId);
            connector.setConfig(connectorInfo.config());
            connector.setName(connectorInfo.name());
            connector.setTasks(connectorInfo.tasks());
            connector.setType(connectorInfo.type());

            return Result.buildSuc(connector);
        } catch (Exception e) {
            LOGGER.error(
                    "method=createConnector||connectClusterId={}||connectorName={}||configs={}||operator={}||errMsg=exception",
                    connectClusterId, connectorName, configs, operator, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_CONNECTOR_READ_FAILED, e.getMessage());
        }
    }

    @Override
    public Result<Void> resumeConnector(Long connectClusterId, String connectorName, String operator) {
        try {
            ConnectCluster connectCluster = connectClusterService.getById(connectClusterId);
            if (ValidateUtils.isNull(connectCluster)) {
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
            }

            restTool.putJsonForObject(
                    connectCluster.getSuitableRequestUrl() + String.format(RESUME_CONNECTOR_URI, connectorName),
                    new HashMap<>(),
                    String.class
            );

            this.updateStatus(connectCluster, connectClusterId, connectorName);

            opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                    operator,
                    OperationEnum.ENABLE.getDesc(),
                    ModuleEnum.KAFKA_CONNECT_CONNECTOR.getDesc(),
                    MsgConstant.getConnectorBizStr(connectClusterId, connectorName),
                    ""
            ));

            return Result.buildSuc();
        } catch (Exception e) {
            LOGGER.error(
                    "class=ConnectorServiceImpl||method=resumeConnector||connectClusterId={}||errMsg=exception",
                    connectClusterId, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_CONNECTOR_READ_FAILED, e.getMessage());
        }
    }

    @Override
    public Result<Void> restartConnector(Long connectClusterId, String connectorName, String operator) {
        try {
            ConnectCluster connectCluster = connectClusterService.getById(connectClusterId);
            if (ValidateUtils.isNull(connectCluster)) {
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
            }

            restTool.postObjectWithJsonContent(
                    connectCluster.getSuitableRequestUrl() + String.format(RESTART_CONNECTOR_URI, connectorName),
                    new HashMap<>(),
                    String.class
            );

            this.updateStatus(connectCluster, connectClusterId, connectorName);

            opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                    operator,
                    OperationEnum.RESTART.getDesc(),
                    ModuleEnum.KAFKA_CONNECT_CONNECTOR.getDesc(),
                    MsgConstant.getConnectorBizStr(connectClusterId, connectorName),
                    ""
            ));

            return Result.buildSuc();
        } catch (Exception e) {
            LOGGER.error(
                    "method=restartConnector||connectClusterId={}||errMsg=exception",
                    connectClusterId, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_CONNECTOR_READ_FAILED, e.getMessage());
        }
    }

    @Override
    public Result<Void> stopConnector(Long connectClusterId, String connectorName, String operator) {
        try {
            ConnectCluster connectCluster = connectClusterService.getById(connectClusterId);
            if (ValidateUtils.isNull(connectCluster)) {
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
            }

            restTool.putJsonForObject(
                    connectCluster.getSuitableRequestUrl() + String.format(PAUSE_CONNECTOR_URI, connectorName),
                    new HashMap<>(),
                    String.class
            );

            this.updateStatus(connectCluster, connectClusterId, connectorName);

            opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                    operator,
                    OperationEnum.DISABLE.getDesc(),
                    ModuleEnum.KAFKA_CONNECT_CONNECTOR.getDesc(),
                    MsgConstant.getConnectorBizStr(connectClusterId, connectorName),
                    ""
            ));

            return Result.buildSuc();
        } catch (Exception e) {
            LOGGER.error(
                    "method=stopConnector||connectClusterId={}||errMsg=exception",
                    connectClusterId, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_CONNECTOR_READ_FAILED, e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteConnector(Long connectClusterId, String connectorName, String operator) {
        try {
            ConnectCluster connectCluster = connectClusterService.getById(connectClusterId);
            if (ValidateUtils.isNull(connectCluster)) {
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
            }

            restTool.deleteWithParamsAndHeader(
                    connectCluster.getSuitableRequestUrl() + String.format(DELETE_CONNECTOR_URI, connectorName),
                    new HashMap<>(),
                    new HashMap<>(),
                    String.class
            );

            opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                    operator,
                    OperationEnum.DELETE.getDesc(),
                    ModuleEnum.KAFKA_CONNECT_CONNECTOR.getDesc(),
                    MsgConstant.getConnectorBizStr(connectClusterId, connectorName),
                    ""
            ));

            this.deleteConnectorInDB(connectClusterId, connectorName);

            return Result.buildSuc();
        } catch (Exception e) {
            LOGGER.error(
                    "method=deleteConnector||connectClusterId={}||errMsg=exception",
                    connectClusterId, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_CONNECTOR_READ_FAILED, e.getMessage());
        }
    }

    @Override
    public Result<Void> updateConnectorConfig(Long connectClusterId, String connectorName, Properties configs, String operator) {
        try {
            ConnectCluster connectCluster = connectClusterService.getById(connectClusterId);
            if (ValidateUtils.isNull(connectCluster)) {
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
            }

            ConnectorInfo connectorInfo = restTool.putJsonForObject(
                    connectCluster.getSuitableRequestUrl() + String.format(UPDATE_CONNECTOR_CONFIG_URI, connectorName),
                    configs,
                    ConnectorInfo.class
            );

            this.updateStatus(connectCluster, connectClusterId, connectorName);

            opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                    operator,
                    OperationEnum.EDIT.getDesc(),
                    ModuleEnum.KAFKA_CONNECT_CONNECTOR.getDesc(),
                    MsgConstant.getConnectorBizStr(connectClusterId, connectorName),
                    ConvertUtil.obj2Json(configs)
            ));

            return Result.buildSuc();
        } catch (Exception e) {
            LOGGER.error(
                    "method=updateConnectorConfig||connectClusterId={}||errMsg=exception",
                    connectClusterId, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_CONNECTOR_READ_FAILED, e.getMessage());
        }
    }

    @Override
    public void addNewToDB(KSConnector connector) {
        try {
            connectorDAO.insert(ConvertUtil.obj2Obj(connector, ConnectorPO.class));
        } catch (DuplicateKeyException dke) {
            // ignore
        }
    }

    /**************************************************** private method ****************************************************/
    private int deleteConnectorInDB(Long connectClusterId, String connectorName) {
        LambdaQueryWrapper<ConnectorPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectorPO::getConnectClusterId, connectClusterId);
        lambdaQueryWrapper.eq(ConnectorPO::getConnectorName, connectorName);

        return connectorDAO.delete(lambdaQueryWrapper);
    }

    private Result<KSConnectorStateInfo> getConnectorStateInfoFromCluster(ConnectCluster connectCluster, String connectorName) {
        try {
            KSConnectorStateInfo connectorStateInfo = restTool.getForObject(
                    connectCluster.getSuitableRequestUrl() + String.format(GET_CONNECTOR_STATUS_URI, connectorName),
                    new HashMap<>(),
                    KSConnectorStateInfo.class
            );

            return Result.buildSuc(connectorStateInfo);
        } catch (Exception e) {
            LOGGER.error(
                    "method=getConnectorStateInfoFromCluster||connectClusterId={}||connectorName={}||errMsg=exception",
                    connectCluster.getId(), connectorName, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_CONNECTOR_READ_FAILED, e.getMessage());
        }
    }

    private void updateStatus(ConnectCluster connectCluster, Long connectClusterId, String connectorName) {
        try {
            // 延迟3秒
            BackoffUtils.backoff(2000);

            Result<KSConnectorStateInfo> stateInfoResult = this.getConnectorStateInfoFromCluster(connectCluster, connectorName);
            if (stateInfoResult.failed()) {
                return;
            }

            ConnectorPO po = new ConnectorPO();
            po.setConnectClusterId(connectClusterId);
            po.setConnectorName(connectorName);
            po.setState(stateInfoResult.getData().getConnector().getState());

            LambdaQueryWrapper<ConnectorPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(ConnectorPO::getConnectClusterId, connectClusterId);
            lambdaQueryWrapper.eq(ConnectorPO::getConnectorName, connectorName);

            connectorDAO.update(po, lambdaQueryWrapper);
        } catch (Exception e) {
            LOGGER.error(
                    "method=updateStatus||connectClusterId={}||connectorName={}||errMsg=exception",
                    connectClusterId, connectorName, e
            );
        }
    }
}
