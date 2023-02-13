package com.xiaojukeji.kafka.manager.common.bizenum.ha;

import lombok.Getter;

/**
 * @author zengqiao
 * @date 20/7/28
 */
@Getter
public enum HaResTypeEnum {
    CLUSTER(0, "Cluster"),

    TOPIC(1, "Topic"),

    KAFKA_USER(2, "KafkaUser"),

    KAFKA_USER_AND_CLIENT(3, "KafkaUserAndClient"),

    ;

    private final int code;

    private final String msg;

    HaResTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
