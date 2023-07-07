package com.xiaojukeji.know.streaming.km.common.enums.group;

import lombok.Getter;


/**
 * @author wyb
 * @date 2022/10/11
 */
@Getter
public enum DeleteGroupTypeEnum {
    UNKNOWN(-1, "Unknown"),

    GROUP(0, "Group纬度"),

    GROUP_TOPIC(1, "GroupTopic纬度"),

    GROUP_TOPIC_PARTITION(2, "GroupTopicPartition纬度");

    private final Integer code;

    private final String msg;

    DeleteGroupTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
