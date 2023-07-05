package com.xiaojukeji.know.streaming.km.collector.metric.connect.mm2;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.collector.metric.connect.AbstractConnectMetricCollector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.mm2.MirrorMakerTopic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.mm2.MirrorMakerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.mm2.MirrorMakerMetricEvent;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectorPO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.mm2.MirrorMakerMetricService;
import com.xiaojukeji.know.streaming.km.core.service.connect.mm2.MirrorMakerService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.constant.connect.KafkaConnectConstant.MIRROR_MAKER_SOURCE_CONNECTOR_TYPE;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_CONNECT_MIRROR_MAKER;

/**
 * @author wyb
 * @date 2022/12/15
 */
@Component
public class MirrorMakerMetricCollector extends AbstractConnectMetricCollector<MirrorMakerMetrics> {
    protected static final ILog LOGGER = LogFactory.getLog(MirrorMakerMetricCollector.class);

    @Autowired
    private VersionControlService versionControlService;

    @Autowired
    private MirrorMakerService mirrorMakerService;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private MirrorMakerMetricService mirrorMakerMetricService;

    @Override
    public VersionItemTypeEnum collectorType() {
        return METRIC_CONNECT_MIRROR_MAKER;
    }

    @Override
    public List<MirrorMakerMetrics> collectConnectMetrics(ConnectCluster connectCluster) {
        Long clusterPhyId     = connectCluster.getKafkaClusterPhyId();
        Long connectClusterId = connectCluster.getId();

        List<ConnectorPO> mirrorMakerList = connectorService.listByConnectClusterIdFromDB(connectClusterId).stream().filter(elem -> elem.getConnectorClassName().equals(MIRROR_MAKER_SOURCE_CONNECTOR_TYPE)).collect(Collectors.toList());
        Map<String, MirrorMakerTopic> mirrorMakerTopicMap = mirrorMakerService.getMirrorMakerTopicMap(connectClusterId).getData();

        List<VersionControlItem> items  = versionControlService.listVersionControlItem(this.getClusterVersion(connectCluster), collectorType().getCode());
        FutureWaitUtil<Void> future     = this.getFutureUtilByClusterPhyId(clusterPhyId);

        List<MirrorMakerMetrics> metricsList = new ArrayList<>();

        for (ConnectorPO mirrorMaker : mirrorMakerList) {
            MirrorMakerMetrics metrics = new MirrorMakerMetrics(clusterPhyId, connectClusterId, mirrorMaker.getConnectorName());
            metricsList.add(metrics);

            List<MirrorMakerTopic> mirrorMakerTopicList = mirrorMakerService.getMirrorMakerTopicList(mirrorMaker, mirrorMakerTopicMap);
            future.runnableTask(String.format("class=MirrorMakerMetricCollector||connectClusterId=%d||mirrorMakerName=%s", connectClusterId, mirrorMaker.getConnectorName()),
                    30000,
                    () -> collectMetrics(connectClusterId, mirrorMaker.getConnectorName(), metrics, items, mirrorMakerTopicList));
        }
        future.waitResult(30000);

        this.publishMetric(new MirrorMakerMetricEvent(this,metricsList));

        return metricsList;
    }

    /**************************************************** private method ****************************************************/
    private void collectMetrics(Long connectClusterId, String mirrorMakerName, MirrorMakerMetrics metrics, List<VersionControlItem> items, List<MirrorMakerTopic> mirrorMakerTopicList) {
        long startTime = System.currentTimeMillis();
        metrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, Constant.COLLECT_METRICS_ERROR_COST_TIME);

        for (VersionControlItem v : items) {
            try {
                //已测量指标过滤
                if (metrics.getMetrics().get(v.getName()) != null) {
                    continue;
                }

                Result<MirrorMakerMetrics> ret = mirrorMakerMetricService.collectMirrorMakerMetricsFromKafka(connectClusterId, mirrorMakerName, mirrorMakerTopicList, v.getName());
                if (ret == null || !ret.hasData()) {
                    continue;
                }
                metrics.putMetric(ret.getData().getMetrics());

            } catch (Exception e) {
                LOGGER.error(
                        "method=collectMetrics||connectClusterId={}||mirrorMakerName={}||metric={}||errMsg=exception!",
                        connectClusterId, mirrorMakerName, v.getName(), e
                );

            }
        }
        metrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, (System.currentTimeMillis() - startTime) / 1000.0f);

    }
}



