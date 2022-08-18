package com.xiaojukeji.know.streaming.km.common.enums;

import lombok.Getter;

/**
 * 重置offset
 * @author zengqiao
 * @date 19/4/8
 */
@Getter
public enum GroupOffsetResetEnum {
    LATEST(0, "重置到最新"),

    EARLIEST(1, "重置到最旧"),

    PRECISE_TIMESTAMP(2, "按时间进行重置"),

    PRECISE_OFFSET(3, "重置到指定位置"),

    ;

    private final int resetType;

    private final String message;

    GroupOffsetResetEnum(int resetType, String message) {
        this.resetType = resetType;
        this.message = message;
    }
}
