package com.xiaojukeji.know.streaming.km.core.service.health.checker.connect;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.HealthCompareValueConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ConnectClusterParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterMetricService;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.AbstractHealthCheckService;
import com.xiaojukeji.know.streaming.km.persistence.connect.cache.LoadedConnectClusterCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.connect.ConnectClusterMetricVersionItems.CONNECT_CLUSTER_METRIC_CONNECTOR_STARTUP_FAILURE_PERCENTAGE;

/**
 * @author wyb
 * @date 2022/11/9
 */
@Service
public class HealthCheckConnectClusterService extends AbstractHealthCheckService {
    private static final ILog log = LogFactory.getLog(HealthCheckConnectClusterService.class);

    @Autowired
    private ConnectClusterService connectClusterService;

    @Autowired
    private ConnectClusterMetricService connectClusterMetricService;

    @PostConstruct
    private void init() {
        functionMap.putIfAbsent(HealthCheckNameEnum.CONNECT_CLUSTER_TASK_STARTUP_FAILURE_PERCENTAGE.getConfigName(), this::checkStartupFailurePercentage);
    }

    @Override
    public List<ClusterParam> getResList(Long connectClusterId) {
        List<ClusterParam> paramList = new ArrayList<>();
        if (LoadedConnectClusterCache.containsByPhyId(connectClusterId)) {
            paramList.add(new ConnectClusterParam(connectClusterId));
        }
        return paramList;
    }

    @Override
    public HealthCheckDimensionEnum getHealthCheckDimensionEnum() {
        return HealthCheckDimensionEnum.CONNECT_CLUSTER;
    }

    @Override
    public Integer getDimensionCodeIfSupport(Long kafkaClusterPhyId) {
        List<ConnectCluster> clusterList = connectClusterService.listByKafkaCluster(kafkaClusterPhyId);
        if (ValidateUtils.isEmptyList(clusterList)) {
            return null;
        }

        return this.getHealthCheckDimensionEnum().getDimension();
    }

    private HealthCheckResult checkStartupFailurePercentage(Tuple<ClusterParam, BaseClusterHealthConfig> paramTuple) {
        ConnectClusterParam param                = (ConnectClusterParam) paramTuple.getV1();
        HealthCompareValueConfig compareConfig   = (HealthCompareValueConfig) paramTuple.getV2();

        Long connectClusterId = param.getConnectClusterId();
        String metricName = CONNECT_CLUSTER_METRIC_CONNECTOR_STARTUP_FAILURE_PERCENTAGE;

        Result<ConnectClusterMetrics> ret = connectClusterMetricService.collectConnectClusterMetricsFromKafka(connectClusterId, metricName);

        if (!ret.hasData()) {
            log.error("method=checkStartupFailurePercentage||connectClusterId={}||metricName={}||errMsg=get metrics failed",
                    param.getConnectClusterId(), metricName);
            return null;
        }

        Float value = ret.getData().getMetric(metricName);

        if (value == null) {
            log.error("method=checkStartupFailurePercentage||connectClusterId={}||metricName={}||errMsg=get metrics failed",
                    param.getConnectClusterId(), metricName);
            return null;
        }

        ConnectCluster connectCluster = LoadedConnectClusterCache.getByPhyId(connectClusterId);

        HealthCheckResult checkResult = new HealthCheckResult(
                HealthCheckDimensionEnum.CONNECT_CLUSTER.getDimension(),
                HealthCheckNameEnum.CONNECT_CLUSTER_TASK_STARTUP_FAILURE_PERCENTAGE.getConfigName(),
                connectCluster.getKafkaClusterPhyId(),
                String.valueOf(connectClusterId)
        );
        checkResult.setPassed(value <= compareConfig.getValue() ? Constant.YES : Constant.NO);
        return checkResult;

    }
}
