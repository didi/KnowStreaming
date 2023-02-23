package com.xiaojukeji.know.streaming.km.rebalance.model;

import org.apache.kafka.common.TopicPartition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author leewei
 * @date 2022/5/11
 */
public class Partition implements Comparable<Partition> {
    private final TopicPartition topicPartition;
    private final List<Replica> replicas;

    public Partition(TopicPartition topicPartition) {
        this.topicPartition = topicPartition;
        this.replicas = new ArrayList<>();
    }

    public TopicPartition topicPartition() {
        return topicPartition;
    }

    public List<Replica> replicas() {
        return replicas;
    }

    public Broker originalLeaderBroker() {
        return replicas.stream().filter(r -> r.original().isLeader())
                .findFirst().orElseThrow(IllegalStateException::new).broker();
    }

    public Replica leader() {
        return replicas.stream()
                .filter(Replica::isLeader)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Not found leader of partition " + topicPartition)
                );
    }

    public Replica leaderOrNull() {
        return replicas.stream()
                .filter(Replica::isLeader)
                .findFirst()
                .orElse(null);
    }

    public List<Replica> followers() {
        return replicas.stream()
                .filter(r -> !r.isLeader())
                .collect(Collectors.toList());
    }

    Replica replica(long brokerId) {
        return replicas.stream()
                .filter(r -> r.broker().id() == brokerId)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Requested replica " + brokerId + " is not a replica of partition " + topicPartition)
                );
    }

    public boolean isLeaderChanged() {
        // return originalLeaderBroker() != this.leader().broker();
        return replicas.stream().anyMatch(Replica::isLeaderChanged);
    }

    public boolean isChanged() {
        return replicas.stream().anyMatch(Replica::isChanged);
    }

    void addLeader(Replica leader, int index) {
        if (leaderOrNull() != null) {
            throw new IllegalArgumentException(String.format("Partition %s already has a leader replica %s. Cannot "
                    + "add a new leader replica %s", this.topicPartition, leaderOrNull(), leader));
        }
        if (!leader.isLeader()) {
            throw new IllegalArgumentException("Inconsistent leadership information. Trying to set " + leader.broker()
                    + " as the leader for partition " + this.topicPartition + " while the replica is not marked "
                    + "as a leader.");
        }
        this.replicas.add(index, leader);
    }

    void addFollower(Replica follower, int index) {
        if (follower.isLeader()) {
            throw new IllegalArgumentException("Inconsistent leadership information. Trying to add follower replica "
                    + follower + " while it is a leader.");
        }
        if (!follower.topicPartition().equals(this.topicPartition)) {
            throw new IllegalArgumentException("Inconsistent topic partition. Trying to add follower replica " + follower
                    + " to partition " + this.topicPartition + ".");
        }
        this.replicas.add(index, follower);
    }

    void relocateLeadership(Replica newLeader) {
        if (!newLeader.isLeader()) {
            throw new IllegalArgumentException("Inconsistent leadership information. Trying to set " + newLeader.broker()
                    + " as the leader for partition " + this.topicPartition + " while the replica is not marked "
                    + "as a leader.");
        }
        int leaderPos = this.replicas.indexOf(newLeader);
        swapReplicaPositions(0, leaderPos);
    }

    void swapReplicaPositions(int index1, int index2) {
        Replica replica1 = this.replicas.get(index1);
        Replica replica2 = this.replicas.get(index2);

        this.replicas.set(index2, replica1);
        this.replicas.set(index1, replica2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partition partition = (Partition) o;
        return topicPartition.equals(partition.topicPartition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topicPartition);
    }

    @Override
    public String toString() {
        return "Partition{" +
                "topicPartition=" + topicPartition +
                ", replicas=" + replicas +
                ", originalLeaderBroker=" + originalLeaderBroker().id() +
                ", leader=" + leaderOrNull() +
                '}';
    }



    @Override
    public int compareTo(Partition o) {
        return Integer.compare(topicPartition.partition(), o.topicPartition.partition());
    }
}
