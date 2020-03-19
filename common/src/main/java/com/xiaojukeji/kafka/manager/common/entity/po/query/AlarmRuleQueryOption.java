package com.xiaojukeji.kafka.manager.common.entity.po.query;

/**
 * @author zengqiao
 * @date 19/12/2
 */
public class AlarmRuleQueryOption extends BaseQueryOption {
    private String alarmName;

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }
}