package com.xiaojukeji.know.streaming.km.collector.metric.kafka;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.collector.metric.AbstractMetricCollector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.LoggerUtil;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author didi
 */
public abstract class AbstractKafkaMetricCollector<M> extends AbstractMetricCollector<M, ClusterPhy> {
    private static final ILog LOGGER                    = LogFactory.getLog(AbstractMetricCollector.class);

    protected static final ILog METRIC_COLLECTED_LOGGER = LoggerUtil.getMetricCollectedLogger();

    @Autowired
    private ClusterPhyService clusterPhyService;

    public abstract List<M> collectKafkaMetrics(ClusterPhy clusterPhy);

    @Override
    public String getClusterVersion(ClusterPhy clusterPhy){
        return clusterPhyService.getVersionFromCacheFirst(clusterPhy.getId());
    }

    @Override
    public void collectMetrics(ClusterPhy clusterPhy) {
        long startTime = System.currentTimeMillis();

        // 采集指标
        List<M> metricsList = this.collectKafkaMetrics(clusterPhy);

        // 输出耗时信息
        LOGGER.info(
                "metricType={}||clusterPhyId={}||costTimeUnitMs={}",
                this.collectorType().getMessage(), clusterPhy.getId(), System.currentTimeMillis() - startTime
        );

        // 输出采集到的指标信息
        METRIC_COLLECTED_LOGGER.debug("metricType={}||clusterPhyId={}||metrics={}!",
                this.collectorType().getMessage(), clusterPhy.getId(), ConvertUtil.obj2Json(metricsList)
        );
    }
}
