package com.xiaojukeji.know.streaming.km.common.enums.health;

import lombok.Getter;


/**
 * 健康状态
 */
@Getter
public enum HealthStateEnum {
    UNKNOWN(-1, "未知"),

    GOOD(0, "好"),

    MEDIUM(1, "中"),

    POOR(2, "差"),

    DEAD(3, "Down"),

    ;

    private final int dimension;

    private final String message;

    HealthStateEnum(int dimension, String message) {
        this.dimension = dimension;
        this.message = message;
    }
}
