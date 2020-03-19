package com.xiaojukeji.kafka.manager.common.entity.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * FieldSelector
 * @author huangyiminghappy@163.com
 * @date 2019-06-19
 */
@Target(ElementType.FIELD)
@Retention(RUNTIME)
@Documented
public @interface FieldSelector {
    //注解的属性
    String name() default "";

    int[] types() default {};

}
