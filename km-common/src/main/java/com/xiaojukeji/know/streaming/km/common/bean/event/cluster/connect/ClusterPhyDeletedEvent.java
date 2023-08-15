package com.xiaojukeji.know.streaming.km.common.bean.event.cluster.connect;

import com.xiaojukeji.know.streaming.km.common.bean.event.cluster.ClusterPhyBaseEvent;
import lombok.Getter;

/**
 * 集群删除事件
 * @author zengqiao
 * @date 23/08/15
 */
@Getter
public class ClusterPhyDeletedEvent extends ClusterPhyBaseEvent {
    public ClusterPhyDeletedEvent(Object source, Long clusterPhyId) {
        super(source, clusterPhyId);
    }
}
