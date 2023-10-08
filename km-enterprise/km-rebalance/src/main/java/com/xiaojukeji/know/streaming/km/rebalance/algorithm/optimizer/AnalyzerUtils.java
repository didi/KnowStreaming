package com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer;

import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.ClusterModel;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.Replica;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.ReplicaPlacementInfo;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.Resource;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.goals.Goal;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.ActionAcceptance.ACCEPT;

public class AnalyzerUtils {
    private static final Logger logger = LoggerFactory.getLogger(AnalyzerUtils.class);
    public static Set<String> getSplitTopics(String value) {
        if (StringUtils.isBlank(value)) {
            return new HashSet<>();
        }
        String[] arr = value.split(",");
        return Arrays.stream(arr).collect(Collectors.toSet());
    }

    public static Set<Integer> getSplitBrokers(String value) {
        if (StringUtils.isBlank(value)) {
            return new HashSet<>();
        }
        String[] arr = value.split(",");
        return Arrays.stream(arr).map(Integer::valueOf).collect(Collectors.toSet());
    }

    public static Set<ExecutionProposal> getDiff(Map<TopicPartition, List<ReplicaPlacementInfo>> initialReplicaDistribution,
                                                 Map<TopicPartition, ReplicaPlacementInfo> initialLeaderDistribution,
                                                 ClusterModel optimizedClusterModel) {
        Map<TopicPartition, List<ReplicaPlacementInfo>> finalReplicaDistribution = optimizedClusterModel.getReplicaDistribution();
        if (!initialReplicaDistribution.keySet().equals(finalReplicaDistribution.keySet())) {
            throw new IllegalArgumentException("diff distributions with different partitions.");
        }
        Set<ExecutionProposal> diff = new HashSet<>();
        for (Map.Entry<TopicPartition, List<ReplicaPlacementInfo>> entry : initialReplicaDistribution.entrySet()) {
            TopicPartition tp = entry.getKey();
            List<ReplicaPlacementInfo> initialReplicas = entry.getValue();
            List<ReplicaPlacementInfo> finalReplicas = finalReplicaDistribution.get(tp);
            Replica finalLeader = optimizedClusterModel.partition(tp);
            ReplicaPlacementInfo finalLeaderPlacementInfo = new ReplicaPlacementInfo(finalLeader.broker().id(), finalLeader.logDir());
            if (finalReplicas.equals(initialReplicas) && initialLeaderDistribution.get(tp).equals(finalLeaderPlacementInfo)) {
                continue;
            }
            if(!balanceCheck(initialReplicas, finalReplicas)) {
                logger.warn("illegal balance, topicPartition {}, before {}, after {}", tp, initialReplicas, finalReplicas);
                continue;
            }
            if (!finalLeaderPlacementInfo.equals(finalReplicas.get(0))) {
                int leaderPos = finalReplicas.indexOf(finalLeaderPlacementInfo);
                finalReplicas.set(leaderPos, finalReplicas.get(0));
                finalReplicas.set(0, finalLeaderPlacementInfo);
            }
            double partitionSize = optimizedClusterModel.partition(tp).load().loadFor(Resource.DISK);
            diff.add(new ExecutionProposal(tp, partitionSize, initialLeaderDistribution.get(tp), initialReplicas, finalReplicas));
        }
        return diff;
    }


    /**
     * 检查是否会出现相同broker不同logDir之间迁移的情况，此情况会触发kafka bug:https://issues.apache.org/jira/browse/KAFKA-9087
     * @param beforeReplicaPlacementInfos
     * @param afterReplicaPlacementInfos
     * @return
     */
    private static boolean balanceCheck(List<ReplicaPlacementInfo> beforeReplicaPlacementInfos, List<ReplicaPlacementInfo> afterReplicaPlacementInfos) {
        for(ReplicaPlacementInfo beforePlacement : beforeReplicaPlacementInfos) {
            for(ReplicaPlacementInfo afterPlacement : afterReplicaPlacementInfos) {
                if(beforePlacement.brokerId() == afterPlacement.brokerId()
                        && !"".equals(beforePlacement.logdir())
                        && !beforePlacement.logdir().equals(afterPlacement.logdir())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static ActionAcceptance isProposalAcceptableForOptimizedGoals(Set<Goal> optimizedGoals,
                                                                         BalancingAction proposal,
                                                                         ClusterModel clusterModel) {
        for (Goal optimizedGoal : optimizedGoals) {
            ActionAcceptance actionAcceptance = optimizedGoal.actionAcceptance(proposal, clusterModel);
            if (actionAcceptance != ACCEPT) {
                return actionAcceptance;
            }
        }
        return ACCEPT;
    }
}
