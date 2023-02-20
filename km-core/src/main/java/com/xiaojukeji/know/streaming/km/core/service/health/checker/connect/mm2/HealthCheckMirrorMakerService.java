package com.xiaojukeji.know.streaming.km.core.service.health.checker.connect.mm2;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.HealthCompareValueConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.mm2.MirrorMakerTopic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.mm2.MirrorMakerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.connect.mm2.MirrorMakerParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectorPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.mm2.MirrorMakerMetricService;
import com.xiaojukeji.know.streaming.km.core.service.connect.mm2.MirrorMakerService;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.AbstractHealthCheckService;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.connect.HealthCheckConnectorService;
import com.xiaojukeji.know.streaming.km.persistence.connect.cache.LoadedConnectClusterCache;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.connect.connector.ConnectorMetricESDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.constant.connect.KafkaConnectConstant.MIRROR_MAKER_SOURCE_CONNECTOR_TYPE;
import static com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectorTypeEnum.SOURCE;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.connect.ConnectorMetricVersionItems.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.connect.MirrorMakerMetricVersionItems.MIRROR_MAKER_METRIC_REPLICATION_LATENCY_MS_MAX;

/**
 * @author wyb
 * @date 2022/12/21
 */
@Service
public class HealthCheckMirrorMakerService extends AbstractHealthCheckService {
    private static final ILog log = LogFactory.getLog(HealthCheckMirrorMakerService.class);

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private MirrorMakerService mirrorMakerService;

    @Autowired
    private ConnectClusterService connectClusterService;

    @Autowired
    private MirrorMakerMetricService mirrorMakerMetricService;

    @Autowired
    private ConnectorMetricESDAO connectorMetricESDAO;

    @Autowired
    private HealthCheckConnectorService healthCheckConnectorService;

    private static final Long TEN_MIN = 10 * 60 * 1000L;

    @PostConstruct
    private void init() {
        functionMap.put(HealthCheckNameEnum.MIRROR_MAKER_UNASSIGNED_TASK_COUNT.getConfigName(), this::checkUnassignedTaskCount);
        functionMap.put(HealthCheckNameEnum.MIRROR_MAKER_FAILED_TASK_COUNT.getConfigName(), this::checkFailedTaskCount);
        functionMap.put(HealthCheckNameEnum.MIRROR_MAKER_REPLICATION_LATENCY_MS_MAX.getConfigName(), this::checkReplicationLatencyMsMax);
        functionMap.put(HealthCheckNameEnum.MIRROR_MAKER_TOTAL_RECORD_ERRORS.getConfigName(), this::checkTotalRecordErrors);
    }

    @Override
    public List<ClusterParam> getResList(Long connectClusterId) {
        List<ClusterParam> paramList = new ArrayList<>();
        List<ConnectorPO> mirrorMakerList = connectorService.listByConnectClusterIdFromDB(connectClusterId).stream().filter(elem -> elem.getConnectorType().equals(SOURCE.name()) && elem.getConnectorClassName().equals(MIRROR_MAKER_SOURCE_CONNECTOR_TYPE)).collect(Collectors.toList());

        if (mirrorMakerList.isEmpty()) {
            return paramList;
        }
        Result<Map<String, MirrorMakerTopic>> ret = mirrorMakerService.getMirrorMakerTopicMap(connectClusterId);

        if (!ret.hasData()) {
            log.error("method=getResList||connectClusterId={}||get MirrorMakerTopicMap failed!", connectClusterId);
            return paramList;
        }

        Map<String, MirrorMakerTopic> mirrorMakerTopicMap = ret.getData();

        for (ConnectorPO mirrorMaker : mirrorMakerList) {
            List<MirrorMakerTopic> mirrorMakerTopicList = mirrorMakerService.getMirrorMakerTopicList(mirrorMaker, mirrorMakerTopicMap);
            paramList.add(new MirrorMakerParam(connectClusterId, mirrorMaker.getConnectorType(), mirrorMaker.getConnectorName(), mirrorMakerTopicList));
        }
        return paramList;
    }

    @Override
    public HealthCheckDimensionEnum getHealthCheckDimensionEnum() {
        return HealthCheckDimensionEnum.MIRROR_MAKER;
    }

    @Override
    public Integer getDimensionCodeIfSupport(Long kafkaClusterPhyId) {
        List<ConnectCluster> clusterList = connectClusterService.listByKafkaCluster(kafkaClusterPhyId);
        if (ValidateUtils.isEmptyList(clusterList)) {
            return null;
        }

        return this.getHealthCheckDimensionEnum().getDimension();
    }

    private HealthCheckResult checkFailedTaskCount(Tuple<ClusterParam, BaseClusterHealthConfig> paramTuple) {
        MirrorMakerParam param                    = (MirrorMakerParam) paramTuple.getV1();
        HealthCompareValueConfig compareConfig  = (HealthCompareValueConfig) paramTuple.getV2();

        Long connectClusterId   = param.getConnectClusterId();
        String mirrorMakerName  = param.getMirrorMakerName();
        String connectorType    = param.getConnectorType();
        Double compareValue     = compareConfig.getValue();

        return healthCheckConnectorService.getHealthCompareResult(connectClusterId, mirrorMakerName, connectorType, HealthCheckDimensionEnum.MIRROR_MAKER.getDimension(), CONNECTOR_METRIC_CONNECTOR_FAILED_TASK_COUNT, HealthCheckNameEnum.MIRROR_MAKER_FAILED_TASK_COUNT, compareValue);
    }

    private HealthCheckResult checkUnassignedTaskCount(Tuple<ClusterParam, BaseClusterHealthConfig> paramTuple) {
        MirrorMakerParam param                    = (MirrorMakerParam) paramTuple.getV1();
        HealthCompareValueConfig compareConfig  = (HealthCompareValueConfig) paramTuple.getV2();

        Long connectClusterId   = param.getConnectClusterId();
        String mirrorMakerName  = param.getMirrorMakerName();
        String connectorType    = param.getConnectorType();
        Double compareValue     = compareConfig.getValue();

        return healthCheckConnectorService.getHealthCompareResult(connectClusterId, mirrorMakerName, connectorType, HealthCheckDimensionEnum.MIRROR_MAKER.getDimension(), CONNECTOR_METRIC_CONNECTOR_UNASSIGNED_TASK_COUNT, HealthCheckNameEnum.MIRROR_MAKER_UNASSIGNED_TASK_COUNT, compareValue);
    }

    private HealthCheckResult checkReplicationLatencyMsMax(Tuple<ClusterParam, BaseClusterHealthConfig> paramTuple) {
        MirrorMakerParam param = (MirrorMakerParam) paramTuple.getV1();
        HealthCompareValueConfig compareConfig = (HealthCompareValueConfig) paramTuple.getV2();

        Long connectClusterId = param.getConnectClusterId();
        String mirrorMakerName = param.getMirrorMakerName();
        List<MirrorMakerTopic> mirrorMakerTopicList = param.getMirrorMakerTopicList();
        String metricName = MIRROR_MAKER_METRIC_REPLICATION_LATENCY_MS_MAX;

        Result<MirrorMakerMetrics> ret = mirrorMakerMetricService.collectMirrorMakerMetricsFromKafka(connectClusterId, mirrorMakerName, mirrorMakerTopicList, metricName);

        if (!ret.hasData() || ret.getData().getMetric(metricName) == null) {
            log.error("method=checkReplicationLatencyMsMax||connectClusterId={}||metricName={}||errMsg=get metrics failed",
                    param.getConnectClusterId(), metricName);
            return null;
        }

        Float value = ret.getData().getMetric(metricName);

        HealthCheckResult checkResult = new HealthCheckResult(
                HealthCheckDimensionEnum.MIRROR_MAKER.getDimension(),
                HealthCheckNameEnum.MIRROR_MAKER_REPLICATION_LATENCY_MS_MAX.getConfigName(),
                connectClusterId,
                mirrorMakerName
        );
        checkResult.setPassed(value <= compareConfig.getValue() ? Constant.YES : Constant.NO);
        return checkResult;
    }

    private HealthCheckResult checkTotalRecordErrors(Tuple<ClusterParam, BaseClusterHealthConfig> paramTuple){
        MirrorMakerParam param = (MirrorMakerParam) paramTuple.getV1();
        HealthCompareValueConfig compareConfig = (HealthCompareValueConfig) paramTuple.getV2();

        Long connectClusterId                       = param.getConnectClusterId();
        String mirrorMakerName                      = param.getMirrorMakerName();
        List<MirrorMakerTopic> mirrorMakerTopicList = param.getMirrorMakerTopicList();

        ConnectCluster connectCluster               = LoadedConnectClusterCache.getByPhyId(connectClusterId);
        Long endTime                                = System.currentTimeMillis();
        Long startTime                              = endTime - TEN_MIN;
        Tuple<Long, String> connectClusterIdAndName = new Tuple<>(connectClusterId, mirrorMakerName);
        String metricName                           = CONNECTOR_METRIC_TOTAL_RECORD_ERRORS;

        Table<String, Tuple<Long, String>, List<MetricPointVO>> table = connectorMetricESDAO.listMetricsByConnectors(connectCluster.getKafkaClusterPhyId(), Arrays.asList(metricName), "avg", Arrays.asList(connectClusterIdAndName), startTime, endTime);
        List<MetricPointVO> pointVOList = table.get(metricName, connectClusterIdAndName);
        Collections.sort(pointVOList, (p1, p2) -> p2.getTimeStamp().compareTo(p1.getTimeStamp()));

        HealthCheckResult checkResult = new HealthCheckResult(
                HealthCheckDimensionEnum.MIRROR_MAKER.getDimension(),
                HealthCheckNameEnum.MIRROR_MAKER_TOTAL_RECORD_ERRORS.getConfigName(),
                connectClusterId,
                mirrorMakerName
        );

        double diff = 0;
        if (pointVOList.size() > 1) {
            diff = Double.valueOf(pointVOList.get(0).getValue()) - Double.valueOf(pointVOList.get(1).getValue());
        }
        checkResult.setPassed(diff <= compareConfig.getValue() ? Constant.YES : Constant.NO);

        return checkResult;
    }


}
