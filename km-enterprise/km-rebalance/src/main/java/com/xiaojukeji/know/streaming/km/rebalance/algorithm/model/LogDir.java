package com.xiaojukeji.know.streaming.km.rebalance.algorithm.model;

import org.apache.kafka.common.TopicPartition;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LogDir implements Comparable<LogDir>{

    private final String name;
    private final Broker broker;
    private final Set<Replica> replicas;
    private final Map<TopicPartition, Replica> topicPartitionReplicas;
    private final Load load;
    private final Capacity capacity;


    public LogDir(String name, Broker broker, Capacity capacity) {
        this.name = name;
        this.broker = broker;
        this.replicas = new HashSet<>();
        this.load = new Load();
        this.capacity = capacity;
        this.topicPartitionReplicas = new HashMap<>();
    }

    public void addReplica(Replica replica) {
        if (this.replicas.contains(replica)) {
            throw new IllegalStateException(String.format("Broker %d logDir %s already has replica %s", this.broker.id(), this.name,
                    replica.topicPartition()));
        }
        this.replicas.add(replica);
        this.topicPartitionReplicas.put(replica.topicPartition(), replica);
        this.load.addLoad(replica.load());
    }

    Replica removeReplica(TopicPartition topicPartition) {
        Replica replica = this.topicPartitionReplicas.get(topicPartition);
        if(replica == null) {
            return replica;
        }
        this.replicas.remove(replica);
        this.topicPartitionReplicas.remove(topicPartition);
        this.load.subtractLoad(replica.load());
        return replica;
    }

    public Set<Replica> replicas() {
        return Collections.unmodifiableSet(this.replicas);
    }

    public String name() {
        return this.name;
    }

    public Broker broker() {
        return this.broker;
    }

    public Load load() {
        return this.load;
    }

    public Capacity capacity() {
        return this.capacity;
    }

    public double utilizationFor(Resource resource) {
        return this.load.loadFor(resource) / this.capacity.capacityFor(resource);
    }


    public double expectedUtilizationAfterAdd(Resource resource, Load loadToChange) {
        return (this.load.loadFor(resource) + ((loadToChange == null) ? 0 : loadToChange.loadFor(resource)))
                / this.capacity.capacityFor(resource);
    }


    public double expectedUtilizationAfterRemove(Resource resource, Load loadToChange) {
        return (this.load.loadFor(resource) - ((loadToChange == null) ? 0 : loadToChange.loadFor(resource)))
                / this.capacity.capacityFor(resource);
    }


    public SortedSet<Replica> sortedReplicasFor(Predicate<? super Replica> filter, Resource resource, boolean reverse) {
        Comparator<Replica> comparator =
                Comparator.<Replica>comparingDouble(r -> r.load().loadFor(resource))
                        .thenComparingInt(Replica::hashCode);
        if (reverse)
            comparator = comparator.reversed();
        SortedSet<Replica> sortedReplicas = new TreeSet<>(comparator);
        if (filter == null) {
            sortedReplicas.addAll(this.replicas);
        } else {
            sortedReplicas.addAll(this.replicas.stream()
                    .filter(filter).collect(Collectors.toList()));
        }

        return sortedReplicas;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogDir logDir = (LogDir) o;
        return broker.equals(logDir.broker) && name.equals(logDir.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, broker.id());
    }

    @Override
    public int compareTo(LogDir o) {
        int rst = Integer.compare(broker.id(), o.broker.id());
        if(rst != 0) {
            return rst;
        } else {
            return name.compareTo(o.name());
        }
    }

    @Override
    public String toString() {
        return "LogDir{" +
                "name=" + name +
                ", broker='" + broker + '\'' +
                ", replicas=" + replicas +
                ", load=" + load +
                ", capacity=" + capacity +
                '}';
    }
}
