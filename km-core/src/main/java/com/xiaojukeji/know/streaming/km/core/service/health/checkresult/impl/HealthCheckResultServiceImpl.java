package com.xiaojukeji.know.streaming.km.core.service.health.checkresult.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.Lists;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckAggResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.po.config.PlatformClusterConfigPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.health.HealthCheckResultPO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.config.ConfigGroupEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.cache.DataBaseDataLocalCache;
import com.xiaojukeji.know.streaming.km.core.service.config.PlatformClusterConfigService;
import com.xiaojukeji.know.streaming.km.core.service.health.checkresult.HealthCheckResultService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.health.HealthCheckResultDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HealthCheckResultServiceImpl implements HealthCheckResultService {
    private static final ILog LOGGER = LogFactory.getLog(HealthCheckResultServiceImpl.class);

    @Autowired
    private HealthCheckResultDAO healthCheckResultDAO;

    @Autowired
    private PlatformClusterConfigService platformClusterConfigService;

    @Override
    public List<HealthCheckAggResult> getHealthCheckAggResult(Long clusterPhyId, HealthCheckDimensionEnum dimensionEnum, String resNme) {
        List<HealthCheckResultPO> poList = this.listCheckResultFromCache(clusterPhyId, dimensionEnum, resNme);

        return this.convert2HealthCheckAggResultList(poList, dimensionEnum.getDimension());
    }

    @Override
    public List<HealthCheckAggResult> getHealthCheckAggResult(Long clusterPhyId, HealthCheckDimensionEnum dimensionEnum) {
        List<HealthCheckResultPO> poList = this.listCheckResultFromCache(clusterPhyId, dimensionEnum);

        return this.convert2HealthCheckAggResultList(poList, dimensionEnum.getDimension());
    }

    @Override
    public List<HealthCheckResultPO> listAll() {
        return healthCheckResultDAO.selectList(null);
    }

    @Override
    public List<HealthCheckResultPO> listCheckResult(Long clusterPhyId) {
        LambdaQueryWrapper<HealthCheckResultPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HealthCheckResultPO::getClusterPhyId, clusterPhyId);

        return healthCheckResultDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<HealthCheckResultPO> listCheckResult(Long clusterPhyId, Integer resDimension) {
        LambdaQueryWrapper<HealthCheckResultPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HealthCheckResultPO::getDimension, resDimension);
        lambdaQueryWrapper.eq(HealthCheckResultPO::getClusterPhyId, clusterPhyId);

        return healthCheckResultDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<HealthCheckResultPO> listCheckResult(Long clusterPhyId, Integer resDimension, String resNme) {
        LambdaQueryWrapper<HealthCheckResultPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HealthCheckResultPO::getDimension, resDimension);
        lambdaQueryWrapper.eq(HealthCheckResultPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(HealthCheckResultPO::getResName, resNme);

        return healthCheckResultDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<HealthCheckResultPO> listCheckResultFromCache(Long clusterPhyId, HealthCheckDimensionEnum dimensionEnum) {
        Map<String, List<HealthCheckResultPO>> poMap = DataBaseDataLocalCache.getHealthCheckResults(clusterPhyId, dimensionEnum);
        if (poMap != null) {
            return poMap.values().stream().collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
        }

        return new ArrayList<>();
    }

    @Override
    public List<HealthCheckResultPO> listCheckResultFromCache(Long clusterPhyId, HealthCheckDimensionEnum dimensionEnum, String resNme) {
        Map<String, List<HealthCheckResultPO>> poMap = DataBaseDataLocalCache.getHealthCheckResults(clusterPhyId, dimensionEnum);
        if (poMap != null) {
            return poMap.getOrDefault(resNme, new ArrayList<>());
        }

        return new ArrayList<>();
    }

    @Override
    public Map<String, BaseClusterHealthConfig> getClusterHealthConfig(Long clusterPhyId) {
        Map<String, PlatformClusterConfigPO> configPOMap = platformClusterConfigService.getByClusterAndGroupWithoutDefault(clusterPhyId, ConfigGroupEnum.HEALTH.name());

        Map<String, BaseClusterHealthConfig> configMap = new HashMap<>();
        for (PlatformClusterConfigPO po: configPOMap.values()) {
            try {
                HealthCheckNameEnum nameEnum = HealthCheckNameEnum.getByName(po.getValueName());
                if (HealthCheckNameEnum.UNKNOWN.equals(nameEnum)) {
                    LOGGER.warn("method=getClusterHealthConfig||config={}||errMsg=config name illegal", po);
                    continue;
                }

                BaseClusterHealthConfig healthConfig = (BaseClusterHealthConfig) ConvertUtil.str2ObjByJson(po.getValue(), nameEnum.getConfigClazz());
                healthConfig.setCheckNameEnum(nameEnum);
                healthConfig.setClusterPhyId(clusterPhyId);
                configMap.put(po.getValueName(), healthConfig);
            } catch (Exception e) {
                LOGGER.error("method=getClusterHealthConfig||config={}||errMsg=exception!", po, e);
            }
        }
        return configMap;
    }

    @Override
    public void batchReplace(Long clusterPhyId, Integer dimension, List<HealthCheckResult> healthCheckResults) {
        List<List<HealthCheckResult>> healthCheckResultPartitions = Lists.partition(healthCheckResults, Constant.PER_BATCH_MAX_VALUE);
        for (List<HealthCheckResult> checkResultPartition : healthCheckResultPartitions) {
            List<HealthCheckResultPO> healthCheckResultPos = ConvertUtil.list2List(checkResultPartition, HealthCheckResultPO.class);
            try {
                healthCheckResultDAO.batchReplace(healthCheckResultPos);
            } catch (Exception e) {
                LOGGER.error("method=batchReplace||clusterPhyId={}||checkResultList={}||errMsg=exception!", clusterPhyId, healthCheckResultPos, e);
            }
        }
    }

    private List<HealthCheckAggResult> convert2HealthCheckAggResultList(List<HealthCheckResultPO> poList, Integer dimensionCode) {
        Map<String /*检查名*/, List<HealthCheckResultPO> /*检查结果列表*/> groupByCheckNamePOMap = new HashMap<>();
        for (HealthCheckResultPO po: poList) {
            groupByCheckNamePOMap.putIfAbsent(po.getConfigName(), new ArrayList<>());
            groupByCheckNamePOMap.get(po.getConfigName()).add(po);
        }

        List<HealthCheckAggResult> stateList = new ArrayList<>();
        for (HealthCheckNameEnum nameEnum: HealthCheckNameEnum.getByDimensionCode(dimensionCode)) {
            stateList.add(new HealthCheckAggResult(nameEnum, groupByCheckNamePOMap.getOrDefault(nameEnum.getConfigName(), new ArrayList<>())));
        }

        return stateList;
    }
}
