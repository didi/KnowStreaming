package com.xiaojukeji.kafka.manager.task.common;

import org.springframework.context.ApplicationEvent;

/**
 * @author zengqiao
 * @date 20/9/24
 */
public class BaseTaskEvent extends ApplicationEvent {
    private long startTime;

    public BaseTaskEvent(Object source, long startTime) {
        super(source);
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }
}