package com.xiaojukeji.kafka.manager.common.entity.bizenum;

/**
 * 操作类型
 * @author zengqiao
 * @date 19/11/21
 */
public enum OperationEnum {
    CREATE_TOPIC("create_topic"),
    DELETE_TOPIC("delete_topic"),
    MODIFY_TOPIC_CONFIG("modify_topic_config"),
    EXPAND_TOPIC_PARTITION("expand_topic_partition");

    public String message;

    OperationEnum(String message) {
        this.message = message;
    }
}