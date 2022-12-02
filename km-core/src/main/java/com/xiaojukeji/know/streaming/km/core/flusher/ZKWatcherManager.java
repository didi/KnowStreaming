package com.xiaojukeji.know.streaming.km.core.flusher;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.component.SpringTool;
import com.xiaojukeji.know.streaming.km.common.enums.cluster.ClusterRunStateEnum;
import com.xiaojukeji.know.streaming.km.common.utils.FutureUtil;
import com.xiaojukeji.know.streaming.km.core.flusher.zk.AbstractZKWatcher;
import com.xiaojukeji.know.streaming.km.persistence.AbstractClusterLoadedChangedHandler;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class ZKWatcherManager extends AbstractClusterLoadedChangedHandler {
    protected static final ILog log = LogFactory.getLog(ZKWatcherManager.class);

    private List<AbstractZKWatcher> watcherList;

    @PostConstruct
    private void abstractInit() {
        watcherList = new ArrayList<>();
        watcherList.addAll(SpringTool.getBeansOfType(AbstractZKWatcher.class).values());
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void scheduledTriggerFlush() {
        for (ClusterPhy clusterPhy: LoadedClusterPhyCache.listAll().values()) {
            if (!clusterPhy.getRunState().equals(ClusterRunStateEnum.RUN_ZK.getRunState())) {
                continue;
            }

            for (AbstractZKWatcher abstractZKWatcher: watcherList) {
                try {
                    FutureUtil.quickStartupFutureUtil.submitTask(
                            () -> {
                                log.debug("runClass={}||method=scheduledTriggerFlush||clusterPhyId={}||msg=flush task start"
                                        , abstractZKWatcher.getClass().getSimpleName(), clusterPhy.getId());

                                long startTime = System.currentTimeMillis();

                                abstractZKWatcher.flush(clusterPhy);

                                log.info("runClass={}||method=scheduledTriggerFlush||clusterPhyId={}||costTime={}ms||msg=flush task finished"
                                        , abstractZKWatcher.getClass().getSimpleName(), clusterPhy.getId(), System.currentTimeMillis() - startTime);
                            });
                } catch (Exception e) {
                    log.error("method=scheduledTriggerFlush||clusterPhyId={}||errMsg=exception", clusterPhy.getId(), e);
                }
            }
        }
    }

    @Override
    protected void add(ClusterPhy clusterPhy) {
        // ignore
    }

    @Override
    protected void remove(ClusterPhy clusterPhy) {
        for (AbstractZKWatcher abstractMetadataFlush: watcherList) {
            abstractMetadataFlush.remove(clusterPhy.getId());
        }
    }

    @Override
    protected void modify(ClusterPhy newClusterPhy, ClusterPhy oldClusterPhy) {
        if (newClusterPhy.getZookeeper().equals(oldClusterPhy.getZookeeper())
                && newClusterPhy.getBootstrapServers().equals(oldClusterPhy.getBootstrapServers())
                && newClusterPhy.getClientProperties().equals(oldClusterPhy.getClientProperties())
                && newClusterPhy.getRunState().equals(oldClusterPhy.getRunState())) {
            // 集群ZK及服务地址等信息无变化，则元信息无需变化，直接返回
            return;
        }

        this.remove(newClusterPhy);

        this.add(newClusterPhy);
    }
}
