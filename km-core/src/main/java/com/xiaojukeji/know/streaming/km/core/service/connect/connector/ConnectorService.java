package com.xiaojukeji.know.streaming.km.core.service.connect.connector;

import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnectorInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnectorStateInfo;
import com.xiaojukeji.know.streaming.km.core.service.meta.MetaDataService;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectorPO;
import com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectorTypeEnum;

import java.util.List;

/**
 * 查看Connector
 */
public interface ConnectorService extends MetaDataService<KSConnector> {
    /**
     * 获取所有的连接器名称列表
     */
    Result<List<String>> listConnectorsFromCluster(ConnectCluster connectCluster);

    /**
     * 获取单个连接器信息
     */
    Result<KSConnectorInfo> getConnectorInfoFromCluster(Long connectClusterId, String connectorName);

    Result<KSConnectorStateInfo> getConnectorStateInfoFromCluster(Long connectClusterId, String connectorName);

    Result<KSConnector> getConnectorFromKafka(Long connectClusterId, String connectorName);

    List<ConnectorPO> listByKafkaClusterIdFromDB(Long kafkaClusterPhyId);

    List<ConnectorPO> listByConnectClusterIdFromDB(Long connectClusterId);

    int countByConnectClusterIdFromDB(Long connectClusterId);

    ConnectorPO getConnectorFromDB(Long connectClusterId, String connectorName);

    ConnectorTypeEnum getConnectorType(Long connectClusterId, String connectorName);
}
