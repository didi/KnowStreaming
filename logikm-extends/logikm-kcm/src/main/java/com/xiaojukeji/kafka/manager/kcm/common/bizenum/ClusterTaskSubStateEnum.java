package com.xiaojukeji.kafka.manager.kcm.common.bizenum;

import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusEnum;

/**
 * @author zengqiao
 * @date 20/9/7
 */
public enum ClusterTaskSubStateEnum {
    WAITING(TaskStatusEnum.WAITING),
    RUNNING(TaskStatusEnum.RUNNING),
    FAILED(TaskStatusEnum.FAILED),
    SUCCEED(TaskStatusEnum.SUCCEED),
    TIMEOUT(TaskStatusEnum.TIMEOUT),
    CANCELED(TaskStatusEnum.CANCELED),
    IGNORED(TaskStatusEnum.IGNORED),
    KILLING(TaskStatusEnum.KILLING),
    KILL_FAILED(TaskStatusEnum.KILL_FAILED),
    ;

    private Integer code;

    private String message;

    ClusterTaskSubStateEnum(TaskStatusEnum statusEnum) {
        this.code = statusEnum.getCode();
        this.message = statusEnum.getMessage();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ClusterTaskSubState{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}