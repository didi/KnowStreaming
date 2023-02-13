package com.xiaojukeji.kafka.manager.common.bizenum.ha;

import lombok.Getter;

/**
 * @author zengqiao
 * @date 20/7/28
 */
@Getter
public enum HaRelationTypeEnum {
    UNKNOWN(-1, "非高可用"),

    STANDBY(0, "备"),

    ACTIVE(1, "主"),

    MUTUAL_BACKUP(2 , "互备");

    private final int code;

    private final String msg;

    HaRelationTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
