package com.xiaojukeji.know.streaming.km.core.service.health.checkresult.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckAggResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckResult;
import com.xiaojukeji.know.streaming.km.common.bean.po.config.PlatformClusterConfigPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectClusterPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.health.HealthCheckResultPO;
import com.xiaojukeji.know.streaming.km.common.enums.config.ConfigGroupEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.persistence.cache.DataBaseDataLocalCache;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.config.PlatformClusterConfigService;
import com.xiaojukeji.know.streaming.km.core.service.health.checkresult.HealthCheckResultService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.connect.ConnectClusterDAO;
import com.xiaojukeji.know.streaming.km.persistence.mysql.connect.ConnectorDAO;
import com.xiaojukeji.know.streaming.km.persistence.mysql.health.HealthCheckResultDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum.CONNECTOR;
import static com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum.MIRROR_MAKER;

@Service
public class HealthCheckResultServiceImpl implements HealthCheckResultService {
    private static final ILog LOGGER = LogFactory.getLog(HealthCheckResultServiceImpl.class);

    @Autowired
    private HealthCheckResultDAO healthCheckResultDAO;

    @Autowired
    private ConnectClusterDAO connectClusterDAO;

    @Autowired
    private ConnectorDAO connectorDAO;

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
    public List<HealthCheckResultPO> getConnectorHealthCheckResult(Long clusterPhyId) {
        List<HealthCheckResultPO> resultPOList = new ArrayList<>();

        //查找connect集群
        LambdaQueryWrapper<ConnectClusterPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectClusterPO::getKafkaClusterPhyId, clusterPhyId);
        List<Long> connectClusterIdList = connectClusterDAO.selectList(lambdaQueryWrapper).stream().map(elem -> elem.getId()).collect(Collectors.toList());
        if (ValidateUtils.isEmptyList(connectClusterIdList)) {
            return resultPOList;
        }

        LambdaQueryWrapper<HealthCheckResultPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthCheckResultPO::getDimension, CONNECTOR.getDimension());
        wrapper.in(HealthCheckResultPO::getClusterPhyId, connectClusterIdList);
        resultPOList = healthCheckResultDAO.selectList(wrapper);
        return resultPOList;
    }

    @Override
    public List<HealthCheckResultPO> getMirrorMakerHealthCheckResult(Long clusterPhyId) {
        List<HealthCheckResultPO> resultPOList = new ArrayList<>();

        //查找connect集群
        LambdaQueryWrapper<ConnectClusterPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectClusterPO::getKafkaClusterPhyId, clusterPhyId);
        List<Long> connectClusterIdList = connectClusterDAO.selectList(lambdaQueryWrapper).stream().map(elem -> elem.getId()).collect(Collectors.toList());
        if (ValidateUtils.isEmptyList(connectClusterIdList)) {
            return resultPOList;
        }

        LambdaQueryWrapper<HealthCheckResultPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthCheckResultPO::getDimension, MIRROR_MAKER.getDimension());
        wrapper.in(HealthCheckResultPO::getClusterPhyId, connectClusterIdList);
        resultPOList = healthCheckResultDAO.selectList(wrapper);
        return resultPOList;
    }

    @Override
    public void batchReplace(Long clusterPhyId, Integer dimension, List<HealthCheckResult> healthCheckResults) {
        List<HealthCheckResultPO> inDBList = this.listCheckResult(clusterPhyId, dimension);

        // list 转 map
        Map<String, HealthCheckResultPO> inDBMap = new HashMap<>(inDBList.size());
        inDBList.forEach(elem -> inDBMap.put(elem.getConfigName() + elem.getResName(), elem));

        for (HealthCheckResult checkResult: healthCheckResults) {
            HealthCheckResultPO inDB = inDBMap.remove(checkResult.getConfigName() + checkResult.getResName());

            try {
                HealthCheckResultPO newPO = ConvertUtil.obj2Obj(checkResult, HealthCheckResultPO.class);
                if (inDB == null) {
                    healthCheckResultDAO.insert(newPO);
                } else {
                    newPO.setId(inDB.getId());
                    newPO.setUpdateTime(new Date());
                    healthCheckResultDAO.updateById(newPO);
                }
            } catch (DuplicateKeyException dke) {
                // ignore
            }
        }

        inDBMap.values().forEach(elem -> {
            if (System.currentTimeMillis() - elem.getUpdateTime().getTime() <= 1200000) {
                // 20分钟之内的数据，不进行删除
                return;
            }

            healthCheckResultDAO.deleteById(elem.getId());
        });
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
