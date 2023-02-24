package com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer;

import org.apache.kafka.common.TopicPartition;

public class BalancingAction {
    private final TopicPartition _tp;
    private final Integer _sourceBrokerId;
    private final Integer _destinationBrokerId;
    private final ActionType _actionType;

    public BalancingAction(TopicPartition tp,
                           Integer sourceBrokerId,
                           Integer destinationBrokerId,
                           ActionType actionType) {
        _tp = tp;
        _sourceBrokerId = sourceBrokerId;
        _destinationBrokerId = destinationBrokerId;
        _actionType = actionType;
    }

    public Integer sourceBrokerId() {
        return _sourceBrokerId;
    }

    public Integer destinationBrokerId() {
        return _destinationBrokerId;
    }

    public ActionType balancingAction() {
        return _actionType;
    }

    public TopicPartition topicPartition() {
        return _tp;
    }

    public String topic() {
        return _tp.topic();
    }
}
