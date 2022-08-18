package com.xiaojukeji.know.streaming.km.common.enums.valid;

import lombok.Getter;

@Getter
public enum ValidateKafkaAddressErrorEnum {
    BS_ERROR(10, "服务地址错误"),

    ZK_ERROR(20, "ZK错误"),

    JMX_ERROR(31, "Jmx错误"),

    ;

    public static final String BS_CONNECT_FAILED = "连接服务地址失败，请检查服务地址及集群配置";

    public static final String BS_CONNECT_SUCCESS_BUT_RUN_RAFT = "连接服务地址成功，但因无ZK配置导致版本信息获取失败";

    public static final String BS_CONNECT_SUCCESS_BUT_API_NOT_SUPPORT = "连接服务地址成功，但因集群版本较低等原因，导致版本信息获取失败";

    public static final String ZK_CONNECT_ERROR = "连接ZK地址失败";

    public static final String ZK_CONNECT_SUCCESS_BUT_NODE_NOT_EXIST = "连接ZK地址成功，但ZK上的Kafka相关节点不存在";

    public static final String JMX_WITHOUT_OPEN = "集群Jmx未配置或开启";

    public static final String CONNECT_JMX_ERROR = "连接Jmx失败";

    private final int code;

    private final String message;

    ValidateKafkaAddressErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
