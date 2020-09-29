package com.xiaojukeji.kafka.manager.task.config;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

/**
 * Kafka账单配置
 * @author zengqiao
 * @date 20/6/9
 */
public class TopicBillConfig {
    /**
     * 取当月maxAvgDay天的峰值的均值Quota
     */
    private Integer maxAvgDay;

    /**
     * Quota调整比例
     */
    private Double quotaRatio;

    /**
     * 单价
     */
    private Double priseUnitMB;

    public Integer getMaxAvgDay() {
        return maxAvgDay;
    }

    public void setMaxAvgDay(Integer maxAvgDay) {
        this.maxAvgDay = maxAvgDay;
    }

    public Double getQuotaRatio() {
        return quotaRatio;
    }

    public void setQuotaRatio(Double quotaRatio) {
        this.quotaRatio = quotaRatio;
    }

    public Double getPriseUnitMB() {
        return priseUnitMB;
    }

    public void setPriseUnitMB(Double priseUnitMB) {
        this.priseUnitMB = priseUnitMB;
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNullOrLessThanZero(maxAvgDay)
                || ValidateUtils.isNullOrLessThanZero(quotaRatio)
                || ValidateUtils.isNullOrLessThanZero(priseUnitMB)) {
            return false;
        }
        return true;
    }
}