package com.xiaojukeji.know.streaming.km.rebalance.executor;

import com.xiaojukeji.know.streaming.km.rebalance.executor.common.BalanceGoal;
import com.xiaojukeji.know.streaming.km.rebalance.executor.common.BalanceParameter;
import com.xiaojukeji.know.streaming.km.rebalance.executor.common.BalanceThreshold;
import com.xiaojukeji.know.streaming.km.rebalance.executor.common.BrokerBalanceState;
import com.xiaojukeji.know.streaming.km.rebalance.model.ClusterModel;
import com.xiaojukeji.know.streaming.km.rebalance.model.Load;
import com.xiaojukeji.know.streaming.km.rebalance.model.Resource;
import com.xiaojukeji.know.streaming.km.rebalance.optimizer.GoalOptimizer;
import com.xiaojukeji.know.streaming.km.rebalance.optimizer.OptimizationOptions;
import com.xiaojukeji.know.streaming.km.rebalance.executor.common.OptimizerResult;
import com.xiaojukeji.know.streaming.km.rebalance.utils.GoalUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class ExecutionRebalance {
    private static final Logger logger = LoggerFactory.getLogger(ExecutionRebalance.class);

    public OptimizerResult optimizations(BalanceParameter balanceParameter) {
        Validate.isTrue(StringUtils.isNotBlank(balanceParameter.getCluster()), "cluster is empty");
        Validate.isTrue(balanceParameter.getKafkaConfig() != null, "Kafka config properties is empty");
        Validate.isTrue(balanceParameter.getGoals() != null, "Balance goals is empty");
        Validate.isTrue(StringUtils.isNotBlank(balanceParameter.getEsIndexPrefix()), "EsIndexPrefix is empty");
        Validate.isTrue(StringUtils.isNotBlank(balanceParameter.getEsRestURL()), "EsRestURL is empty");
        Validate.isTrue(balanceParameter.getHardwareEnv() != null, "HardwareEnv is empty");
        logger.info("Cluster balancing start");
        ClusterModel clusterModel = GoalUtils.getInitClusterModel(balanceParameter);
        GoalOptimizer optimizer = new GoalOptimizer();
        OptimizerResult optimizerResult = optimizer.optimizations(clusterModel, new OptimizationOptions(balanceParameter));
        logger.info("Cluster balancing completed");
        return optimizerResult;
    }

    public static Map<Resource, Double> getClusterAvgResourcesState(BalanceParameter balanceParameter) {
        ClusterModel clusterModel = GoalUtils.getInitClusterModel(balanceParameter);
        Load load = clusterModel.load();
        Map<Resource, Double> avgResource = new HashMap<>();
        avgResource.put(Resource.DISK, load.loadFor(Resource.DISK) / clusterModel.brokers().size());
        avgResource.put(Resource.CPU, load.loadFor(Resource.CPU) / clusterModel.brokers().size());
        avgResource.put(Resource.NW_OUT, load.loadFor(Resource.NW_OUT) / clusterModel.brokers().size());
        avgResource.put(Resource.NW_IN, load.loadFor(Resource.NW_IN) / clusterModel.brokers().size());
        return avgResource;
    }

    public static Map<Integer, BrokerBalanceState> getBrokerResourcesBalanceState(BalanceParameter balanceParameter) {
        Map<Integer, BrokerBalanceState> balanceState = new HashMap<>();
        ClusterModel clusterModel = GoalUtils.getInitClusterModel(balanceParameter);
        double[] clusterAvgResource = clusterModel.avgOfUtilization();
        Map<String, BalanceThreshold> balanceThreshold = GoalUtils.getBalanceThreshold(balanceParameter, clusterAvgResource);
        clusterModel.brokers().forEach(i -> {
            BrokerBalanceState state = new BrokerBalanceState();
            if (balanceParameter.getGoals().contains(BalanceGoal.DISK.goal())) {
                state.setDiskAvgResource(i.load().loadFor(Resource.DISK));
                state.setDiskUtilization(i.utilizationFor(Resource.DISK));
                state.setDiskBalanceState(balanceThreshold.get(BalanceGoal.DISK.goal()).state(i.utilizationFor(Resource.DISK)));
            }
            if (balanceParameter.getGoals().contains(BalanceGoal.NW_IN.goal())) {
                state.setBytesInAvgResource(i.load().loadFor(Resource.NW_IN));
                state.setBytesInUtilization(i.utilizationFor(Resource.NW_IN));
                state.setBytesInBalanceState(balanceThreshold.get(BalanceGoal.NW_IN.goal()).state(i.utilizationFor(Resource.NW_IN)));
            }
            if (balanceParameter.getGoals().contains(BalanceGoal.NW_OUT.goal())) {
                state.setBytesOutAvgResource(i.load().loadFor(Resource.NW_OUT));
                state.setBytesOutUtilization(i.utilizationFor(Resource.NW_OUT));
                state.setBytesOutBalanceState(balanceThreshold.get(BalanceGoal.NW_OUT.goal()).state(i.utilizationFor(Resource.NW_OUT)));
            }
            balanceState.put(i.id(), state);
        });
        return balanceState;
    }
}
