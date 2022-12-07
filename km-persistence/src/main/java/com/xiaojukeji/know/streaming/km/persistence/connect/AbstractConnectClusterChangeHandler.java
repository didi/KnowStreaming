package com.xiaojukeji.know.streaming.km.persistence.connect;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.event.cluster.connect.ConnectClusterLoadChangedEvent;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wyb
 * @date 2022/11/7
 */
public abstract class AbstractConnectClusterChangeHandler implements ApplicationListener<ConnectClusterLoadChangedEvent> {

    private static final ILog log = LogFactory.getLog(AbstractConnectClusterChangeHandler.class);

    protected final ReentrantLock modifyClientMapLock = new ReentrantLock();

    protected abstract void add(ConnectCluster connectCluster);

    protected abstract void modify(ConnectCluster newConnectCluster, ConnectCluster oldConnectCluster);

    protected abstract void remove(ConnectCluster connectCluster);


    @Override
    public void onApplicationEvent(ConnectClusterLoadChangedEvent event) {
        switch (event.getOperationEnum()) {
            case ADD:
                this.add(event.getInDBConnectCluster());
                break;
            case EDIT:
                this.modify(event.getInDBConnectCluster(), event.getInCacheConnectCluster());
                break;
            case DELETE:
                this.remove(event.getInCacheConnectCluster());
                break;
            default:
                log.error("method=onApplicationEvent||event={}||msg=illegal event", event);
        }
    }
}
