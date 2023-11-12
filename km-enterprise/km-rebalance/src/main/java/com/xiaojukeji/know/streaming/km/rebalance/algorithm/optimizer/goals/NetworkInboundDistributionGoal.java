package com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.goals;

import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.ClusterModel;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.Resource;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.ActionAcceptance;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.ActionType;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.BalancingAction;

/**
 * @author leewei
 * @date 2022/5/20
 */
public class NetworkInboundDistributionGoal extends ResourceDistributionGoal {

    @Override
    protected Resource resource() {
        return Resource.NW_IN;
    }

    @Override
    public String name() {
        return NetworkInboundDistributionGoal.class.getSimpleName();
    }

    @Override
    public ActionAcceptance actionAcceptance(BalancingAction action, ClusterModel clusterModel) {
        // Leadership movement won't cause inbound network utilization change.
        return action.balancingAction() == ActionType.LEADERSHIP_MOVEMENT ? ActionAcceptance.ACCEPT : super.actionAcceptance(action, clusterModel);
    }
}
