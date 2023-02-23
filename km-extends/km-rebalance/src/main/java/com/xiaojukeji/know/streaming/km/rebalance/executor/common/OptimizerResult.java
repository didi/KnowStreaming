package com.xiaojukeji.know.streaming.km.rebalance.executor.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaojukeji.know.streaming.km.rebalance.model.Broker;
import com.xiaojukeji.know.streaming.km.rebalance.model.ClusterModel;
import com.xiaojukeji.know.streaming.km.rebalance.model.ReplicaPlacementInfo;
import com.xiaojukeji.know.streaming.km.rebalance.model.Resource;
import com.xiaojukeji.know.streaming.km.rebalance.optimizer.ExecutionProposal;
import com.xiaojukeji.know.streaming.km.rebalance.optimizer.OptimizationOptions;
import com.xiaojukeji.know.streaming.km.rebalance.utils.GoalUtils;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;
import java.util.stream.Collectors;

public class OptimizerResult {
    private static final Logger logger = LoggerFactory.getLogger(OptimizerResult.class);
    private Set<ExecutionProposal> _proposals;
    private final BalanceParameter parameter;
    private Set<Broker> _balanceBrokersBefore;
    private Set<Broker> _balanceBrokersAfter;
    private final ClusterModel clusterModel;
    private final Map<TopicPartition, List<BalanceActionHistory>> balanceActionHistory;
    private final Map<String, BalanceThreshold> balanceThreshold;

    public OptimizerResult(ClusterModel clusterModel, OptimizationOptions optimizationOptions) {
        this.clusterModel = clusterModel;
        balanceActionHistory = clusterModel.balanceActionHistory();
        parameter = optimizationOptions.parameter();
        double[] clusterAvgResource = clusterModel.avgOfUtilization();
        balanceThreshold = GoalUtils.getBalanceThreshold(parameter, clusterAvgResource);
    }

    /**
     * 计划概览
     */
    public BalanceOverview resultOverview() {
        BalanceOverview overview = new BalanceOverview();
        overview.setTopicBlacklist(parameter.getExcludedTopics());
        overview.setMoveReplicas(_proposals.size());
        overview.setNodeRange(parameter.getBalanceBrokers());
        overview.setRemoveNode(parameter.getOfflineBrokers());
        Map<Resource, Double> balanceThreshold = new HashMap<>();
        balanceThreshold.put(Resource.CPU, parameter.getCpuThreshold());
        balanceThreshold.put(Resource.DISK, parameter.getDiskThreshold());
        balanceThreshold.put(Resource.NW_IN, parameter.getNetworkInThreshold());
        balanceThreshold.put(Resource.NW_OUT, parameter.getNetworkOutThreshold());
        overview.setBalanceThreshold(balanceThreshold);
        Set<String> moveTopicsSet = _proposals.stream().map(j -> j.tp().topic()).collect(Collectors.toSet());
        String moveTopics = String.join(",", moveTopicsSet);
        overview.setMoveTopics(moveTopics);
        //Leader切换时不需要进行统计
        double totalMoveSize = _proposals.stream().filter(i -> Integer.max(i.replicasToAdd().size(), i.replicasToRemove().size()) != 0).mapToDouble(ExecutionProposal::partitionSize).sum();
        overview.setTotalMoveSize(totalMoveSize);
        return overview;
    }

    /**
     * 计划明细
     */
    public Map<Integer, BalanceDetailed> resultDetailed() {
        Map<Integer, BalanceDetailed> details = new HashMap<>();
        _balanceBrokersBefore.forEach(i -> {
            BalanceDetailed balanceDetailed = new BalanceDetailed();
            balanceDetailed.setBrokerId(i.id());
            balanceDetailed.setHost(i.host());
            balanceDetailed.setCurrentCPUUtilization(i.utilizationFor(Resource.CPU));
            balanceDetailed.setCurrentDiskUtilization(i.utilizationFor(Resource.DISK));
            balanceDetailed.setCurrentNetworkInUtilization(i.utilizationFor(Resource.NW_IN));
            balanceDetailed.setCurrentNetworkOutUtilization(i.utilizationFor(Resource.NW_OUT));
            details.put(i.id(), balanceDetailed);
        });
        Map<Integer, Double> totalAddReplicaCount = new HashMap<>();
        Map<Integer, Double> totalAddDataSize = new HashMap<>();
        Map<Integer, Double> totalRemoveReplicaCount = new HashMap<>();
        Map<Integer, Double> totalRemoveDataSize = new HashMap<>();
        _proposals.forEach(i -> {
            i.replicasToAdd().forEach((k, v) -> {
                totalAddReplicaCount.merge(k, v[0], Double::sum);
                totalAddDataSize.merge(k, v[1], Double::sum);
            });
            i.replicasToRemove().forEach((k, v) -> {
                totalRemoveReplicaCount.merge(k, v[0], Double::sum);
                totalRemoveDataSize.merge(k, v[1], Double::sum);
            });
        });
        _balanceBrokersAfter.forEach(i -> {
            BalanceDetailed balanceDetailed = details.get(i.id());
            balanceDetailed.setLastCPUUtilization(i.utilizationFor(Resource.CPU));
            balanceDetailed.setLastDiskUtilization(i.utilizationFor(Resource.DISK));
            balanceDetailed.setLastNetworkInUtilization(i.utilizationFor(Resource.NW_IN));
            balanceDetailed.setLastNetworkOutUtilization(i.utilizationFor(Resource.NW_OUT));
            balanceDetailed.setMoveInReplicas(totalAddReplicaCount.getOrDefault(i.id(), 0.0));
            balanceDetailed.setMoveOutReplicas(totalRemoveReplicaCount.getOrDefault(i.id(), 0.0));
            balanceDetailed.setMoveInDiskSize(totalAddDataSize.getOrDefault(i.id(), 0.0));
            balanceDetailed.setMoveOutDiskSize(totalRemoveDataSize.getOrDefault(i.id(), 0.0));
            for (String str : parameter.getGoals()) {
                BalanceThreshold threshold = balanceThreshold.get(str);
                if (!threshold.isInRange(i.utilizationFor(threshold.resource()))) {
                    balanceDetailed.setBalanceState(-1);
                    break;
                }
            }
        });

        return details;
    }

    /**
     * 计划任务
     */
    public List<BalanceTask> resultTask() {
        List<BalanceTask> balanceTasks = new ArrayList<>();
        _proposals.forEach(proposal -> {
            BalanceTask task = new BalanceTask();
            task.setTopic(proposal.tp().topic());
            task.setPartition(proposal.tp().partition());
            List<Integer> replicas = proposal.newReplicas().stream().map(ReplicaPlacementInfo::brokerId).collect(Collectors.toList());
            task.setReplicas(replicas);
            balanceTasks.add(task);
        });
        return balanceTasks;
    }

    public Map<TopicPartition, List<BalanceActionHistory>> resultBalanceActionHistory() {
        return Collections.unmodifiableMap(balanceActionHistory);
    }

    public String resultJsonOverview() {
        try {
            return new ObjectMapper().writeValueAsString(resultOverview());
        } catch (Exception e) {
            logger.error("result overview json process error", e);
        }
        return "{}";
    }

    public String resultJsonDetailed() {
        try {
            return new ObjectMapper().writeValueAsString(resultDetailed());
        } catch (Exception e) {
            logger.error("result detailed json process error", e);
        }
        return "{}";
    }

    public String resultJsonTask() {
        try {
            Map<String, Object> reassign = new HashMap<>();
            reassign.put("partitions", resultTask());
            reassign.put("version", 1);
            return new ObjectMapper().writeValueAsString(reassign);
        } catch (Exception e) {
            logger.error("result task json process error", e);
        }
        return "{}";
    }

    public List<TopicChangeHistory> resultTopicChangeHistory() {
        List<TopicChangeHistory> topicChangeHistoryList = new ArrayList<>();
        for (ExecutionProposal proposal : _proposals) {
            TopicChangeHistory changeHistory = new TopicChangeHistory();
            changeHistory.setTopic(proposal.tp().topic());
            changeHistory.setPartition(proposal.tp().partition());
            changeHistory.setOldLeader(proposal.oldLeader().brokerId());
            changeHistory.setNewLeader(proposal.newReplicas().get(0).brokerId());
            List<Integer> balanceBefore = proposal.oldReplicas().stream().map(ReplicaPlacementInfo::brokerId).collect(Collectors.toList());
            List<Integer> balanceAfter = proposal.newReplicas().stream().map(ReplicaPlacementInfo::brokerId).collect(Collectors.toList());
            changeHistory.setBalanceBefore(balanceBefore);
            changeHistory.setBalanceAfter(balanceAfter);
            topicChangeHistoryList.add(changeHistory);
        }
        return topicChangeHistoryList;
    }

    public String resultJsonTopicChangeHistory() {
        try {
            return new ObjectMapper().writeValueAsString(resultTopicChangeHistory());
        } catch (Exception e) {
            logger.error("result balance topic change history json process error", e);
        }
        return "{}";
    }

    public String resultJsonBalanceActionHistory() {
        try {
            return new ObjectMapper().writeValueAsString(balanceActionHistory);
        } catch (Exception e) {
            logger.error("result balance action history json process error", e);
        }
        return "{}";
    }

    public void setBalanceBrokersFormBefore(Set<Broker> balanceBrokersBefore) {
        _balanceBrokersBefore = new HashSet<>();
        balanceBrokersBefore.forEach(i -> {
            Broker broker = new Broker(i.rack(), i.id(), i.host(), false, i.capacity());
            broker.load().addLoad(i.load());
            _balanceBrokersBefore.add(broker);
        });
    }

    public void setBalanceBrokersFormAfter(Set<Broker> balanceBrokersAfter) {
        _balanceBrokersAfter = balanceBrokersAfter;
    }

    public void setExecutionProposal(Set<ExecutionProposal> proposals) {
        _proposals = proposals;
    }

    // test
    public ClusterModel clusterModel() {
        return clusterModel;
    }
}
