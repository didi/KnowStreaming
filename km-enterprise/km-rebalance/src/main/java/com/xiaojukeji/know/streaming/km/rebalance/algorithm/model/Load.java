package com.xiaojukeji.know.streaming.km.rebalance.algorithm.model;

import java.util.Arrays;

/**
 * @author leewei
 * @date 2022/5/9
 */
public class Load {
    private final double[] values;

    public Load() {
        this.values = new double[Resource.values().length];
    }

    public void setLoad(Resource resource, double load) {
        this.values[resource.id()] = load;
    }

    public double loadFor(Resource resource) {
        return this.values[resource.id()];
    }

    public void addLoad(Load loadToAdd) {
        for (Resource resource : Resource.values()) {
            this.setLoad(resource, this.loadFor(resource) + loadToAdd.loadFor(resource));
        }
    }

    public void subtractLoad(Load loadToSubtract) {
        for (Resource resource : Resource.values()) {
            this.setLoad(resource, this.loadFor(resource) - loadToSubtract.loadFor(resource));
        }
    }

    @Override
    public String toString() {
        return "Load{" +
                "values=" + Arrays.toString(values) +
                '}';
    }
}
