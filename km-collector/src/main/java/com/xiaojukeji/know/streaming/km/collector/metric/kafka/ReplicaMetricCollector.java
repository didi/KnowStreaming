package com.xiaojukeji.know.streaming.km.collector.metric.kafka;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.collector.metric.AbstractMetricCollector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ReplicationMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.ReplicaMetricEvent;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.replica.ReplicaMetricService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_REPLICATION;

/**
 * @author didi
 */
@Component
public class ReplicaMetricCollector extends AbstractMetricCollector<ReplicationMetrics> {
    protected static final ILog  LOGGER = LogFactory.getLog(ReplicaMetricCollector.class);

    @Autowired
    private VersionControlService versionControlService;

    @Autowired
    private ReplicaMetricService replicaMetricService;

    @Autowired
    private PartitionService partitionService;

    @Override
    public List<ReplicationMetrics> collectKafkaMetrics(ClusterPhy clusterPhy) {
        Long        clusterPhyId        =   clusterPhy.getId();
        List<VersionControlItem> items  =   versionControlService.listVersionControlItem(clusterPhyId, collectorType().getCode());
        List<Partition> partitions      =   partitionService.listPartitionByCluster(clusterPhyId);

        FutureWaitUtil<Void> future = this.getFutureUtilByClusterPhyId(clusterPhyId);

        List<ReplicationMetrics> metricsList = new ArrayList<>();
        for(Partition partition : partitions) {
            for (Integer brokerId: partition.getAssignReplicaList()) {
                ReplicationMetrics metrics = new ReplicationMetrics(clusterPhyId, partition.getTopicName(), brokerId, partition.getPartitionId());
                metrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, Constant.COLLECT_METRICS_ERROR_COST_TIME);
                metricsList.add(metrics);

                future.runnableTask(
                        String.format("class=ReplicaMetricCollector||clusterPhyId=%d||brokerId=%d||topicName=%s||partitionId=%d",
                                clusterPhyId, brokerId, partition.getTopicName(), partition.getPartitionId()),
                        30000,
                        () -> collectMetrics(clusterPhyId, metrics, items)
                );
            }
        }

        future.waitExecute(30000);

        publishMetric(new ReplicaMetricEvent(this, metricsList));

        return metricsList;
    }

    @Override
    public VersionItemTypeEnum collectorType() {
        return METRIC_REPLICATION;
    }

    /**************************************************** private method ****************************************************/

    private ReplicationMetrics collectMetrics(Long clusterPhyId, ReplicationMetrics metrics, List<VersionControlItem> items) {
        long startTime = System.currentTimeMillis();

        for(VersionControlItem v : items) {
            try {
                if (metrics.getMetrics().containsKey(v.getName())) {
                    continue;
                }

                Result<ReplicationMetrics> ret = replicaMetricService.collectReplicaMetricsFromKafka(
                        clusterPhyId,
                        metrics.getTopic(),
                        metrics.getBrokerId(),
                        metrics.getPartitionId(),
                        v.getName()
                );

                if (null == ret || ret.failed() || null == ret.getData()) {
                    continue;
                }

                metrics.putMetric(ret.getData().getMetrics());
            } catch (Exception e) {
                LOGGER.error(
                        "method=collectMetrics||clusterPhyId={}||topicName={}||partition={}||metricName={}||errMsg=exception!",
                        clusterPhyId, metrics.getTopic(), metrics.getPartitionId(), v.getName(), e
                );
            }
        }

        // 记录采集性能
        metrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, (System.currentTimeMillis() - startTime) / 1000.0f);

        return metrics;
    }
}
