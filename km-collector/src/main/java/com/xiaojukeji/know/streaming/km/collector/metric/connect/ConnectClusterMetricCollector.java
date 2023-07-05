package com.xiaojukeji.know.streaming.km.collector.metric.connect;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.connect.ConnectClusterMetricEvent;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterMetricService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_CONNECT_CLUSTER;

/**
 * @author didi
 */
@Component
public class ConnectClusterMetricCollector extends AbstractConnectMetricCollector<ConnectClusterMetrics> {
    protected static final ILog LOGGER = LogFactory.getLog(ConnectClusterMetricCollector.class);

    @Autowired
    private VersionControlService versionControlService;

    @Autowired
    private ConnectClusterMetricService connectClusterMetricService;

    @Override
    public List<ConnectClusterMetrics> collectConnectMetrics(ConnectCluster connectCluster) {
        Long startTime          = System.currentTimeMillis();
        Long clusterPhyId       = connectCluster.getKafkaClusterPhyId();
        Long connectClusterId   = connectCluster.getId();

        ConnectClusterMetrics metrics = new ConnectClusterMetrics(clusterPhyId, connectClusterId);
        metrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, Constant.COLLECT_METRICS_ERROR_COST_TIME);
        List<VersionControlItem> items = versionControlService.listVersionControlItem(getClusterVersion(connectCluster), collectorType().getCode());
        FutureWaitUtil<Void> future = this.getFutureUtilByClusterPhyId(connectClusterId);

        for (VersionControlItem item : items) {
            future.runnableTask(
                    String.format("class=ConnectClusterMetricCollector||connectClusterId=%d||metricName=%s", connectClusterId, item.getName()),
                    30000,
                    () -> {
                        try {
                            Result<ConnectClusterMetrics> ret = connectClusterMetricService.collectConnectClusterMetricsFromKafka(connectClusterId, item.getName());
                            if (null == ret || !ret.hasData()) {
                                return null;
                            }
                            metrics.putMetric(ret.getData().getMetrics());

                        } catch (Exception e) {
                            LOGGER.error(
                                    "method=collectConnectMetrics||connectClusterId={}||metricName={}||errMsg=exception!",
                                    connectClusterId, item.getName(), e
                            );
                        }
                        return null;
                    }
            );
        }

        future.waitExecute(30000);

        metrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, (System.currentTimeMillis() - startTime) / 1000.0f);

        this.publishMetric(new ConnectClusterMetricEvent(this, Collections.singletonList(metrics)));

        return Collections.singletonList(metrics);
    }

    @Override
    public VersionItemTypeEnum collectorType() {
        return METRIC_CONNECT_CLUSTER;
    }
}
