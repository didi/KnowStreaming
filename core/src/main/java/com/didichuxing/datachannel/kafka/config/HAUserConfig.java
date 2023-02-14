package com.didichuxing.datachannel.kafka.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author leewei
 * @date 2022/6/28
 */
public class HAUserConfig extends AbstractConfig {
    public static final String DIDI_HA_ACTIVE_CLUSTER_CONFIG = "didi.ha.active.cluster";
    public static final String DIDI_HA_ACTIVE_CLUSTER_DOC = "A cluster to be bound by user's AuthId.";

    private static final ConfigDef CONFIG;

    static {
        CONFIG = new ConfigDef()
                .define(DIDI_HA_ACTIVE_CLUSTER_CONFIG,
                        ConfigDef.Type.STRING,
                        null,
                        ConfigDef.Importance.HIGH,
                        DIDI_HA_ACTIVE_CLUSTER_DOC);
    }

    public HAUserConfig(Properties props) {
        super(CONFIG, props);
    }

    public HAUserConfig(Map<String, Object> props) {
        super(CONFIG, props);
    }

    protected HAUserConfig(Map<?, ?> props, boolean doLog) {
        super(CONFIG, props, doLog);
    }

    public static Set<String> configNames() {
        return CONFIG.names();
    }

    public static ConfigDef configDef() {
        return new ConfigDef(CONFIG);
    }


}
