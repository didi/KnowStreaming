package com.xiaojukeji.know.streaming.km.biz.connect.connector;

import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.connector.ConnectorCreateDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.connect.connector.ConnectorStateVO;

import java.util.Properties;

public interface ConnectorManager {
    Result<Void> updateConnectorConfig(Long connectClusterId, String connectorName, Properties configs, String operator);

    Result<Void> createConnector(ConnectorCreateDTO dto, String operator);
    Result<Void> createConnector(ConnectorCreateDTO dto, String heartbeatName, String checkpointName, String operator);

    Result<ConnectorStateVO> getConnectorStateVO(Long connectClusterId, String connectorName);
}
