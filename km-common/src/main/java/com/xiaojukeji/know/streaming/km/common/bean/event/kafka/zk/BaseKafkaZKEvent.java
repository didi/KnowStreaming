package com.xiaojukeji.know.streaming.km.common.bean.event.kafka.zk;

import lombok.Getter;

@Getter
public abstract class BaseKafkaZKEvent {
    /**
     * 触发时间
     */
    protected Long eventTime;

    /**
     * 初始化数据的事件
     */
    protected Boolean initEvent;

    /**
     * 集群ID
     */
    protected Long clusterPhyId;

    protected BaseKafkaZKEvent(Long eventTime, Long clusterPhyId) {
        this.eventTime = eventTime;
        this.clusterPhyId = clusterPhyId;
    }
}
