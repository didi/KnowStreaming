package com.xiaojukeji.know.streaming.km.collector.metric;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.collector.service.CollectThreadPoolService;
import com.xiaojukeji.know.streaming.km.common.utils.LoggerUtil;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.BaseMetricEvent;
import com.xiaojukeji.know.streaming.km.common.component.SpringTool;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * @author didi
 */
public abstract class AbstractMetricCollector<T> {
    protected static final ILog LOGGER                  = LogFactory.getLog(AbstractMetricCollector.class);

    protected static final ILog METRIC_COLLECTED_LOGGER = LoggerUtil.getMetricCollectedLogger();

    public abstract List<T> collectKafkaMetrics(ClusterPhy clusterPhy);

    public abstract VersionItemTypeEnum collectorType();

    @Autowired
    private CollectThreadPoolService collectThreadPoolService;

    public void collectMetrics(ClusterPhy clusterPhy) {
        long startTime = System.currentTimeMillis();

        // 采集指标
        List<T> metricsList = this.collectKafkaMetrics(clusterPhy);

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

    protected FutureWaitUtil<Void> getFutureUtilByClusterPhyId(Long clusterPhyId) {
        return collectThreadPoolService.selectSuitableFutureUtil(clusterPhyId * 1000L + this.collectorType().getCode());
    }

    protected <T extends BaseMetricEvent> void publishMetric(T event){
        SpringTool.publish(event);
    }
}
