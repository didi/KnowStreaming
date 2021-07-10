package com.xiaojukeji.kafka.manager.monitor.common.entry;

/**
 * @author zengqiao
 * @date 20/5/27
 */
public class Silence {
    private Long silenceId;

    /**
     * 屏蔽的策略ID
     */
    private Long strategyId;

    /**
     * 屏蔽开始时间
     */
    private Long beginTime;

    /**
     * 屏蔽结束时间
     */
    private Long endTime;

    /**
     * 备注
     */
    private String description;

    public Long getSilenceId() {
        return silenceId;
    }

    public void setSilenceId(Long silenceId) {
        this.silenceId = silenceId;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Silence{" +
                "silenceId=" + silenceId +
                ", strategyId=" + strategyId +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", description='" + description + '\'' +
                '}';
    }
}