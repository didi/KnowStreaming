package com.xiaojukeji.know.streaming.km.testing.common.enums;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseTesting;
import lombok.Getter;

/**
 * @author zengqiao
 * @date 22/02/25
 */
@Getter
@EnterpriseTesting
public enum KafkaConsumerFilterEnum {
    NONE(0, "无"),

    CONTAINS(1, "包含"),

    NOT_CONTAINS(2, "不包含"),

    EQUAL_SIZE(3, "size等于"),

    ABOVE_SIZE(4, "size大于"),

    UNDER_SIZE(5, "size小于"),

    KEY_CONTAINS(6, "key包含"),

    VALUE_CONTAINS(7, "value包含"),

    ;

    private final Integer code;

    private final String message;

    KafkaConsumerFilterEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
