package com.xiaojukeji.know.streaming.km.core.service.health.checker;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
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
            Function<Tuple<ClusterPhyParam, BaseClusterHealthConfig>, HealthCheckResult>
            > functionMap = new ConcurrentHashMap<>();

    public abstract List<ClusterPhyParam> getResList(Long clusterPhyId);

    public abstract HealthCheckDimensionEnum getHealthCheckDimensionEnum();

    public HealthCheckResult checkAndGetResult(ClusterPhyParam clusterPhyParam, BaseClusterHealthConfig clusterHealthConfig) {
        if (ValidateUtils.anyNull(clusterPhyParam.getClusterPhyId(), clusterPhyParam, clusterHealthConfig)) {
            return null;
        }

        HealthCheckDimensionEnum dimensionEnum = this.getHealthCheckDimensionEnum();
        if (!clusterHealthConfig.getCheckNameEnum().getDimensionEnum().equals(dimensionEnum)) {
            // 类型不匹配
            return null;
        }

        Function<Tuple<ClusterPhyParam, BaseClusterHealthConfig>, HealthCheckResult> function = functionMap.get(clusterHealthConfig.getCheckNameEnum().getConfigName());
        if (function == null) {
            return null;
        }

        try {
            return function.apply(new Tuple<>(clusterPhyParam, clusterHealthConfig));
        } catch (Exception e) {
            log.error("method=checkAndGetResult||clusterPhyParam={}||clusterHealthConfig={}||errMsg=exception!",
                    clusterPhyParam, clusterHealthConfig, e);
        }

        return null;
    }
}
