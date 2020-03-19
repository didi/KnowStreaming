package com.xiaojukeji.kafka.manager.common.entity.dto.alarm;

/**
 * @author zengqiao
 * @date 19/12/16
 */
public class AlarmStrategyActionDTO {
    private String actionWay; // 告知方式: kafka

    private String actionTag;

    public String getActionWay() {
        return actionWay;
    }

    public void setActionWay(String actionWay) {
        this.actionWay = actionWay;
    }

    public String getActionTag() {
        return actionTag;
    }

    public void setActionTag(String actionTag) {
        this.actionTag = actionTag;
    }

    @Override
    public String toString() {
        return "AlarmStrategyActionDTO{" +
                "actionWay='" + actionWay + '\'' +
                ", actionTag='" + actionTag + '\'' +
                '}';
    }

    public boolean legal() {
        if (actionWay == null
                || actionTag == null) {
            return false;
        }
        return true;
    }
}