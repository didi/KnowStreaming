package com.xiaojukeji.kafka.manager.dao.gateway;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.GatewayConfigDO;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/28
 */
public interface GatewayConfigDao {
    List<GatewayConfigDO> getByConfigType(String configType);

    GatewayConfigDO getByConfigTypeAndName(String configType, String configName);

    List<GatewayConfigDO> list();

    int insert(GatewayConfigDO gatewayConfigDO);

    int deleteById(Long id);

    int updateById(GatewayConfigDO gatewayConfigDO);

    GatewayConfigDO getById(Long id);
}