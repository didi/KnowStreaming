package com.xiaojukeji.kafka.manager.task.config;

import com.xiaojukeji.kafka.manager.common.constant.Constant;

/**
 * @author zengqiao
 * @date 20/9/18
 */
public class RegionCapacityConfig {
    private Long clusterId;

    private Integer duration;

    private Long latestTimeUnitMs;

    private Long maxCapacityUnitB;

    public Long getClusterId() {
        if (this.clusterId == null) {
            return Constant.INVALID_CODE.longValue();
        }
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getDuration() {
        if (this.duration == null) {
            return 10;
        }
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Long getLatestTimeUnitMs() {
        if (this.latestTimeUnitMs == null) {
            return 7 * 24 * 60 * 60 * 1000L;
        }
        return latestTimeUnitMs;
    }

    public void setLatestTimeUnitMs(Long latestTimeUnitMs) {
        this.latestTimeUnitMs = latestTimeUnitMs;
    }

    public Long getMaxCapacityUnitB() {
        if (this.maxCapacityUnitB == null) {
            return 120 * 1024 * 1024L;
        }
        return maxCapacityUnitB;
    }

    public void setMaxCapacityUnitB(Long maxCapacityUnitB) {
        this.maxCapacityUnitB = maxCapacityUnitB;
    }

    @Override
    public String toString() {
        return "RegionCapacityConfig{" +
                "clusterId=" + clusterId +
                ", duration=" + duration +
                ", latestTimeUnitMs=" + latestTimeUnitMs +
                ", maxCapacityUnitB=" + maxCapacityUnitB +
                '}';
    }
}