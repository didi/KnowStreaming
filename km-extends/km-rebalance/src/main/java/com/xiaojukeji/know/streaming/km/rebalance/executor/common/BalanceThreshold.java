package com.xiaojukeji.know.streaming.km.rebalance.executor.common;

import com.xiaojukeji.know.streaming.km.rebalance.model.Resource;

public class BalanceThreshold {
    private final Resource _resource;
    private final double _upper;
    private final double _lower;

    public BalanceThreshold(Resource resource, double threshold, double avgResource) {
        _resource = resource;
        _upper = avgResource * (1 + threshold);
        _lower = avgResource * (1 - threshold);
    }

    public Resource resource() {
        return _resource;
    }

    public boolean isInRange(double utilization) {
        return utilization > _lower && utilization < _upper;
    }

    public int state(double utilization) {
        if (utilization <= _lower) {
            return -1;
        } else if (utilization >= _upper) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "BalanceThreshold{" +
                "_resource=" + _resource +
                ", _upper=" + _upper +
                ", _lower=" + _lower +
                '}';
    }
}
