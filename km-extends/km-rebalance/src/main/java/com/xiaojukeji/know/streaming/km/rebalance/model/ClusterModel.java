package com.xiaojukeji.know.streaming.km.rebalance.model;

import com.xiaojukeji.know.streaming.km.rebalance.executor.common.BalanceActionHistory;
import org.apache.kafka.common.TopicPartition;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author leewei
 * @date 2022/4/29
 */
public class ClusterModel {
    private final Map<String, Rack> racksById;
    private final Map<Integer, Broker> brokersById;
    private final Map<String, Map<TopicPartition, Partition>> partitionsByTopic;
    private Map<TopicPartition, List<BalanceActionHistory>> balanceActionHistory;

    public ClusterModel() {
        this.racksById = new HashMap<>();
        this.brokersById = new HashMap<>();
        this.partitionsByTopic = new HashMap<>();
        this.balanceActionHistory = new HashMap<>();
    }

    public Rack rack(String rackId) {
        return this.racksById.get(rackId);
    }

    public Rack addRack(String rackId) {
        Rack rack = new Rack(rackId);
        this.racksById.putIfAbsent(rackId, rack);
        return this.racksById.get(rackId);
    }

    public SortedSet<Broker> brokers() {
        return new TreeSet<>(this.brokersById.values());
    }

    public Set<String> topics() {
        return this.partitionsByTopic.keySet();
    }

    public SortedSet<Partition> topic(String name) {
        return new TreeSet<>(this.partitionsByTopic.get(name).values());
    }

    public SortedSet<Broker> sortedBrokersFor(Resource resource, boolean reverse) {
        return sortedBrokersFor(null, resource, reverse);
    }

    public SortedSet<Broker> sortedBrokersFor(Predicate<? super Broker> filter, Resource resource, boolean reverse) {
        Comparator<Broker> comparator =
                Comparator.<Broker>comparingDouble(b -> b.utilizationFor(resource))
                        .thenComparingInt(Broker::id);
        if (reverse)
            comparator = comparator.reversed();
        SortedSet<Broker> sortedBrokers = new TreeSet<>(comparator);
        if (filter == null) {
            sortedBrokers.addAll(this.brokersById.values());
        } else {
            sortedBrokers.addAll(this.brokersById.values().stream()
                    .filter(filter).collect(Collectors.toList()));
        }

        return sortedBrokers;
    }

    public Load load() {
        Load load = new Load();
        for (Broker broker : this.brokersById.values()) {
            load.addLoad(broker.load());
        }
        return load;
    }

    public Capacity capacity() {
        Capacity capacity = new Capacity();
        for (Broker broker : this.brokersById.values()) {
            capacity.addCapacity(broker.capacity());
        }
        return capacity;
    }

    public double utilizationFor(Resource resource) {
        return load().loadFor(resource) / capacity().capacityFor(resource);
    }

    public double[] avgOfUtilization() {
        Load load = load();
        Capacity capacity = capacity();
        double[] unils = new double[Resource.values().length];
        for (Resource resource : Resource.values()) {
            unils[resource.id()] = load.loadFor(resource) / capacity.capacityFor(resource);
        }
        return unils;
    }

    public Broker broker(int brokerId) {
        return this.brokersById.get(brokerId);
    }

    public Broker addBroker(String rackId, int brokerId, String host, boolean isOffline, Capacity capacity) {
        Rack rack = rack(rackId);
        if (rack == null)
            throw new IllegalArgumentException("Rack: " + rackId + "is not exists.");
        Broker broker = new Broker(rack, brokerId, host, isOffline, capacity);
        rack.addBroker(broker);
        this.brokersById.put(brokerId, broker);
        return broker;
    }

    public Replica addReplica(int brokerId, TopicPartition topicPartition, boolean isLeader, Load load) {
        return addReplica(brokerId, topicPartition, isLeader, false, load);
    }

    public Replica addReplica(int brokerId, TopicPartition topicPartition, boolean isLeader, boolean isOffline, Load load) {
        Broker broker = broker(brokerId);
        if (broker == null) {
            throw new IllegalArgumentException("Broker: " + brokerId + "is not exists.");
        }

        Replica replica = new Replica(broker, topicPartition, isLeader, isOffline);
        replica.setLoad(load);
        // add to broker
        broker.addReplica(replica);

        Map<TopicPartition, Partition> partitions = this.partitionsByTopic
                .computeIfAbsent(topicPartition.topic(), k -> new HashMap<>());

        Partition partition = partitions.computeIfAbsent(topicPartition, Partition::new);
        if (isLeader) {
            partition.addLeader(replica, 0);
        } else {
            partition.addFollower(replica, partition.replicas().size());
        }

        return replica;
    }

    public Replica removeReplica(int brokerId, TopicPartition topicPartition) {
        Broker broker = broker(brokerId);
        return broker.removeReplica(topicPartition);
    }

    public void relocateLeadership(String goal, String actionType, TopicPartition topicPartition, int sourceBrokerId, int destinationBrokerId) {
        relocateLeadership(topicPartition, sourceBrokerId, destinationBrokerId);
        addBalanceActionHistory(goal, actionType, topicPartition, sourceBrokerId, destinationBrokerId);
    }

    public void relocateLeadership(TopicPartition topicPartition, int sourceBrokerId, int destinationBrokerId) {
        Broker sourceBroker = broker(sourceBrokerId);
        Replica sourceReplica = sourceBroker.replica(topicPartition);
        if (!sourceReplica.isLeader()) {
            throw new IllegalArgumentException("Cannot relocate leadership of partition " + topicPartition + "from broker "
                    + sourceBrokerId + " to broker " + destinationBrokerId
                    + " because the source replica isn't leader.");
        }
        Broker destinationBroker = broker(destinationBrokerId);
        Replica destinationReplica = destinationBroker.replica(topicPartition);
        if (destinationReplica.isLeader()) {
            throw new IllegalArgumentException("Cannot relocate leadership of partition " + topicPartition + "from broker "
                    + sourceBrokerId + " to broker " + destinationBrokerId
                    + " because the destination replica is a leader.");
        }
        Load leaderLoadDelta = sourceBroker.makeFollower(topicPartition);
        destinationBroker.makeLeader(topicPartition, leaderLoadDelta);

        Partition partition = this.partitionsByTopic.get(topicPartition.topic()).get(topicPartition);
        partition.relocateLeadership(destinationReplica);
    }

    public void relocateReplica(String goal, String actionType, TopicPartition topicPartition, int sourceBrokerId, int destinationBrokerId) {
        relocateReplica(topicPartition, sourceBrokerId, destinationBrokerId);
        addBalanceActionHistory(goal, actionType, topicPartition, sourceBrokerId, destinationBrokerId);
    }

    public void relocateReplica(TopicPartition topicPartition, int sourceBrokerId, int destinationBrokerId) {
        Replica replica = removeReplica(sourceBrokerId, topicPartition);
        if (replica == null) {
            throw new IllegalArgumentException("Replica is not in the cluster.");
        }
        Broker destinationBroker = broker(destinationBrokerId);
        replica.setBroker(destinationBroker);
        destinationBroker.addReplica(replica);
    }

    private void addBalanceActionHistory(String goal, String actionType, TopicPartition topicPartition, int sourceBrokerId, int destinationBrokerId) {
        BalanceActionHistory history = new BalanceActionHistory();
        history.setActionType(actionType);
        history.setGoal(goal);
        history.setTopic(topicPartition.topic());
        history.setPartition(topicPartition.partition());
        history.setSourceBrokerId(sourceBrokerId);
        history.setDestinationBrokerId(destinationBrokerId);
        this.balanceActionHistory.computeIfAbsent(topicPartition, k -> new ArrayList<>()).add(history);
    }

    public Map<String, Integer> numLeadersPerTopic(Set<String> topics) {
        Map<String, Integer> leaderCountByTopicNames = new HashMap<>();
        topics.forEach(topic -> leaderCountByTopicNames.put(topic, partitionsByTopic.get(topic).size()));
        return leaderCountByTopicNames;
    }

    public Map<TopicPartition, List<ReplicaPlacementInfo>> getReplicaDistribution() {
        Map<TopicPartition, List<ReplicaPlacementInfo>> replicaDistribution = new HashMap<>();
        for (Map<TopicPartition, Partition> tp : partitionsByTopic.values()) {
            tp.values().forEach(i -> {
                i.replicas().forEach(j -> replicaDistribution.computeIfAbsent(j.topicPartition(), k -> new ArrayList<>())
                        .add(new ReplicaPlacementInfo(j.broker().id(), "")));
            });
        }
        return replicaDistribution;
    }

    public Replica partition(TopicPartition tp) {
        return partitionsByTopic.get(tp.topic()).get(tp).leader();
    }

    public Map<TopicPartition, ReplicaPlacementInfo> getLeaderDistribution() {
        Map<TopicPartition, ReplicaPlacementInfo> leaderDistribution = new HashMap<>();
        for (Broker broker : brokersById.values()) {
            broker.leaderReplicas().forEach(i -> leaderDistribution.put(i.topicPartition(), new ReplicaPlacementInfo(broker.id(), "")));
        }
        return leaderDistribution;
    }

    public int numTopicReplicas(String topic) {
        return partitionsByTopic.get(topic).size();
    }

    public Map<TopicPartition, List<BalanceActionHistory>> balanceActionHistory() {
        return this.balanceActionHistory;
    }
}
