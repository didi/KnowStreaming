package com.xiaojukeji.know.streaming.km.rebalance.algorithm.model;

import java.util.*;

/**
 * @author leewei
 * @date 2022/5/9
 */
public class Rack {
    private final String id;
    private final SortedSet<Broker> brokers;

    public Rack(String id) {
        this.id = id;
        this.brokers = new TreeSet<>();
    }

    public String id() {
        return id;
    }

    public SortedSet<Broker> brokers() {
        return Collections.unmodifiableSortedSet(this.brokers);
    }

    public Load load() {
        Load load = new Load();
        for (Broker broker : this.brokers) {
            load.addLoad(broker.load());
        }
        return load;
    }

    public List<Replica> replicas() {
        List<Replica> replicas = new ArrayList<>();
        for (Broker broker : this.brokers) {
            replicas.addAll(broker.replicas());
        }
        return replicas;
    }

    Broker addBroker(Broker broker) {
        this.brokers.add(broker);
        return broker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rack rack = (Rack) o;
        return Objects.equals(id, rack.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Rack{" +
                "id='" + id + '\'' +
                ", brokers=" + brokers +
                '}';
    }
}
