package com.xiaojukeji.know.streaming.km.common.converter;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthScoreResult;
import com.xiaojukeji.know.streaming.km.common.bean.vo.health.HealthCheckConfigVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.health.HealthScoreBaseResultVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.health.HealthScoreResultDetailVO;
import com.xiaojukeji.know.streaming.km.common.enums.config.ConfigGroupEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;

import java.util.ArrayList;
import java.util.List;

public class HealthScoreVOConverter {
    private HealthScoreVOConverter() {
    }

    public static List<HealthScoreResultDetailVO> convert2HealthScoreResultDetailVOList(List<HealthScoreResult> healthScoreResultList) {
        List<HealthScoreResultDetailVO> voList = new ArrayList<>();
        for (HealthScoreResult healthScoreResult: healthScoreResultList) {
            HealthScoreResultDetailVO vo = new HealthScoreResultDetailVO();
            vo.setDimension(healthScoreResult.getCheckNameEnum().getDimensionEnum().getDimension());
            vo.setDimensionName(healthScoreResult.getCheckNameEnum().getDimensionEnum().getMessage());
            vo.setDimensionDisplayName(healthScoreResult.getCheckNameEnum().getDimensionEnum().getDimensionDisplayName());
            vo.setConfigName(healthScoreResult.getCheckNameEnum().getConfigName());
            vo.setConfigItem(healthScoreResult.getCheckNameEnum().getConfigItem());
            vo.setConfigDesc(healthScoreResult.getCheckNameEnum().getConfigDesc());
            vo.setPassed(healthScoreResult.getPassed());
            vo.setCheckConfig(convert2HealthCheckConfigVO(ConfigGroupEnum.HEALTH.name(), healthScoreResult.getBaseConfig()));

            vo.setNotPassedResNameList(healthScoreResult.getNotPassedResNameList());
            vo.setCreateTime(healthScoreResult.getCreateTime());
            vo.setUpdateTime(healthScoreResult.getUpdateTime());
            voList.add(vo);
        }
        return voList;
    }

    public static List<HealthScoreBaseResultVO> convert2HealthScoreBaseResultVOList(List<HealthScoreResult> healthScoreResultList) {
        List<HealthScoreBaseResultVO> voList = new ArrayList<>();
        for (HealthScoreResult healthScoreResult: healthScoreResultList) {
            HealthScoreBaseResultVO vo = new HealthScoreBaseResultVO();
            vo.setDimension(healthScoreResult.getCheckNameEnum().getDimensionEnum().getDimension());
            vo.setDimensionName(healthScoreResult.getCheckNameEnum().getDimensionEnum().getMessage());
            vo.setConfigName(healthScoreResult.getCheckNameEnum().getConfigName());
            vo.setConfigDesc(healthScoreResult.getCheckNameEnum().getConfigDesc());
            vo.setPassed(healthScoreResult.getPassed());
            vo.setCheckConfig(convert2HealthCheckConfigVO(ConfigGroupEnum.HEALTH.name(), healthScoreResult.getBaseConfig()));
            vo.setCreateTime(healthScoreResult.getCreateTime());
            vo.setUpdateTime(healthScoreResult.getUpdateTime());
            voList.add(vo);
        }
        return voList;
    }

    public static List<HealthCheckConfigVO> convert2HealthCheckConfigVOList(String groupName, List<BaseClusterHealthConfig> configList) {
        List<HealthCheckConfigVO> voList = new ArrayList<>();
        for (BaseClusterHealthConfig config: configList) {
            voList.add(convert2HealthCheckConfigVO(groupName, config));
        }
        return voList;
    }

    public static HealthCheckConfigVO convert2HealthCheckConfigVO(String groupName, BaseClusterHealthConfig config) {
        HealthCheckConfigVO vo = new HealthCheckConfigVO();
        vo.setDimensionCode(config.getCheckNameEnum().getDimensionEnum().getDimension());
        vo.setDimensionDisplayName(config.getCheckNameEnum().getDimensionEnum().getDimensionDisplayName());
        vo.setDimensionName(config.getCheckNameEnum().getDimensionEnum().name());
        vo.setConfigGroup(groupName);
        vo.setConfigName(config.getCheckNameEnum().getConfigName());
        vo.setConfigItem(config.getCheckNameEnum().getConfigItem());
        vo.setConfigDesc(config.getCheckNameEnum().getConfigDesc());
        vo.setValue(ConvertUtil.obj2Json(config));
        return vo;
    }
}
