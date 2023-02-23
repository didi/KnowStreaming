package com.xiaojukeji.know.streaming.km.rebalance;

import com.xiaojukeji.know.streaming.km.rebalance.executor.ExecutionRebalance;
import com.xiaojukeji.know.streaming.km.rebalance.executor.common.BalanceParameter;
import com.xiaojukeji.know.streaming.km.rebalance.executor.common.HostEnv;
import com.xiaojukeji.know.streaming.km.rebalance.model.ClusterModel;
import com.xiaojukeji.know.streaming.km.rebalance.model.Partition;
import com.xiaojukeji.know.streaming.km.rebalance.model.Replica;
import com.xiaojukeji.know.streaming.km.rebalance.executor.common.OptimizerResult;
import com.xiaojukeji.know.streaming.km.rebalance.optimizer.goals.Goal;
import org.apache.kafka.clients.CommonClientConfigs;

import java.util.*;

/**
 * @author leewei
 * @date 2022/5/30
 */
public class Test {
    private static HostEnv getHostEnv(int id) {
        HostEnv env = new HostEnv();
        env.setId(id);
        env.setCpu(3200); // 32C
        env.setDisk(2 * 1024D * 1024 * 1024 * 1024); // 2T
        env.setNetwork(10 * 1024D * 1024 * 1024); // 10G
        return env;
    }

    public static void main(String[] args) {
        ServiceLoader<Goal> loader = ServiceLoader.load(Goal.class);
        for (Goal goal : loader) {
            System.out.println(goal.name() + " ： " + goal);
        }

        BalanceParameter balanceParameter = new BalanceParameter();

        balanceParameter.setDiskThreshold(0.05);
        balanceParameter.setCpuThreshold(0.05);
        balanceParameter.setNetworkInThreshold(0.05);
        balanceParameter.setNetworkOutThreshold(0.05);

        List<HostEnv> envList = new ArrayList<>();
        envList.add(getHostEnv(6416));
        envList.add(getHostEnv(6417));
        envList.add(getHostEnv(6431));
        envList.add(getHostEnv(6432));
        envList.add(getHostEnv(6553));
        balanceParameter.setHardwareEnv(envList);

        // String goals = "DiskDistributionGoal,NetworkInboundDistributionGoal,NetworkOutboundDistributionGoal";
        // String goals = "DiskDistributionGoal";
        String goals = "NetworkOutboundDistributionGoal";

        balanceParameter.setGoals(Arrays.asList(goals.split(",")));
        Properties kafkaConfig = new Properties();
        kafkaConfig.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "10.96.64.16:7262");
        // kafkaConfig.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "10.96.64.16:9162,10.96.64.17:9162,10.96.64.31:9162");
        balanceParameter.setKafkaConfig(kafkaConfig);
        balanceParameter.setEsRestURL("10.96.64.13:8061");
        balanceParameter.setEsIndexPrefix("ks_kafka_partition_metric_");

        balanceParameter.setBeforeSeconds(300);

        balanceParameter.setCluster("293");

        ExecutionRebalance exec = new ExecutionRebalance();
        OptimizerResult optimizerResult = exec.optimizations(balanceParameter);

        System.out.println(optimizerResult.resultJsonOverview());
        System.out.println(optimizerResult.resultJsonDetailed());
        System.out.println(optimizerResult.resultJsonTask());
        System.out.println(optimizerResult.resultJsonBalanceActionHistory());

        ClusterModel model = optimizerResult.clusterModel();

        System.out.println("---moved partitions----");
        // moved partitions
        for (String topic : model.topics()) {
            model.topic(topic).stream()
                    .filter(Partition::isChanged)
                    .forEach(partition -> {
                        System.out.println("---> " + partition);
                        System.out.println(partition.topicPartition() + " leader change: " + partition.isLeaderChanged() + " " + (partition.isLeaderChanged() ? partition.originalLeaderBroker().id() + " -> " + partition.leader().broker().id() : ""));
                        partition.replicas().stream()
                                .filter(Replica::isChanged).forEach(r -> {
                            //System.out.println(r);
                            System.out.println(partition.topicPartition() + " replica moved: " + r.original().broker().id() + "（" + r.original().isLeader() + "） -> " + r.broker().id() + " (" + r.isLeader() + ")");
                        });
                    });
        }
        System.out.println("---end moved partitions----");
    }
}
