package com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck;

import lombok.Data;

/**
 * 单集群配置，比较大小
 */
@Data
public class HealthCompareValueConfig extends BaseClusterHealthConfig {
    /**
     * 比较值
     */
    private Double value = 10.0;
}
