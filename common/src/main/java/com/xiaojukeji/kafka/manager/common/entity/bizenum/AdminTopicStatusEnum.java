package com.xiaojukeji.kafka.manager.common.entity.bizenum;

/**
 * 操作Topic的状态
 * @author zengqiao
 * @date 19/11/26
 */
public enum AdminTopicStatusEnum {
    SUCCESS(0, "成功"),
    REPLACE_DB_FAILED(1, "更新DB失败"),
    PARAM_NULL_POINTER(2, "参数错误"),
    PARTITION_NUM_ILLEGAL(3, "分区数错误"),
    BROKER_NUM_NOT_ENOUGH(4, "Broker数不足错误"),
    TOPIC_NAME_ILLEGAL(5, "Topic名称非法"),
    TOPIC_EXISTED(6, "Topic已存在"),
    UNKNOWN_TOPIC_PARTITION(7, "Topic未知"),
    TOPIC_CONFIG_ILLEGAL(8, "Topic配置错误"),
    TOPIC_IN_DELETING(9, "Topic正在删除"),
    UNKNOWN_ERROR(10, "未知错误");

    private Integer code;

    private String message;

    AdminTopicStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

