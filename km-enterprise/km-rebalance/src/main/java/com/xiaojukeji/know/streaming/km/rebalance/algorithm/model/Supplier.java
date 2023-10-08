package com.xiaojukeji.know.streaming.km.rebalance.algorithm.model;

import com.xiaojukeji.know.streaming.km.rebalance.algorithm.metric.MetricStore;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.metric.Metrics;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.metric.elasticsearch.ElasticsearchMetricStore;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.utils.MetadataUtils;
import org.apache.kafka.clients.admin.LogDirDescription;
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
        Map<Integer, Map<String, LogDirDescription>> logDirDescriptions = MetadataUtils.describeLogDirs(kafkaProperties, cluster.nodes());
        // nodes
        for (Node node: cluster.nodes()) {
            addBroker(node, false, model, capacitiesById, logDirDescriptions.get(node.id()).keySet());
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
//                    if (partition.leader() == null) {
                        // set empty load
                    leaderLoad = new Load();
//                    } else {
//                        throw new IllegalArgumentException("Cannot get leader load of topic partiton: " + topicPartition);
//                    }
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
                            addBroker(n, true, model, capacitiesById, logDirDescriptions.get(n.id()).keySet());
                        }
                    }

                    Map<String, LogDirDescription> logDirDescriptionMap = logDirDescriptions.get(n.id());
                    Optional<String> logDir = logDirDescriptionMap.entrySet()
                            .stream()
                            .filter(entry -> entry.getValue().replicaInfos().containsKey(topicPartition))
                            .map(Map.Entry::getKey)
                            .findFirst();
                    if(!logDir.isPresent()) {
                        throw new IllegalArgumentException("Cannot get logDir of topic partiton:" + topicPartition);
                    }

                    model.addReplica(n.id(), logDir.get(), topicPartition, isLeader, isOffline, isLeader ? leaderLoad : followerLoad);
                }
            }
        });
        return model;
    }

    private static String rack(Node node) {
        return (node.rack() == null || "".equals(node.rack())) ? node.host() : node.rack();
    }

    private static void addBroker(Node node, boolean isOffline, ClusterModel model, Map<Integer, Capacity> capacitiesById, Set<String> logDirs) {
        // rack
        Rack rack = model.addRack(rack(node));
        // broker
        Capacity capacity = capacitiesById.get(node.id());
        if (capacity == null)
            throw new IllegalArgumentException("Cannot get capacity of node: " + node);

        Map<String, Capacity> subCapacities = new HashMap<>();
        int logDirSize = logDirs.size();
        for(String name : logDirs) {
            Capacity subCapacity = new Capacity();
            // TODO 默认每个logDir大小相同，均分node的资源，需要改为从配置加载
            subCapacity.setCapacity(Resource.CPU, capacity.capacityFor(Resource.CPU) / logDirSize);
            subCapacity.setCapacity(Resource.DISK, capacity.capacityFor(Resource.DISK) / logDirSize);
            subCapacity.setCapacity(Resource.NW_IN, capacity.capacityFor(Resource.NW_IN) / logDirSize);
            subCapacity.setCapacity(Resource.NW_OUT, capacity.capacityFor(Resource.NW_OUT) / logDirSize);
            subCapacities.put(name, subCapacity);
        }

        model.addBroker(rack.id(), node.id(), node.host(), isOffline, capacity, subCapacities);
    }

}
