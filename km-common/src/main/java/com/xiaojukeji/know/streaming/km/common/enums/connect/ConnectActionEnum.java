package com.xiaojukeji.know.streaming.km.common.enums.connect;

public enum ConnectActionEnum {
    /**
     *
     */

    STOP(2, "stop"),

    RESUME(3,"resume"),

    RESTART(4,"restart"),

    UNKNOWN(-1, "unknown");

    ConnectActionEnum(int status, String value) {
        this.status = status;
        this.value = value;
    }

    private final int status;

    private final String value;

    public int getStatus() {
        return status;
    }

    public String getValue() {
        return value;
    }
}
