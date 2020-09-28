package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * @author zengqiao
 * @date 20/7/27
 */
public enum ApiLevelEnum {
    LEVEL_0(0),
    LEVEL_1(1),
    LEVEL_2(2),
    LEVEL_3(3)
    ;

    private int level;

    ApiLevelEnum(int level) {
        this.level = level;
    }
}