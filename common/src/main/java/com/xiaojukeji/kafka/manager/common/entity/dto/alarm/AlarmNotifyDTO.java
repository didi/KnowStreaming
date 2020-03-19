package com.xiaojukeji.kafka.manager.common.entity.dto.alarm;

/**
 * 告警通知
 * @author zengqiao
 * @date 2020-02-14
 */
public class AlarmNotifyDTO {
    private Long alarmRuleId;

    private String actionTag;

    private String message;

    public Long getAlarmRuleId() {
        return alarmRuleId;
    }

    public void setAlarmRuleId(Long alarmRuleId) {
        this.alarmRuleId = alarmRuleId;
    }

    public String getActionTag() {
        return actionTag;
    }

    public void setActionTag(String actionTag) {
        this.actionTag = actionTag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AlarmNotifyDTO{" +
                "alarmRuleId=" + alarmRuleId +
                ", actionTag='" + actionTag + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
