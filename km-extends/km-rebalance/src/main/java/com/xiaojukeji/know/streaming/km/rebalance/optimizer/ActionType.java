package com.xiaojukeji.know.streaming.km.rebalance.optimizer;

public enum ActionType {
    REPLICA_MOVEMENT("REPLICA"),
    LEADERSHIP_MOVEMENT("LEADER");
//    REPLICA_SWAP("SWAP");

    private final String _balancingAction;

    ActionType(String balancingAction) {
        _balancingAction = balancingAction;
    }

    @Override
    public String toString() {
        return _balancingAction;
    }
}
