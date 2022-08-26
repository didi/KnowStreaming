package com.xiaojukeji.know.streaming.km.common.enums.job;

import com.xiaojukeji.know.streaming.km.common.constant.JobConstant;

public enum JobHandleEnum {
    UNKNOWN(-1, "unknown"),

    TOPIC_REPLICA_MOVE(0, JobConstant.TOPIC_REPLICA_MOVE),

    TOPIC_REPLICA_SCALA(1, JobConstant.TOPIC_REPLICA_SCALA),

    CLUSTER_BALANCE(2, JobConstant.CLUSTER_BALANCE),

    ;

    JobHandleEnum(Integer type, String message) {
        this.type = type;
        this.message = message;
    }

    private final Integer type;

    private final String message;

    public Integer getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public static JobHandleEnum valueOfType(Integer type) {
        if (type == null) {
            return JobHandleEnum.UNKNOWN;
        }
        for (JobHandleEnum typeEnum : JobHandleEnum.values()) {
            if (type.equals(typeEnum.getType())) {
                return typeEnum;
            }
        }

        return JobHandleEnum.UNKNOWN;
    }
}