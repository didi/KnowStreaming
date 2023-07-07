package com.xiaojukeji.know.streaming.km.common.enums.version;

public enum VersionItemTypeEnum {
    /**
     * 指标
     */
    METRIC_TOPIC(100, "TopicMetric"),
    METRIC_CLUSTER(101, "ClusterMetric"),
    METRIC_GROUP(102, "GroupMetric"),
    METRIC_BROKER(103, "BrokerMetric"),
    METRIC_PARTITION(104, "PartitionMetric"),
    METRIC_REPLICATION(105, "ReplicaMetric"),

    METRIC_ZOOKEEPER(110, "ZookeeperMetric"),

    METRIC_CONNECT_CLUSTER(120, "ConnectClusterMetric"),
    METRIC_CONNECT_CONNECTOR(121, "ConnectConnectorMetric"),
    METRIC_CONNECT_MIRROR_MAKER(122, "ConnectMirrorMakerMetric"),

    /**
     * 服务端查询
     */
    SERVICE_SEARCH_CONFIG(200, "service_config_search"),
    SERVICE_SEARCH_BROKER(201, "service_broker_search"),
    SERVICE_SEARCH_GROUP(202, "service_broker_search"),

    /**
     * 服务端操作
     */
    SERVICE_OP_TOPIC(300, "service_topic_operation"),
    SERVICE_OP_CONFIG(301, "service_config_operation"),
    SERVICE_OP_ACL(302, "service_acl_operation"),
    SERVICE_OP_KAFKA_USER(303, "service_kafka-acl_operation"),

    SERVICE_OP_TOPIC_CONFIG(310, "service_topic-config_operation"),
    SERVICE_OP_BROKER_CONFIG(311, "service_broker-config_operation"),


    SERVICE_OP_PARTITION(320, "service_partition_operation"),
    SERVICE_OP_PARTITION_LEADER(321, "service_partition-leader_operation"),

    SERVICE_OP_REASSIGNMENT(330, "service_reassign_operation"),

    SERVICE_OP_GROUP(340, "service_group_operation"),

    SERVICE_OP_CONNECT_CLUSTER(400, "service_connect_cluster_operation"),
    SERVICE_OP_CONNECT_CONNECTOR(401, "service_connect_connector_operation"),
    SERVICE_OP_CONNECT_PLUGIN(402, "service_connect_plugin_operation"),

    /**
     * 前端操作
     */
    WEB_OP(901, "web_operation"),
    ;

    private final Integer code;

    private final String message;

    VersionItemTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "VersionItemTypeEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
