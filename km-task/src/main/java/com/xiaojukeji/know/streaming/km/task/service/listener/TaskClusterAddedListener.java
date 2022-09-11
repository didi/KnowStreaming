package com.xiaojukeji.know.streaming.km.task.service.listener;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.event.cluster.ClusterPhyAddedEvent;
import com.xiaojukeji.know.streaming.km.common.component.SpringTool;
import com.xiaojukeji.know.streaming.km.common.utils.BackoffUtils;
import com.xiaojukeji.know.streaming.km.common.utils.FutureUtil;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import com.xiaojukeji.know.streaming.km.task.metadata.AbstractAsyncMetadataDispatchTask;
import com.xiaojukeji.know.streaming.km.task.metrics.AbstractAsyncMetricsDispatchTask;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskClusterAddedListener implements ApplicationListener<ClusterPhyAddedEvent> {
    private static final ILog LOGGER = LogFactory.getLog(TaskClusterAddedListener.class);

    @Override
    public void onApplicationEvent(ClusterPhyAddedEvent event) {
        LOGGER.info("class=TaskClusterAddedListener||method=onApplicationEvent||clusterPhyId={}||msg=listened new cluster", event.getClusterPhyId());
        Long now = System.currentTimeMillis();

        // 交由KS自定义的线程池，异步执行任务
        FutureUtil.quickStartupFutureUtil.submitTask(() -> triggerAllTask(event.getClusterPhyId(), now));
    }

    private void triggerAllTask(Long clusterPhyId, Long startTimeUnitMs) {
        ClusterPhy tempClusterPhy = null;

        // 120秒内无加载进来，则直接返回退出
        while (System.currentTimeMillis() - startTimeUnitMs <= 120L * 1000L) {
            tempClusterPhy = LoadedClusterPhyCache.getByPhyId(clusterPhyId);
            if (tempClusterPhy != null) {
                break;
            }

            BackoffUtils.backoff(1000);
        }

        if (tempClusterPhy == null) {
            return;
        }

        // 获取到之后，再延迟5秒，保证相关的集群都被正常加载进来，这里的5秒不固定
        BackoffUtils.backoff(5000);
        final ClusterPhy clusterPhy = tempClusterPhy;

        // 集群执行集群元信息同步
        List<AbstractAsyncMetadataDispatchTask> metadataServiceList = new ArrayList<>(SpringTool.getBeansOfType(AbstractAsyncMetadataDispatchTask.class).values());
        for (AbstractAsyncMetadataDispatchTask dispatchTask: metadataServiceList) {
            try {
                dispatchTask.asyncProcessSubTask(clusterPhy, startTimeUnitMs);
            } catch (Exception e) {
                // ignore
            }
        }

        // 再延迟5秒，保证集群元信息都已被正常同步至DB，这里的5秒不固定
        BackoffUtils.backoff(5000);

        // 集群集群指标采集
        List<AbstractAsyncMetricsDispatchTask> metricsServiceList = new ArrayList<>(SpringTool.getBeansOfType(AbstractAsyncMetricsDispatchTask.class).values());
        for (AbstractAsyncMetricsDispatchTask dispatchTask: metricsServiceList) {
            try {
                dispatchTask.asyncProcessSubTask(clusterPhy, startTimeUnitMs);
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
