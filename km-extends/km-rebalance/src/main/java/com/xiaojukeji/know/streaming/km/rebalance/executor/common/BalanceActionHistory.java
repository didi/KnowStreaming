package com.xiaojukeji.know.streaming.km.rebalance.executor.common;

public class BalanceActionHistory {
    //均衡目标
    private String goal;
    //均衡动作
    private String actionType;
    //均衡Topic
    private String topic;
    //均衡分区
    private int partition;
    //源Broker
    private int sourceBrokerId;
    //目标Broker
    private int destinationBrokerId;

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public int getSourceBrokerId() {
        return sourceBrokerId;
    }

    public void setSourceBrokerId(int sourceBrokerId) {
        this.sourceBrokerId = sourceBrokerId;
    }

    public int getDestinationBrokerId() {
        return destinationBrokerId;
    }

    public void setDestinationBrokerId(int destinationBrokerId) {
        this.destinationBrokerId = destinationBrokerId;
    }

    @Override
    public String toString() {
        return "BalanceActionHistory{" +
                "goal='" + goal + '\'' +
                ", actionType='" + actionType + '\'' +
                ", topic='" + topic + '\'' +
                ", partition='" + partition + '\'' +
                ", sourceBrokerId='" + sourceBrokerId + '\'' +
                ", destinationBrokerId='" + destinationBrokerId + '\'' +
                '}';
    }
}
