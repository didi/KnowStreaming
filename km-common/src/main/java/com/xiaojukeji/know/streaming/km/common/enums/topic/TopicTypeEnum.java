package com.xiaojukeji.know.streaming.km.common.enums.topic;

import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import lombok.Getter;

/**
 * Topic类型
 */
@Getter
public enum TopicTypeEnum {
    UNKNOWN(-1, "未知"),

    NORMAL(0, "普通"),

    KAFKA_INTERNAL(1, "Kafka内部Topic"),

    ;

    private final Integer code;

    private final String msg;

    TopicTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Integer getTopicTypeCode(String topicName) {
        return KafkaConstant.KAFKA_INTERNAL_TOPICS.contains(topicName)? KAFKA_INTERNAL.getCode(): NORMAL.getCode();
    }
}
