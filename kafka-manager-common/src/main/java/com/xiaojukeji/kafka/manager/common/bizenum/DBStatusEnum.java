package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * @author zengqiao
 * @date 20/6/4
 */
public enum DBStatusEnum {
    DEAD(-1),
    ALIVE(0)
    ;

    private int status;

    DBStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}