package com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck;

import lombok.Data;

/**
 * 最近N分钟被检测到M次
 */
@Data
public class HealthDetectedInLatestMinutesConfig extends BaseClusterHealthConfig {
    /**
     * 最近多少时间
     */
    private Integer latestMinutes = 10;

    /**
     * 被检测到的次数
     */
    private Integer detectedTimes = 8;
}
