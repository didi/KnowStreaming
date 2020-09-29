package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * Topic迁移动作
 * @author zengqiao
 * @date 20/4/16
 */
public enum TopicReassignActionEnum {
    START("start"),
    MODIFY("modify"),
    CANCEL("cancel"),
            ;

    private String action;

    TopicReassignActionEnum(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "TopicReassignActionEnum{" +
                "action='" + action + '\'' +
                '}';
    }

    public static TopicReassignActionEnum getByAction(String action) {
        for (TopicReassignActionEnum elem: TopicReassignActionEnum.values()) {
            if (elem.action.equals(action)) {
                return elem;
            }
        }
        return null;
    }
}