package com.xiaojukeji.know.streaming.km.core.service.health.checker.connect;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.HealthCompareValueConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectorMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.connect.ConnectorParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectorPO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectorTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorMetricService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.AbstractHealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.connect.ConnectorMetricVersionItems.CONNECTOR_METRIC_CONNECTOR_FAILED_TASK_COUNT;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.connect.ConnectorMetricVersionItems.CONNECTOR_METRIC_CONNECTOR_UNASSIGNED_TASK_COUNT;

/**
 * @author wyb
 * @date 2022/11/8
 */
@Service
public class HealthCheckConnectorService extends AbstractHealthCheckService {
    private static final ILog log = LogFactory.getLog(HealthCheckConnectorService.class);

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private ConnectClusterService connectClusterService;

    @Autowired
    private ConnectorMetricService connectorMetricService;

    @PostConstruct
    private void init() {
        functionMap.putIfAbsent(HealthCheckNameEnum.CONNECTOR_FAILED_TASK_COUNT.getConfigName(), this::checkFailedTaskCount);
        functionMap.putIfAbsent(HealthCheckNameEnum.CONNECTOR_UNASSIGNED_TASK_COUNT.getConfigName(), this::checkUnassignedTaskCount);
    }

    @Override
    public List<ClusterParam> getResList(Long connectClusterId) {
        List<ClusterParam> paramList = new ArrayList<>();
        List<ConnectorPO> connectorPOList = connectorService.listByConnectClusterIdFromDB(connectClusterId);

        for (ConnectorPO connectorPO : connectorPOList) {
            paramList.add(new ConnectorParam(connectClusterId, connectorPO.getConnectorName(), connectorPO.getConnectorType()));
        }

        return paramList;
    }

    @Override
    public HealthCheckDimensionEnum getHealthCheckDimensionEnum() {
        return HealthCheckDimensionEnum.CONNECTOR;
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
        ConnectorParam param                    = (ConnectorParam) paramTuple.getV1();
        HealthCompareValueConfig compareConfig  = (HealthCompareValueConfig) paramTuple.getV2();

        Long connectClusterId   = param.getConnectClusterId();
        String connectorName    = param.getConnectorName();
        String connectorType    = param.getConnectorType();
        Double compareValue     = compareConfig.getValue();

        return this.getHealthCompareResult(connectClusterId, connectorName, connectorType, HealthCheckDimensionEnum.CONNECTOR.getDimension(), CONNECTOR_METRIC_CONNECTOR_FAILED_TASK_COUNT, HealthCheckNameEnum.CONNECTOR_FAILED_TASK_COUNT, compareValue);
    }

    private HealthCheckResult checkUnassignedTaskCount(Tuple<ClusterParam, BaseClusterHealthConfig> paramTuple) {
        ConnectorParam param                    = (ConnectorParam) paramTuple.getV1();
        HealthCompareValueConfig compareConfig  = (HealthCompareValueConfig) paramTuple.getV2();

        Long connectClusterId   = param.getConnectClusterId();
        String connectorName    = param.getConnectorName();
        String connectorType    = param.getConnectorType();
        Double compareValue     = compareConfig.getValue();

        return this.getHealthCompareResult(connectClusterId, connectorName, connectorType, HealthCheckDimensionEnum.CONNECTOR.getDimension(), CONNECTOR_METRIC_CONNECTOR_UNASSIGNED_TASK_COUNT, HealthCheckNameEnum.CONNECTOR_UNASSIGNED_TASK_COUNT, compareValue);

    }

    public HealthCheckResult getHealthCompareResult(Long connectClusterId, String connectorName, String connectorType, Integer dimension, String metricName, HealthCheckNameEnum healthCheckNameEnum, Double compareValue) {

        Result<ConnectorMetrics> ret = connectorMetricService.collectConnectClusterMetricsFromKafka(connectClusterId, connectorName, metricName , ConnectorTypeEnum.getByName(connectorType));

        if (!ret.hasData() || ret.getData().getMetric(metricName) == null) {
            log.error("method=getHealthCompareResult||connectClusterId={}||connectorName={}||metricName={}||errMsg=get metrics failed",
                    connectClusterId, connectorName, metricName);
            return null;
        }

        Float value = ret.getData().getMetric(metricName);

        HealthCheckResult checkResult = new HealthCheckResult(
                dimension,
                healthCheckNameEnum.getConfigName(),
                connectClusterId,
                connectorName
        );
        checkResult.setPassed(compareValue >= value ? Constant.YES : Constant.NO);
        return checkResult;

    }


}
