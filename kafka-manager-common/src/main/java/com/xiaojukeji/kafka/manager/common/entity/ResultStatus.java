package com.xiaojukeji.kafka.manager.common.entity;

import com.xiaojukeji.kafka.manager.common.constant.Constant;

/**
 * 返回状态
 * @author zengqiao
 * @date 20/4/16
 */
public enum ResultStatus {
    GATEWAY_INVALID_REQUEST(-1, "invalid request"),

    SUCCESS(Constant.SUCCESS, "success"),

    LOGIN_FAILED(1, "login failed, please check username and password"),


    /**
     * 内部依赖错误, [1000, 1200)
     * ------------------------------------------------------------------------------------------
     */
    MYSQL_ERROR(1000, "operate database failed"),

    CONNECT_ZOOKEEPER_FAILED(1000, "connect zookeeper failed"),
    READ_ZOOKEEPER_FAILED(1000, "read zookeeper failed"),
    READ_JMX_FAILED(1000, "read jmx failed"),


    // 内部依赖错误 —— Kafka特定错误, [1000, 1100)
    BROKER_NUM_NOT_ENOUGH(1000, "broker not enough"),
    CONTROLLER_NOT_ALIVE(1000, "controller not alive"),
    CLUSTER_METADATA_ERROR(1000, "cluster metadata error"),
    TOPIC_CONFIG_ERROR(1000, "topic config error"),


    /**
     * 外部依赖错误, [1200, 1400)
     * ------------------------------------------------------------------------------------------
     */
    CALL_CLUSTER_TASK_AGENT_FAILED(1000, " call cluster task agent failed"),
    CALL_MONITOR_SYSTEM_ERROR(1000, " call monitor-system failed"),



    /**
     * 外部用户操作错误, [1400, 1600)
     * ------------------------------------------------------------------------------------------
     */
    PARAM_ILLEGAL(1400, "param illegal"),
    OPERATION_FAILED(1401, "operation failed"),
    OPERATION_FORBIDDEN(1402, "operation forbidden"),
    API_CALL_EXCEED_LIMIT(1403, "api call exceed limit"),

    // 资源不存在
    CLUSTER_NOT_EXIST(10000, "cluster not exist"),
    BROKER_NOT_EXIST(10000, "broker not exist"),
    TOPIC_NOT_EXIST(10000, "topic not exist"),
    PARTITION_NOT_EXIST(10000, "partition not exist"),

    ACCOUNT_NOT_EXIST(10000, "account not exist"),
    APP_NOT_EXIST(1000, "app not exist"),
    ORDER_NOT_EXIST(1000, "order not exist"),
    CONFIG_NOT_EXIST(1000, "config not exist"),
    IDC_NOT_EXIST(1000, "idc not exist"),
    TASK_NOT_EXIST(1110, "task not exist"),

    AUTHORITY_NOT_EXIST(1000, "authority not exist"),

    MONITOR_NOT_EXIST(1110, "monitor not exist"),

    QUOTA_NOT_EXIST(1000, "quota not exist, please check clusterId, topicName and appId"),

    // 资源不存在, 已存在, 已被使用
    RESOURCE_NOT_EXIST(1200, "资源不存在"),
    RESOURCE_ALREADY_EXISTED(1200, "资源已经存在"),
    RESOURCE_NAME_DUPLICATED(1200, "资源名称重复"),
    RESOURCE_ALREADY_USED(1000, "资源早已被使用"),


    /**
     * 资源参数错误
     */
    CG_LOCATION_ILLEGAL(10000, "consumer group location illegal"),
    ORDER_ALREADY_HANDLED(1000, "order already handled"),

    APP_ID_OR_PASSWORD_ILLEGAL(1000, "app or password illegal"),
    SYSTEM_CODE_ILLEGAL(1000, "system code illegal"),

    CLUSTER_TASK_HOST_LIST_ILLEGAL(1000, "主机列表错误，请检查主机列表"),









    ///////////////////////////////////////////////////////////////

    USER_WITHOUT_AUTHORITY(1000, "user without authority"),



    JSON_PARSER_ERROR(1000, "json parser error"),


    TOPIC_OPERATION_PARAM_NULL_POINTER(2, "参数错误"),
    TOPIC_OPERATION_PARTITION_NUM_ILLEGAL(3, "分区数错误"),
    TOPIC_OPERATION_BROKER_NUM_NOT_ENOUGH(4, "Broker数不足错误"),
    TOPIC_OPERATION_TOPIC_NAME_ILLEGAL(5, "Topic名称非法"),
    TOPIC_OPERATION_TOPIC_EXISTED(6, "Topic已存在"),
    TOPIC_OPERATION_UNKNOWN_TOPIC_PARTITION(7, "Topic未知"),
    TOPIC_OPERATION_TOPIC_CONFIG_ILLEGAL(8, "Topic配置错误"),
    TOPIC_OPERATION_TOPIC_IN_DELETING(9, "Topic正在删除"),
    TOPIC_OPERATION_UNKNOWN_ERROR(10, "未知错误"),
    TOPIC_EXIST_CONNECT_CANNOT_DELETE(10, "topic exist connect cannot delete"),
    EXIST_TOPIC_CANNOT_DELETE(10, "exist topic cannot delete"),


    /**
     * 工单
     */
    CHANGE_ZOOKEEPER_FORBIDEN(100, "change zookeeper forbiden"),
//    APP_EXIST_TOPIC_AUTHORITY_CANNOT_DELETE(1000, "app exist topic authority cannot delete"),

    UPLOAD_FILE_FAIL(1000, "upload file fail"),
    FILE_TYPE_NOT_SUPPORT(1000, "File type not support"),
    DOWNLOAD_FILE_FAIL(1000, "download file fail"),


    TOPIC_ALREADY_EXIST(17400, "topic already existed"),
    CONSUMER_GROUP_NOT_EXIST(17411, "consumerGroup not exist"),
    ;

    private int code;
    private String message;

    ResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
