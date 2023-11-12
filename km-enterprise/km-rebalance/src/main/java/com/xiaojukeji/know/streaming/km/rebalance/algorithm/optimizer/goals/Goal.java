package com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.goals;

import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.ClusterModel;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.ActionAcceptance;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.BalancingAction;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.OptimizationOptions;

import java.util.Set;

public interface Goal {

    void optimize(ClusterModel clusterModel, Set<Goal> optimizedGoals, OptimizationOptions optimizationOptions);

    String name();

    ActionAcceptance actionAcceptance(BalancingAction action, ClusterModel clusterModel);
}