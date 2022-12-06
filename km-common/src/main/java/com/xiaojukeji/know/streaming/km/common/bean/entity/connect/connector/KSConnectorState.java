package com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector;

import lombok.Data;
import org.apache.kafka.connect.runtime.rest.entities.ConnectorStateInfo;

/**
 * @see ConnectorStateInfo.ConnectorState
 */
@Data
public class KSConnectorState extends KSAbstractConnectState {
}
