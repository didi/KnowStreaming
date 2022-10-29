package com.xiaojukeji.know.streaming.km.task.health;

import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.AbstractHealthCheckService;
import com.xiaojukeji.know.streaming.km.core.service.health.checkresult.HealthCheckResultService;
import com.xiaojukeji.know.streaming.km.task.metrics.AbstractAsyncMetricsDispatchTask;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class AbstractHealthCheckTask extends AbstractAsyncMetricsDispatchTask {
    private static final ILog log = LogFactory.getLog(AbstractHealthCheckTask.class);

    @Autowired
    private HealthCheckResultService healthCheckResultService;

    public abstract AbstractHealthCheckService getCheckService();

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) {
        return this.calAndUpdateHealthCheckResult(clusterPhy, triggerTimeUnitMs);
    }

    private TaskResult calAndUpdateHealthCheckResult(ClusterPhy clusterPhy, long triggerTimeUnitMs) {
        // 获取配置，<配置名，配置信息>
        Map<String, BaseClusterHealthConfig> healthConfigMap = healthCheckResultService.getClusterHealthConfig(clusterPhy.getId());

        // 检查结果
        List<HealthCheckResult> resultList = new ArrayList<>();

        // 遍历Check-Service
        List<ClusterPhyParam> paramList = this.getCheckService().getResList(clusterPhy.getId());
        if (ValidateUtils.isEmptyList(paramList)) {
            // 当前无该维度的资源，则直接设置为
            resultList.addAll(this.getNoResResult(clusterPhy.getId(), this.getCheckService(), healthConfigMap));
        }

        // 遍历资源
        for (ClusterPhyParam clusterPhyParam: paramList) {
            resultList.addAll(this.checkAndGetResult(clusterPhyParam, healthConfigMap));
        }

        try {
            healthCheckResultService.batchReplace(clusterPhy.getId(), resultList);
        } catch (Exception e) {
            log.error("class=AbstractHealthCheckTask||method=processSubTask||clusterPhyId={}||errMsg=exception!", clusterPhy.getId(), e);
        }

        // 删除10分钟之前的检查结果
        try {
            healthCheckResultService.deleteByUpdateTimeBeforeInDB(clusterPhy.getId(), new Date(triggerTimeUnitMs - 20 * 60 * 1000));
        } catch (Exception e) {
            log.error("class=AbstractHealthCheckTask||method=processSubTask||clusterPhyId={}||errMsg=exception!", clusterPhy.getId(), e);
        }

        return TaskResult.SUCCESS;
    }

    private List<HealthCheckResult> getNoResResult(Long clusterPhyId, AbstractHealthCheckService healthCheckService, Map<String, BaseClusterHealthConfig> healthConfigMap) {
        List<HealthCheckResult> resultList = new ArrayList<>();

        // 进行检查
        for (BaseClusterHealthConfig clusterHealthConfig: healthConfigMap.values()) {
            HealthCheckDimensionEnum dimensionEnum = healthCheckService.getHealthCheckDimensionEnum();
            if (!clusterHealthConfig.getCheckNameEnum().getDimensionEnum().equals(dimensionEnum)) {
                // 类型不匹配
                continue;
            }

            // 记录
            HealthCheckResult checkResult = new HealthCheckResult(
                    dimensionEnum.getDimension(),
                    clusterHealthConfig.getCheckNameEnum().getConfigName(),
                    clusterPhyId,
                    "-1"
            );
            checkResult.setPassed(Constant.YES);
            resultList.add(checkResult);
        }

        return resultList;
    }

    private List<HealthCheckResult> checkAndGetResult(ClusterPhyParam clusterPhyParam,
                                                      Map<String, BaseClusterHealthConfig> healthConfigMap) {
        List<HealthCheckResult> resultList = new ArrayList<>();

        // 进行检查
        for (BaseClusterHealthConfig clusterHealthConfig: healthConfigMap.values()) {
            HealthCheckResult healthCheckResult = this.getCheckService().checkAndGetResult(clusterPhyParam, clusterHealthConfig);
            if (healthCheckResult == null) {
                continue;
            }

            // 记录
            resultList.add(healthCheckResult);
        }

        return resultList;
    }
}
