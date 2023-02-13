package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * 任务动作
 * @author zengqiao
 * @date 20/4/26
 */
public enum TaskActionEnum {
    UNKNOWN("unknown"),

    START("start"),
    PAUSE("pause"),

    IGNORE("ignore"),
    CANCEL("cancel"),

    REDO("redo"),
    KILL("kill"),

    FORCE("force"),

    ROLLBACK("rollback"),

    ;

    private final String action;

    TaskActionEnum(String action) {
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
