package com.xiaojukeji.know.streaming.km.common.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Kafka源码
 * @author zengqiao
 * @date 2020-07-20
 */
@Target({ElementType.TYPE, ElementType.LOCAL_VARIABLE})
@Retention(RUNTIME)
@Documented
public @interface KafkaSource {
    int modified() default 0;

    String modifyDesc() default "";
}
