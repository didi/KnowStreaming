package com.xiaojukeji.kafka.manager.monitor.common.entry;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/4
 */
public class Strategy {
    private Long id;

    private String name;

    private Integer priority;

    private String periodHoursOfDay;

    private String periodDaysOfWeek;

    private List<StrategyExpression> strategyExpressionList;

    private List<StrategyFilter> strategyFilterList;

    private List<StrategyAction> strategyActionList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getPeriodHoursOfDay() {
        return periodHoursOfDay;
    }

    public void setPeriodHoursOfDay(String periodHoursOfDay) {
        this.periodHoursOfDay = periodHoursOfDay;
    }

    public String getPeriodDaysOfWeek() {
        return periodDaysOfWeek;
    }

    public void setPeriodDaysOfWeek(String periodDaysOfWeek) {
        this.periodDaysOfWeek = periodDaysOfWeek;
    }

    public List<StrategyExpression> getStrategyExpressionList() {
        return strategyExpressionList;
    }

    public void setStrategyExpressionList(List<StrategyExpression> strategyExpressionList) {
        this.strategyExpressionList = strategyExpressionList;
    }

    public List<StrategyFilter> getStrategyFilterList() {
        return strategyFilterList;
    }

    public void setStrategyFilterList(List<StrategyFilter> strategyFilterList) {
        this.strategyFilterList = strategyFilterList;
    }

    public List<StrategyAction> getStrategyActionList() {
        return strategyActionList;
    }

    public void setStrategyActionList(List<StrategyAction> strategyActionList) {
        this.strategyActionList = strategyActionList;
    }

    @Override
    public String toString() {
        return "Strategy{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                ", periodHoursOfDay='" + periodHoursOfDay + '\'' +
                ", periodDaysOfWeek='" + periodDaysOfWeek + '\'' +
                ", strategyExpressionList=" + strategyExpressionList +
                ", strategyFilterList=" + strategyFilterList +
                ", strategyActionList=" + strategyActionList +
                '}';
    }

    public boolean paramLegal() {
        if (name == null
                || priority == null
                || periodHoursOfDay == null
                || periodDaysOfWeek == null
                || strategyExpressionList == null || strategyExpressionList.isEmpty()
                || strategyFilterList == null || strategyFilterList.isEmpty()
                || strategyActionList == null || strategyActionList.isEmpty()) {
            return false;
        }

        for (StrategyExpression dto: strategyExpressionList) {
            if (!dto.paramLegal()) {
                return false;
            }
        }

        for (StrategyFilter dto: strategyFilterList) {
            if (!dto.paramLegal()) {
                return false;
            }
        }
        for (StrategyAction dto: strategyActionList) {
            if (!dto.paramLegal()) {
                return false;
            }
        }
        return true;
    }
}