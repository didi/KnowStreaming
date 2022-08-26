package com.xiaojukeji.know.streaming.km.collector.metric;

import com.xiaojukeji.know.streaming.km.collector.service.CollectThreadPoolService;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.BaseMetricEvent;
import com.xiaojukeji.know.streaming.km.common.component.SpringTool;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author didi
 */
public abstract class AbstractMetricCollector<T> {
    public abstract void collectMetrics(ClusterPhy clusterPhy);

    public abstract VersionItemTypeEnum collectorType();

    @Autowired
    private CollectThreadPoolService collectThreadPoolService;

    protected FutureWaitUtil<Void> getFutureUtilByClusterPhyId(Long clusterPhyId) {
        return collectThreadPoolService.selectSuitableFutureUtil(clusterPhyId * 1000L + this.collectorType().getCode());
    }

    protected <T extends BaseMetricEvent> void publishMetric(T event){
        SpringTool.publish(event);
    }
}
