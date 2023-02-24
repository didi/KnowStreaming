package com.xiaojukeji.know.streaming.km.rebalance.algorithm.utils;

import com.xiaojukeji.know.streaming.km.rebalance.algorithm.executor.common.BalanceGoal;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.executor.common.BalanceParameter;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.executor.common.BalanceThreshold;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.executor.common.HostEnv;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.*;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.optimizer.AnalyzerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class GoalUtils {
    private static final Logger logger = LoggerFactory.getLogger(GoalUtils.class);

    public static Set<String> getNotExcludeTopics(ClusterModel clusterModel, Set<String> excludedTopics) {
        return clusterModel.topics().stream().filter(topicName -> !excludedTopics.contains(topicName)).collect(Collectors.toSet());
    }

    public static Set<Broker> getNotExcludeBrokers(ClusterModel clusterModel, Set<Integer> excludedBrokers) {
        return clusterModel.brokers().stream().filter(broker -> !excludedBrokers.contains(broker.id())).collect(Collectors.toSet());
    }

    /**
     * 在Broker上获取指定的离线副本列表
     */
    public static Set<Replica> retainCurrentOfflineBrokerReplicas(Broker broker, Collection<Replica> replicas) {
        Set<Replica> offlineReplicas = new HashSet<>(replicas);
        offlineReplicas.retainAll(broker.currentOfflineReplicas());
        return offlineReplicas;
    }

    public static ClusterModel getInitClusterModel(BalanceParameter parameter) {
        logger.info("Cluster model initialization");
        List<HostEnv> hostsEnv = parameter.getHardwareEnv();
        Map<Integer, Capacity> capacities = new HashMap<>();
        for (HostEnv env : hostsEnv) {
            Capacity capacity = new Capacity();
            capacity.setCapacity(Resource.CPU, env.getCpu());
            capacity.setCapacity(Resource.DISK, env.getDisk());
            capacity.setCapacity(Resource.NW_IN, env.getNetwork());
            capacity.setCapacity(Resource.NW_OUT, env.getNetwork());
            capacities.put(env.getId(), capacity);
        }
        return Supplier.load(parameter.getCluster(), parameter.getBeforeSeconds(), parameter.getKafkaConfig(),
                parameter.getEsRestURL(), parameter.getEsIndexPrefix(), capacities, AnalyzerUtils.getSplitTopics(parameter.getIgnoredTopics()));
    }

    public static Map<String, BalanceThreshold> getBalanceThreshold(BalanceParameter parameter, double[] clusterAvgResource) {
        Map<String, BalanceThreshold> balanceThreshold = new HashMap<>();
        balanceThreshold.put(BalanceGoal.DISK.goal(), new BalanceThreshold(Resource.DISK, parameter.getDiskThreshold(), clusterAvgResource[Resource.DISK.id()]));
        balanceThreshold.put(BalanceGoal.NW_IN.goal(), new BalanceThreshold(Resource.NW_IN, parameter.getNetworkInThreshold(), clusterAvgResource[Resource.NW_IN.id()]));
        balanceThreshold.put(BalanceGoal.NW_OUT.goal(), new BalanceThreshold(Resource.NW_OUT, parameter.getNetworkOutThreshold(), clusterAvgResource[Resource.NW_OUT.id()]));
        return balanceThreshold;
    }

}
