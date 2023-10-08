package com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.goals;

import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.*;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.ActionAcceptance;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.ActionType;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.BalancingAction;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.OptimizationOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author leewei
 * @date 2022/5/20
 */
public abstract class ResourceDistributionGoal extends AbstractLogDirGoal {
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
        for(LogDir logDir : broker.logDirs().values()) {
            double utilization = logDir.utilizationFor(resource());

            boolean requireLessLoad = utilization > this.balanceUpperThreshold;
            boolean requireMoreLoad = utilization < this.balanceLowerThreshold;
            if (!requireMoreLoad && !requireLessLoad) {
                return;
            }

            // First try leadership movement
            if (resource() == Resource.NW_OUT || resource() == Resource.CPU) {
                if (requireLessLoad && rebalanceByMovingLoadOut(logDir, clusterModel, optimizedGoals,
                        ActionType.LEADERSHIP_MOVEMENT, optimizationOptions)) {
                    logger.debug("Successfully balanced {} for broker {} logDir {} by moving out leaders.", resource(), broker.id(), logDir.name());
                    requireLessLoad = false;
                }
                if (requireMoreLoad && rebalanceByMovingLoadIn(logDir, clusterModel, optimizedGoals,
                        ActionType.LEADERSHIP_MOVEMENT, optimizationOptions)) {
                    logger.debug("Successfully balanced {} for broker {} logDir {} by moving in leaders.", resource(), broker.id(), logDir.name());
                    requireMoreLoad = false;
                }
            }

            boolean balanced = true;
            if (requireLessLoad) {
                if (!rebalanceByMovingLoadOut(logDir, clusterModel, optimizedGoals,
                        ActionType.REPLICA_MOVEMENT, optimizationOptions)) {
                    balanced = rebalanceBySwappingLoadOut(logDir, clusterModel, optimizedGoals, optimizationOptions);
                }
            } else if (requireMoreLoad) {
                if (!rebalanceByMovingLoadIn(logDir, clusterModel, optimizedGoals,
                        ActionType.REPLICA_MOVEMENT, optimizationOptions)) {
                    balanced = rebalanceBySwappingLoadIn(logDir, clusterModel, optimizedGoals, optimizationOptions);
                }
            }
            if (balanced) {
                logger.debug("Successfully balanced {} for broker {} logDir {} by moving leaders and replicas.", resource(), broker.id(), logDir.name());
            } else {
                logger.debug("Balance {} for broker {} logDir {} failed.", resource(), broker.id(), logDir.name());
            }
        }
    }

    private boolean rebalanceByMovingLoadOut(LogDir logDir,
                                             ClusterModel clusterModel,
                                             Set<Goal> optimizedGoals,
                                             ActionType actionType,
                                             OptimizationOptions optimizationOptions) {

        SortedSet<LogDir> candidateLogDirs = sortedCandidateLogDirsUnderThreshold(clusterModel, this.balanceUpperThreshold, optimizationOptions, logDir.broker(), false);
        SortedSet<Replica> replicasToMove = sortedCandidateReplicas(logDir, actionType, optimizationOptions, true);

        for (Replica replica : replicasToMove) {
            LogDir acceptedLogDir = maybeApplyBalancingAction(clusterModel, replica, candidateLogDirs, actionType, optimizedGoals, optimizationOptions);

            if (acceptedLogDir != null) {
                if (logDir.utilizationFor(resource()) < this.balanceUpperThreshold) {
                    return true;
                }
                // Remove and reinsert the logDir so the order is correct.
                // candidateBrokers.remove(acceptedBroker);
                candidateLogDirs.removeIf(b -> b.broker().id() == acceptedLogDir.broker().id() && b.name().equals(acceptedLogDir.name()));
                if (acceptedLogDir.utilizationFor(resource()) < this.balanceUpperThreshold) {
                    candidateLogDirs.add(acceptedLogDir);
                }
            }
        }

        return false;
    }

    private boolean rebalanceByMovingLoadIn(LogDir logDir,
                                            ClusterModel clusterModel,
                                            Set<Goal> optimizedGoals,
                                            ActionType actionType,
                                            OptimizationOptions optimizationOptions) {
        SortedSet<LogDir> candidateLogDirs = sortedCandidateLogDirsOverThreshold(clusterModel, this.balanceLowerThreshold, optimizationOptions, logDir.broker(), true);
        Iterator<LogDir> candidateLogDirsIt = candidateLogDirs.iterator();
        LogDir nextCandidateLogDir = null;
        while (true) {
            LogDir candidateLogDir;
            if (nextCandidateLogDir != null) {
                candidateLogDir = nextCandidateLogDir;
                nextCandidateLogDir = null;
            } else if (candidateLogDirsIt.hasNext()) {
                candidateLogDir = candidateLogDirsIt.next();
            } else {
                break;
            }
            SortedSet<Replica> replicasToMove = sortedCandidateReplicas(candidateLogDir, actionType, optimizationOptions, true);

            for (Replica replica : replicasToMove) {
                LogDir acceptedLogDir = maybeApplyBalancingAction(clusterModel, replica, Collections.singletonList(logDir), actionType, optimizedGoals, optimizationOptions);
                if (acceptedLogDir != null) {
                    if (logDir.utilizationFor(resource()) > this.balanceLowerThreshold) {
                        return true;
                    }
                    if (candidateLogDirsIt.hasNext() || nextCandidateLogDir != null) {
                        if (nextCandidateLogDir == null) {
                            nextCandidateLogDir = candidateLogDirsIt.next();
                        }
                        if (candidateLogDir.utilizationFor(resource()) < nextCandidateLogDir.utilizationFor(resource())) {
                            break;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean rebalanceBySwappingLoadOut(LogDir logDir,
                                             ClusterModel clusterModel,
                                             Set<Goal> optimizedGoals,
                                             OptimizationOptions optimizationOptions) {
        return false;
    }

    private boolean rebalanceBySwappingLoadIn(LogDir logDir,
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

    private SortedSet<LogDir> sortedCandidateLogDirsUnderThreshold(ClusterModel clusterModel,
                                                                   double utilizationThreshold,
                                                                   OptimizationOptions optimizationOptions,
                                                                   Broker excludedBroker,
                                                                   boolean reverse) {

        return clusterModel.sortedLogDirsFor(
                b -> b.utilizationFor(resource()) < utilizationThreshold
                        && !excludedBroker.equals(b.broker())
                        // filter brokers
                        && (optimizationOptions.balanceBrokers().isEmpty() || optimizationOptions.balanceBrokers().contains(b.broker().id()))
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

    private SortedSet<LogDir> sortedCandidateLogDirsOverThreshold(ClusterModel clusterModel,
                                                                   double utilizationThreshold,
                                                                   OptimizationOptions optimizationOptions,
                                                                   Broker excludedBroker,
                                                                   boolean reverse) {

        return clusterModel.sortedLogDirsFor(
                b -> b.utilizationFor(resource()) > utilizationThreshold
                        && !excludedBroker.equals(b.broker())
                        // filter brokers
                        && (optimizationOptions.balanceBrokers().isEmpty() || optimizationOptions.balanceBrokers().contains(b.broker().id()))
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

    private SortedSet<Replica> sortedCandidateReplicas(LogDir logDir,
                                                       ActionType actionType,
                                                       OptimizationOptions optimizationOptions,
                                                       boolean reverse) {
        return logDir.sortedReplicasFor(
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
        LogDir destinationLogDir = destinationBroker.logDir(action.destinationLogDir());
        Broker sourceBroker = clusterModel.broker(action.sourceBrokerId());
        LogDir sourceLogDir = sourceBroker.logDir(action.sourceLogDir());
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
        double sourceUtilization = sourceLogDir.expectedUtilizationAfterRemove(resource(), loadToChange);
        double destinationUtilization = destinationLogDir.expectedUtilizationAfterAdd(resource(), loadToChange);

        return sourceUtilization >= this.balanceLowerThreshold && destinationUtilization <= this.balanceUpperThreshold;
    }

    @Override
    public ActionAcceptance actionAcceptance(BalancingAction action, ClusterModel clusterModel) {
        return this.selfSatisfied(clusterModel, action) ? ActionAcceptance.ACCEPT : ActionAcceptance.REJECT;
    }
}
