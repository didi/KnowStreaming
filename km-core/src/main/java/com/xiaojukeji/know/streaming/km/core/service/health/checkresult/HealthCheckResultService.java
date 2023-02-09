package com.xiaojukeji.know.streaming.km.core.service.health.checkresult;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckAggResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.po.health.HealthCheckResultPO;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;

import java.util.List;
import java.util.Map;

public interface HealthCheckResultService {
    List<HealthCheckAggResult> getHealthCheckAggResult(Long clusterPhyId, HealthCheckDimensionEnum dimensionEnum, String resNme);
    List<HealthCheckAggResult> getHealthCheckAggResult(Long clusterPhyId, HealthCheckDimensionEnum dimensionEnum);

    List<HealthCheckResultPO> listAll();
    List<HealthCheckResultPO> listCheckResult(Long clusterPhyId);
    List<HealthCheckResultPO> listCheckResult(Long clusterPhyId, Integer resDimension);
    List<HealthCheckResultPO> listCheckResult(Long clusterPhyId, Integer resDimension, String resNme);

    List<HealthCheckResultPO> listCheckResultFromCache(Long clusterPhyId, HealthCheckDimensionEnum dimensionEnum);
    List<HealthCheckResultPO> listCheckResultFromCache(Long clusterPhyId, HealthCheckDimensionEnum dimensionEnum, String resNme);

    Map<String, BaseClusterHealthConfig> getClusterHealthConfig(Long clusterPhyId);

    void batchReplace(Long clusterPhyId, Integer dimension, List<HealthCheckResult> healthCheckResults);

    List<HealthCheckResultPO> getConnectorHealthCheckResult(Long clusterPhyId);

    List<HealthCheckResultPO> getMirrorMakerHealthCheckResult(Long clusterPhyId);
}
