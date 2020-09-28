package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ConfigDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.ConfigVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/3/19
 */
public class ConfigConverter {
    public static List<ConfigVO> convert2ConfigVOList(List<ConfigDO> doList) {
        if (ValidateUtils.isEmptyList(doList)) {
            return new ArrayList<>();
        }

        List<ConfigVO> voList = new ArrayList<>();
        for (ConfigDO configDO: doList) {
            ConfigVO vo = new ConfigVO();
            vo.setId(configDO.getId());
            vo.setConfigKey(configDO.getConfigKey());
            vo.setConfigValue(configDO.getConfigValue());
            vo.setConfigDescription(configDO.getConfigDescription());
            vo.setGmtCreate(configDO.getGmtCreate().getTime());
            vo.setGmtModify(configDO.getGmtModify().getTime());
            voList.add(vo);
        }
        return voList;
    }
}