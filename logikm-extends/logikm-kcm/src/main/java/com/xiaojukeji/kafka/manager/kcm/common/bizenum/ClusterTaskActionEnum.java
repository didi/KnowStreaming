package com.xiaojukeji.kafka.manager.kcm.common.bizenum;

/**
 * 任务动作
 * @author zengqiao
 * @date 20/4/26
 */
public enum ClusterTaskActionEnum {
    UNKNOWN("unknown"),

    START("start"),
    PAUSE("pause"),

    IGNORE("ignore"),
    CANCEL("cancel"),

    REDO("redo"),
    KILL("kill"),

    ROLLBACK("rollback"),

    ;

    private String action;

    ClusterTaskActionEnum(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "ClusterTaskActionEnum{" +
                "action='" + action + '\'' +
                '}';
    }
}
