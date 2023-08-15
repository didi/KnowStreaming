package com.xiaojukeji.know.streaming.km.core.service.connect.connector.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnectorInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnectorStateInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectorPO;
import com.xiaojukeji.know.streaming.km.common.component.RestTool;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.converter.ConnectConverter;
import com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectorTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.Triple;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.connect.ConnectorDAO;
import org.apache.kafka.connect.runtime.rest.entities.ActiveTopicsInfo;
import org.apache.kafka.connect.runtime.rest.entities.ConnectorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConnectorServiceImpl implements ConnectorService {
    private static final ILog LOGGER = LogFactory.getLog(ConnectorServiceImpl.class);

    @Autowired
    private RestTool restTool;

    @Autowired
    private ConnectorDAO connectorDAO;

    @Autowired
    private ConnectClusterService connectClusterService;

    private static final String LIST_CONNECTORS_URI                 = "/connectors";
    private static final String GET_CONNECTOR_INFO_PREFIX_URI       = "/connectors";
    private static final String GET_CONNECTOR_TOPICS_URI            = "/connectors/%s/topics";
    private static final String GET_CONNECTOR_STATUS_URI            = "/connectors/%s/status";

    @Override
    public Result<List<String>> listConnectorsFromCluster(ConnectCluster connectCluster) {
        try {
            List<String> nameList = restTool.getArrayObjectWithJsonContent(
                    connectCluster.getSuitableRequestUrl() + LIST_CONNECTORS_URI,
                    new HashMap<>(),
                    String.class
            );

            return Result.buildSuc(nameList);
        } catch (Exception e) {
            LOGGER.error(
                    "method=listConnectorsFromCluster||connectClusterId={}||connectClusterSuitableUrl={}||errMsg=exception",
                    connectCluster.getId(), connectCluster.getSuitableRequestUrl(), e
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
    public Result<KSConnectorStateInfo> getConnectorStateInfoFromCluster(Long connectClusterId, String connectorName) {
        ConnectCluster connectCluster = connectClusterService.getById(connectClusterId);
        if (ValidateUtils.isNull(connectCluster)) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
        }

        return this.getConnectorStateInfoFromCluster(connectCluster, connectorName);
    }

    @Override
    public Result<KSConnector> getConnectorFromKafka(Long connectClusterId, String connectorName) {
        ConnectCluster connectCluster = connectClusterService.getById(connectClusterId);
        if (ValidateUtils.isNull(connectCluster)) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
        }

        Result<Triple<KSConnectorInfo, List<String>, KSConnectorStateInfo>> fullInfoResult = this.getConnectorFullInfoFromKafka(connectCluster, connectorName);
        if (fullInfoResult.failed()) {
            return Result.buildFromIgnoreData(fullInfoResult);
        }

        return Result.buildSuc(ConnectConverter.convert2KSConnector(
                connectCluster.getKafkaClusterPhyId(),
                connectCluster.getId(),
                fullInfoResult.getData().v1(),
                fullInfoResult.getData().v3(),
                fullInfoResult.getData().v2()
        ));
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
    public Result<Tuple<Set<String>, List<KSConnector>>> getDataFromKafka(ConnectCluster connectCluster) {
        Result<List<String>> nameListResult = this.listConnectorsFromCluster(connectCluster);
        if (nameListResult.failed()) {
            return Result.buildFromIgnoreData(nameListResult);
        }

        // 逐个获取
        List<Triple<KSConnectorInfo, List<String>, KSConnectorStateInfo>> connectorFullInfoList = new ArrayList<>();
        for (String connectorName: nameListResult.getData()) {
            Result<Triple<KSConnectorInfo, List<String>, KSConnectorStateInfo>> ksConnectorResult = this.getConnectorFullInfoFromKafka(connectCluster, connectorName);
            if (ksConnectorResult.failed()) {
                continue;
            }

            connectorFullInfoList.add(ksConnectorResult.getData());
        }

        // 返回结果
        return Result.buildSuc(new Tuple<>(
                new HashSet<>(nameListResult.getData()),
                ConnectConverter.convertAndSupplyMirrorMakerInfo(connectCluster, connectorFullInfoList)) // 转换并补充mm2相关信息
        );
    }

    @Override
    public void writeToDB(Long connectClusterId, Set<String> fullNameSet, List<KSConnector> dataList) {
        List<ConnectorPO> poList = this.listByConnectClusterIdFromDB(connectClusterId);

        Map<String, ConnectorPO> oldPOMap = new HashMap<>();
        poList.forEach(elem -> oldPOMap.put(elem.getConnectorName(), elem));

        for (KSConnector connector: dataList) {
            try {
                ConnectorPO oldPO = oldPOMap.remove(connector.getConnectorName());
                if (oldPO == null) {
                    oldPO = ConvertUtil.obj2Obj(connector, ConnectorPO.class);
                    connectorDAO.insert(oldPO);
                    continue;
                }

                ConnectorPO newPO = ConvertUtil.obj2Obj(connector, ConnectorPO.class);
                newPO.setId(oldPO.getId());
                if (!ValidateUtils.isBlank(oldPO.getCheckpointConnectorName())
                        && ValidateUtils.isBlank(newPO.getCheckpointConnectorName())
                        && fullNameSet.contains(oldPO.getCheckpointConnectorName())) {
                    // 新的po里面没有checkpoint的信息，但是db中的数据显示有，且集群中有该connector，则保留该checkpoint数据
                    newPO.setCheckpointConnectorName(oldPO.getCheckpointConnectorName());
                }

                if (!ValidateUtils.isBlank(oldPO.getHeartbeatConnectorName())
                        && ValidateUtils.isBlank(newPO.getHeartbeatConnectorName())
                        && fullNameSet.contains(oldPO.getHeartbeatConnectorName())) {
                    // 新的po里面没有checkpoint的信息，但是db中的数据显示有，且集群中有该connector，则保留该checkpoint数据
                    newPO.setHeartbeatConnectorName(oldPO.getHeartbeatConnectorName());
                }

                connectorDAO.updateById(newPO);
            } catch (DuplicateKeyException dke) {
                // ignore
            } catch (Exception e) {
                LOGGER.error(
                        "method=writeToDB||connectClusterId={}||connectorName={}||errMsg=exception",
                        connector.getConnectClusterId(), connector.getConnectorName(), e
                );
            }
        }

        try {
            oldPOMap.values().forEach(elem -> {
                if (fullNameSet.contains(elem.getConnectorName())) {
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
    public int deleteInDBByKafkaClusterId(Long clusterPhyId) {
        LambdaQueryWrapper<ConnectorPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectorPO::getKafkaClusterPhyId, clusterPhyId);

        return connectorDAO.delete(lambdaQueryWrapper);
    }

    /**************************************************** private method ****************************************************/

    private Result<KSConnectorInfo> getConnectorInfoFromCluster(ConnectCluster connectCluster, String connectorName) {
        try {
            ConnectorInfo connectorInfo = restTool.getForObject(
                    connectCluster.getSuitableRequestUrl() + GET_CONNECTOR_INFO_PREFIX_URI + "/" + connectorName,
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
                    connectCluster.getSuitableRequestUrl() + String.format(GET_CONNECTOR_TOPICS_URI, connectorName),
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

    private Result<Triple<KSConnectorInfo, List<String>, KSConnectorStateInfo>> getConnectorFullInfoFromKafka(ConnectCluster connectCluster, String connectorName) {
        Result<KSConnectorInfo> connectorResult = this.getConnectorInfoFromCluster(connectCluster, connectorName);
        if (connectorResult.failed()) {
            LOGGER.error(
                    "method=getConnectorAllInfoFromKafka||connectClusterId={}||connectClusterSuitableUrl={}||result={}||errMsg=get connectors info from cluster failed",
                    connectCluster.getId(), connectCluster.getSuitableRequestUrl(), connectorResult
            );

            return Result.buildFromIgnoreData(connectorResult);
        }

        Result<List<String>> topicNameListResult = this.getConnectorTopicsFromCluster(connectCluster, connectorName);
        if (topicNameListResult.failed()) {
            LOGGER.error(
                    "method=getConnectorAllInfoFromKafka||connectClusterId={}||connectClusterSuitableUrl={}||result={}||errMsg=get connectors topics from cluster failed",
                    connectCluster.getId(), connectCluster.getSuitableRequestUrl(), topicNameListResult
            );
        }

        Result<KSConnectorStateInfo> stateInfoResult = this.getConnectorStateInfoFromCluster(connectCluster, connectorName);
        if (stateInfoResult.failed()) {
            LOGGER.error(
                    "method=getConnectorAllInfoFromKafka||connectClusterId={}||connectClusterSuitableUrl={}||result={}||errMsg=get connectors state from cluster failed",
                    connectCluster.getId(), connectCluster.getSuitableRequestUrl(), stateInfoResult
            );
        }

        return Result.buildSuc(new Triple<>(
                connectorResult.getData(),
                topicNameListResult.getData(),
                stateInfoResult.getData()
        ));
    }
}
