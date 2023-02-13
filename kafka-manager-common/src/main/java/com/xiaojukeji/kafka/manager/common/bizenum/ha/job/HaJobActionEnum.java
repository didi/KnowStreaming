package com.xiaojukeji.kafka.manager.common.bizenum.ha.job;

public enum HaJobActionEnum {
    /**
     *
     */
    START(1,"start"),

    STOP(2, "stop"),

    CANCEL(3,"cancel"),

    CONTINUE(4,"continue"),

    UNKNOWN(-1, "unknown");

    HaJobActionEnum(int status, String value) {
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

    public static HaJobActionEnum valueOfStatus(int status) {
        for (HaJobActionEnum statusEnum : HaJobActionEnum.values()) {
            if (status == statusEnum.getStatus()) {
                return statusEnum;
            }
        }

        return HaJobActionEnum.UNKNOWN;
    }

}
