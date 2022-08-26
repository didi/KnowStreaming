package com.xiaojukeji.know.streaming.km.common.bean.event.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import lombok.Getter;

/**
 * 被加载集群已变化事件
 * @author zengqiao
 * @date 22/02/25
 */
@Getter
public class ClusterPhyLoadChangedEvent extends ClusterPhyBaseEvent {
    private final ClusterPhy inDBClusterPhy;

    private final ClusterPhy inCacheClusterPhy;

    private final OperationEnum operationEnum;

    public ClusterPhyLoadChangedEvent(Object source, ClusterPhy inDBClusterPhy, ClusterPhy inCacheClusterPhy, OperationEnum operationEnum) {
        super(source, inDBClusterPhy != null? inDBClusterPhy.getId(): (inCacheClusterPhy != null? inCacheClusterPhy.getId(): null));
        this.inDBClusterPhy = inDBClusterPhy;
        this.inCacheClusterPhy = inCacheClusterPhy;
        this.operationEnum = operationEnum;
    }
}
