package com.xiaojukeji.kafka.manager.kcm.common.bizenum;

/**
 * 任务动作
 * @author zengqiao
 * @date 20/4/26
 */
public enum ClusterTaskActionEnum {
    START(0, "start"),
    PAUSE(1, "pause"),
    IGNORE(2, "ignore"),
    CANCEL(3, "cancel"),
    ROLLBACK(4, "rollback"),
    ;
    private Integer code;

    private String message;

    ClusterTaskActionEnum(Integer code, String message) {
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
        return "TaskActionEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
