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

    FAIL(1, "操作失败"),

    /**
     * 操作错误[1000, 2000)
     * ------------------------------------------------------------------------------------------
     */
    OPERATION_FAILED(1401, "operation failed"),
    OPERATION_FORBIDDEN(1402, "operation forbidden"),
    API_CALL_EXCEED_LIMIT(1403, "api call exceed limit"),
    USER_WITHOUT_AUTHORITY(1404, "user without authority"),
    CHANGE_ZOOKEEPER_FORBIDDEN(1405, "change zookeeper forbidden"),


    TOPIC_OPERATION_PARAM_NULL_POINTER(1450, "参数错误"),
    TOPIC_OPERATION_PARTITION_NUM_ILLEGAL(1451, "分区数错误"),
    TOPIC_OPERATION_BROKER_NUM_NOT_ENOUGH(1452, "Broker数不足错误"),
    TOPIC_OPERATION_TOPIC_NAME_ILLEGAL(1453, "Topic名称非法"),
    TOPIC_OPERATION_TOPIC_EXISTED(1454, "Topic已存在"),
    TOPIC_OPERATION_UNKNOWN_TOPIC_PARTITION(1455, "Topic未知"),
    TOPIC_OPERATION_TOPIC_CONFIG_ILLEGAL(1456, "Topic配置错误"),
    TOPIC_OPERATION_TOPIC_IN_DELETING(1457, "Topic正在删除"),
    TOPIC_OPERATION_UNKNOWN_ERROR(1458, "未知错误"),

    /**
     * 参数错误[2000, 3000)
     * ------------------------------------------------------------------------------------------
     */
    PARAM_ILLEGAL(2000, "param illegal"),
    CG_LOCATION_ILLEGAL(2001, "consumer group location illegal"),
    ORDER_ALREADY_HANDLED(2002, "order already handled"),
    APP_ID_OR_PASSWORD_ILLEGAL(2003, "app or password illegal"),
    SYSTEM_CODE_ILLEGAL(2004, "system code illegal"),
    CLUSTER_TASK_HOST_LIST_ILLEGAL(2005, "主机列表错误，请检查主机列表"),
    JSON_PARSER_ERROR(2006, "json parser error"),

    BROKER_NUM_NOT_ENOUGH(2050, "broker not enough"),
    CONTROLLER_NOT_ALIVE(2051, "controller not alive"),
    CLUSTER_METADATA_ERROR(2052, "cluster metadata error"),
    TOPIC_CONFIG_ERROR(2053, "topic config error"),

    /**
     * 参数错误 - 资源检查错误
     * 因为外部系统的问题, 操作时引起的错误, [7000, 8000)
     * ------------------------------------------------------------------------------------------
     */
    RESOURCE_NOT_EXIST(7100, "资源不存在"),
    CLUSTER_NOT_EXIST(7101, "cluster not exist"),
    BROKER_NOT_EXIST(7102, "broker not exist"),
    TOPIC_NOT_EXIST(7103, "topic not exist"),
    PARTITION_NOT_EXIST(7104, "partition not exist"),
    ACCOUNT_NOT_EXIST(7105, "account not exist"),
    APP_NOT_EXIST(7106, "app not exist"),
    ORDER_NOT_EXIST(7107, "order not exist"),
    CONFIG_NOT_EXIST(7108, "config not exist"),
    IDC_NOT_EXIST(7109, "idc not exist"),
    TASK_NOT_EXIST(7110, "task not exist"),
    AUTHORITY_NOT_EXIST(7111, "authority not exist"),
    MONITOR_NOT_EXIST(7112, "monitor not exist"),
    QUOTA_NOT_EXIST(7113, "quota not exist, please check clusterId, topicName and appId"),
    CONSUMER_GROUP_NOT_EXIST(7114, "consumerGroup not exist"),
    TOPIC_BIZ_DATA_NOT_EXIST(7115, "topic biz data not exist, please sync topic to db"),
    LDAP_AUTHENTICATION_FAILED(7116, "LDAP authentication failed"),


    // 资源已存在
    RESOURCE_ALREADY_EXISTED(7200, "资源已经存在"),
    TOPIC_ALREADY_EXIST(7201, "topic already existed"),

    // 资源重名
    RESOURCE_NAME_DUPLICATED(7300, "资源名称重复"),

    // 资源已被使用
    RESOURCE_ALREADY_USED(7400, "资源早已被使用"),


    /**
     * 因为外部系统的问题, 操作时引起的错误, [8000, 9000)
     * ------------------------------------------------------------------------------------------
     */
    MYSQL_ERROR(8010, "operate database failed"),

    ZOOKEEPER_CONNECT_FAILED(8020, "zookeeper connect failed"),
    ZOOKEEPER_READ_FAILED(8021, "zookeeper read failed"),
    ZOOKEEPER_WRITE_FAILED(8022, "zookeeper write failed"),
    ZOOKEEPER_DELETE_FAILED(8023, "zookeeper delete failed"),

    // 调用集群任务里面的agent失败
    CALL_CLUSTER_TASK_AGENT_FAILED(8030, " call cluster task agent failed"),

    // 调用监控系统失败
    CALL_MONITOR_SYSTEM_ERROR(8040, " call monitor-system failed"),

    // 存储相关的调用失败
    STORAGE_UPLOAD_FILE_FAILED(8050, "upload file failed"),
    STORAGE_FILE_TYPE_NOT_SUPPORT(8051, "File type not support"),
    STORAGE_DOWNLOAD_FILE_FAILED(8052, "download file failed"),

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
