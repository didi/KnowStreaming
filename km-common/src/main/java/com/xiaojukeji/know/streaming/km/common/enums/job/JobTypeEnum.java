package com.xiaojukeji.know.streaming.km.common.enums.job;

/**
 * @author didi
 */
public enum JobTypeEnum {
    UNKNOWN(-1, "unknown"),

    TOPIC_REPLICA_MOVE(0, "Topic迁移"),

    TOPIC_REPLICA_SCALA(1, "Topic扩缩副本"),

    CLUSTER_BALANCE(2, "集群均衡"),


    ;

    JobTypeEnum(Integer type, String message) {
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

    public static JobTypeEnum valueOfType(Integer type) {
        if (type == null) {
            return JobTypeEnum.UNKNOWN;
        }
        for (JobTypeEnum typeEnum : JobTypeEnum.values()) {
            if (type.equals(typeEnum.getType())) {
                return typeEnum;
            }
        }

        return JobTypeEnum.UNKNOWN;
    }

}
