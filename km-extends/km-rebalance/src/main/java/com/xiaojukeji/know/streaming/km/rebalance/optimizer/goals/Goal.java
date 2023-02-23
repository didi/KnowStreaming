package com.xiaojukeji.know.streaming.km.rebalance.optimizer.goals;

import com.xiaojukeji.know.streaming.km.rebalance.model.ClusterModel;
import com.xiaojukeji.know.streaming.km.rebalance.optimizer.ActionAcceptance;
import com.xiaojukeji.know.streaming.km.rebalance.optimizer.BalancingAction;
import com.xiaojukeji.know.streaming.km.rebalance.optimizer.OptimizationOptions;

import java.util.Set;

public interface Goal {

    void optimize(ClusterModel clusterModel, Set<Goal> optimizedGoals, OptimizationOptions optimizationOptions);

    String name();

    ActionAcceptance actionAcceptance(BalancingAction action, ClusterModel clusterModel);
}