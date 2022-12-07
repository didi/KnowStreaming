package com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.kafka.connect.runtime.rest.entities.ConnectorStateInfo;

/**
 * @see ConnectorStateInfo.AbstractState
 */
@Data
public abstract class KSAbstractConnectState {
    private String state;

    private String trace;

    @JSONField(name="worker_id")
    @JsonProperty("worker_id")
    private String workerId;
}
