package com.xiaojukeji.kafka.manager.service.service.gateway;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.*;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.GatewayConfigDO;

import java.util.List;

public interface GatewayConfigService {
    /**
     * 获取集群服务地址
     * @param requestVersion 请求的版本
     * @return
     */
    KafkaBootstrapServerConfig getKafkaBootstrapServersConfig(Long requestVersion);

    /**
     * 获取服务发现的请求队列的配置
     * @param requestVersion 请求的版本
     * @return
     */
    RequestQueueConfig getRequestQueueConfig(Long requestVersion);

    /**
     * 获取服务发现的App请求速度的配置
     * @param requestVersion 请求的版本
     * @return
     */
    AppRateConfig getAppRateConfig(Long requestVersion);

    /**
     * 获取服务发现的IP请求速度的配置
     * @param requestVersion 请求的版本
     * @return
     */
    IpRateConfig getIpRateConfig(Long requestVersion);

    /**
     * 获取服务发现的具体IP或者应用纬度的限速配置
     * @param requestVersion 请求的版本
     * @return
     */
    SpRateConfig getSpRateConfig(Long requestVersion);

    /**
     * 获取配置
     * @param configType 配置类型
     * @param configName 配置名称
     * @return
     */
    GatewayConfigDO getByTypeAndName(String configType, String configName);

    /**
     * 获取配置
     * @return
     */
    List<GatewayConfigDO> list();

    /**
     * 新建配置
     * @param gatewayConfigDO 配置信息
     * @return
     */
    Result insert(GatewayConfigDO gatewayConfigDO);

    /**
     * 删除配置
     * @param id 配置ID
     * @return
     */
    Result deleteById(Long id);

    /**
     * 更新配置
     * @param gatewayConfigDO 配置信息
     * @return
     */
    Result updateById(GatewayConfigDO gatewayConfigDO);

    /**
     * 获取配置
     * @param id 配置ID
     * @return
     */
    GatewayConfigDO getById(Long id);
}
