package com.xiaojukeji.know.streaming.km.rebalance.optimizer;

import com.xiaojukeji.know.streaming.km.rebalance.executor.common.OptimizerResult;
import com.xiaojukeji.know.streaming.km.rebalance.model.ClusterModel;
import com.xiaojukeji.know.streaming.km.rebalance.model.ReplicaPlacementInfo;
import com.xiaojukeji.know.streaming.km.rebalance.optimizer.goals.Goal;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author leewei
 * @date 2022/4/29
 */
public class GoalOptimizer {
    private static final Logger logger = LoggerFactory.getLogger(GoalOptimizer.class);

    public OptimizerResult optimizations(ClusterModel clusterModel, OptimizationOptions optimizationOptions) {
        Set<Goal> optimizedGoals = new HashSet<>();
        OptimizerResult optimizerResult = new OptimizerResult(clusterModel, optimizationOptions);
        optimizerResult.setBalanceBrokersFormBefore(clusterModel.brokers());
        Map<TopicPartition, List<ReplicaPlacementInfo>> initReplicaDistribution = clusterModel.getReplicaDistribution();
        Map<TopicPartition, ReplicaPlacementInfo> initLeaderDistribution = clusterModel.getLeaderDistribution();
        try {
            Map<String, Goal> goalMap = new HashMap<>();
            ServiceLoader<Goal> serviceLoader = ServiceLoader.load(Goal.class);
            for (Goal goal : serviceLoader) {
                goalMap.put(goal.name(), goal);
            }
            for (String g : optimizationOptions.goals()) {
                Goal goal = goalMap.get(g);
                if (goal != null) {
                    logger.info("Start {} balancing", goal.name());
                    goal.optimize(clusterModel, optimizedGoals, optimizationOptions);
                    optimizedGoals.add(goal);
                }
            }
        } catch (Exception e) {
            logger.error("Cluster balancing goal error", e);
        }
        Set<ExecutionProposal> proposals = AnalyzerUtils.getDiff(initReplicaDistribution, initLeaderDistribution, clusterModel);
        optimizerResult.setBalanceBrokersFormAfter(clusterModel.brokers());
        optimizerResult.setExecutionProposal(proposals);
        return optimizerResult;
    }
}
