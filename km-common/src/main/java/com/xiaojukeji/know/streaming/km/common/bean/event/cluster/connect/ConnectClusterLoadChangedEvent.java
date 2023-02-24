package com.xiaojukeji.know.streaming.km.common.bean.event.cluster.connect;

import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author wyb
 * @date 2022/11/7
 */
@Getter
public class ConnectClusterLoadChangedEvent extends ApplicationEvent {

    private ConnectCluster inDBConnectCluster;

    private ConnectCluster inCacheConnectCluster;

    private final OperationEnum operationEnum;

    public ConnectClusterLoadChangedEvent(Object source, ConnectCluster inDBConnectCluster, ConnectCluster inCacheConnectCluster, OperationEnum operationEnum) {
        super(source);
        this.inDBConnectCluster = inDBConnectCluster;
        this.inCacheConnectCluster = inCacheConnectCluster;
        this.operationEnum = operationEnum;
    }
}
