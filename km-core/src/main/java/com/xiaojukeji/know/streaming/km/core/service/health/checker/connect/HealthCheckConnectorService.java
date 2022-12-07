package com.xiaojukeji.know.streaming.km.core.service.health.checker.connect;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.HealthCompareValueConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectorMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.connect.ConnectorParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
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
    private ConnectorMetricService connectorMetricService;

    @PostConstruct
    private void init() {
        functionMap.putIfAbsent(HealthCheckNameEnum.CONNECTOR_FAILED_TASK_COUNT.getConfigName(), this::checkFailedTaskCount);
        functionMap.putIfAbsent(HealthCheckNameEnum.CONNECTOR_UNASSIGNED_TASK_COUNT.getConfigName(), this::checkUnassignedTaskCount);
    }

    @Override
    public List<ClusterParam> getResList(Long connectClusterId) {
        List<ClusterParam> paramList = new ArrayList<>();
        Result<List<String>> ret = connectorService.listConnectorsFromCluster(connectClusterId);
        if (!ret.hasData()) {
            return paramList;
        }

        for (String connectorName : ret.getData()) {
            paramList.add(new ConnectorParam(connectClusterId, connectorName));
        }

        return paramList;
    }

    @Override
    public HealthCheckDimensionEnum getHealthCheckDimensionEnum() {
        return HealthCheckDimensionEnum.CONNECTOR;
    }

    private HealthCheckResult checkFailedTaskCount(Tuple<ClusterParam, BaseClusterHealthConfig> paramTuple) {
        ConnectorParam param                    = (ConnectorParam) paramTuple.getV1();
        HealthCompareValueConfig compareConfig  = (HealthCompareValueConfig) paramTuple.getV2();

        Long connectClusterId   = param.getConnectClusterId();
        String connectorName    = param.getConnectorName();
        Double compareValue     = compareConfig.getValue();

        return this.getHealthCompareResult(connectClusterId, connectorName, CONNECTOR_METRIC_CONNECTOR_FAILED_TASK_COUNT, HealthCheckNameEnum.CONNECTOR_FAILED_TASK_COUNT, compareValue);
    }

    private HealthCheckResult checkUnassignedTaskCount(Tuple<ClusterParam, BaseClusterHealthConfig> paramTuple) {
        ConnectorParam param                    = (ConnectorParam) paramTuple.getV1();
        HealthCompareValueConfig compareConfig  = (HealthCompareValueConfig) paramTuple.getV2();

        Long connectClusterId   = param.getConnectClusterId();
        String connectorName    = param.getConnectorName();
        Double compareValue     = compareConfig.getValue();

        return this.getHealthCompareResult(connectClusterId, connectorName, CONNECTOR_METRIC_CONNECTOR_UNASSIGNED_TASK_COUNT, HealthCheckNameEnum.CONNECTOR_UNASSIGNED_TASK_COUNT, compareValue);

    }

    private HealthCheckResult getHealthCompareResult(Long connectClusterId, String connectorName, String metricName, HealthCheckNameEnum healthCheckNameEnum, Double compareValue) {

        Result<ConnectorMetrics> ret = connectorMetricService.collectConnectClusterMetricsFromKafka(connectClusterId, connectorName, metricName);

        if (!ret.hasData()) {
            log.error("method=getHealthCompareResult||connectClusterId={}||connectorName={}||metricName={}||errMsg=get metrics failed",
                    connectClusterId, connectorName, metricName);
            return null;
        }

        Float value = ret.getData().getMetric(metricName);

        if (value == null) {
            log.error("method=getHealthCompareResult||connectClusterId={}||connectorName={}||metricName={}||errMsg=get metrics failed",
                    connectClusterId, connectorName, metricName);
            return null;
        }

        HealthCheckResult checkResult = new HealthCheckResult(
                HealthCheckDimensionEnum.CONNECTOR.getDimension(),
                healthCheckNameEnum.getConfigName(),
                connectClusterId,
                connectorName
        );
        checkResult.setPassed(compareValue >= value ? Constant.YES : Constant.NO);
        return checkResult;

    }


}
