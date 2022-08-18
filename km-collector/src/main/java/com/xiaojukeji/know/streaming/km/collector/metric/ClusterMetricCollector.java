package com.xiaojukeji.know.streaming.km.collector.metric;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.ClusterMetricEvent;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.ClusterMetricPO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.EnvUtil;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterMetricService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_CLUSTER;

/**
 * @author didi
 */
@Component
public class ClusterMetricCollector extends AbstractMetricCollector<ClusterMetricPO> {
    protected static final ILog  LOGGER = LogFactory.getLog("METRIC_LOGGER");

    @Autowired
    private VersionControlService versionControlService;

    @Autowired
    private ClusterMetricService  clusterMetricService;

    @Override
    public void collectMetrics(ClusterPhy clusterPhy) {
        Long        startTime           =   System.currentTimeMillis();
        Long        clusterPhyId        =   clusterPhy.getId();
        List<VersionControlItem> items  =   versionControlService.listVersionControlItem(clusterPhyId, collectorType().getCode());

        ClusterMetrics metrics = new ClusterMetrics(clusterPhyId, clusterPhy.getKafkaVersion());

        FutureWaitUtil<Void> future = this.getFutureUtilByClusterPhyId(clusterPhyId);

        for(VersionControlItem v : items) {
            future.runnableTask(
                    String.format("method=ClusterMetricCollector||clusterPhyId=%d||metricName=%s", clusterPhyId, v.getName()),
                    30000,
                    () -> {
                        try {
                            if(null != metrics.getMetrics().get(v.getName())){return null;}

                            Result<ClusterMetrics> ret = clusterMetricService.collectClusterMetricsFromKafka(clusterPhyId, v.getName());
                            if(null == ret || ret.failed() || null == ret.getData()){return null;}

                            metrics.putMetric(ret.getData().getMetrics());

                            if(!EnvUtil.isOnline()){
                                LOGGER.info("method=ClusterMetricCollector||clusterPhyId={}||metricName={}||metricValue={}",
                                        clusterPhyId, v.getName(), ConvertUtil.obj2Json(ret.getData().getMetrics()));
                            }
                        } catch (Exception e){
                            LOGGER.error("method=ClusterMetricCollector||clusterPhyId={}||metricName={}||errMsg=exception!",
                                    clusterPhyId, v.getName(), e);
                        }

                        return null;
                    });
        }

        future.waitExecute(30000);
        doOptimizeMetric(metrics);

        metrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, (System.currentTimeMillis() - startTime) / 1000.0f);

        publishMetric(new ClusterMetricEvent(this, Arrays.asList(metrics)));

        LOGGER.info("method=ClusterMetricCollector||clusterPhyId={}||startTime={}||costTime={}||msg=msg=collect finished.",
                clusterPhyId, startTime, System.currentTimeMillis() - startTime);
    }

    @Override
    public VersionItemTypeEnum collectorType() {
        return METRIC_CLUSTER;
    }
}
