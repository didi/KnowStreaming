package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * @author zengqiao
 * @date 20/6/11
 */
public enum TaskStatusReassignEnum {
    UNKNOWN(TaskStatusEnum.UNKNOWN),

    NEW(TaskStatusEnum.NEW),

    RUNNABLE(TaskStatusEnum.RUNNABLE),

    RUNNING(TaskStatusEnum.RUNNING),

//    FINISHED(TaskStatusEnum.FINISHED),
        SUCCEED(TaskStatusEnum.SUCCEED),
        FAILED(TaskStatusEnum.FAILED),
        CANCELED(TaskStatusEnum.CANCELED),
            ;

    private Integer code;

    private String message;

    TaskStatusReassignEnum(TaskStatusEnum taskStatusEnum) {
        this.code = taskStatusEnum.getCode();
        this.message = taskStatusEnum.getMessage();
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "TaskStatusReassignEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    public static Boolean isFinished(Integer code) {
        return SUCCEED.getCode().equals(code) || FAILED.getCode().equals(code) || CANCELED.getCode().equals(code);
    }
}