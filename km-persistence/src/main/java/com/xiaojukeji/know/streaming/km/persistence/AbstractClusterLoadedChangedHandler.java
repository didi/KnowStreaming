package com.xiaojukeji.know.streaming.km.persistence;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.event.cluster.ClusterPhyLoadChangedEvent;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 加载的集群发生变化的处理类
 * @author zengqiao
 * @date 22/02/25
 */
public abstract class AbstractClusterLoadedChangedHandler implements ApplicationListener<ClusterPhyLoadChangedEvent> {
    private static final ILog log = LogFactory.getLog(AbstractClusterLoadedChangedHandler.class);

    protected final ReentrantLock modifyClientMapLock = new ReentrantLock();

    protected abstract void add(ClusterPhy clusterPhy);

    protected abstract void modify(ClusterPhy newClusterPhy, ClusterPhy oldClusterPhy);

    protected abstract void remove(ClusterPhy clusterPhy);

    @Override
    public void onApplicationEvent(ClusterPhyLoadChangedEvent event) {
        switch (event.getOperationEnum()) {
            case ADD:
                this.add(event.getInDBClusterPhy());
                break;
            case EDIT:
                this.modify(event.getInDBClusterPhy(), event.getInCacheClusterPhy());
                break;
            case DELETE:
                this.remove(event.getInCacheClusterPhy());
                break;
            default:
                log.error("method=onApplicationEvent||event={}||msg=illegal event", event);
        }
    }
}
