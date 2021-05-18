package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * 过期Topic状态
 * @author zengqiao
 * @date 21/01/25
 */
public enum TopicExpiredStatusEnum {
    ALREADY_NOTIFIED_AND_DELETED(-2, "已通知, 已下线"),
    ALREADY_NOTIFIED_AND_CAN_DELETE(-1, "已通知, 可下线"),
    ALREADY_EXPIRED_AND_WAIT_NOTIFY(0, "已过期, 待通知"),
    ALREADY_NOTIFIED_AND_WAIT_RESPONSE(1, "已通知, 待反馈"),

    ;

    private int status;

    private String message;

    TopicExpiredStatusEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "TopicExpiredStatusEnum{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
