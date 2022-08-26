package com.xiaojukeji.know.streaming.km.common.enums;

import lombok.Getter;

/**
 * 聚合方式
 */
@Getter
public enum AggTypeEnum {
    AVG("avg"),

    MAX("max"),

    MIN("min"),

    SUM("sum"),

    ;

    private final String aggType;

    AggTypeEnum(String aggType) {
        this.aggType = aggType;
    }
}
