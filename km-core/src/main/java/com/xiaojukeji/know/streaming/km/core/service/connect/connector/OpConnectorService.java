package com.xiaojukeji.know.streaming.km.core.service.connect.connector;

import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnectorInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;

import java.util.Properties;

/**
 * 查看Connector
 */
public interface OpConnectorService {
    Result<KSConnectorInfo> createConnector(Long connectClusterId, String connectorName, Properties configs, String operator);

    Result<Void> resumeConnector(Long connectClusterId, String connectorName, String operator);

    Result<Void> restartConnector(Long connectClusterId, String connectorName, String operator);

    Result<Void> stopConnector(Long connectClusterId, String connectorName, String operator);

    Result<Void> deleteConnector(Long connectClusterId, String connectorName, String operator);

    Result<Void> updateConnectorConfig(Long connectClusterId, String connectorName, Properties configs, String operator);

    void addNewToDB(KSConnector connector);
}
