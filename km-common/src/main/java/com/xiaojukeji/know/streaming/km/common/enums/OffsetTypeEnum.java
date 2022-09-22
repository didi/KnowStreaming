package com.xiaojukeji.know.streaming.km.common.enums;

import lombok.Getter;

/**
 * offset类型
 * @author zengqiao
 * @date 19/4/8
 */
@Getter
public enum OffsetTypeEnum {
    LATEST(0, "最新"),

    EARLIEST(1, "最旧"),

    PRECISE_TIMESTAMP(2, "指定时间"),

    PRECISE_OFFSET(3, "指定位置"),

    ;

    private final int resetType;

    private final String message;

    OffsetTypeEnum(int resetType, String message) {
        this.resetType = resetType;
        this.message = message;
    }
}
