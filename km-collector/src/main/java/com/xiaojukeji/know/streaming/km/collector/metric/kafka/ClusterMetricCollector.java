package com.xiaojukeji.know.streaming.km.collector.metric.kafka;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.ClusterMetricEvent;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterMetricService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_CLUSTER;

/**
 * @author didi
 */
@Component
public class ClusterMetricCollector extends AbstractKafkaMetricCollector<ClusterMetrics> {
    protected static final ILog LOGGER = LogFactory.getLog(ClusterMetricCollector.class);

    @Autowired
    private VersionControlService versionControlService;

    @Autowired
    private ClusterMetricService  clusterMetricService;

    @Override
    public List<ClusterMetrics> collectKafkaMetrics(ClusterPhy clusterPhy) {
        Long        startTime           =   System.currentTimeMillis();
        Long        clusterPhyId        =   clusterPhy.getId();
        List<VersionControlItem> items  =   versionControlService.listVersionControlItem(this.getClusterVersion(clusterPhy), collectorType().getCode());

        ClusterMetrics metrics = new ClusterMetrics(clusterPhyId, clusterPhy.getKafkaVersion());
        metrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, Constant.COLLECT_METRICS_ERROR_COST_TIME);

        FutureWaitUtil<Void> future = this.getFutureUtilByClusterPhyId(clusterPhyId);

        for(VersionControlItem v : items) {
            future.runnableTask(
                    String.format("class=ClusterMetricCollector||clusterPhyId=%d||metricName=%s", clusterPhyId, v.getName()),
                    30000,
                    () -> {
                        try {
                            if(null != metrics.getMetrics().get(v.getName())){
                                return null;
                            }

                            Result<ClusterMetrics> ret = clusterMetricService.collectClusterMetricsFromKafka(clusterPhyId, v.getName());
                            if(null == ret || ret.failed() || null == ret.getData()){
                                return null;
                            }

                            metrics.putMetric(ret.getData().getMetrics());
                        } catch (Exception e){
                            LOGGER.error(
                                    "method=collectKafkaMetrics||clusterPhyId={}||metricName={}||errMsg=exception!",
                                    clusterPhyId, v.getName(), e
                            );
                        }

                        return null;
                    });
        }

        future.waitExecute(30000);

        metrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, (System.currentTimeMillis() - startTime) / 1000.0f);

        publishMetric(new ClusterMetricEvent(this, Collections.singletonList(metrics)));

        return Collections.singletonList(metrics);
    }

    @Override
    public VersionItemTypeEnum collectorType() {
        return METRIC_CLUSTER;
    }
}
