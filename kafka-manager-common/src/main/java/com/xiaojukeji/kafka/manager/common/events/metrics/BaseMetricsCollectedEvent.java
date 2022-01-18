package com.xiaojukeji.kafka.manager.common.events.metrics;

import org.springframework.context.ApplicationEvent;

/**
 * @author zengqiao
 * @date 22/01/17
 */
public class BaseMetricsCollectedEvent extends ApplicationEvent {
    /**
     * 物理集群ID
     */
    protected final Long physicalClusterId;

    /**
     * 收集时间，依据业务需要来设置，可以设置任务开始时间，也可以设置任务结束时间
     */
    protected final Long collectTime;

    public BaseMetricsCollectedEvent(Object source, Long physicalClusterId, Long collectTime) {
        super(source);
        this.physicalClusterId = physicalClusterId;
        this.collectTime = collectTime;
    }

    public Long getPhysicalClusterId() {
        return physicalClusterId;
    }

    public Long getCollectTime() {
        return collectTime;
    }
}
