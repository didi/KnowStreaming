package com.xiaojukeji.kafka.manager.common.bizenum;

import lombok.Getter;

@Getter
public enum JobLogBizTypEnum {
    HA_SWITCH_JOB_LOG(100, "HA-主备切换日志"),

    UNKNOWN(-1, "unknown"),

        ;

    JobLogBizTypEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;

    private final String msg;
}
