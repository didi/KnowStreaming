package com.xiaojukeji.kafka.manager.common.annotations;

import com.xiaojukeji.kafka.manager.common.constant.ApiLevelContent;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 接口分级限流
 * @author zengqiao
 * @date 2020-07-20
 */
@Target(ElementType.METHOD)
@Retention(RUNTIME)
@Documented
public @interface ApiLevel {
    int level() default ApiLevelContent.LEVEL_DEFAULT_4;

    int rateLimit() default Integer.MAX_VALUE;
}
