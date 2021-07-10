package com.xiaojukeji.kafka.manager.common.zookeeper.znode;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/1/15
 */
public class ReassignmentElemData {
    private String topic;

    private Integer partition;

    private List<Integer> replicas;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
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
        return "ReassignmentElemDTO{" +
                "topic='" + topic + '\'' +
                ", partition=" + partition +
                ", replicas=" + replicas +
                '}';
    }
}