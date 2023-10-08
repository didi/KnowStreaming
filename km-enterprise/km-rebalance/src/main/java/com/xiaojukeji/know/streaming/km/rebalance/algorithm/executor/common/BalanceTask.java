package com.xiaojukeji.know.streaming.km.rebalance.algorithm.executor.common;

import java.util.List;

public class BalanceTask {
    private String topic;
    private int partition;
    //副本分配列表
    private List<Integer> replicas;
    private List<String> logDirs;

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

    public List<String> getLogDirs() {
        return logDirs;
    }

    public void setLogDirs(List<String> logDirs) {
        this.logDirs = logDirs;
    }

    @Override
    public String toString() {
        return "BalanceTask{" +
                "topic='" + topic + '\'' +
                ", partition=" + partition +
                ", replicas=" + replicas +
                ", logDirs=" + logDirs +
                '}';
    }
}
