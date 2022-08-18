package com.xiaojukeji.know.streaming.km.collector.metric;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaojukeji.know.streaming.km.collector.service.CollectThreadPoolService;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.BaseMetricEvent;
import com.xiaojukeji.know.streaming.km.common.component.SpringTool;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * @author didi
 */
public abstract class AbstractMetricCollector<T> {
    private static final double SIZE_THRESHOLD  = 0.8;

    private final Cache<String, BaseMetrics> latestMetricsMap = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .build();

    public abstract void collectMetrics(ClusterPhy clusterPhy);

    public abstract VersionItemTypeEnum collectorType();

    @Autowired
    private CollectThreadPoolService collectThreadPoolService;

    /**
     * 如果最近3分钟内的指标有异常，则采用之前的值
     */
    protected void doOptimizeMetric(BaseMetrics metricPO){
        BaseMetrics latestMetrics = latestMetricsMap.getIfPresent(metricPO.unique());
        if (latestMetrics == null) {
            latestMetrics = metricPO;
        }

        if(metricPO.getMetrics().size() < latestMetrics.getMetrics().size() * SIZE_THRESHOLD) {
            // 异常采集时，则替换metrics
            metricPO.putMetric(latestMetrics.getMetrics());
        } else {
            // 正常采集时，则替换cache
            latestMetricsMap.put(metricPO.unique(), metricPO);
        }
    }

    protected FutureWaitUtil<Void> getFutureUtilByClusterPhyId(Long clusterPhyId) {
        return collectThreadPoolService.selectSuitableFutureUtil(clusterPhyId * 1000L + this.collectorType().getCode());
    }

    protected <T extends BaseMetricEvent> void publishMetric(T event){
        SpringTool.publish(event);
    }
}
