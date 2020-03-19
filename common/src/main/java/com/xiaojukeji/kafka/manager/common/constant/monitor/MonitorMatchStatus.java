package com.xiaojukeji.kafka.manager.common.constant.monitor;

/**
 * @author zengqiao
 * @date 20/3/18
 */
public enum MonitorMatchStatus {
    UNKNOWN(0),

    YES(1),

    NO(2);

    public Integer status;

    MonitorMatchStatus(Integer status) {
        this.status = status;
    }
}