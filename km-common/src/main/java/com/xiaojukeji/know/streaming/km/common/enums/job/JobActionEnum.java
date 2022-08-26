package com.xiaojukeji.know.streaming.km.common.enums.job;

public enum JobActionEnum {
    /**
     *
     */
    START(1,"start"),

    STOP(2, "stop"),

    CANCEL(3,"cancel"),

    CONTINUE(4,"continue"),

    UNKNOWN(-1, "unknown");

    JobActionEnum(int status, String value) {
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

    public static JobActionEnum valueOfStatus(int status) {
        for (JobActionEnum statusEnum : JobActionEnum.values()) {
            if (status == statusEnum.getStatus()) {
                return statusEnum;
            }
        }

        return JobActionEnum.UNKNOWN;
    }

}
