package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * @author zengqiao
 * @date 20/5/20
 */
public enum KafkaBrokerRoleEnum {
    NORMAL("NormalBroker"),

    COORDINATOR("Coordinator"),

    CONTROLLER("Controller"),
        ;
    private String role;

    KafkaBrokerRoleEnum(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "KafkaBrokerRoleEnum{" +
                "role='" + role + '\'' +
                '}';
    }
}