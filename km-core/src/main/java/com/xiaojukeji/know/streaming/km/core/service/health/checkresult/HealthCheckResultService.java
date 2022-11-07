package com.xiaojukeji.know.streaming.km.core.service.health.checkresult;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.po.health.HealthCheckResultPO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HealthCheckResultService {
    int replace(HealthCheckResult healthCheckResult);

    int deleteByUpdateTimeBeforeInDB(Long clusterPhyId, Date beforeTime);

    List<HealthCheckResultPO> getClusterHealthCheckResult(Long clusterPhyId);

    List<HealthCheckResultPO> getClusterResourcesHealthCheckResult(Long clusterPhyId, Integer resDimension);

    List<HealthCheckResultPO> getResHealthCheckResult(Long clusterPhyId, Integer dimension, String resNme);

    Map<String, BaseClusterHealthConfig> getClusterHealthConfig(Long clusterPhyId);

    void batchReplace(Long clusterPhyId, List<HealthCheckResult> healthCheckResults, int dimension);
}
