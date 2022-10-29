package com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.BaseClusterConfigValue;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import lombok.Data;

/**
 * 单集群的配置
 */
@Data
public class BaseClusterHealthConfig extends BaseClusterConfigValue {
    /**
     * 健康检查名称
     */
    protected HealthCheckNameEnum checkNameEnum;
}
