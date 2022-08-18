package com.xiaojukeji.know.streaming.km.common.bean.event.cluster;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 集群事件基础类
 * @author zengqiao
 * @date 21/9/11
 */
@Getter
public class ClusterPhyBaseEvent extends ApplicationEvent {
    protected final Long clusterPhyId;

    public ClusterPhyBaseEvent(Object source, Long clusterPhyId) {
        super(source);
        this.clusterPhyId = clusterPhyId;
    }
}
