package com.didichuxing.datachannel.kafka.config.manager;

import com.didichuxing.datachannel.kafka.config.MirrorConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.errors.InvalidConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Array$;
import scala.collection.Set;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ClusterConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ClusterConfigManager.class);

    private static Map<String, Properties> didiHAClusterConfigs = new HashMap<>();

    private static ConfigDef DIDI_HA_CLUSTER_CONFIGS = MirrorConfig.configDef();

    public static void initWithMap(Map<String, Properties> propertiesMap){
        didiHAClusterConfigs = propertiesMap;
    }

    public static void validateConfigs(Properties configs) {
        for (String key : configs.stringPropertyNames()) {
            if (!MirrorConfig.configNames().contains(key))
                throw new InvalidConfigurationException(String.format("Unknown cluster config name: %s", key));
        }
        Map<String, Object> realConfigs = DIDI_HA_CLUSTER_CONFIGS.parse(configs);
        for (String key : configs.stringPropertyNames()) {
            if (DIDI_HA_CLUSTER_CONFIGS.configKeys().get(key).validator != null)
                DIDI_HA_CLUSTER_CONFIGS.configKeys().get(key).validator.ensureValid(key, realConfigs.get(key));
        }
    }

    public void configure(String cluster, Properties configs) {
        if (configs.isEmpty()) {
            if (didiHAClusterConfigs.containsKey(cluster)) {
                didiHAClusterConfigs.remove(cluster);
                log.info("clean configs with cluster: {}", cluster);
            }
            return;
        }
        didiHAClusterConfigs.put(cluster, configs);
        log.info("set or update configs for cluster: {}, configs of this cluster: {}", cluster, getConfigs(cluster));
    }

    public static Object getConfig(String clusterId, String key) {
        if (!didiHAClusterConfigs.containsKey(clusterId)) return null;
        return didiHAClusterConfigs.get(clusterId).get(key);
    }

    public static Properties getConfigs(String clusterId) {
        if (!didiHAClusterConfigs.containsKey(clusterId)) return new Properties();
        return didiHAClusterConfigs.get(clusterId);
    }

    public static Map<String, Properties> getAllConfigs() {
       return didiHAClusterConfigs;
    }
}

