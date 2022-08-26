package com.xiaojukeji.know.streaming.km.common.constant.kafka;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.annotations.KafkaSource;

import java.lang.reflect.Field;
import java.util.*;

@KafkaSource(modified = 1, modifyDesc = "除了0.10.2及以下版本是需要按照格式组装之外，其他版本是直接拷贝的源码")
public abstract class AbstractTopicConfig {
    private static final ILog log = LogFactory.getLog(AbstractTopicConfig.class);

    public static final String KAFKA_CONFIG_KEY_NAME = "KAFKA_CONFIG_KEY_NAME";

    public static final String KAFKA_CONFIG_DOC_NAME = "KAFKA_CONFIG_DOC_NAME";

    // 遍历版本
    public static List<Properties> getTopicConfigNamesAndDocs(Class clazz) {
        Map<String, Properties> configMap = new HashMap<>();

        // 遍历版本对应的字段
        for (Field field: clazz.getDeclaredFields()) {
            try {
                if (field.getName().endsWith("_CONFIG")) {
                    String fieldNamePrefix = field.getName().substring(0, field.getName().length() - "_CONFIG".length());

                    Properties properties = configMap.getOrDefault(fieldNamePrefix, new Properties());
                    properties.put(KAFKA_CONFIG_KEY_NAME, field.get(null));
                    configMap.put(fieldNamePrefix, properties);
                } else if (field.getName().endsWith("_DOC")){
                    String fieldNamePrefix = field.getName().substring(0, field.getName().length() - "_DOC".length());

                    Properties properties = configMap.getOrDefault(fieldNamePrefix, new Properties());
                    properties.put(KAFKA_CONFIG_DOC_NAME, field.get(null));
                    configMap.put(fieldNamePrefix, properties);
                }
            } catch (Exception e) {
                log.error("method=static||class={}||field={}||errMsg=exception", clazz.getSimpleName(), field.getName());
            }
        }

        List<Properties> propsList = new ArrayList<>(configMap.values());
        if (clazz != TopicConfig0100.class) {
            Properties leaderReplicationProps = new Properties();
            leaderReplicationProps.put(KAFKA_CONFIG_KEY_NAME, LeaderReplicationThrottledReplicasProp);
            leaderReplicationProps.put(KAFKA_CONFIG_DOC_NAME, LeaderReplicationThrottledReplicasDoc);
            propsList.add(leaderReplicationProps);

            Properties followerReplicationProps = new Properties();
            leaderReplicationProps.put(KAFKA_CONFIG_KEY_NAME, FollowerReplicationThrottledReplicasProp);
            leaderReplicationProps.put(KAFKA_CONFIG_DOC_NAME, FollowerReplicationThrottledReplicasDoc);
            propsList.add(followerReplicationProps);
        }

        return propsList;
    }

    // Leave these out of TopicConfig for now as they are replication quota configs
    public static final String LeaderReplicationThrottledReplicasProp = "leader.replication.throttled.replicas";
    public static final String LeaderReplicationThrottledReplicasDoc = "A list of replicas for which log replication should be throttled on " +
            "the leader side. The list should describe a set of replicas in the form " +
            "[PartitionId]:[BrokerId],[PartitionId]:[BrokerId]:... or alternatively the wildcard '*' can be used to throttle " +
            "all replicas for this topic.";

    public static final String FollowerReplicationThrottledReplicasProp = "follower.replication.throttled.replicas";
    public static final String FollowerReplicationThrottledReplicasDoc = "A list of replicas for which log replication should be throttled on " +
            "the follower side. The list should describe a set of " + "replicas in the form " +
            "[PartitionId]:[BrokerId],[PartitionId]:[BrokerId]:... or alternatively the wildcard '*' can be used to throttle " +
            "all replicas for this topic.";

    protected AbstractTopicConfig() {
    }
}
