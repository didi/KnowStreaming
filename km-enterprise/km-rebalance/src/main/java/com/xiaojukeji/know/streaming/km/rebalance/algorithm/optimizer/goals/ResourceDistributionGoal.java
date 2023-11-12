package com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.goals;

import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.*;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.ActionAcceptance;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.ActionType;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.BalancingAction;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.OptimizationOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

/**
 * @author leewei
 * @date 2022/5/20
 */
public abstract class ResourceDistributionGoal extends AbstractGoal {
    private static final Logger logger = LoggerFactory.getLogger(ResourceDistributionGoal.class);
    private double balanceUpperThreshold;
    private double balanceLowerThreshold;

    @Override
    protected void initGoalState(ClusterModel clusterModel, OptimizationOptions optimizationOptions) {
        double avgUtilization = clusterModel.utilizationFor(resource());
        double balancePercentage = optimizationOptions.resourceBalancePercentageFor(resource());
        this.balanceUpperThreshold = avgUtilization * (1 + balancePercentage);
        this.balanceLowerThreshold = avgUtilization * (1 - balancePercentage);
    }

    @Override
    protected void rebalanceForBroker(Broker broker,
                                      ClusterModel clusterModel,
                                      Set<Goal> optimizedGoals,
                                      OptimizationOptions optimizationOptions) {
        double utilization = broker.utilizationFor(resource());

        boolean requireLessLoad = utilization > this.balanceUpperThreshold;
        boolean requireMoreLoad = utilization < this.balanceLowerThreshold;
        if (!requireMoreLoad && !requireLessLoad) {
            return;
        }

        // First try leadership movement
        if (resource() == Resource.NW_OUT || resource() == Resource.CPU) {
            if (requireLessLoad && rebalanceByMovingLoadOut(broker, clusterModel, optimizedGoals,
                    ActionType.LEADERSHIP_MOVEMENT, optimizationOptions)) {
                logger.debug("Successfully balanced {} for broker {} by moving out leaders.", resource(), broker.id());
                requireLessLoad = false;
            }
            if (requireMoreLoad && rebalanceByMovingLoadIn(broker, clusterModel, optimizedGoals,
                    ActionType.LEADERSHIP_MOVEMENT, optimizationOptions)) {
                logger.debug("Successfully balanced {} for broker {} by moving in leaders.", resource(), broker.id());
                requireMoreLoad = false;
            }
        }

        boolean balanced = true;
        if (requireLessLoad) {
            if (!rebalanceByMovingLoadOut(broker, clusterModel, optimizedGoals,
                    ActionType.REPLICA_MOVEMENT, optimizationOptions)) {
                balanced = rebalanceBySwappingLoadOut(broker, clusterModel, optimizedGoals, optimizationOptions);
            }
        } else if (requireMoreLoad) {
            if (!rebalanceByMovingLoadIn(broker, clusterModel, optimizedGoals,
                    ActionType.REPLICA_MOVEMENT, optimizationOptions)) {
                balanced = rebalanceBySwappingLoadIn(broker, clusterModel, optimizedGoals, optimizationOptions);
            }
        }
        if (balanced) {
            logger.debug("Successfully balanced {} for broker {} by moving leaders and replicas.", resource(), broker.id());
        }
    }

    private boolean rebalanceByMovingLoadOut(Broker broker,
                                             ClusterModel clusterModel,
                                             Set<Goal> optimizedGoals,
                                             ActionType actionType,
                                             OptimizationOptions optimizationOptions) {

        SortedSet<Broker> candidateBrokers = sortedCandidateBrokersUnderThreshold(clusterModel, this.balanceUpperThreshold, optimizationOptions, broker, false);
        SortedSet<Replica> replicasToMove = sortedCandidateReplicas(broker, actionType, optimizationOptions, true);

        for (Replica replica : replicasToMove) {
            Broker acceptedBroker = maybeApplyBalancingAction(clusterModel, replica, candidateBrokers, actionType, optimizedGoals, optimizationOptions);

            if (acceptedBroker != null) {
                if (broker.utilizationFor(resource()) < this.balanceUpperThreshold) {
                    return true;
                }
                // Remove and reinsert the broker so the order is correct.
                // candidateBrokers.remove(acceptedBroker);
                candidateBrokers.removeIf(b -> b.id() == acceptedBroker.id());
                if (acceptedBroker.utilizationFor(resource()) < this.balanceUpperThreshold) {
                    candidateBrokers.add(acceptedBroker);
                }
            }
        }

        return false;
    }

    private boolean rebalanceByMovingLoadIn(Broker broker,
                                            ClusterModel clusterModel,
                                            Set<Goal> optimizedGoals,
                                            ActionType actionType,
                                            OptimizationOptions optimizationOptions) {
        SortedSet<Broker> candidateBrokers = sortedCandidateBrokersOverThreshold(clusterModel, this.balanceLowerThreshold, optimizationOptions, broker, true);
        Iterator<Broker> candidateBrokersIt = candidateBrokers.iterator();
        Broker nextCandidateBroker = null;
        while (true) {
            Broker candidateBroker;
            if (nextCandidateBroker != null) {
                candidateBroker = nextCandidateBroker;
                nextCandidateBroker = null;
            } else if (candidateBrokersIt.hasNext()) {
                candidateBroker = candidateBrokersIt.next();
            } else {
                break;
            }
            SortedSet<Replica> replicasToMove = sortedCandidateReplicas(candidateBroker, actionType, optimizationOptions, true);

            for (Replica replica : replicasToMove) {
                Broker acceptedBroker = maybeApplyBalancingAction(clusterModel, replica, Collections.singletonList(broker), actionType, optimizedGoals, optimizationOptions);
                if (acceptedBroker != null) {
                    if (broker.utilizationFor(resource()) > this.balanceLowerThreshold) {
                        return true;
                    }
                    if (candidateBrokersIt.hasNext() || nextCandidateBroker != null) {
                        if (nextCandidateBroker == null) {
                            nextCandidateBroker = candidateBrokersIt.next();
                        }
                        if (candidateBroker.utilizationFor(resource()) < nextCandidateBroker.utilizationFor(resource())) {
                            break;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean rebalanceBySwappingLoadOut(Broker broker,
                                             ClusterModel clusterModel,
                                             Set<Goal> optimizedGoals,
                                             OptimizationOptions optimizationOptions) {
        return false;
    }

    private boolean rebalanceBySwappingLoadIn(Broker broker,
                                               ClusterModel clusterModel,
                                               Set<Goal> optimizedGoals,
                                               OptimizationOptions optimizationOptions) {
        return false;
    }

    private SortedSet<Broker> sortedCandidateBrokersUnderThreshold(ClusterModel clusterModel,
                                                     double utilizationThreshold,
                                                     OptimizationOptions optimizationOptions,
                                                     Broker excludedBroker,
                                                     boolean reverse) {
        return clusterModel.sortedBrokersFor(
                b -> b.utilizationFor(resource()) < utilizationThreshold
                        && !excludedBroker.equals(b)
                        // filter brokers
                        && (optimizationOptions.balanceBrokers().isEmpty() || optimizationOptions.balanceBrokers().contains(b.id()))
                , resource(), reverse);
    }

    private SortedSet<Broker> sortedCandidateBrokersOverThreshold(ClusterModel clusterModel,
                                                                   double utilizationThreshold,
                                                                   OptimizationOptions optimizationOptions,
                                                                   Broker excludedBroker,
                                                                   boolean reverse) {
        return clusterModel.sortedBrokersFor(
                b -> b.utilizationFor(resource()) > utilizationThreshold
                        && !excludedBroker.equals(b)
                        // filter brokers
                        && (optimizationOptions.balanceBrokers().isEmpty() || optimizationOptions.balanceBrokers().contains(b.id()))
                , resource(), reverse);
    }

    private SortedSet<Replica> sortedCandidateReplicas(Broker broker,
                                                       ActionType actionType,
                                                       OptimizationOptions optimizationOptions,
                                                       boolean reverse) {
        return broker.sortedReplicasFor(
                // exclude topic
                r -> !optimizationOptions.excludedTopics().contains(r.topicPartition().topic())
                        && r.load().loadFor(resource()) > 0.0
                        // LEADERSHIP_MOVEMENT or NW_OUT is require leader replica
                        && (actionType != ActionType.LEADERSHIP_MOVEMENT && resource() != Resource.NW_OUT || r.isLeader())
                , resource(), reverse);
    }

    protected abstract Resource resource();

    @Override
    protected boolean selfSatisfied(ClusterModel clusterModel, BalancingAction action) {
        Broker destinationBroker = clusterModel.broker(action.destinationBrokerId());
        Broker sourceBroker = clusterModel.broker(action.sourceBrokerId());
        Replica sourceReplica = sourceBroker.replica(action.topicPartition());

        Load loadToChange;
        if (action.balancingAction() == ActionType.LEADERSHIP_MOVEMENT) {
            Replica destinationReplica = destinationBroker.replica(action.topicPartition());
            Load delta = new Load();
            delta.addLoad(sourceReplica.load());
            delta.subtractLoad(destinationReplica.load());
            loadToChange = delta;
        } else {
            loadToChange = sourceReplica.load();
        }
        double sourceUtilization = sourceBroker.expectedUtilizationAfterRemove(resource(), loadToChange);
        double destinationUtilization = destinationBroker.expectedUtilizationAfterAdd(resource(), loadToChange);

        return sourceUtilization >= this.balanceLowerThreshold && destinationUtilization <= this.balanceUpperThreshold;
    }

    @Override
    public ActionAcceptance actionAcceptance(BalancingAction action, ClusterModel clusterModel) {
        return this.selfSatisfied(clusterModel, action) ? ActionAcceptance.ACCEPT : ActionAcceptance.REJECT;
    }
}
