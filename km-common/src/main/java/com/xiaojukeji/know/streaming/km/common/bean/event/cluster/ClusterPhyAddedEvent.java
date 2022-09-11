package com.xiaojukeji.know.streaming.km.common.bean.event.cluster;

import lombok.Getter;

/**
 * 集群新增事件
 * @author zengqiao
 * @date 22/02/25
 */
@Getter
public class ClusterPhyAddedEvent extends ClusterPhyBaseEvent {
    public ClusterPhyAddedEvent(Object source, Long clusterPhyId) {
        super(source, clusterPhyId);
    }
}
