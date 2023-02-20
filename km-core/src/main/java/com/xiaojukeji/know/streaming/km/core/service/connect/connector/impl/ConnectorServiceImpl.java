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
import com.xiaojukeji.know.streaming.km.common.converter.ConnectConverter;
import com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectorTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.BackoffUtils;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.oprecord.OpLogWrapService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseVersionControlService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.connect.ConnectorDAO;
import org.apache.kafka.connect.runtime.rest.entities.ActiveTopicsInfo;
import org.apache.kafka.connect.runtime.rest.entities.ConnectorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.constant.connect.KafkaConnectConstant.MIRROR_MAKER_SOURCE_CLUSTER_BOOTSTRAP_SERVERS_FIELD_NAME;
import static com.xiaojukeji.know.streaming.km.common.constant.connect.KafkaConnectConstant.MIRROR_MAKER_TARGET_CLUSTER_BOOTSTRAP_SERVERS_FIELD_NAME;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.SERVICE_OP_CONNECT_CONNECTOR;

@Service
public class ConnectorServiceImpl extends BaseVersionControlService implements ConnectorService {
    private static final ILog LOGGER = LogFactory.getLog(ConnectorServiceImpl.class);

    @Autowired
    private RestTool restTool;

    @Autowired
    private ConnectorDAO connectorDAO;

    @Autowired
    private ConnectClusterService connectClusterService;

    @Autowired
    private OpLogWrapService opLogWrapService;

    private static final String LIST_CONNECTORS_URI                 = "/connectors";
    private static final String GET_CONNECTOR_INFO_PREFIX_URI       = "/connectors";
    private static final String GET_CONNECTOR_TOPICS_URI            = "/connectors/%s/topics";
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
                    connectCluster.getClusterUrl() + CREATE_CONNECTOR_URI,
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
    public Result<List<String>> listConnectorsFromCluster(Long connectClusterId) {
        try {
            ConnectCluster connectCluster = connectClusterService.getById(connectClusterId);
            if (ValidateUtils.isNull(connectCluster)) {
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
            }

            List<String> nameList = restTool.getArrayObjectWithJsonContent(
                    connectCluster.getClusterUrl() + LIST_CONNECTORS_URI,
                    new HashMap<>(),
                    String.class
            );

            return Result.buildSuc(nameList);
        } catch (Exception e) {
            LOGGER.error(
                    "method=listConnectorsFromCluster||connectClusterId={}||errMsg=exception",
                    connectClusterId, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_CONNECTOR_READ_FAILED, e.getMessage());
        }
    }

    @Override
    public Result<KSConnectorInfo> getConnectorInfoFromCluster(Long connectClusterId, String connectorName) {
        ConnectCluster connectCluster = connectClusterService.getById(connectClusterId);
        if (ValidateUtils.isNull(connectCluster)) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
        }

        return this.getConnectorInfoFromCluster(connectCluster, connectorName);
    }

    @Override
    public Result<List<String>> getConnectorTopicsFromCluster(Long connectClusterId, String connectorName) {
        ConnectCluster connectCluster = connectClusterService.getById(connectClusterId);
        if (ValidateUtils.isNull(connectCluster)) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
        }

        return this.getConnectorTopicsFromCluster(connectCluster, connectorName);
    }

    @Override
    public Result<KSConnectorStateInfo> getConnectorStateInfoFromCluster(Long connectClusterId, String connectorName) {
        ConnectCluster connectCluster = connectClusterService.getById(connectClusterId);
        if (ValidateUtils.isNull(connectCluster)) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
        }

        return this.getConnectorStateInfoFromCluster(connectCluster, connectorName);
    }

    @Override
    public Result<KSConnector> getAllConnectorInfoFromCluster(Long connectClusterId, String connectorName) {
        ConnectCluster connectCluster = connectClusterService.getById(connectClusterId);
        if (ValidateUtils.isNull(connectCluster)) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
        }

        Result<KSConnectorInfo> connectorResult = this.getConnectorInfoFromCluster(connectCluster, connectorName);
        if (connectorResult.failed()) {
            LOGGER.error(
                    "method=getAllConnectorInfoFromCluster||connectClusterId={}||connectorName={}||result={}",
                    connectClusterId, connectorName, connectorResult
            );

            return Result.buildFromIgnoreData(connectorResult);
        }

        Result<List<String>> topicNameListResult = this.getConnectorTopicsFromCluster(connectCluster, connectorName);
        if (topicNameListResult.failed()) {
            LOGGER.error(
                    "method=getAllConnectorInfoFromCluster||connectClusterId={}||connectorName={}||result={}",
                    connectClusterId, connectorName, connectorResult
            );
        }

        Result<KSConnectorStateInfo> stateInfoResult = this.getConnectorStateInfoFromCluster(connectCluster, connectorName);
        if (stateInfoResult.failed()) {
            LOGGER.error(
                    "method=getAllConnectorInfoFromCluster||connectClusterId={}||connectorName={}||result={}",
                    connectClusterId, connectorName, connectorResult
            );
        }

        return Result.buildSuc(ConnectConverter.convert2KSConnector(
                connectCluster.getKafkaClusterPhyId(),
                connectCluster.getId(),
                connectorResult.getData(),
                stateInfoResult.getData(),
                topicNameListResult.getData()
        ));
    }

    @Override
    public Result<Void> resumeConnector(Long connectClusterId, String connectorName, String operator) {
        try {
            ConnectCluster connectCluster = connectClusterService.getById(connectClusterId);
            if (ValidateUtils.isNull(connectCluster)) {
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
            }

            restTool.putJsonForObject(
                    connectCluster.getClusterUrl() + String.format(RESUME_CONNECTOR_URI, connectorName),
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
                    connectCluster.getClusterUrl() + String.format(RESTART_CONNECTOR_URI, connectorName),
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
                    connectCluster.getClusterUrl() + String.format(PAUSE_CONNECTOR_URI, connectorName),
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
                    connectCluster.getClusterUrl() + String.format(DELETE_CONNECTOR_URI, connectorName),
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
                    connectCluster.getClusterUrl() + String.format(UPDATE_CONNECTOR_CONFIG_URI, connectorName),
                    configs,
                    org.apache.kafka.connect.runtime.rest.entities.ConnectorInfo.class
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
    public void batchReplace(Long kafkaClusterPhyId, Long connectClusterId, List<KSConnector> connectorList, Set<String> allConnectorNameSet) {
        List<ConnectorPO> poList = this.listByConnectClusterIdFromDB(connectClusterId);

        Map<String, ConnectorPO> oldPOMap = new HashMap<>();
        poList.forEach(elem -> oldPOMap.put(elem.getConnectorName(), elem));

        for (KSConnector connector: connectorList) {
            try {
                ConnectorPO oldPO = oldPOMap.remove(connector.getConnectorName());
                if (oldPO == null) {
                    oldPO = ConvertUtil.obj2Obj(connector, ConnectorPO.class);
                    connectorDAO.insert(oldPO);
                } else {
                    ConnectorPO newPO = ConvertUtil.obj2Obj(connector, ConnectorPO.class);
                    newPO.setId(oldPO.getId());
                    connectorDAO.updateById(newPO);
                }
            } catch (DuplicateKeyException dke) {
                // ignore
            }
        }

        try {
            oldPOMap.values().forEach(elem -> {
                if (allConnectorNameSet.contains(elem.getConnectorName())) {
                    // 当前connector还存在
                    return;
                }

                // 当前connector不存在了，则进行删除
                connectorDAO.deleteById(elem.getId());
            });
        } catch (Exception e) {
            // ignore
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

    @Override
    public List<ConnectorPO> listByKafkaClusterIdFromDB(Long kafkaClusterPhyId) {
        LambdaQueryWrapper<ConnectorPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectorPO::getKafkaClusterPhyId, kafkaClusterPhyId);

        return connectorDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<ConnectorPO> listByConnectClusterIdFromDB(Long connectClusterId) {
        LambdaQueryWrapper<ConnectorPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectorPO::getConnectClusterId, connectClusterId);

        return connectorDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public int countByConnectClusterIdFromDB(Long connectClusterId) {
        LambdaQueryWrapper<ConnectorPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectorPO::getConnectClusterId, connectClusterId);

        return connectorDAO.selectCount(lambdaQueryWrapper);
    }

    @Override
    public ConnectorPO getConnectorFromDB(Long connectClusterId, String connectorName) {
        LambdaQueryWrapper<ConnectorPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectorPO::getConnectClusterId, connectClusterId);
        lambdaQueryWrapper.eq(ConnectorPO::getConnectorName, connectorName);

        return connectorDAO.selectOne(lambdaQueryWrapper);
    }

    @Override
    public ConnectorTypeEnum getConnectorType(Long connectClusterId, String connectorName) {
        ConnectorTypeEnum connectorType = ConnectorTypeEnum.UNKNOWN;
        ConnectorPO connector = this.getConnectorFromDB(connectClusterId, connectorName);
        if (connector != null) {
            connectorType = ConnectorTypeEnum.getByName(connector.getConnectorType());
        }
        return connectorType;
    }

    @Override
    public void completeMirrorMakerInfo(ConnectCluster connectCluster, List<KSConnector> connectorList) {
        List<KSConnector> sourceConnectorList = connectorList.stream().filter(elem -> elem.getConnectorClassName().equals(KafkaConnectConstant.MIRROR_MAKER_SOURCE_CONNECTOR_TYPE)).collect(Collectors.toList());
        if (sourceConnectorList.isEmpty()) {
            return;
        }

        List<KSConnector> heartBeatConnectorList = connectorList.stream().filter(elem -> elem.getConnectorClassName().equals(KafkaConnectConstant.MIRROR_MAKER_HEARTBEAT_CONNECTOR_TYPE)).collect(Collectors.toList());
        List<KSConnector> checkpointConnectorList = connectorList.stream().filter(elem -> elem.getConnectorClassName().equals(KafkaConnectConstant.MIRROR_MAKER_CHECKPOINT_CONNECTOR_TYPE)).collect(Collectors.toList());

        Map<String, String> heartbeatMap = this.buildMirrorMakerMap(connectCluster, heartBeatConnectorList);
        Map<String, String> checkpointMap = this.buildMirrorMakerMap(connectCluster, checkpointConnectorList);

        for (KSConnector sourceConnector : sourceConnectorList) {
            Result<KSConnectorInfo> ret = this.getConnectorInfoFromCluster(connectCluster, sourceConnector.getConnectorName());

            if (!ret.hasData()) {
                LOGGER.error(
                        "method=completeMirrorMakerInfo||connectClusterId={}||connectorName={}||get connectorInfo fail!",
                        connectCluster.getId(), sourceConnector.getConnectorName()
                );
                continue;
            }
            KSConnectorInfo ksConnectorInfo = ret.getData();
            String targetServers = ksConnectorInfo.getConfig().get(MIRROR_MAKER_TARGET_CLUSTER_BOOTSTRAP_SERVERS_FIELD_NAME);
            String sourceServers = ksConnectorInfo.getConfig().get(MIRROR_MAKER_SOURCE_CLUSTER_BOOTSTRAP_SERVERS_FIELD_NAME);

            if (ValidateUtils.anyBlank(targetServers, sourceServers)) {
                continue;
            }

            String[] targetBrokerList = getBrokerList(targetServers);
            String[] sourceBrokerList = getBrokerList(sourceServers);
            sourceConnector.setHeartbeatConnectorName(this.findBindConnector(targetBrokerList, sourceBrokerList, heartbeatMap));
            sourceConnector.setCheckpointConnectorName(this.findBindConnector(targetBrokerList, sourceBrokerList, checkpointMap));
        }

    }

    /**************************************************** private method ****************************************************/
    private int deleteConnectorInDB(Long connectClusterId, String connectorName) {
        LambdaQueryWrapper<ConnectorPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectorPO::getConnectClusterId, connectClusterId);
        lambdaQueryWrapper.eq(ConnectorPO::getConnectorName, connectorName);

        return connectorDAO.delete(lambdaQueryWrapper);
    }

    private Result<KSConnectorInfo> getConnectorInfoFromCluster(ConnectCluster connectCluster, String connectorName) {
        try {
            ConnectorInfo connectorInfo = restTool.getForObject(
                    connectCluster.getClusterUrl() + GET_CONNECTOR_INFO_PREFIX_URI + "/" + connectorName,
                    new HashMap<>(),
                    ConnectorInfo.class
            );

            KSConnectorInfo connector = new KSConnectorInfo();
            connector.setConnectClusterId(connectCluster.getId());
            connector.setConfig(connectorInfo.config());
            connector.setName(connectorInfo.name());
            connector.setTasks(connectorInfo.tasks());
            connector.setType(connectorInfo.type());

            return Result.buildSuc(connector);
        } catch (Exception e) {
            LOGGER.error(
                    "method=getConnectorInfoFromCluster||connectClusterId={}||connectorName={}||errMsg=exception",
                    connectCluster.getId(), connectorName, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_CONNECTOR_READ_FAILED, e.getMessage());
        }
    }

    private Result<List<String>> getConnectorTopicsFromCluster(ConnectCluster connectCluster, String connectorName) {
        try {
            Properties properties = restTool.getForObject(
                    connectCluster.getClusterUrl() + String.format(GET_CONNECTOR_TOPICS_URI, connectorName),
                    new HashMap<>(),
                    Properties.class
            );

            ActiveTopicsInfo activeTopicsInfo = ConvertUtil.toObj(ConvertUtil.obj2Json(properties.get(connectorName)), ActiveTopicsInfo.class);
            return Result.buildSuc(new ArrayList<>(activeTopicsInfo.topics()));
        } catch (Exception e) {
            LOGGER.error(
                    "method=getConnectorTopicsFromCluster||connectClusterId={}||connectorName={}||errMsg=exception",
                    connectCluster.getId(), connectorName, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_CONNECTOR_READ_FAILED, e.getMessage());
        }
    }

    private Result<KSConnectorStateInfo> getConnectorStateInfoFromCluster(ConnectCluster connectCluster, String connectorName) {
        try {
            KSConnectorStateInfo connectorStateInfo = restTool.getForObject(
                    connectCluster.getClusterUrl() + String.format(GET_CONNECTOR_STATUS_URI, connectorName),
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

    private Map<String, String> buildMirrorMakerMap(ConnectCluster connectCluster, List<KSConnector> ksConnectorList) {
        Map<String, String> bindMap = new HashMap<>();

        for (KSConnector ksConnector : ksConnectorList) {
            Result<KSConnectorInfo> ret = this.getConnectorInfoFromCluster(connectCluster, ksConnector.getConnectorName());

            if (!ret.hasData()) {
                LOGGER.error(
                        "method=buildMirrorMakerMap||connectClusterId={}||connectorName={}||get connectorInfo fail!",
                        connectCluster.getId(), ksConnector.getConnectorName()
                );
                continue;
            }

            KSConnectorInfo ksConnectorInfo = ret.getData();
            String targetServers = ksConnectorInfo.getConfig().get(MIRROR_MAKER_TARGET_CLUSTER_BOOTSTRAP_SERVERS_FIELD_NAME);
            String sourceServers = ksConnectorInfo.getConfig().get(MIRROR_MAKER_SOURCE_CLUSTER_BOOTSTRAP_SERVERS_FIELD_NAME);

            if (ValidateUtils.anyBlank(targetServers, sourceServers)) {
                continue;
            }

            String[] targetBrokerList = getBrokerList(targetServers);
            String[] sourceBrokerList = getBrokerList(sourceServers);
            for (String targetBroker : targetBrokerList) {
                for (String sourceBroker : sourceBrokerList) {
                    bindMap.put(targetBroker + "@" + sourceBroker, ksConnector.getConnectorName());
                }
            }

        }
        return bindMap;
    }

    private String findBindConnector(String[] targetBrokerList, String[] sourceBrokerList, Map<String, String> connectorBindMap) {
        for (String targetBroker : targetBrokerList) {
            for (String sourceBroker : sourceBrokerList) {
                String connectorName = connectorBindMap.get(targetBroker + "@" + sourceBroker);
                if (connectorName != null) {
                    return connectorName;
                }
            }
        }
        return "";
    }

    private String[] getBrokerList(String str) {
        if (ValidateUtils.isBlank(str)) {
            return new String[0];
        }
        if (str.contains(";")) {
            return str.split(";");
        }
        if (str.contains(",")) {
            return str.split(",");
        }
        return new String[]{str};
    }
}
