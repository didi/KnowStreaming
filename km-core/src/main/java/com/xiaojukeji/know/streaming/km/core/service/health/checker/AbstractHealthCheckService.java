package com.xiaojukeji.know.streaming.km.core.service.health.checker;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterParam;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Data
public abstract class AbstractHealthCheckService {
    private static final ILog log = LogFactory.getLog(AbstractHealthCheckService.class);

    protected static final Map<
            String,
            Function<Tuple<ClusterParam, BaseClusterHealthConfig>, HealthCheckResult>
            > functionMap = new ConcurrentHashMap<>();

    public abstract List<ClusterParam> getResList(Long clusterId);

    public abstract HealthCheckDimensionEnum getHealthCheckDimensionEnum();

    public abstract Integer getDimensionCodeIfSupport(Long kafkaClusterPhyId);

    public HealthCheckResult checkAndGetResult(ClusterParam clusterParam, BaseClusterHealthConfig clusterHealthConfig) {
        if (ValidateUtils.anyNull(clusterParam, clusterHealthConfig)) {
            return null;
        }

        HealthCheckDimensionEnum dimensionEnum = this.getHealthCheckDimensionEnum();
        if (!clusterHealthConfig.getCheckNameEnum().getDimensionEnum().equals(dimensionEnum)) {
            // 类型不匹配
            return null;
        }

        Function<Tuple<ClusterParam, BaseClusterHealthConfig>, HealthCheckResult> function = functionMap.get(clusterHealthConfig.getCheckNameEnum().getConfigName());
        if (function == null) {
            return null;
        }

        try {
            return function.apply(new Tuple<>(clusterParam, clusterHealthConfig));
        } catch (Exception e) {
            log.error(
                    "method=checkAndGetResult||clusterParam={}||clusterHealthConfig={}||errMsg=exception!",
                    clusterParam, clusterHealthConfig, e
            );
        }

        return null;
    }
}
