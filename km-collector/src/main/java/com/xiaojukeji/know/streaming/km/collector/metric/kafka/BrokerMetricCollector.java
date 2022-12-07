package com.xiaojukeji.know.streaming.km.collector.metric.kafka;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BrokerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.BrokerMetricEvent;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerMetricService;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_BROKER;

/**
 * @author didi
 */
@Component
public class BrokerMetricCollector extends AbstractKafkaMetricCollector<BrokerMetrics> {
    private static final ILog LOGGER = LogFactory.getLog(BrokerMetricCollector.class);

    @Autowired
    private VersionControlService versionControlService;

    @Autowired
    private BrokerMetricService brokerMetricService;

    @Autowired
    private BrokerService brokerService;

    @Override
    public List<BrokerMetrics> collectKafkaMetrics(ClusterPhy clusterPhy) {
        Long clusterPhyId  = clusterPhy.getId();

        List<Broker>             brokers = brokerService.listAliveBrokersFromDB(clusterPhy.getId());
        List<VersionControlItem> items   = versionControlService.listVersionControlItem(this.getClusterVersion(clusterPhy), collectorType().getCode());

        FutureWaitUtil<Void> future = this.getFutureUtilByClusterPhyId(clusterPhyId);

        List<BrokerMetrics> metricsList = new ArrayList<>();
        for(Broker broker : brokers) {
            BrokerMetrics metrics = new BrokerMetrics(clusterPhyId, broker.getBrokerId(), broker.getHost(), broker.getPort());
            metrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, Constant.COLLECT_METRICS_ERROR_COST_TIME);
            metricsList.add(metrics);

            future.runnableTask(
                    String.format("class=BrokerMetricCollector||clusterPhyId=%d||brokerId=%d", clusterPhyId, broker.getBrokerId()),
                    30000,
                    () -> collectMetrics(clusterPhyId, metrics, items)
            );
        }

        future.waitExecute(30000);
        this.publishMetric(new BrokerMetricEvent(this, metricsList));

        return metricsList;
    }

    @Override
    public VersionItemTypeEnum collectorType() {
        return METRIC_BROKER;
    }

    /**************************************************** private method ****************************************************/

    private void collectMetrics(Long clusterPhyId, BrokerMetrics metrics, List<VersionControlItem> items) {
        long startTime = System.currentTimeMillis();

        for(VersionControlItem v : items) {
            try {
                if(metrics.getMetrics().containsKey(v.getName())) {
                    continue;
                }

                Result<BrokerMetrics> ret = brokerMetricService.collectBrokerMetricsFromKafkaWithCacheFirst(clusterPhyId, metrics.getBrokerId(), v.getName());
                if(null == ret || ret.failed() || null == ret.getData()){
                    continue;
                }

                metrics.putMetric(ret.getData().getMetrics());
            } catch (Exception e){
                LOGGER.error(
                        "method=collectMetrics||clusterPhyId={}||brokerId={}||metricName={}||errMsg=exception!",
                        clusterPhyId, metrics.getBrokerId(), v.getName(), e
                );
            }
        }

        // 记录采集性能
        metrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, (System.currentTimeMillis() - startTime) / 1000.0f);
    }
}
