package com.didichuxing.datachannel.kafka.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author 杨阳
 * @date {2022/6/1}
 */
public class HAGroupConfig extends AbstractConfig {
    private static final ConfigDef CONFIG;

    /** <code>didi.kafka.enable</code> */
    public static final String DIDI_KAFKA_ENABLE_CONFIG = "didi.kafka.enable";
    private static final String DIDI_KAFKA_ENABLE_DOC = "Enable didi kafka premium features.";

    /** <code>didi.ha.remote.cluster</code> */
    public static final String DIDI_HA_REMOTE_CLUSTER_CONFIG = "didi.ha.remote.cluster";
    public static final String DIDI_HA_REMOTE_CLUSTER_DOC = "This configuration controls the cluster in which the group to be synchronized exists.";

    static {
        CONFIG = new ConfigDef()
                .define(DIDI_KAFKA_ENABLE_CONFIG,
                        ConfigDef.Type.BOOLEAN,
                        false,
                        ConfigDef.Importance.HIGH,
                        DIDI_KAFKA_ENABLE_DOC)
                .define(DIDI_HA_REMOTE_CLUSTER_CONFIG,
                        ConfigDef.Type.STRING,
                        null,
                        ConfigDef.Importance.MEDIUM,
                        DIDI_HA_REMOTE_CLUSTER_DOC);
    }

    public HAGroupConfig(Properties props) {
        super(CONFIG, props);
    }

    public HAGroupConfig(Map<String, Object> props) {
        super(CONFIG, props);
    }

    protected HAGroupConfig(Map<?, ?> props, boolean doLog) {
        super(CONFIG, props, doLog);
    }

    public Boolean isDidiKafka() {
        return getBoolean(DIDI_KAFKA_ENABLE_CONFIG);
    }

    public static Set<String> configNames() {
        return CONFIG.names();
    }

    public static ConfigDef configDef() {
        return new ConfigDef(CONFIG);
    }
}
