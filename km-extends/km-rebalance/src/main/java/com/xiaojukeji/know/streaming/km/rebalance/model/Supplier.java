package com.xiaojukeji.know.streaming.km.rebalance.model;

import com.xiaojukeji.know.streaming.km.rebalance.metric.MetricStore;
import com.xiaojukeji.know.streaming.km.rebalance.metric.Metrics;
import com.xiaojukeji.know.streaming.km.rebalance.metric.elasticsearch.ElasticsearchMetricStore;
import com.xiaojukeji.know.streaming.km.rebalance.utils.MetadataUtils;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author leewei
 * @date 2022/5/12
 */
public class Supplier {
    public static Map<String, String> subConfig(Map<String, String> config, String prefix, boolean stripPrefix) {
        return config.entrySet().stream()
                .filter(e -> e.getKey().startsWith(prefix))
                .collect(Collectors.toMap(e -> stripPrefix ? e.getKey().substring(prefix.length()) : e.getKey(),
                        Map.Entry::getValue));
    }

    public static ClusterModel load(String clusterName, int beforeSeconds, String kafkaBootstrapServer, String esUrls, String esIndexPrefix, Map<Integer, Capacity> capacitiesById, Set<String> ignoredTopics) {
        Properties kafkaProperties = new Properties();
        kafkaProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
        return load(clusterName, beforeSeconds, kafkaProperties, esUrls, esIndexPrefix, capacitiesById, ignoredTopics);
    }

    public static ClusterModel load(String clusterName, int beforeSeconds, Properties kafkaProperties, String esUrls, String esIndexPrefix, Map<Integer, Capacity> capacitiesById, Set<String> ignoredTopics) {
        MetricStore store = new ElasticsearchMetricStore(esUrls, esIndexPrefix);
        Metrics metrics = store.getMetrics(clusterName, beforeSeconds);
        return load(kafkaProperties, capacitiesById, metrics, ignoredTopics);
    }

    public static ClusterModel load(Properties kafkaProperties, Map<Integer, Capacity> capacitiesById, Metrics metrics, Set<String> ignoredTopics) {
        ClusterModel model = new ClusterModel();
        Cluster cluster = MetadataUtils.metadata(kafkaProperties);

        // nodes
        for (Node node: cluster.nodes()) {
            addBroker(node, false, model, capacitiesById);
        }

        // replicas
        cluster.topics()
                .stream()
                .filter(topic -> !ignoredTopics.contains(topic))
                .forEach(topic -> {
            List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
            for (PartitionInfo partition : partitions) {
                // TODO fix ignore no partition leader
                if (partition.leader() == null) {
                    continue;
                }

                TopicPartition topicPartition = new TopicPartition(partition.topic(), partition.partition());
                Load leaderLoad = metrics.load(topicPartition);
                if (leaderLoad == null) {
                    if (partition.leader() == null) {
                        // set empty load
                        leaderLoad = new Load();
                    } else {
                        throw new IllegalArgumentException("Cannot get leader load of topic partiton: " + topicPartition);
                    }
                }

                // leader nw out + follower nw out
                leaderLoad.setLoad(Resource.NW_OUT,
                        leaderLoad.loadFor(Resource.NW_OUT) +
                                leaderLoad.loadFor(Resource.NW_IN) * (partition.replicas().length - 1));

                Load followerLoad = new Load();
                followerLoad.addLoad(leaderLoad);
                followerLoad.setLoad(Resource.NW_OUT, 0);
                List<Node> offlineReplicas = Arrays.asList(partition.offlineReplicas());
                for (Node n : partition.replicas()) {
                    boolean isLeader = partition.leader() != null && partition.leader().equals(n);
                    boolean isOffline = offlineReplicas.contains(n);
                    if (isOffline) {
                        if (model.broker(n.id()) == null) {
                            // add offline broker
                            addBroker(n, true, model, capacitiesById);
                        }
                    }
                    model.addReplica(n.id(), topicPartition, isLeader, isOffline, isLeader ? leaderLoad : followerLoad);
                }
            }
        });
        return model;
    }

    private static String rack(Node node) {
        return (node.rack() == null || "".equals(node.rack())) ? node.host() : node.rack();
    }

    private static void addBroker(Node node, boolean isOffline, ClusterModel model, Map<Integer, Capacity> capacitiesById) {
        // rack
        Rack rack = model.addRack(rack(node));
        // broker
        Capacity capacity = capacitiesById.get(node.id());
        if (capacity == null)
            throw new IllegalArgumentException("Cannot get capacity of node: " + node);

        model.addBroker(rack.id(), node.id(), node.host(), isOffline, capacity);
    }

}
