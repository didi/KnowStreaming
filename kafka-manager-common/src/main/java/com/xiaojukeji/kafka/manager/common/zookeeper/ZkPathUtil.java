package com.xiaojukeji.kafka.manager.common.zookeeper;

/**
 * @author tukun
 * @date 15/11/05
 * @version 1.0.0
 */
public class ZkPathUtil {
    private static final String     ZOOKEEPER_SEPARATOR         = "/";

    public static final String      BROKER_ROOT_NODE            = ZOOKEEPER_SEPARATOR + "brokers";

    public static final String      CONTROLLER_ROOT_NODE        = ZOOKEEPER_SEPARATOR + "controller";

    public static final String      BROKER_IDS_ROOT             = BROKER_ROOT_NODE + ZOOKEEPER_SEPARATOR + "ids";

    public static final String      BROKER_TOPICS_ROOT          = BROKER_ROOT_NODE + ZOOKEEPER_SEPARATOR + "topics";

    public static final String      CONSUMER_ROOT_NODE          = ZOOKEEPER_SEPARATOR + "consumers";

    public static final String      REASSIGN_PARTITIONS_ROOT_NODE       = "/admin/reassign_partitions";

    /**
     * config
     */
    public static final String      CONFIG_ROOT_NODE            = ZOOKEEPER_SEPARATOR + "config";

    public static final String      CONFIG_TOPICS_ROOT_NODE     = CONFIG_ROOT_NODE + ZOOKEEPER_SEPARATOR + "topics";

    public static final String      CONFIG_CLIENTS_ROOT_NODE    = CONFIG_ROOT_NODE + ZOOKEEPER_SEPARATOR + "clients";

    public static final String      CONFIG_ENTITY_CHANGES_ROOT_NODE     = CONFIG_ROOT_NODE + ZOOKEEPER_SEPARATOR + "changes/config_change_";

    private static final String      D_METRICS_CONFIG_ROOT_NODE         = CONFIG_ROOT_NODE + ZOOKEEPER_SEPARATOR + "KafkaExMetrics";

    public static final String       D_CONFIG_EXTENSION_ROOT_NODE       = CONFIG_ROOT_NODE + ZOOKEEPER_SEPARATOR +  "extension";

    public static final String       D_CONTROLLER_CANDIDATES            = D_CONFIG_EXTENSION_ROOT_NODE  + ZOOKEEPER_SEPARATOR + "candidates";

    public static String getBrokerIdNodePath(Integer brokerId) {
        return BROKER_IDS_ROOT + ZOOKEEPER_SEPARATOR + String.valueOf(brokerId);
    }

    public static String getBrokerTopicRoot(String topicName) {
        return BROKER_TOPICS_ROOT + ZOOKEEPER_SEPARATOR + topicName;
    }

    public static String getBrokerTopicPartitionStatePath(String topicName, Integer partitionId) {
        return BROKER_TOPICS_ROOT + ZOOKEEPER_SEPARATOR + topicName + ZOOKEEPER_SEPARATOR + "partitions"
                + ZOOKEEPER_SEPARATOR + String.valueOf(partitionId) + ZOOKEEPER_SEPARATOR + "state";
    }

    //for consumer
    public static String getConsumerTopicPartitionOffsetNodePath(String consumerGroup,
                                                                 String topic, int partitionId) {
        return String.format(CONSUMER_ROOT_NODE + ZOOKEEPER_SEPARATOR + "%s" + ZOOKEEPER_SEPARATOR
                             + "offset" + "%s" + "%d", consumerGroup, topic, partitionId);
    }

    public static String getConsumerGroupRoot(String consumerGroup) {
        return CONSUMER_ROOT_NODE + ZOOKEEPER_SEPARATOR + consumerGroup;
    }

    public static String getConsumerGroupIdsRoot(String consumerGroup) {
        return CONSUMER_ROOT_NODE + ZOOKEEPER_SEPARATOR + consumerGroup + ZOOKEEPER_SEPARATOR
               + "ids";
    }

    public static String getConsumerGroupOffsetRoot(String consumerGroup) {
        return CONSUMER_ROOT_NODE + ZOOKEEPER_SEPARATOR + consumerGroup + ZOOKEEPER_SEPARATOR
               + "offsets";
    }

    public static String getConsumerGroupOwnersRoot(String consumerGroup) {
        return CONSUMER_ROOT_NODE + ZOOKEEPER_SEPARATOR + consumerGroup + ZOOKEEPER_SEPARATOR
               + "owners";
    }

    public static String getConsumerGroupConsumerIdsNodePath(String consumerGroup, String consumerId) {
        return getConsumerGroupIdsRoot(consumerGroup) + ZOOKEEPER_SEPARATOR + consumerId;
    }

    public static String getConsumerGroupOffsetTopicNode(String consumerGroup, String topic) {
        return getConsumerGroupOffsetRoot(consumerGroup) + ZOOKEEPER_SEPARATOR + topic;
    }

    public static String getConsumerGroupOffsetTopicPartitionNode(String consumerGroup,
                                                                  String topic, int partitionId) {
        return getConsumerGroupOffsetTopicNode(consumerGroup, topic) + ZOOKEEPER_SEPARATOR
               + partitionId;
    }

    public static String getConsumerGroupOwnersTopicNode(String consumerGroup, String topic) {
        return getConsumerGroupOwnersRoot(consumerGroup) + ZOOKEEPER_SEPARATOR + topic;
    }

    public static String getConsumerGroupOwnersTopicPartitionNode(String consumerGroup,
                                                                  String topic, int partitionId) {
        return getConsumerGroupOwnersTopicNode(consumerGroup, topic) + ZOOKEEPER_SEPARATOR
               + partitionId;
    }

    public static String getConfigTopicNode(String topicName) {
        return CONFIG_TOPICS_ROOT_NODE + ZOOKEEPER_SEPARATOR + topicName;
    }

    public static String getConfigClientNodePath(String appId, String topicName) {
        return CONFIG_CLIENTS_ROOT_NODE + ZOOKEEPER_SEPARATOR + appId + "." + topicName;
    }

    public static String parseLastPartFromZkPath(String zkPath) {
        return zkPath.substring(zkPath.lastIndexOf(ZOOKEEPER_SEPARATOR) + 1);
    }

    public static String getKafkaExtraMetricsPath(Integer brokerId) {
        return D_METRICS_CONFIG_ROOT_NODE + ZOOKEEPER_SEPARATOR + brokerId;
    }

    public static String getControllerCandidatePath(Integer brokerId) {
        return D_CONTROLLER_CANDIDATES + ZOOKEEPER_SEPARATOR + brokerId;
    }

    private ZkPathUtil() {
    }
}
