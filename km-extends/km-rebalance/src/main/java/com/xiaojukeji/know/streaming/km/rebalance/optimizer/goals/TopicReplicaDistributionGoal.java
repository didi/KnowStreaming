package com.xiaojukeji.know.streaming.km.rebalance.optimizer.goals;

import com.xiaojukeji.know.streaming.km.rebalance.model.Broker;
import com.xiaojukeji.know.streaming.km.rebalance.model.ClusterModel;
import com.xiaojukeji.know.streaming.km.rebalance.model.Replica;
import com.xiaojukeji.know.streaming.km.rebalance.optimizer.ActionAcceptance;
import com.xiaojukeji.know.streaming.km.rebalance.optimizer.ActionType;
import com.xiaojukeji.know.streaming.km.rebalance.optimizer.BalancingAction;
import com.xiaojukeji.know.streaming.km.rebalance.optimizer.OptimizationOptions;
import com.xiaojukeji.know.streaming.km.rebalance.utils.GoalUtils;

import java.util.*;
import java.util.stream.Collectors;

public class TopicReplicaDistributionGoal extends AbstractGoal {
    private final Map<String, Integer> _balanceUpperLimitByTopic;
    private final Map<String, Integer> _balanceLowerLimitByTopic;
    private Set<Broker> _brokersAllowedReplicaMove;
    private final Map<String, Double> _avgTopicReplicasOnBroker;

    public TopicReplicaDistributionGoal() {
        _balanceUpperLimitByTopic = new HashMap<>();
        _balanceLowerLimitByTopic = new HashMap<>();
        _avgTopicReplicasOnBroker = new HashMap<>();
    }

    @Override
    protected void rebalanceForBroker(Broker broker, ClusterModel clusterModel, Set<Goal> optimizedGoals, OptimizationOptions optimizationOptions) {
        for (String topic : broker.topics()) {
            if (isTopicExcludedFromRebalance(topic)) {
                continue;
            }
            Collection<Replica> replicas = broker.replicasOfTopicInBroker(topic);
            int numTopicReplicas = replicas.size();
            boolean isExcludedForReplicaMove = isExcludedForReplicaMove(broker);
            int numOfflineTopicReplicas = GoalUtils.retainCurrentOfflineBrokerReplicas(broker, replicas).size();
            boolean requireLessReplicas = numOfflineTopicReplicas > 0 || numTopicReplicas > _balanceUpperLimitByTopic.get(topic) && !isExcludedForReplicaMove;
            boolean requireMoreReplicas = !isExcludedForReplicaMove && numTopicReplicas - numOfflineTopicReplicas < _balanceLowerLimitByTopic.get(topic);

            if (requireLessReplicas) {
                rebalanceByMovingReplicasOut(broker, topic, clusterModel, optimizedGoals, optimizationOptions);
            }
            if (requireMoreReplicas) {
                rebalanceByMovingReplicasIn(broker, topic, clusterModel, optimizedGoals, optimizationOptions);
            }
        }
    }

    /**
     * 初始化均衡条件:
     * 1.Topic平均分布副本数量
     * 2.Topic在平均副本的基础上向上浮动数量
     * 3.Topic在平均副本的基础上向下浮动数量
     */
    @Override
    protected void initGoalState(ClusterModel clusterModel, OptimizationOptions optimizationOptions) {
        Set<String> excludedTopics = optimizationOptions.excludedTopics();
        Set<Integer> excludedBrokers = optimizationOptions.offlineBrokers();
        Set<String> topicsAllowedRebalance = GoalUtils.getNotExcludeTopics(clusterModel, excludedTopics);
        _brokersAllowedReplicaMove = GoalUtils.getNotExcludeBrokers(clusterModel, excludedBrokers);
        if (_brokersAllowedReplicaMove.isEmpty()) {
            return;
        }
        for (String topic : topicsAllowedRebalance) {
            int numTopicReplicas = clusterModel.numTopicReplicas(topic);
            _avgTopicReplicasOnBroker.put(topic, numTopicReplicas / (double) _brokersAllowedReplicaMove.size());
            _balanceUpperLimitByTopic.put(topic, balanceUpperLimit(topic, optimizationOptions));
            _balanceLowerLimitByTopic.put(topic, balanceLowerLimit(topic, optimizationOptions));
        }
    }

    /**
     * 指定Topic平均副本向下浮动,默认10%
     */
    private Integer balanceLowerLimit(String topic, OptimizationOptions optimizationOptions) {
        return (int) Math.floor(_avgTopicReplicasOnBroker.get(topic)
                * Math.max(0, (1 - optimizationOptions.topicReplicaThreshold())));
    }

    /**
     * 指定Topic平均副本向上浮动,默认10%
     */
    private Integer balanceUpperLimit(String topic, OptimizationOptions optimizationOptions) {
        return (int) Math.ceil(_avgTopicReplicasOnBroker.get(topic)
                * (1 + optimizationOptions.topicReplicaThreshold()));
    }

    @Override
    protected boolean selfSatisfied(ClusterModel clusterModel, BalancingAction action) {
        Broker sourceBroker = clusterModel.broker(action.sourceBrokerId());
        if (sourceBroker.replica(action.topicPartition()).isCurrentOffline()) {
            return action.balancingAction() == ActionType.REPLICA_MOVEMENT;
        }
        Broker destinationBroker = clusterModel.broker(action.destinationBrokerId());
        String sourceTopic = action.topic();
        return isReplicaCountAddUpperLimit(sourceTopic, destinationBroker)
                && (isExcludedForReplicaMove(sourceBroker) || isReplicaCountRemoveLowerLimit(sourceTopic, sourceBroker));

    }

    @Override
    public String name() {
        return TopicReplicaDistributionGoal.class.getSimpleName();
    }

    @Override
    public ActionAcceptance actionAcceptance(BalancingAction action, ClusterModel clusterModel) {
        Broker sourceBroker = clusterModel.broker(action.sourceBrokerId());
        Broker destinationBroker = clusterModel.broker(action.destinationBrokerId());
        String sourceTopic = action.topic();
        switch (action.balancingAction()) {
            case LEADERSHIP_MOVEMENT:
                return ActionAcceptance.ACCEPT;
            case REPLICA_MOVEMENT:
                return (isReplicaCountAddUpperLimit(sourceTopic, destinationBroker)
                        && (isExcludedForReplicaMove(sourceBroker)
                        || isReplicaCountRemoveLowerLimit(sourceTopic, sourceBroker))) ? ActionAcceptance.ACCEPT : ActionAcceptance.REJECT;
            default:
                throw new IllegalArgumentException("Unsupported balancing action " + action.balancingAction() + " is provided.");
        }
    }

    /**
     * 指定的Broker上存在Topic副本数大于阈值则迁出副本
     */
    private boolean rebalanceByMovingReplicasOut(Broker broker,
                                                 String topic,
                                                 ClusterModel clusterModel,
                                                 Set<Goal> optimizedGoals,
                                                 OptimizationOptions optimizationOptions) {
        //筛选出现低于UpperLimit的所有Broker做为存放目标
        SortedSet<Broker> candidateBrokers = new TreeSet<>(
                Comparator.comparingInt((Broker b) -> b.numReplicasOfTopicInBroker(topic)).thenComparingInt(Broker::id));
        Set<Broker> filterUpperLimitBroker = clusterModel.brokers().stream().filter(b -> b.numReplicasOfTopicInBroker(topic) < _balanceUpperLimitByTopic.get(topic)).collect(Collectors.toSet());
        candidateBrokers.addAll(filterUpperLimitBroker);
        Collection<Replica> replicasOfTopicInBroker = broker.replicasOfTopicInBroker(topic);
        int numReplicasOfTopicInBroker = replicasOfTopicInBroker.size();
        int numOfflineTopicReplicas = GoalUtils.retainCurrentOfflineBrokerReplicas(broker, replicasOfTopicInBroker).size();
        int balanceUpperLimitForSourceBroker = isExcludedForReplicaMove(broker) ? 0 : _balanceUpperLimitByTopic.get(topic);
        boolean wasUnableToMoveOfflineReplica = false;

        for (Replica replica : replicasToMoveOut(broker, topic)) {
            //当前Broker没有离线副本及Topic的副本数量低于UpperLimit则结束均衡
            if (wasUnableToMoveOfflineReplica && !replica.isCurrentOffline() && numReplicasOfTopicInBroker <= balanceUpperLimitForSourceBroker) {
                return false;
            }
            boolean wasOffline = replica.isCurrentOffline();
            Broker b = maybeApplyBalancingAction(clusterModel, replica, candidateBrokers, ActionType.REPLICA_MOVEMENT,
                    optimizedGoals, optimizationOptions);
            // Only check if we successfully moved something.
            if (b != null) {
                if (wasOffline) {
                    numOfflineTopicReplicas--;
                }
                if (--numReplicasOfTopicInBroker <= (numOfflineTopicReplicas == 0 ? balanceUpperLimitForSourceBroker : 0)) {
                    return false;
                }

                // Remove and reinsert the broker so the order is correct.
                candidateBrokers.remove(b);
                if (b.numReplicasOfTopicInBroker(topic) < _balanceUpperLimitByTopic.get(topic)) {
                    candidateBrokers.add(b);
                }
            } else if (wasOffline) {
                wasUnableToMoveOfflineReplica = true;
            }
        }
        return !broker.replicasOfTopicInBroker(topic).isEmpty();
    }

    /**
     * 1.离线副本优行处理
     * 2.小分区号优行处理
     */
    private SortedSet<Replica> replicasToMoveOut(Broker broker, String topic) {
        SortedSet<Replica> replicasToMoveOut = new TreeSet<>((r1, r2) -> {
            boolean r1Offline = broker.currentOfflineReplicas().contains(r1);
            boolean r2Offline = broker.currentOfflineReplicas().contains(r2);
            if (r1Offline && !r2Offline) {
                return -1;
            } else if (!r1Offline && r2Offline) {
                return 1;
            }
            if (r1.topicPartition().partition() > r2.topicPartition().partition()) {
                return 1;
            } else if (r1.topicPartition().partition() < r2.topicPartition().partition()) {
                return -1;
            }
            return 0;
        });
        replicasToMoveOut.addAll(broker.replicasOfTopicInBroker(topic));
        return replicasToMoveOut;
    }

    /**
     * Topic副本数>最低阈值的副本，迁到指定的Broker上
     */
    private boolean rebalanceByMovingReplicasIn(Broker broker,
                                                String topic,
                                                ClusterModel clusterModel,
                                                Set<Goal> optimizedGoals,
                                                OptimizationOptions optimizationOptions) {
        PriorityQueue<Broker> eligibleBrokers = new PriorityQueue<>((b1, b2) -> {
            Collection<Replica> replicasOfTopicInB2 = b2.replicasOfTopicInBroker(topic);
            int numReplicasOfTopicInB2 = replicasOfTopicInB2.size();
            int numOfflineTopicReplicasInB2 = GoalUtils.retainCurrentOfflineBrokerReplicas(b2, replicasOfTopicInB2).size();
            Collection<Replica> replicasOfTopicInB1 = b1.replicasOfTopicInBroker(topic);
            int numReplicasOfTopicInB1 = replicasOfTopicInB1.size();
            int numOfflineTopicReplicasInB1 = GoalUtils.retainCurrentOfflineBrokerReplicas(b1, replicasOfTopicInB1).size();

            int resultByOfflineReplicas = Integer.compare(numOfflineTopicReplicasInB2, numOfflineTopicReplicasInB1);
            if (resultByOfflineReplicas == 0) {
                int resultByAllReplicas = Integer.compare(numReplicasOfTopicInB2, numReplicasOfTopicInB1);
                return resultByAllReplicas == 0 ? Integer.compare(b1.id(), b2.id()) : resultByAllReplicas;
            }
            return resultByOfflineReplicas;
        });
        //筛选当前Topic高于LowerLimit、存在离线副本、的所有Broker做为需要迁的副本
        for (Broker sourceBroker : clusterModel.brokers()) {
            if (sourceBroker.numReplicasOfTopicInBroker(topic) > _balanceLowerLimitByTopic.get(topic)
                    || !sourceBroker.currentOfflineReplicas().isEmpty() || isExcludedForReplicaMove(sourceBroker)) {
                eligibleBrokers.add(sourceBroker);
            }
        }
        Collection<Replica> replicasOfTopicInBroker = broker.replicasOfTopicInBroker(topic);
        int numReplicasOfTopicInBroker = replicasOfTopicInBroker.size();
        //当前Broker做为存放目标
        Set<Broker> candidateBrokers = Collections.singleton(broker);
        while (!eligibleBrokers.isEmpty()) {
            Broker sourceBroker = eligibleBrokers.poll();
            SortedSet<Replica> replicasToMove = replicasToMoveOut(sourceBroker, topic);
            int numOfflineTopicReplicas = GoalUtils.retainCurrentOfflineBrokerReplicas(sourceBroker, replicasToMove).size();

            for (Replica replica : replicasToMove) {
                boolean wasOffline = replica.isCurrentOffline();
                Broker b = maybeApplyBalancingAction(clusterModel, replica, candidateBrokers, ActionType.REPLICA_MOVEMENT,
                        optimizedGoals, optimizationOptions);
                if (b != null) {
                    if (wasOffline) {
                        numOfflineTopicReplicas--;
                    }
                    if (++numReplicasOfTopicInBroker >= _balanceLowerLimitByTopic.get(topic)) {
                        return false;
                    }
                    if (!eligibleBrokers.isEmpty() && numOfflineTopicReplicas == 0
                            && sourceBroker.numReplicasOfTopicInBroker(topic) < eligibleBrokers.peek().numReplicasOfTopicInBroker(topic)) {
                        eligibleBrokers.add(sourceBroker);
                        break;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 目标Broker增加副本后，Topic副本数<=最高阈值
     */
    private boolean isReplicaCountAddUpperLimit(String topic, Broker destinationBroker) {
        int numTopicReplicas = destinationBroker.numReplicasOfTopicInBroker(topic);
        int brokerBalanceUpperLimit = _balanceUpperLimitByTopic.get(topic);
        return numTopicReplicas + 1 <= brokerBalanceUpperLimit;
    }

    /**
     * 源Broker迁走副本后，Topic副本数>=最低阈值
     */
    private boolean isReplicaCountRemoveLowerLimit(String topic, Broker sourceBroker) {
        int numTopicReplicas = sourceBroker.numReplicasOfTopicInBroker(topic);
        int brokerBalanceLowerLimit = _balanceLowerLimitByTopic.get(topic);
        return numTopicReplicas - 1 >= brokerBalanceLowerLimit;
    }

    /**
     * 判断指定的Broker是否可以进行副本迁移操作
     */
    private boolean isExcludedForReplicaMove(Broker broker) {
        return !_brokersAllowedReplicaMove.contains(broker);
    }

    /**
     * 判断指定的Topic是否在可均衡的列表中
     */
    private boolean isTopicExcludedFromRebalance(String topic) {
        return _avgTopicReplicasOnBroker.get(topic) == null;
    }
}
