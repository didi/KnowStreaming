package com.xiaojukeji.kafka.manager.common.entity.po;

public class AlarmRuleDO extends BaseDO {
    private String alarmName;

    private String strategyExpressions;

    private String strategyFilters;

    private String strategyActions;

    private String principals;

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public String getStrategyExpressions() {
        return strategyExpressions;
    }

    public void setStrategyExpressions(String strategyExpressions) {
        this.strategyExpressions = strategyExpressions;
    }

    public String getStrategyFilters() {
        return strategyFilters;
    }

    public void setStrategyFilters(String strategyFilters) {
        this.strategyFilters = strategyFilters;
    }

    public String getStrategyActions() {
        return strategyActions;
    }

    public void setStrategyActions(String strategyActions) {
        this.strategyActions = strategyActions;
    }

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

    @Override
    public String toString() {
        return "AlarmRuleDO{" +
                "alarmName='" + alarmName + '\'' +
                ", strategyExpressions='" + strategyExpressions + '\'' +
                ", strategyFilters='" + strategyFilters + '\'' +
                ", strategyActions='" + strategyActions + '\'' +
                ", principals='" + principals + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}