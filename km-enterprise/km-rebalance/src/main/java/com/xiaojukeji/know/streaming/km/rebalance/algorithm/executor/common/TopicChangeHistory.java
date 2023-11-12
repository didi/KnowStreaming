package com.xiaojukeji.know.streaming.km.rebalance.algorithm.executor.common;

import java.util.List;

public class TopicChangeHistory {
    //均衡Topic
    private String topic;
    //均衡分区
    private int partition;
    //旧Leader的BrokerID
    private int oldLeader;
    //均衡前副本分布
    private List<Integer> balanceBefore;
    //新Leader的BrokerID
    private int newLeader;
    //均衡后副本分布
    private List<Integer> balanceAfter;

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

    public int getOldLeader() {
        return oldLeader;
    }

    public void setOldLeader(int oldLeader) {
        this.oldLeader = oldLeader;
    }

    public List<Integer> getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(List<Integer> balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public int getNewLeader() {
        return newLeader;
    }

    public void setNewLeader(int newLeader) {
        this.newLeader = newLeader;
    }

    public List<Integer> getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(List<Integer> balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    @Override
    public String toString() {
        return "TopicChangeHistory{" +
                "topic='" + topic + '\'' +
                ", partition='" + partition + '\'' +
                ", oldLeader=" + oldLeader +
                ", balanceBefore=" + balanceBefore +
                ", newLeader=" + newLeader +
                ", balanceAfter=" + balanceAfter +
                '}';
    }
}
