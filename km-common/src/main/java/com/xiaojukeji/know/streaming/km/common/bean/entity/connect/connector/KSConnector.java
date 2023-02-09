package com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector;

import lombok.Data;

import java.io.Serializable;

@Data
public class KSConnector implements Serializable {
    /**
     * Kafka集群ID
     */
    private Long kafkaClusterPhyId;

    /**
     * connect集群ID
     */
    private Long connectClusterId;

    /**
     * connector名称
     */
    private String connectorName;

    /**
     * connector类名
     */
    private String connectorClassName;

    /**
     * connector类型
     */
    private String connectorType;

    /**
     * 访问过的Topic列表
     */
    private String topics;

    /**
     * task数
     */
    private Integer taskCount;

    /**
     * 状态
     */
    private String state;

    /**
     *   心跳检测connector名称
     */
    private String heartbeatConnectorName;

    /**
     *   进度确认connector名称
     */
    private String checkpointConnectorName;
}
