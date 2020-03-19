package com.xiaojukeji.kafka.manager.common.utils.zk;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储结构：
 * 
 * <pre>
 * /consumers
 *    consumer-group
 *      ids
 *        consumerId
 *      offsets
 *        topic-0
 *           0(partition编号，节点内容表示)
 *           1
 *           2
 *        topic-1
 *      owners
 * /brokers
 *     topics
 *          topic-0  (节点内容是 ("0",[0,1,2]))
 *              partitions
 *                  0
 *                      state（节点内容是leader的brokerId，同步副本信息等）
 *                  1
 *                  2
 *          topic-x
 *     ids
 *          1(临时节点，broker编号，节点信息为broker相关信息，如JMX端口，host和port等)
 *          2
 *          n
 * </pre>
 * 
 * @author tukun @ 2015-11-5
 * @version 1.0.0
 */
public class ZkPathUtil {

    public static final String         ZOOKEEPER_SEPARATOR  = "/";

    public static final String         BROKER_ROOT_NODE     = ZOOKEEPER_SEPARATOR + "brokers";

    public static final String         CONTROLLER_ROOT_NODE = ZOOKEEPER_SEPARATOR + "controller";

    public static final String         BROKER_IDS_ROOT      = BROKER_ROOT_NODE
                                                              + ZOOKEEPER_SEPARATOR + "ids";

    public static final String         BROKER_TOPICS_ROOT   = BROKER_ROOT_NODE
                                                              + ZOOKEEPER_SEPARATOR + "topics";

    public static final String         CONSUMER_ROOT_NODE   = ZOOKEEPER_SEPARATOR + "consumers";

    public static final String         CONFIG_ROOT_NODE   = ZOOKEEPER_SEPARATOR + "config";

    public static final String         CONFIG_TOPICS_ROOT_NODE   = CONFIG_ROOT_NODE + ZOOKEEPER_SEPARATOR + "topics";

    //存储监控的参数name到获取的object_name的映射关系图
    private static Map<String, String> zkPathMap            = new HashMap<String, String>();

    static {
        zkPathMap.put("ConusmerPartitionOffset", CONSUMER_ROOT_NODE + ZOOKEEPER_SEPARATOR
                                                 + "${consumerGroup}" + ZOOKEEPER_SEPARATOR
                                                 + "offsets" + ZOOKEEPER_SEPARATOR + "${topic}"
                                                 + ZOOKEEPER_SEPARATOR + "${partition}");
    }

    //for broker目录
    public static String getBrokerIdNodePath(long brokerId) {
        return String.format(BROKER_IDS_ROOT + ZOOKEEPER_SEPARATOR + "%d", brokerId);
    }

    public static String getBrokerTopicRoot(String topic) {
        return BROKER_TOPICS_ROOT + ZOOKEEPER_SEPARATOR + topic;
    }

    public static String getBrokerTopicPartitionRoot(String topic) {
        return BROKER_TOPICS_ROOT + ZOOKEEPER_SEPARATOR + topic + ZOOKEEPER_SEPARATOR
               + "partitions";
    }

    public static String getBrokerTopicPartitionStatePath(String topic, int partitionId) {
        return String.format(getBrokerTopicPartitionRoot(topic) + ZOOKEEPER_SEPARATOR + "%d"
                             + ZOOKEEPER_SEPARATOR + "state", partitionId);
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

    public static String parseLastPartFromZkPath(String zkPath) {
        return zkPath.substring(zkPath.lastIndexOf("/") + 1);
    }

    public static Map<String, String> getZkPathMap() {
        return zkPathMap;
    }

    public static void setZkPathMap(Map<String, String> zkPathMap) {
        ZkPathUtil.zkPathMap = zkPathMap;
    }

    public static String getControllerRootNode() {
        return CONTROLLER_ROOT_NODE;
    }

    public static String getEntityConfigPath(String entityType, String entity) {
        return getEntityConfigRootPath(entityType) + "/" + entity;
    }

    public static String getEntityConfigRootPath(String entityType) {
        return CONFIG_ROOT_NODE + "/" + entityType;
    }
}
