package com.xiaojukeji.know.streaming.km.rebalance.algorithm.model;

import org.apache.kafka.common.TopicPartition;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author leewei
 * @date 2022/4/29
 */
public class Broker implements Comparable<Broker> {

    private final Rack rack;
    private final int id;
    private final String host;
    private final boolean isOffline;
    private final Set<Replica> replicas;
    private final Set<Replica> leaderReplicas;
    private final Map<String, Map<Integer, Replica>> topicReplicas;
    private final Map<String, LogDir> logDirs;
    private final Load load;
    private final Capacity capacity;

    public Broker(Rack rack, int id, String host, boolean isOffline, Capacity capacity) {
        this.rack = rack;
        this.id = id;
        this.host = host;
        this.isOffline = isOffline;
        this.replicas = new HashSet<>();
        this.leaderReplicas = new HashSet<>();
        this.topicReplicas = new HashMap<>();
        this.logDirs = new HashMap<>();
        this.load = new Load();
        this.capacity = capacity;
    }

    public void addLogDir(LogDir logDir) {
        this.logDirs.put(logDir.name(), logDir);
    }

    public Rack rack() {
        return rack;
    }

    public int id() {
        return id;
    }

    public String host() {
        return host;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public Set<Replica> replicas() {
        return Collections.unmodifiableSet(this.replicas);
    }

    public Map<String, LogDir> logDirs() {
        return Collections.unmodifiableMap(this.logDirs);
    }

    public SortedSet<Replica> sortedReplicasFor(Resource resource, boolean reverse) {
        return sortedReplicasFor(null, resource, reverse);
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

    public Set<Replica> leaderReplicas() {
        return Collections.unmodifiableSet(this.leaderReplicas);
    }

    public Load load() {
        return this.load;
    }

    public Capacity capacity() {
        return capacity;
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

    public LogDir logDir(String name) {
        return this.logDirs.get(name);
    }

    public Replica replica(TopicPartition topicPartition) {
        Map<Integer, Replica> replicas = this.topicReplicas.get(topicPartition.topic());
        if (replicas == null) {
            return null;
        }
        return replicas.get(topicPartition.partition());
    }

    void addReplica(String logDir, Replica replica) {
        // Add replica to list of all replicas in the broker.
        if (this.replicas.contains(replica)) {
            throw new IllegalStateException(String.format("Broker %d already has replica %s", this.id,
                    replica.topicPartition()));
        }
        this.replicas.add(replica);
        // Add topic replica.
        this.topicReplicas.computeIfAbsent(replica.topicPartition().topic(), t -> new HashMap<>())
                .put(replica.topicPartition().partition(), replica);

        // Add leader replica.
        if (replica.isLeader()) {
            this.leaderReplicas.add(replica);
        }

        // Add replica load to the broker load.
        this.load.addLoad(replica.load());

        // Add replica to list of replicas in the logDir
        this.logDirs.get(logDir).addReplica(replica);
    }

    Replica removeReplica(String sourceLogDir, TopicPartition topicPartition) {
        Replica replica = replica(topicPartition);
        if (replica != null) {
            this.replicas.remove(replica);
            Map<Integer, Replica> replicas = this.topicReplicas.get(topicPartition.topic());
            if (replicas != null) {
                replicas.remove(topicPartition.partition());
            }
            if (replica.isLeader()) {
                this.leaderReplicas.remove(replica);
            }
            this.load.subtractLoad(replica.load());
            this.logDirs.get(sourceLogDir).removeReplica(topicPartition);
        }
        return replica;
    }

    Load makeFollower(TopicPartition topicPartition) {
        Replica replica = replica(topicPartition);
        Load leaderLoadDelta = replica.makeFollower();
        // Remove leadership load from load.
        this.load.subtractLoad(leaderLoadDelta);
        this.leaderReplicas.remove(replica);
        return leaderLoadDelta;
    }

    void makeLeader(TopicPartition topicPartition, Load leaderLoadDelta) {
        Replica replica = replica(topicPartition);
        replica.makeLeader(leaderLoadDelta);
        // Add leadership load to load.
        this.load.addLoad(leaderLoadDelta);
        this.leaderReplicas.add(replica);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Broker broker = (Broker) o;
        return id == broker.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Broker o) {
        return Integer.compare(id, o.id());
    }

    @Override
    public String toString() {
        return "Broker{" +
                "id=" + id +
                ", host='" + host + '\'' +
                ", rack=" + rack.id() +
                ", replicas=" + replicas +
                ", leaderReplicas=" + leaderReplicas +
                ", topicReplicas=" + topicReplicas +
                ", logDirs=" + logDirs +
                ", load=" + load +
                ", capacity=" + capacity +
                '}';
    }

    public int numLeadersFor(String topicName) {
        return (int) replicasOfTopicInBroker(topicName).stream().filter(Replica::isLeader).count();
    }

    public Set<String> topics() {
        return topicReplicas.keySet();
    }

    public int numReplicasOfTopicInBroker(String topic) {
        Map<Integer, Replica> replicaMap = topicReplicas.get(topic);
        return replicaMap == null ? 0 : replicaMap.size();
    }

    public Collection<Replica> replicasOfTopicInBroker(String topic) {
        Map<Integer, Replica> replicaMap = topicReplicas.get(topic);
        return replicaMap == null ? Collections.emptySet() : replicaMap.values();
    }

    public Set<Replica> currentOfflineReplicas() {
        return replicas.stream().filter(Replica::isCurrentOffline).collect(Collectors.toSet());
    }

    public LogDir randomLogDir() {
        return logDirs.values().stream().collect(Collectors.toList()).get(ThreadLocalRandom.current().nextInt(logDirs.size()));
    }
}
