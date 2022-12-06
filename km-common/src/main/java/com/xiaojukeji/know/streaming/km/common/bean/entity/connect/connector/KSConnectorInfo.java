package com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector;

import lombok.Data;
import org.apache.kafka.connect.runtime.rest.entities.ConnectorType;
import org.apache.kafka.connect.util.ConnectorTaskId;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * copy from:
 * @see org.apache.kafka.connect.runtime.rest.entities.ConnectorInfo
 */
@Data
public class KSConnectorInfo implements Serializable {
    private Long connectClusterId;

    private String name;

    private Map<String, String> config;

    private List<ConnectorTaskId> tasks;

    private ConnectorType type;
}
