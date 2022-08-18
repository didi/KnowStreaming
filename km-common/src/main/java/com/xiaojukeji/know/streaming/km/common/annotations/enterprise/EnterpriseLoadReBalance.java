package com.xiaojukeji.know.streaming.km.common.annotations.enterprise;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Load-reBalance能力
 */
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RUNTIME)
@Documented
public @interface EnterpriseLoadReBalance {
    boolean all() default true; // 是否所有代码都是，默认是都是
}
