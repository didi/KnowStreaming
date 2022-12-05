package com.xiaojukeji.know.streaming.km.task.kafka.health;

import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.collector.service.CollectThreadPoolService;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterParam;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.AbstractHealthCheckService;
import com.xiaojukeji.know.streaming.km.core.service.health.checkresult.HealthCheckResultService;
import com.xiaojukeji.know.streaming.km.task.kafka.metrics.AbstractAsyncMetricsDispatchTask;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public abstract class AbstractHealthCheckTask extends AbstractAsyncMetricsDispatchTask {
    private static final ILog LOGGER = LogFactory.getLog(AbstractHealthCheckTask.class);

    @Autowired
    private HealthCheckResultService healthCheckResultService;

    @Autowired
    private CollectThreadPoolService collectThreadPoolService;

    public abstract AbstractHealthCheckService getCheckService();

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) {
        return this.calAndUpdateHealthCheckResult(clusterPhy, triggerTimeUnitMs);
    }

    private TaskResult calAndUpdateHealthCheckResult(ClusterPhy clusterPhy, long triggerTimeUnitMs) {
        // 获取配置，<配置名，配置信息>
        Map<String, BaseClusterHealthConfig> healthConfigMap = healthCheckResultService.getClusterHealthConfig(clusterPhy.getId());

        // 获取资源列表
        List<ClusterParam> paramList = this.getCheckService().getResList(clusterPhy.getId());

        // 检查结果
        List<HealthCheckResult> checkResultList = Collections.synchronizedList(new ArrayList<>());
        if (ValidateUtils.isEmptyList(paramList)) {
            // 当前无该维度的资源，则直接设置为
            checkResultList.addAll(this.getNoResResult(clusterPhy.getId(), this.getCheckService(), healthConfigMap));
        }

        // 获取合适的线程池
        FutureWaitUtil<Void> futureWaitUtil = collectThreadPoolService.selectSuitableFutureUtil(clusterPhy.getId() * 100 + this.getCheckService().getHealthCheckDimensionEnum().getDimension());

        // 遍历资源
        for (ClusterParam clusterParam: paramList) {
            futureWaitUtil.runnableTask(
                    String.format("class=%s||method=calAndUpdateHealthCheckResult||clusterId=%d", this.getCheckService().getClass().getSimpleName(), clusterPhy.getId()),
                    30000,
                    () -> checkResultList.addAll(this.checkAndGetResult(clusterParam, healthConfigMap))
            );
        }

        futureWaitUtil.waitExecute(30000);

        try {
            healthCheckResultService.batchReplace(clusterPhy.getId(), this.getCheckService().getHealthCheckDimensionEnum().getDimension(), checkResultList);
        } catch (Exception e) {
            LOGGER.error(
                    "extendClass={}||method=calAndUpdateHealthCheckResult||clusterPhyId={}||errMsg=exception!",
                    this.getCheckService().getClass().getSimpleName(), clusterPhy.getId(), e
            );
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

    private List<HealthCheckResult> checkAndGetResult(ClusterParam clusterParam,
                                                      Map<String, BaseClusterHealthConfig> healthConfigMap) {
        List<HealthCheckResult> resultList = new ArrayList<>();

        // 进行检查
        for (BaseClusterHealthConfig clusterHealthConfig: healthConfigMap.values()) {
            HealthCheckResult healthCheckResult = this.getCheckService().checkAndGetResult(clusterParam, clusterHealthConfig);
            if (healthCheckResult == null) {
                continue;
            }

            // 记录
            resultList.add(healthCheckResult);
        }

        return resultList;
    }
}
