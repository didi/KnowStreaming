package com.xiaojukeji.know.streaming.km.core.service.config.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.config.PlatformClusterConfigPO;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.config.PlatformClusterConfigService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.config.PlatformClusterConfigDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PlatformClusterConfigServiceImpl implements PlatformClusterConfigService {
    private static final ILog log = LogFactory.getLog(PlatformClusterConfigServiceImpl.class);

    @Autowired
    private PlatformClusterConfigDAO platformClusterConfigDAO;

    @Override
    public Result<Void> batchReplace(List<PlatformClusterConfigPO> poList, String operator) {
        try {
            platformClusterConfigDAO.batchReplace(poList);
            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=batchReplace||data={}||errMsg=exception", ConvertUtil.obj2Json(poList), e);
            return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, e.getMessage());
        }
    }

    @Override
    public List<PlatformClusterConfigPO> getByClusterAndGroup(Long clusterPhyId, String group) {
        LambdaQueryWrapper<PlatformClusterConfigPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PlatformClusterConfigPO::getClusterId, clusterPhyId);
        lambdaQueryWrapper.eq(PlatformClusterConfigPO::getValueGroup, group);

        return platformClusterConfigDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public Map<String, PlatformClusterConfigPO> getByClusterAndGroupWithoutDefault(Long clusterPhyId, String group) {
        LambdaQueryWrapper<PlatformClusterConfigPO> rowLambdaQueryWrapper = new LambdaQueryWrapper<>();
        rowLambdaQueryWrapper.eq(PlatformClusterConfigPO::getClusterId, clusterPhyId);
        rowLambdaQueryWrapper.eq(PlatformClusterConfigPO::getValueGroup, group);

        LambdaQueryWrapper<PlatformClusterConfigPO> defaultLambdaQueryWrapper = new LambdaQueryWrapper<>();
        defaultLambdaQueryWrapper.eq(PlatformClusterConfigPO::getClusterId, -1L);
        defaultLambdaQueryWrapper.eq(PlatformClusterConfigPO::getValueGroup, group);

        List<PlatformClusterConfigPO> rowPOList = platformClusterConfigDAO.selectList(rowLambdaQueryWrapper);
        List<PlatformClusterConfigPO> defaultPOList = platformClusterConfigDAO.selectList(defaultLambdaQueryWrapper);

        Map<String, PlatformClusterConfigPO> configPOMap = rowPOList.stream().collect(Collectors.toMap(PlatformClusterConfigPO::getValueName, Function.identity()));
        for (PlatformClusterConfigPO defaultPO: defaultPOList) {
            // 修改集群ID
            defaultPO.setClusterId(clusterPhyId);

            // 存储到Map中
            configPOMap.putIfAbsent(defaultPO.getValueName(), defaultPO);
        }

        return configPOMap;
    }

    @Override
    public Map<Long, Map<String, PlatformClusterConfigPO>> listByGroup(String groupName) {
        LambdaQueryWrapper<PlatformClusterConfigPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PlatformClusterConfigPO::getValueGroup, groupName);

        List<PlatformClusterConfigPO> poList = platformClusterConfigDAO.selectList(lambdaQueryWrapper);

        Map<Long, Map<String, PlatformClusterConfigPO>> poMap = new HashMap<>();
        poList.forEach(elem -> {
            poMap.putIfAbsent(elem.getClusterId(), new HashMap<>());
            poMap.get(elem.getClusterId()).put(elem.getValueName(), elem);
        });

        return poMap;
    }
}
