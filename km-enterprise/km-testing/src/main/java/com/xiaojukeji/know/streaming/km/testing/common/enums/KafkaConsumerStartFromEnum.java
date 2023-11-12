package com.xiaojukeji.know.streaming.km.testing.common.enums;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseTesting;
import lombok.Getter;

/**
 * @author zengqiao
 * @date 22/02/25
 */
@Getter
@EnterpriseTesting
public enum KafkaConsumerStartFromEnum {
    LATEST(0, "最新位置开始消费"),

    EARLIEST(1, "最旧位置开始消费"),

    PRECISE_TIMESTAMP(2, "指定时间开始消费"),

    PRECISE_OFFSET(3, "指定位置开始消费"),

    CONSUMER_GROUP(4, "指定消费组进行消费"),

    LATEST_MINUS_X_OFFSET(5, "近X条数据开始消费"),

    ;

    private final Integer code;

    private final String message;

    KafkaConsumerStartFromEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
