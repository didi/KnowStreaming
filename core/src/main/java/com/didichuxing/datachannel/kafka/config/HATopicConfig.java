package com.didichuxing.datachannel.kafka.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author 杨阳
 * @date {2022/6/7}
 */
public class HATopicConfig extends AbstractConfig {
    private static final ConfigDef CONFIG;

    public static final String DIDI_HA_REMOTE_CLUSTER = "didi.ha.remote.cluster";
    public static final String DIDI_HA_REMOTE_CLUSTER_DOC = "This configuration controls the cluster in which the topic to be synchronized exists.";

    public static final String DIDI_HA_REMOTE_TOPIC = "didi.ha.remote.topic";
    public static final String DIDI_HA_REMOTE_TOPIC_DOC = "This configuration controls the topic to be synchronized.";

    public static final String DIDI_HA_SYNC_TOPIC_PARTITIONS_ENABLED = "didi.ha.sync.topic.partitions.enabled";
    public static final String DIDI_HA_SYNC_TOPIC_PARTITIONS_ENABLED_DOC = "This configuration controls whether the topic partitions are to be synchronized.";

    public static final String DIDI_HA_SYNC_TOPIC_CONFIGS_ENABLED = "didi.ha.sync.topic.configs.enabled";
    public static final String DIDI_HA_SYNC_TOPIC_CONFIGS_ENABLED_DOC = "This configuration controls whether the topic configs are to be synchronized.";

    public static final String DIDI_HA_SYNC_TOPIC_ACLS_ENABLED = "didi.ha.sync.topic.acls.enabled";
    public static final String DIDI_HA_SYNC_TOPIC_ACLS_ENABLED_DOC = "This configuration controls whether the topic acls are to be synchronized.";

    static {
        CONFIG = new ConfigDef()
                .define(DIDI_HA_REMOTE_CLUSTER,
                        ConfigDef.Type.STRING,
                        null,
                        ConfigDef.Importance.LOW,
                        DIDI_HA_REMOTE_CLUSTER_DOC)
                .define(DIDI_HA_REMOTE_TOPIC,
                        ConfigDef.Type.STRING,
                        null,
                        ConfigDef.Importance.LOW,
                        DIDI_HA_REMOTE_TOPIC_DOC)
                .define(DIDI_HA_SYNC_TOPIC_PARTITIONS_ENABLED,
                        ConfigDef.Type.BOOLEAN,
                        false,
                        ConfigDef.Importance.LOW,
                        DIDI_HA_SYNC_TOPIC_PARTITIONS_ENABLED_DOC)
                .define(DIDI_HA_SYNC_TOPIC_CONFIGS_ENABLED,
                        ConfigDef.Type.BOOLEAN,
                        false,
                        ConfigDef.Importance.LOW,
                        DIDI_HA_SYNC_TOPIC_CONFIGS_ENABLED_DOC)
                .define(DIDI_HA_SYNC_TOPIC_ACLS_ENABLED,
                        ConfigDef.Type.BOOLEAN,
                        false,
                        ConfigDef.Importance.LOW,
                        DIDI_HA_SYNC_TOPIC_ACLS_ENABLED_DOC);
    }

    public HATopicConfig(Properties props) {
        super(CONFIG, props);
    }

    public HATopicConfig(Map<String, Object> props) {
        super(CONFIG, props);
    }

    protected HATopicConfig(Map<?, ?> props, boolean doLog) {
        super(CONFIG, props, doLog);
    }

    public static Set<String> configNames() {
        return CONFIG.names();
    }

    public static ConfigDef configDef() {
        return new ConfigDef(CONFIG);
    }
}
