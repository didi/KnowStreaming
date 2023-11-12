package com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer;

import com.xiaojukeji.know.streaming.km.rebalance.algorithm.executor.common.BalanceParameter;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.Resource;

import java.util.*;

public class OptimizationOptions {
    private final Set<String> _excludedTopics;
    private final Set<Integer> _offlineBrokers;
    private final Set<Integer> _balanceBrokers;
    private final Map<Resource, Double> _resourceBalancePercentage;
    private final List<String> _goals;
    private final BalanceParameter _parameter;

    public OptimizationOptions(BalanceParameter parameter) {
        _parameter = parameter;
        _goals = parameter.getGoals();
        _excludedTopics = AnalyzerUtils.getSplitTopics(parameter.getExcludedTopics());
        _offlineBrokers = AnalyzerUtils.getSplitBrokers(parameter.getOfflineBrokers());
        _balanceBrokers = AnalyzerUtils.getSplitBrokers(parameter.getBalanceBrokers());
        _resourceBalancePercentage = new HashMap<>();
        _resourceBalancePercentage.put(Resource.CPU, parameter.getCpuThreshold());
        _resourceBalancePercentage.put(Resource.DISK, parameter.getDiskThreshold());
        _resourceBalancePercentage.put(Resource.NW_IN, parameter.getNetworkInThreshold());
        _resourceBalancePercentage.put(Resource.NW_OUT, parameter.getNetworkOutThreshold());
    }

    public Set<String> excludedTopics() {
        return Collections.unmodifiableSet(_excludedTopics);
    }

    public Set<Integer> offlineBrokers() {
        return Collections.unmodifiableSet(_offlineBrokers);
    }

    public Set<Integer> balanceBrokers() {
        return Collections.unmodifiableSet(_balanceBrokers);
    }

    public double resourceBalancePercentageFor(Resource resource) {
        return _resourceBalancePercentage.get(resource);
    }

    public List<String> goals() {
        return Collections.unmodifiableList(_goals);
    }

    public double topicReplicaThreshold() {
        return _parameter.getTopicReplicaThreshold();
    }

    public BalanceParameter parameter() {
        return _parameter;
    }

    public double topicLeaderThreshold() {
        return _parameter.getTopicLeaderThreshold();
    }
}
