package com.xiaojukeji.know.streaming.km.rebalance.optimizer.goals;

import com.xiaojukeji.know.streaming.km.rebalance.model.Broker;
import com.xiaojukeji.know.streaming.km.rebalance.model.ClusterModel;
import com.xiaojukeji.know.streaming.km.rebalance.model.Replica;
import com.xiaojukeji.know.streaming.km.rebalance.optimizer.ActionAcceptance;
import com.xiaojukeji.know.streaming.km.rebalance.optimizer.BalancingAction;
import com.xiaojukeji.know.streaming.km.rebalance.optimizer.OptimizationOptions;
import com.xiaojukeji.know.streaming.km.rebalance.utils.GoalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.rebalance.optimizer.ActionAcceptance.ACCEPT;
import static com.xiaojukeji.know.streaming.km.rebalance.optimizer.ActionAcceptance.REJECT;
import static com.xiaojukeji.know.streaming.km.rebalance.optimizer.ActionType.REPLICA_MOVEMENT;
import static com.xiaojukeji.know.streaming.km.rebalance.optimizer.ActionType.LEADERSHIP_MOVEMENT;

public class TopicLeadersDistributionGoal extends AbstractGoal {
    private static final Logger logger = LoggerFactory.getLogger(TopicLeadersDistributionGoal.class);
    private Map<String, Integer> _mustHaveTopicMinLeadersPerBroker;

    /**
     * 执行Topic Leader均衡
     */
    @Override
    protected void rebalanceForBroker(Broker broker, ClusterModel clusterModel, Set<Goal> optimizedGoals, OptimizationOptions optimizationOptions) {
        moveAwayOfflineReplicas(broker, clusterModel, optimizedGoals, optimizationOptions);
        if (_mustHaveTopicMinLeadersPerBroker.isEmpty()) {
            return;
        }
        if (optimizationOptions.offlineBrokers().contains(broker.id())) {
            return;
        }

        for (String topicName : _mustHaveTopicMinLeadersPerBroker.keySet()) {
            maybeMoveLeaderOfTopicToBroker(topicName, broker, clusterModel, optimizedGoals, optimizationOptions);
        }
    }

    /**
     * 初始化均衡条件:
     * 1.排除不需要的Broker、Topic
     * 2.计算每个Topic在集群中所有Broker的平均分布数量
     */
    @Override
    protected void initGoalState(ClusterModel clusterModel, OptimizationOptions optimizationOptions) {
        _mustHaveTopicMinLeadersPerBroker = new HashMap<>();
        Set<String> excludedTopics = optimizationOptions.excludedTopics();
        Set<Integer> excludedBrokers = optimizationOptions.offlineBrokers();
        Set<String> mustHaveTopicLeadersPerBroker = GoalUtils.getNotExcludeTopics(clusterModel, excludedTopics);
        Map<String, Integer> numLeadersByTopicNames = clusterModel.numLeadersPerTopic(mustHaveTopicLeadersPerBroker);
        Set<Broker> allBrokers = GoalUtils.getNotExcludeBrokers(clusterModel, excludedBrokers);
        for (String topicName : mustHaveTopicLeadersPerBroker) {
            int topicNumLeaders = numLeadersByTopicNames.get(topicName);
            int avgLeaders = allBrokers.size() == 0 ? 0 : (int) Math.ceil(topicNumLeaders / (double) allBrokers.size() * (1 + optimizationOptions.topicLeaderThreshold()));
            _mustHaveTopicMinLeadersPerBroker.put(topicName, avgLeaders);
        }
    }

    /**
     * 已满足均衡条件判断:
     * 1.待操作Broker的副本已下线
     * 2.待操作Broker上Topic Leader数量大于Topic平均分布数量
     */
    @Override
    protected boolean selfSatisfied(ClusterModel clusterModel, BalancingAction action) {
        Broker sourceBroker = clusterModel.broker(action.sourceBrokerId());
        Replica replicaToBeMoved = sourceBroker.replica(action.topicPartition());
        if (replicaToBeMoved.broker().replica(action.topicPartition()).isCurrentOffline()) {
            return action.balancingAction() == REPLICA_MOVEMENT;
        }
        String topicName = replicaToBeMoved.topicPartition().topic();
        return sourceBroker.numLeadersFor(topicName) > minTopicLeadersPerBroker(topicName);
    }

    /**
     * 获取Topic在每台Broker上的最小Leader数
     */
    private int minTopicLeadersPerBroker(String topicName) {
        return _mustHaveTopicMinLeadersPerBroker.get(topicName);
    }

    @Override
    public String name() {
        return TopicLeadersDistributionGoal.class.getSimpleName();
    }

    /**
     * 判断Topic Leader均衡动作是否可以执行
     */
    @Override
    public ActionAcceptance actionAcceptance(BalancingAction action, ClusterModel clusterModel) {
        if (_mustHaveTopicMinLeadersPerBroker.containsKey(action.topic())) {
            return ACCEPT;
        }
        switch (action.balancingAction()) {
            case LEADERSHIP_MOVEMENT:
            case REPLICA_MOVEMENT:
                Replica replicaToBeRemoved = clusterModel.broker(action.sourceBrokerId()).replica(action.topicPartition());
                return doesLeaderRemoveViolateOptimizedGoal(replicaToBeRemoved) ? REJECT : ACCEPT;
            default:
                throw new IllegalArgumentException("Unsupported balancing action " + action.balancingAction() + " is provided.");
        }
    }

    /**
     * 根据指定的副本判断是否可以执行均衡动作
     */
    private boolean doesLeaderRemoveViolateOptimizedGoal(Replica replicaToBeRemoved) {
        if (!replicaToBeRemoved.isLeader()) {
            return false;
        }
        String topic = replicaToBeRemoved.topicPartition().topic();
        if (!_mustHaveTopicMinLeadersPerBroker.containsKey(topic)) {
            return false;
        }
        int topicLeaderCountOnSourceBroker = replicaToBeRemoved.broker().numLeadersFor(topic);
        return topicLeaderCountOnSourceBroker <= minTopicLeadersPerBroker(topic);
    }

    /**
     * 执行具体的均衡逻辑:
     * 先通过Leader切换如果还不满足条件则进行副本迁移
     */
    private void maybeMoveLeaderOfTopicToBroker(String topicName,
                                                Broker broker,
                                                ClusterModel clusterModel,
                                                Set<Goal> optimizedGoals,
                                                OptimizationOptions optimizationOptions) {
        int topicLeaderCount = broker.numLeadersFor(topicName);
        //判断Topic在当前Broker上的Leader数量是否超过最小Leader分布
        if (topicLeaderCount >= minTopicLeadersPerBroker(topicName)) {
            return;
        }
        //获取Topic在当前Broker上的所有follower副本
        List<Replica> followerReplicas = broker.replicas().stream().filter(i -> !i.isLeader() && i.topicPartition().topic().equals(topicName)).collect(Collectors.toList());
        for (Replica followerReplica : followerReplicas) {
            //根据follower副本信息从集群中获取对应的Leader副本
            Replica leader = clusterModel.partition(followerReplica.topicPartition());
            //如果Leader副本所在Broker的Topic Leader数量超过最小Leader分布则进行Leader切换
            if (leader.broker().numLeadersFor(topicName) > minTopicLeadersPerBroker(topicName)) {
                if (maybeApplyBalancingAction(clusterModel, leader, Collections.singleton(broker),
                        LEADERSHIP_MOVEMENT, optimizedGoals, optimizationOptions) != null) {
                    topicLeaderCount++;
                    //Topic在当前Broker的Leader分布大于等于最小Leader分布则结束均衡
                    if (topicLeaderCount >= minTopicLeadersPerBroker(topicName)) {
                        return;
                    }
                }
            }
        }
        //根据Topic获取需要Leader数量大于最小Leader分布待迁移的Broker列表
        PriorityQueue<Broker> brokersWithExcessiveLeaderToMove = getBrokersWithExcessiveLeaderToMove(topicName, clusterModel);
        while (!brokersWithExcessiveLeaderToMove.isEmpty()) {
            Broker brokerWithExcessiveLeaderToMove = brokersWithExcessiveLeaderToMove.poll();
            List<Replica> leadersOfTopic = brokerWithExcessiveLeaderToMove.leaderReplicas().stream()
                    .filter(i -> i.topicPartition().topic().equals(topicName)).collect(Collectors.toList());
            boolean leaderMoved = false;
            int leaderMoveCount = leadersOfTopic.size();
            for (Replica leaderOfTopic : leadersOfTopic) {
                Broker destinationBroker = maybeApplyBalancingAction(clusterModel, leaderOfTopic, Collections.singleton(broker),
                        REPLICA_MOVEMENT, optimizedGoals, optimizationOptions);
                if (destinationBroker != null) {
                    leaderMoved = true;
                    break;
                }
            }
            if (leaderMoved) {
                //当前Topic Leader数量在满足最小Leader分布后则结束均衡
                topicLeaderCount++;
                if (topicLeaderCount >= minTopicLeadersPerBroker(topicName)) {
                    return;
                }
                //分布过多的Broker在进行副本迁移后Topic Leader依然大于最小Leader分布则继续迁移
                leaderMoveCount--;
                if (leaderMoveCount > minTopicLeadersPerBroker(topicName)) {
                    brokersWithExcessiveLeaderToMove.add(brokerWithExcessiveLeaderToMove);
                }
            }
        }
    }

    /**
     * 根据指定的TopicName,筛选出集群内超过该TopicName Leader平均分布数量的所有Broker并且降序排列
     */
    private PriorityQueue<Broker> getBrokersWithExcessiveLeaderToMove(String topicName, ClusterModel clusterModel) {
        PriorityQueue<Broker> brokersWithExcessiveLeaderToMove = new PriorityQueue<>((broker1, broker2) -> {
            int broker1LeaderCount = broker1.numLeadersFor(topicName);
            int broker2LeaderCount = broker2.numLeadersFor(topicName);
            int leaderCountCompareResult = Integer.compare(broker2LeaderCount, broker1LeaderCount);
            return leaderCountCompareResult == 0 ? Integer.compare(broker1.id(), broker2.id()) : leaderCountCompareResult;
        });
        clusterModel.brokers().stream().filter(broker -> broker.numLeadersFor(topicName) > minTopicLeadersPerBroker(topicName))
                .forEach(brokersWithExcessiveLeaderToMove::add);
        return brokersWithExcessiveLeaderToMove;
    }

    /**
     * 下线副本优先处理迁移
     */
    private void moveAwayOfflineReplicas(Broker srcBroker,
                                         ClusterModel clusterModel,
                                         Set<Goal> optimizedGoals,
                                         OptimizationOptions optimizationOptions) {
        if (srcBroker.currentOfflineReplicas().isEmpty()) {
            return;
        }
        SortedSet<Broker> eligibleBrokersToMoveOfflineReplicasTo = new TreeSet<>(
                Comparator.comparingInt((Broker broker) -> broker.replicas().size()).thenComparingInt(Broker::id));
        Set<Replica> offlineReplicas = new HashSet<>(srcBroker.currentOfflineReplicas());
        for (Replica offlineReplica : offlineReplicas) {
            if (maybeApplyBalancingAction(clusterModel, offlineReplica, eligibleBrokersToMoveOfflineReplicasTo,
                    REPLICA_MOVEMENT, optimizedGoals, optimizationOptions) == null) {
                logger.error(String.format("[%s] offline replica %s from broker %d (has %d replicas) move error", name(),
                        offlineReplica, srcBroker.id(), srcBroker.replicas().size()));
            }
        }
    }
}
