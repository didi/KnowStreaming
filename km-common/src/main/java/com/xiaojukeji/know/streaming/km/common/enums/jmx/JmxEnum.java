package com.xiaojukeji.know.streaming.km.common.enums.jmx;

import lombok.Getter;

@Getter
public enum JmxEnum {
    NOT_OPEN(-1, "未开启JMX端口"),

    UNKNOWN(-2, "JMX端口未知"),

    ;

    private final Integer port;
    private final String message;

    JmxEnum(Integer port, String message) {
        this.port = port;
        this.message = message;
    }
}
