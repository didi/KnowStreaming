package com.xiaojukeji.know.streaming.km.common.enums.connect;

import org.apache.kafka.connect.runtime.AbstractStatus;

/**
 * connector运行状态
 * @see AbstractStatus
 */
public enum ConnectStatusEnum {
    UNASSIGNED(0, "UNASSIGNED"),

    RUNNING(1,"RUNNING"),

    PAUSED(2,"PAUSED"),

    FAILED(3, "FAILED"),

    DESTROYED(4, "DESTROYED"),

    UNKNOWN(-1, "UNKNOWN")

    ;

    ConnectStatusEnum(int status, String value) {
        this.status = status;
        this.value = value;
    }

    private final int status;

    private final String value;

    public static ConnectStatusEnum getByValue(String value) {
        for (ConnectStatusEnum statusEnum: ConnectStatusEnum.values()) {
            if (statusEnum.value.equals(value)) {
                return statusEnum;
            }
        }

        return ConnectStatusEnum.UNKNOWN;
    }

    public int getStatus() {
        return status;
    }

    public String getValue() {
        return value;
    }
}
