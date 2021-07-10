package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * 任务状态
 * @author zengqiao
 * @date 2017/6/29.
 */
public enum TaskStatusEnum {
    UNKNOWN(    -1, "未知"),

        NEW(        0,  "新建"),

        RUNNABLE(   20, "就绪"),
            WAITING(   21, "等待"),

        RUNNING(    30, "运行中"),
            KILLING(    31, "杀死中"),

        BLOCKED(    40, "暂停"),

    UNFINISHED(  99, "未完成"),
    FINISHED(   100, "完成"),

        SUCCEED(    101, "成功"),
        FAILED(     102, "失败"),
        CANCELED(   103, "取消"),
        IGNORED(    104, "忽略"),
        TIMEOUT(    105, "超时"),
        KILL_FAILED(106, "杀死失败"),

    ;

    private Integer code;

    private String message;

    TaskStatusEnum(Integer code, String message) {
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
        return "TaskStatusEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    public static Boolean isFinished(Integer code) {
        return code >= FINISHED.getCode();
    }
}
