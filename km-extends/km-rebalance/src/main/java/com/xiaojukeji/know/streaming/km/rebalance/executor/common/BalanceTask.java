package com.xiaojukeji.know.streaming.km.rebalance.executor.common;

import java.util.List;

public class BalanceTask {
    private String topic;
    private int partition;
    //副本分配列表
    private List<Integer> replicas;

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

    public List<Integer> getReplicas() {
        return replicas;
    }

    public void setReplicas(List<Integer> replicas) {
        this.replicas = replicas;
    }

    @Override
    public String toString() {
        return "BalanceTask{" +
                "topic='" + topic + '\'' +
                ", partition=" + partition +
                ", replicas=" + replicas +
                '}';
    }
}
