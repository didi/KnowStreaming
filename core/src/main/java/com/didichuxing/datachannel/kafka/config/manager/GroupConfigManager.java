package com.didichuxing.datachannel.kafka.config.manager;

import com.didichuxing.datachannel.kafka.config.HAGroupConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.errors.InvalidConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author 杨阳
 * @date {2022/6/1}
 */
public class GroupConfigManager {

    private static final Logger log = LoggerFactory.getLogger(GroupConfigManager.class);

    private static Map<String, Properties> didiHAGroupConfigs = new HashMap<>();

    private static ConfigDef DIDI_HA_GROUP_CONFIGS = HAGroupConfig.configDef();

    public GroupConfigManager() {

    }

    public static void validateConfigs(Properties configs) {
        for (String key : configs.stringPropertyNames()) {
            if (!HAGroupConfig.configNames().contains(key))
                throw new InvalidConfigurationException(String.format("Unknown group config name: %s", key));
        }
        Map<String, Object> realConfigs = DIDI_HA_GROUP_CONFIGS.parse(configs);
        for (String key : configs.stringPropertyNames()) {
            if (DIDI_HA_GROUP_CONFIGS.configKeys().get(key).validator != null)
                DIDI_HA_GROUP_CONFIGS.configKeys().get(key).validator.ensureValid(key, realConfigs.get(key));
        }

    }

    public void configure(String group, Properties configs) {
        if (configs.isEmpty()) {
            if (didiHAGroupConfigs.containsKey(group)) {
                didiHAGroupConfigs.remove(group);
                log.info("clean configs for group:" + group);
            }
            return;
        }
        if (didiHAGroupConfigs.put(group, configs) != null) {
            // reload changes
        }
        log.info("set or update configs for group: {}, configs of this group: {}", group, getConfigs(group));
    }

    public static Object getConfig(String group, String key) {
        if (!didiHAGroupConfigs.containsKey(group)) return null;
        return didiHAGroupConfigs.get(group).get(key);
    }

    public static Properties getConfigs(String group) {
        if (!didiHAGroupConfigs.containsKey(group)) return new Properties();
        return didiHAGroupConfigs.get(group);
    }

    public static HAGroupConfig getHAGroupConfig(String group) {
        Properties props = getConfigs(group);
        return new HAGroupConfig(props);
    }
}
