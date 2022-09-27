package com.xiaojukeji.know.streaming.km.task.health;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
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
import com.xiaojukeji.know.streaming.km.core.service.health.checker.cluster.HealthCheckClusterService;
import com.xiaojukeji.know.streaming.km.core.service.health.checkresult.HealthCheckResultService;
import com.xiaojukeji.know.streaming.km.task.metrics.AbstractAsyncMetricsDispatchTask;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Task(name = "ClusterHealthCheckTask",
        description = "Cluster健康检查",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class ClusterHealthCheckTask extends AbstractAsyncMetricsDispatchTask {
    private static final ILog log = LogFactory.getLog(ClusterHealthCheckTask.class);

    @Autowired
    private HealthCheckResultService healthCheckResultService;

    @Autowired
    private HealthCheckClusterService healthCheckClusterService;

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
        List<ClusterPhyParam> paramList = healthCheckClusterService.getResList(clusterPhy.getId());
        if (ValidateUtils.isEmptyList(paramList)) {
            // 当前无该维度的资源，则直接设置为
            resultList.addAll(this.getNoResResult(clusterPhy.getId(), healthCheckClusterService, healthConfigMap));
        }

        // 遍历资源
        for (ClusterPhyParam clusterPhyParam: paramList) {
            resultList.addAll(this.checkAndGetResult(healthCheckClusterService, clusterPhyParam, healthConfigMap));
        }

        for (HealthCheckResult checkResult: resultList) {
            try {
                healthCheckResultService.replace(checkResult);
            } catch (Exception e) {
                log.error("class=ClusterHealthCheckTask||method=processSubTask||clusterPhyId={}||checkResult={}||errMsg=exception!", clusterPhy.getId(), checkResult, e);
            }
        }

        // 删除10分钟之前的检查结果
        try {
            healthCheckResultService.deleteByUpdateTimeBeforeInDB(clusterPhy.getId(), new Date(triggerTimeUnitMs - 10 * 60 * 1000));
        } catch (Exception e) {
            log.error("class=ClusterHealthCheckTask||method=processSubTask||clusterPhyId={}||errMsg=exception!", clusterPhy.getId(), e);
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

    private List<HealthCheckResult> checkAndGetResult(AbstractHealthCheckService healthCheckService,
                                                      ClusterPhyParam clusterPhyParam,
                                                      Map<String, BaseClusterHealthConfig> healthConfigMap) {
        List<HealthCheckResult> resultList = new ArrayList<>();

        // 进行检查
        for (BaseClusterHealthConfig clusterHealthConfig: healthConfigMap.values()) {
            HealthCheckResult healthCheckResult = healthCheckService.checkAndGetResult(clusterPhyParam, clusterHealthConfig);
            if (healthCheckResult == null) {
                continue;
            }

            // 记录
            resultList.add(healthCheckResult);
        }

        return resultList;
    }
}
