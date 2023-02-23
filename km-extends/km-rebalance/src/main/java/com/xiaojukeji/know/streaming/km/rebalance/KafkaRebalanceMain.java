package com.xiaojukeji.know.streaming.km.rebalance;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaojukeji.know.streaming.km.rebalance.executor.ExecutionRebalance;
import com.xiaojukeji.know.streaming.km.rebalance.executor.common.BalanceParameter;
import com.xiaojukeji.know.streaming.km.rebalance.executor.common.HostEnv;
import com.xiaojukeji.know.streaming.km.rebalance.executor.common.OptimizerResult;
import com.xiaojukeji.know.streaming.km.rebalance.utils.CommandLineUtils;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.CommonClientConfigs;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class KafkaRebalanceMain {

    public void run(OptionSet options) {
        try {
            BalanceParameter balanceParameter = new BalanceParameter();
            if (options.has("excluded-topics")) {
                balanceParameter.setExcludedTopics(options.valueOf("excluded-topics").toString());
            }
            if (options.has("offline-brokers")) {
                balanceParameter.setOfflineBrokers(options.valueOf("offline-brokers").toString());
            }
            if (options.has("disk-threshold")) {
                Double diskThreshold = (Double) options.valueOf("disk-threshold");
                balanceParameter.setDiskThreshold(diskThreshold);
            }
            if (options.has("cpu-threshold")) {
                Double cpuThreshold = (Double) options.valueOf("cpu-threshold");
                balanceParameter.setCpuThreshold(cpuThreshold);
            }
            if (options.has("network-in-threshold")) {
                Double networkInThreshold = (Double) options.valueOf("network-in-threshold");
                balanceParameter.setNetworkInThreshold(networkInThreshold);
            }
            if (options.has("network-out-threshold")) {
                Double networkOutThreshold = (Double) options.valueOf("network-out-threshold");
                balanceParameter.setNetworkOutThreshold(networkOutThreshold);
            }
            if (options.has("balance-brokers")) {
                balanceParameter.setBalanceBrokers(options.valueOf("balance-brokers").toString());
            }
            if (options.has("topic-leader-threshold")) {
                Double topicLeaderThreshold = (Double) options.valueOf("topic-leader-threshold");
                balanceParameter.setTopicLeaderThreshold(topicLeaderThreshold);
            }
            if (options.has("topic-replica-threshold")) {
                Double topicReplicaThreshold = (Double) options.valueOf("topic-replica-threshold");
                balanceParameter.setTopicReplicaThreshold(topicReplicaThreshold);
            }
            if (options.has("ignored-topics")) {
                balanceParameter.setIgnoredTopics(options.valueOf("ignored-topics").toString());
            }
            String path = options.valueOf("output-path").toString();
            String goals = options.valueOf("goals").toString();
            balanceParameter.setGoals(Arrays.asList(goals.split(",")));
            balanceParameter.setCluster(options.valueOf("cluster").toString());
            Properties kafkaConfig = new Properties();
            kafkaConfig.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, options.valueOf("bootstrap-servers").toString());
            balanceParameter.setKafkaConfig(kafkaConfig);
            balanceParameter.setEsRestURL(options.valueOf("es-rest-url").toString());
            balanceParameter.setEsIndexPrefix(options.valueOf("es-index-prefix").toString());
            balanceParameter.setBeforeSeconds((Integer) options.valueOf("before-seconds"));
            String envFile = options.valueOf("hardware-env-file").toString();
            String envJson = FileUtils.readFileToString(new File(envFile), "UTF-8");
            List<HostEnv> env = new ObjectMapper().readValue(envJson, new TypeReference<List<HostEnv>>() {
            });
            balanceParameter.setHardwareEnv(env);
            ExecutionRebalance exec = new ExecutionRebalance();
            OptimizerResult optimizerResult = exec.optimizations(balanceParameter);
            FileUtils.write(new File(path.concat("/overview.json")), optimizerResult.resultJsonOverview(), "UTF-8");
            FileUtils.write(new File(path.concat("/detailed.json")), optimizerResult.resultJsonDetailed(), "UTF-8");
            FileUtils.write(new File(path.concat("/task.json")), optimizerResult.resultJsonTask(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        parser.accepts("bootstrap-servers", "Kafka cluster boot server").withRequiredArg().ofType(String.class);
        parser.accepts("es-rest-url", "The url of elasticsearch").withRequiredArg().ofType(String.class);
        parser.accepts("es-index-prefix", "The Index Prefix of elasticsearch").withRequiredArg().ofType(String.class);
        parser.accepts("goals", "Balanced goals include TopicLeadersDistributionGoal,TopicReplicaDistributionGoal,DiskDistributionGoal,NetworkInboundDistributionGoal,NetworkOutboundDistributionGoal").withRequiredArg().ofType(String.class);
        parser.accepts("cluster", "Balanced cluster name").withRequiredArg().ofType(String.class);
        parser.accepts("excluded-topics", "Topic does not perform data balancing").withOptionalArg().ofType(String.class);
        parser.accepts("ignored-topics","Topics that do not contain model calculations").withOptionalArg().ofType(String.class);
        parser.accepts("offline-brokers", "Broker does not perform data balancing").withOptionalArg().ofType(String.class);
        parser.accepts("balance-brokers", "Balanced brokers list").withOptionalArg().ofType(String.class);
        parser.accepts("disk-threshold", "Disk data balance threshold").withOptionalArg().ofType(Double.class);
        parser.accepts("topic-leader-threshold","topic leader threshold").withOptionalArg().ofType(Double.class);
        parser.accepts("topic-replica-threshold","topic replica threshold").withOptionalArg().ofType(Double.class);
        parser.accepts("cpu-threshold", "Cpu utilization balance threshold").withOptionalArg().ofType(Double.class);
        parser.accepts("network-in-threshold", "Network inflow threshold").withOptionalArg().ofType(Double.class);
        parser.accepts("network-out-threshold", "Network outflow threshold").withOptionalArg().ofType(Double.class);
        parser.accepts("before-seconds", "Query es data time").withRequiredArg().ofType(Integer.class);
        parser.accepts("hardware-env-file", "Machine environment information includes cpu, disk and network").withRequiredArg().ofType(String.class);
        parser.accepts("output-path", "Cluster balancing result file directory").withRequiredArg().ofType(String.class);
        OptionSet options = parser.parse(args);
        if (args.length == 0) {
            CommandLineUtils.printUsageAndDie(parser, "Running parameters need to be configured to perform cluster balancing");
        }
        if (!options.has("bootstrap-servers")) {
            CommandLineUtils.printUsageAndDie(parser, "bootstrap-servers cannot be empty");
        }
        if (!options.has("es-rest-url")) {
            CommandLineUtils.printUsageAndDie(parser, "es-rest-url cannot be empty");
        }
        if (!options.has("es-index-prefix")) {
            CommandLineUtils.printUsageAndDie(parser, "es-index-prefix cannot be empty");
        }
        if (!options.has("goals")) {
            CommandLineUtils.printUsageAndDie(parser, "goals cannot be empty");
        }
        if (!options.has("cluster")) {
            CommandLineUtils.printUsageAndDie(parser, "cluster name cannot be empty");
        }
        if (!options.has("before-seconds")) {
            CommandLineUtils.printUsageAndDie(parser, "before-seconds cannot be empty");
        }
        if (!options.has("hardware-env-file")) {
            CommandLineUtils.printUsageAndDie(parser, "hardware-env-file cannot be empty");
        }
        if (!options.has("output-path")) {
            CommandLineUtils.printUsageAndDie(parser, "output-path cannot be empty");
        }
        KafkaRebalanceMain rebalanceMain = new KafkaRebalanceMain();
        rebalanceMain.run(options);
    }
}
