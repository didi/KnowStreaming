package com.xiaojukeji.know.streaming.km.core.service.broker.impl;

import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.BrokerSpec;
import com.xiaojukeji.know.streaming.km.common.bean.po.config.PlatformClusterConfigPO;
import com.xiaojukeji.know.streaming.km.common.enums.config.ConfigGroupEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerSpecService;
import com.xiaojukeji.know.streaming.km.core.service.config.PlatformClusterConfigService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BrokerSpecServiceImpl implements BrokerSpecService {
    @Autowired
    private PlatformClusterConfigService platformClusterConfigService;

    @Override
    public Map<Integer, BrokerSpec> getBrokerSpecMap(Long clusterPhyId) {
        //获取规格信息
        Map<String, PlatformClusterConfigPO> specMap = platformClusterConfigService.getByClusterAndGroupWithoutDefault(clusterPhyId, ConfigGroupEnum.BROKER_SPEC.name());
        if (specMap == null || specMap.isEmpty()){
            return new HashMap<>();
        }
        Map<Integer, BrokerSpec> brokerSpecMap = new HashMap<>();
        for (Map.Entry<String, PlatformClusterConfigPO> entry : specMap.entrySet()){
            if (StringUtils.isBlank(entry.getValue().getValue())){
                continue;
            }
            BrokerSpec baseData = ConvertUtil.str2ObjByJson(entry.getValue().getValue(), BrokerSpec.class);
            brokerSpecMap.put(baseData.getBrokerId(), baseData);
        }
        return brokerSpecMap;
    }
}
