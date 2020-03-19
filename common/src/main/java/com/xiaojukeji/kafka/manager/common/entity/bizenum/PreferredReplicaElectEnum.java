package com.xiaojukeji.kafka.manager.common.entity.bizenum;

/**
 * 优先副本选举状态
 * @author zengqiao
 * @date 2017/6/29.
 */
public enum PreferredReplicaElectEnum {
    SUCCESS(0, "成功[创建成功|执行成功]"),
    RUNNING(1, "正在执行"),
    ALREADY_EXIST(2, "任务已存在"),
    PARAM_ILLEGAL(3, "参数错误"),
    UNKNOWN(4, "进度未知");

    private Integer code;

    private String message;

    PreferredReplicaElectEnum(Integer code, String message) {
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
