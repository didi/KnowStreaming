package com.xiaojukeji.kafka.manager.task.component;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author limeng
 * @date 20/8/10
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface CustomScheduled {
    String name();

    String cron();

    int threadNum() default 1;
}