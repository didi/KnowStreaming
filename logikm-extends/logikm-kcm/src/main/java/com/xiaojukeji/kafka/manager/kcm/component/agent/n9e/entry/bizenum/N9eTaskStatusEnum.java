package com.xiaojukeji.kafka.manager.kcm.component.agent.n9e.entry.bizenum;

import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskStateEnum;

/**
 * @author zengqiao
 * @date 20/9/3
 */
public enum N9eTaskStatusEnum {
    DONE(0, "done", ClusterTaskStateEnum.FINISHED),
    PAUSE(1, "pause", ClusterTaskStateEnum.BLOCKED),
    START(2, "start", ClusterTaskStateEnum.RUNNING),
    ;

    private Integer code;

    private String message;

    private ClusterTaskStateEnum status;

    N9eTaskStatusEnum(Integer code, String message, ClusterTaskStateEnum status) {
        this.code = code;
        this.message = message;
        this.status = status;
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

    public ClusterTaskStateEnum getStatus() {
        return status;
    }

    public void setStatus(ClusterTaskStateEnum status) {
        this.status = status;
    }

    public static N9eTaskStatusEnum getByMessage(String message) {
        for (N9eTaskStatusEnum elem: N9eTaskStatusEnum.values()) {
            if (elem.message.equals(message)) {
                return elem;
            }
        }
        return null;
    }
}