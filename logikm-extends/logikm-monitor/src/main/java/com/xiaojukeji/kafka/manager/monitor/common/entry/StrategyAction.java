package com.xiaojukeji.kafka.manager.monitor.common.entry;

/**
 * @author zengqiao
 * @date 20/5/27
 */
public class StrategyAction {
    private String notifyGroup;

    private String converge;

    private String callback;

    public String getNotifyGroup() {
        return notifyGroup;
    }

    public void setNotifyGroup(String notifyGroup) {
        this.notifyGroup = notifyGroup;
    }

    public String getConverge() {
        return converge;
    }

    public void setConverge(String converge) {
        this.converge = converge;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    @Override
    public String toString() {
        return "StrategyAction{" +
                "notifyGroup=" + notifyGroup +
                ", converge='" + converge + '\'' +
                ", callback='" + callback + '\'' +
                '}';
    }

    public boolean paramLegal() {
        if (notifyGroup == null || converge == null) {
            return false;
        }
        callback = (callback == null? "": callback);
        return true;
    }
}