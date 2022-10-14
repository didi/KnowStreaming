package com.xiaojukeji.know.streaming.km.core.service.health.checkresult.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.po.config.PlatformClusterConfigPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.health.HealthCheckResultPO;
import com.xiaojukeji.know.streaming.km.common.enums.config.ConfigGroupEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.config.PlatformClusterConfigService;
import com.xiaojukeji.know.streaming.km.core.service.health.checkresult.HealthCheckResultService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.health.HealthCheckResultDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HealthCheckResultServiceImpl implements HealthCheckResultService {
    private static final ILog log = LogFactory.getLog(HealthCheckResultServiceImpl.class);

    @Autowired
    private HealthCheckResultDAO healthCheckResultDAO;

    @Autowired
    private PlatformClusterConfigService platformClusterConfigService;

    @Override
    public int replace(HealthCheckResult healthCheckResult) {
        return healthCheckResultDAO.replace(ConvertUtil.obj2Obj(healthCheckResult, HealthCheckResultPO.class));
    }

    @Override
    public int deleteByUpdateTimeBeforeInDB(Long clusterPhyId, Date beforeTime) {
        LambdaQueryWrapper<HealthCheckResultPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HealthCheckResultPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.le(HealthCheckResultPO::getUpdateTime, beforeTime);
        return healthCheckResultDAO.delete(lambdaQueryWrapper);
    }

    @Override
    public List<HealthCheckResultPO> getClusterHealthCheckResult(Long clusterPhyId) {
        LambdaQueryWrapper<HealthCheckResultPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HealthCheckResultPO::getClusterPhyId, clusterPhyId);
        return healthCheckResultDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<HealthCheckResultPO> getClusterResourcesHealthCheckResult(Long clusterPhyId, Integer resDimension) {
        LambdaQueryWrapper<HealthCheckResultPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HealthCheckResultPO::getDimension, resDimension);
        lambdaQueryWrapper.eq(HealthCheckResultPO::getClusterPhyId, clusterPhyId);
        return healthCheckResultDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<HealthCheckResultPO> getResHealthCheckResult(Long clusterPhyId, Integer resDimension, String resNme) {
        LambdaQueryWrapper<HealthCheckResultPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HealthCheckResultPO::getDimension, resDimension);
        lambdaQueryWrapper.eq(HealthCheckResultPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(HealthCheckResultPO::getResName, resNme);
        return healthCheckResultDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public Map<String, BaseClusterHealthConfig> getClusterHealthConfig(Long clusterPhyId) {
        Map<String, PlatformClusterConfigPO> configPOMap = platformClusterConfigService.getByClusterAndGroupWithoutDefault(clusterPhyId, ConfigGroupEnum.HEALTH.name());

        Map<String, BaseClusterHealthConfig> configMap = new HashMap<>();
        for (PlatformClusterConfigPO po: configPOMap.values()) {
            try {
                HealthCheckNameEnum nameEnum = HealthCheckNameEnum.getByName(po.getValueName());
                if (HealthCheckNameEnum.UNKNOWN.equals(nameEnum)) {
                    log.warn("method=getClusterHealthConfig||config={}||errMsg=config name illegal", po);
                    continue;
                }

                BaseClusterHealthConfig healthConfig = (BaseClusterHealthConfig) ConvertUtil.str2ObjByJson(po.getValue(), nameEnum.getConfigClazz());
                healthConfig.setCheckNameEnum(nameEnum);
                healthConfig.setClusterPhyId(clusterPhyId);
                configMap.put(po.getValueName(), healthConfig);
            } catch (Exception e) {
                log.error("method=getClusterHealthConfig||config={}||errMsg=exception!", po, e);
            }
        }
        return configMap;
    }

    @Override
    public int batchReplace(List<HealthCheckResult> healthCheckResults) {
        List<HealthCheckResultPO> healthCheckResultPos = healthCheckResults.stream().map(healthCheckResult -> ConvertUtil.obj2Obj(healthCheckResult,
                HealthCheckResultPO.class)).collect(Collectors.toCollection(() -> new ArrayList<>(healthCheckResults.size())));
        return healthCheckResultDAO.batchReplace(healthCheckResultPos);
    }
}
