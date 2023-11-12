package com.xiaojukeji.know.streaming.km.collector.metric.connect;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectorMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.connect.ConnectorMetricEvent;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectorTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorMetricService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_CONNECT_CONNECTOR;

/**
 * @author didi
 */
@Component
public class ConnectConnectorMetricCollector extends AbstractConnectMetricCollector<ConnectorMetrics> {
    protected static final ILog LOGGER = LogFactory.getLog(ConnectConnectorMetricCollector.class);

    @Autowired
    private VersionControlService versionControlService;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private ConnectorMetricService connectorMetricService;

    @Override
    public List<ConnectorMetrics> collectConnectMetrics(ConnectCluster connectCluster) {
        Long clusterPhyId       = connectCluster.getKafkaClusterPhyId();
        Long connectClusterId   = connectCluster.getId();

        List<VersionControlItem> items      = versionControlService.listVersionControlItem(this.getClusterVersion(connectCluster), collectorType().getCode());
        Result<List<String>> connectorList  = connectorService.listConnectorsFromCluster(connectCluster);

        FutureWaitUtil<Void> future         = this.getFutureUtilByClusterPhyId(connectClusterId);

        List<ConnectorMetrics> metricsList = new ArrayList<>();
        for (String connectorName : connectorList.getData()) {
            ConnectorMetrics metrics = new ConnectorMetrics(connectClusterId, connectorName);
            metrics.setClusterPhyId(clusterPhyId);

            metricsList.add(metrics);
            future.runnableTask(
                    String.format("class=ConnectConnectorMetricCollector||connectClusterId=%d||connectorName=%s", connectClusterId, connectorName),
                    30000,
                    () -> collectMetrics(connectClusterId, connectorName, metrics, items)
            );
        }
        future.waitResult(30000);

        this.publishMetric(new ConnectorMetricEvent(this, metricsList));

        return metricsList;
    }

    @Override
    public VersionItemTypeEnum collectorType() {
        return METRIC_CONNECT_CONNECTOR;
    }

    /**************************************************** private method ****************************************************/

    private void collectMetrics(Long connectClusterId, String connectorName, ConnectorMetrics metrics, List<VersionControlItem> items) {
        long startTime = System.currentTimeMillis();
        ConnectorTypeEnum connectorType = connectorService.getConnectorType(connectClusterId, connectorName);

        metrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, Constant.COLLECT_METRICS_ERROR_COST_TIME);

        for (VersionControlItem v : items) {
            try {
                //过滤已测得指标
                if (metrics.getMetrics().get(v.getName()) != null) {
                    continue;
                }

                Result<ConnectorMetrics> ret = connectorMetricService.collectConnectClusterMetricsFromKafka(connectClusterId, connectorName, v.getName(), connectorType);
                if (null == ret || ret.failed() || null == ret.getData()) {
                    continue;
                }

                metrics.putMetric(ret.getData().getMetrics());
            } catch (Exception e) {
                LOGGER.error(
                        "method=collectMetrics||connectClusterId={}||connectorName={}||metric={}||errMsg=exception!",
                        connectClusterId, connectorName, v.getName(), e
                );
            }
        }

        // 记录采集性能
        metrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, (System.currentTimeMillis() - startTime) / 1000.0f);
    }
}
