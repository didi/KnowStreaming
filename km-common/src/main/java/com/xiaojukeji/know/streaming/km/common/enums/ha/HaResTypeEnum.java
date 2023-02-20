package com.xiaojukeji.know.streaming.km.common.enums.ha;

import lombok.Getter;

/**
 * @author zengqiao
 * @date 20/7/28
 */
@Getter
public enum HaResTypeEnum {
    CLUSTER(0, "Cluster"),

    MIRROR_TOPIC(1, "镜像Topic"),

    ;

    private final int code;

    private final String msg;

    HaResTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
