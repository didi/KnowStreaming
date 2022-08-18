package com.xiaojukeji.know.streaming.km.common.enums.version;

public enum VersionItemTypeEnum {
    /**
     * 指标
     */
    METRIC_TOPIC(100, "topic_metric"),
    METRIC_CLUSTER(101, "cluster_metric"),
    METRIC_GROUP(102, "group_metric"),
    METRIC_BROKER(103, "broker_metric"),
    METRIC_PARTITION(104, "partition_metric"),
    METRIC_REPLICATION (105, "replication_metric"),

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

    SERVICE_OP_REASSIGNMENT(330, "service_reassign_operation"),

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
