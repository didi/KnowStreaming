package com.xiaojukeji.know.streaming.km.rebalance.optimizer;

import com.xiaojukeji.know.streaming.km.rebalance.model.ReplicaPlacementInfo;
import org.apache.kafka.common.TopicPartition;

import java.util.*;
import java.util.stream.Collectors;

public class ExecutionProposal {

    private final TopicPartition _tp;
    private final double _partitionSize;
    private final ReplicaPlacementInfo _oldLeader;
    private final List<ReplicaPlacementInfo> _oldReplicas;
    private final List<ReplicaPlacementInfo> _newReplicas;
    private final Set<ReplicaPlacementInfo> _replicasToAdd;
    private final Set<ReplicaPlacementInfo> _replicasToRemove;

    public ExecutionProposal(TopicPartition tp,
                             double partitionSize,
                             ReplicaPlacementInfo oldLeader,
                             List<ReplicaPlacementInfo> oldReplicas,
                             List<ReplicaPlacementInfo> newReplicas) {
        _tp = tp;
        _partitionSize = partitionSize;
        _oldLeader = oldLeader;
        _oldReplicas = oldReplicas == null ? Collections.emptyList() : oldReplicas;
        _newReplicas = newReplicas;
        Set<Integer> newBrokerList = _newReplicas.stream().mapToInt(ReplicaPlacementInfo::brokerId).boxed().collect(Collectors.toSet());
        Set<Integer> oldBrokerList = _oldReplicas.stream().mapToInt(ReplicaPlacementInfo::brokerId).boxed().collect(Collectors.toSet());
        _replicasToAdd = _newReplicas.stream().filter(r -> !oldBrokerList.contains(r.brokerId())).collect(Collectors.toSet());
        _replicasToRemove = _oldReplicas.stream().filter(r -> !newBrokerList.contains(r.brokerId())).collect(Collectors.toSet());
    }

    public TopicPartition tp() {
        return _tp;
    }

    public double partitionSize() {
        return _partitionSize;
    }

    public ReplicaPlacementInfo oldLeader() {
        return _oldLeader;
    }

    public List<ReplicaPlacementInfo> oldReplicas() {
        return _oldReplicas;
    }

    public List<ReplicaPlacementInfo> newReplicas() {
        return _newReplicas;
    }

    public Map<Integer, Double[]> replicasToAdd() {
        Map<Integer, Double[]> addData = new HashMap<>();
        _replicasToAdd.forEach(i -> {
            Double[] total = {1d, _partitionSize};
            addData.put(i.brokerId(), total);
        });
        return Collections.unmodifiableMap(addData);
    }

    public Map<Integer, Double[]> replicasToRemove() {
        Map<Integer, Double[]> removeData = new HashMap<>();
        _replicasToRemove.forEach(i -> {
            Double[] total = {1d, _partitionSize};
            removeData.put(i.brokerId(), total);
        });
        return Collections.unmodifiableMap(removeData);
    }
}
