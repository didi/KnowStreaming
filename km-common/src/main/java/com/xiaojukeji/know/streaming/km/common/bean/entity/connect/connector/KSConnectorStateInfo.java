package com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector;

import lombok.Data;
import org.apache.kafka.connect.runtime.rest.entities.ConnectorStateInfo;
import org.apache.kafka.connect.runtime.rest.entities.ConnectorType;

import java.util.List;

/**
 * @see ConnectorStateInfo
 */
@Data
public class KSConnectorStateInfo {
    private String name;

    private KSConnectorState connector;

    private List<KSTaskState> tasks;

    private ConnectorType type;
}
