package com.xiaojukeji.know.streaming.km.core.service.health.checker.cluster;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.HealthCompareValueConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterMetricService;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.AbstractHealthCheckService;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.ClusterMetricVersionItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Service
public class HealthCheckClusterService extends AbstractHealthCheckService {
    private static final ILog log = LogFactory.getLog(HealthCheckClusterService.class);

    @Autowired
    private ClusterMetricService clusterMetricService;

    @PostConstruct
    private void init() {
        functionMap.putIfAbsent(HealthCheckNameEnum.CLUSTER_NO_CONTROLLER.getConfigName(), this::checkClusterNoController);
    }

    @Override
    public List<ClusterParam> getResList(Long clusterPhyId) {
        return Arrays.asList(new ClusterPhyParam(clusterPhyId));
    }

    @Override
    public HealthCheckDimensionEnum getHealthCheckDimensionEnum() {
        return HealthCheckDimensionEnum.CLUSTER;
    }

    @Override
    public Integer getDimensionCodeIfSupport(Long kafkaClusterPhyId) {
        return this.getHealthCheckDimensionEnum().getDimension();
    }

    /**
     * 检查NoController
     */
    private HealthCheckResult checkClusterNoController(Tuple<ClusterParam, BaseClusterHealthConfig> singleConfigSimpleTuple) {
        ClusterPhyParam param =(ClusterPhyParam) singleConfigSimpleTuple.getV1();
        HealthCompareValueConfig valueConfig = (HealthCompareValueConfig) singleConfigSimpleTuple.getV2();

        Result<ClusterMetrics> clusterMetricsResult = clusterMetricService.getLatestMetricsFromES(param.getClusterPhyId(), Arrays.asList(ClusterMetricVersionItems.CLUSTER_METRIC_ACTIVE_CONTROLLER_COUNT));
        if (clusterMetricsResult.failed() || !clusterMetricsResult.hasData()) {
            log.error("method=checkClusterNoController||param={}||config={}||result={}||errMsg=get metrics from es failed",
                    param, valueConfig, clusterMetricsResult);
            return null;
        }

        HealthCheckResult checkResult = new HealthCheckResult(
                HealthCheckDimensionEnum.CLUSTER.getDimension(),
                HealthCheckNameEnum.CLUSTER_NO_CONTROLLER.getConfigName(),
                param.getClusterPhyId(),
                ""
        );

        Float activeController = clusterMetricsResult.getData().getMetric(ClusterMetricVersionItems.CLUSTER_METRIC_ACTIVE_CONTROLLER_COUNT);
        if (activeController == null) {
            log.error("method=checkClusterNoController||param={}||config={}||errMsg=get metrics from es failed, activeControllerCount is null",
                    param, valueConfig);
            return null;
        }

        checkResult.setPassed(activeController.intValue() != valueConfig.getValue().intValue() ? 0: 1);

        return checkResult;
    }
}
