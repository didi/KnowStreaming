package com.xiaojukeji.kafka.manager.common.entity.bizenum;

/**
 * 迁移状态
 * @author zengqiao
 * @date 19/12/29
 */
public enum ReassignmentStatusEnum {
    WAITING(0, "等待执行"),
    RUNNING(1, "正在执行"),
    SUCCESS(2, "迁移成功"),
    FAILED(3, "迁移失败"),
    CANCELED(4, "取消任务");

    private Integer code;

    private String message;

    ReassignmentStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static boolean triggerTask(Integer status) {
        if (WAITING.code.equals(status) || RUNNING.code.equals(status)) {
            return true;
        }
        return false;
    }

    public static boolean cancelTask(Integer status) {
        if (WAITING.code.equals(status)) {
            return true;
        }
        return false;
    }
}