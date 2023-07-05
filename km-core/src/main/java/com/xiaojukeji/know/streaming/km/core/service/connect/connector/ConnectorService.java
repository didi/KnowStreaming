package com.xiaojukeji.know.streaming.km.core.service.connect.connector;

import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnectorInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnectorStateInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectorPO;
import com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectorTypeEnum;

import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * 查看Connector
 */
public interface ConnectorService {
    Result<KSConnectorInfo> createConnector(Long connectClusterId, String connectorName, Properties configs, String operator);

    /**
     * 获取所有的连接器名称列表
     */
    Result<List<String>> listConnectorsFromCluster(Long connectClusterId);

    /**
     * 获取单个连接器信息
     */
    Result<KSConnectorInfo> getConnectorInfoFromCluster(Long connectClusterId, String connectorName);

    Result<List<String>> getConnectorTopicsFromCluster(Long connectClusterId, String connectorName);

    Result<KSConnectorStateInfo> getConnectorStateInfoFromCluster(Long connectClusterId, String connectorName);

    Result<KSConnector> getAllConnectorInfoFromCluster(Long connectClusterId, String connectorName);

    Result<Void> resumeConnector(Long connectClusterId, String connectorName, String operator);

    Result<Void> restartConnector(Long connectClusterId, String connectorName, String operator);

    Result<Void> stopConnector(Long connectClusterId, String connectorName, String operator);

    Result<Void> deleteConnector(Long connectClusterId, String connectorName, String operator);

    Result<Void> updateConnectorConfig(Long connectClusterId, String connectorName, Properties configs, String operator);

    void batchReplace(Long kafkaClusterPhyId, Long connectClusterId, List<KSConnector> connectorList, Set<String> allConnectorNameSet);

    void addNewToDB(KSConnector connector);

    List<ConnectorPO> listByKafkaClusterIdFromDB(Long kafkaClusterPhyId);

    List<ConnectorPO> listByConnectClusterIdFromDB(Long connectClusterId);

    int countByConnectClusterIdFromDB(Long connectClusterId);

    ConnectorPO getConnectorFromDB(Long connectClusterId, String connectorName);

    ConnectorTypeEnum getConnectorType(Long connectClusterId, String connectorName);

    void completeMirrorMakerInfo(ConnectCluster connectCluster, List<KSConnector> connectorList);
}
