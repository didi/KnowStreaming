package com.xiaojukeji.kafka.manager.kcm.common.bizenum;

import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusEnum;

/**
 * @author zengqiao
 * @date 20/9/7
 */
public enum ClusterTaskStateEnum {
    RUNNING(TaskStatusEnum.RUNNING),
    BLOCKED(TaskStatusEnum.BLOCKED),
    FINISHED(TaskStatusEnum.FINISHED),
    ;

    private Integer code;

    private String message;

    ClusterTaskStateEnum(TaskStatusEnum statusEnum) {
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
        return "ClusterTaskStateEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}