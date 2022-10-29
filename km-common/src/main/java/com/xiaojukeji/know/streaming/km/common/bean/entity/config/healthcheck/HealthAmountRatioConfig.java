package com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck;

import lombok.Data;

/**
 * @author wyb
 * @date 2022/10/26
 */
@Data
public class HealthAmountRatioConfig extends BaseClusterHealthConfig {
    /**
     * 总数
     */
    private Integer amount;
    /**
     * 比例
     */
    private Double ratio;
}
