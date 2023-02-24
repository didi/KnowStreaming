package com.xiaojukeji.know.streaming.km.rebalance.algorithm.executor.common;

public enum BalanceGoal {
    // KM传参时使用
    TOPIC_LEADERS("TopicLeadersDistributionGoal"),
    TOPIC_REPLICA("TopicReplicaDistributionGoal"),
    DISK("DiskDistributionGoal"),
    NW_IN("NetworkInboundDistributionGoal"),
    NW_OUT("NetworkOutboundDistributionGoal");

    private final String goal;

    BalanceGoal(String goal) {
        this.goal = goal;
    }

    public String goal() {
        return goal;
    }
}
