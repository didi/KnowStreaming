package com.xiaojukeji.kafka.manager.service.service.gateway;

import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.*;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.GatewayConfigDO;

public interface GatewayConfigService {
    KafkaBootstrapServerConfig getKafkaBootstrapServersConfig(Long requestVersion);

    RequestQueueConfig getRequestQueueConfig(Long requestVersion);

    AppRateConfig getAppRateConfig(Long requestVersion);

    IpRateConfig getIpRateConfig(Long requestVersion);

    SpRateConfig getSpRateConfig(Long requestVersion);

    GatewayConfigDO getByTypeAndName(String configType, String configName);
}
