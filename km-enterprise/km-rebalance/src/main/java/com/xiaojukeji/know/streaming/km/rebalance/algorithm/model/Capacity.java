package com.xiaojukeji.know.streaming.km.rebalance.algorithm.model;

import java.util.Arrays;

/**
 * @author leewei
 * @date 2022/5/9
 */
public class Capacity {
    private final double[] values;

    public Capacity() {
        this.values = new double[Resource.values().length];
    }

    public void setCapacity(Resource resource, double capacity) {
        this.values[resource.id()] = capacity;
    }

    public double capacityFor(Resource resource) {
        return this.values[resource.id()];
    }

    public void addCapacity(Capacity capacityToAdd) {
        for (Resource resource : Resource.values()) {
            this.setCapacity(resource, this.capacityFor(resource) + capacityToAdd.capacityFor(resource));
        }
    }

    @Override
    public String toString() {
        return "Capacity{" +
                "values=" + Arrays.toString(values) +
                '}';
    }
}
