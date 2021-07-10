package com.xiaojukeji.kafka.manager.monitor.component.n9e.entry.bizenum;

public enum CategoryEnum {
    DEVICE_RELATED(1, "设备相关"),
    DEVICE_INDEPENDENT(2, "设备无关"),
    ;
    private int code;

    private String msg;

    CategoryEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
