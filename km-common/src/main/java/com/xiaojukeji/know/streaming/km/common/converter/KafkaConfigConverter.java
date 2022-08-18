package com.xiaojukeji.know.streaming.km.common.converter;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.kafkaconfig.KafkaConfigDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.kafkaconfig.KafkaTopicDefaultConfig;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.constant.kafka.AbstractTopicConfig;
import com.xiaojukeji.know.streaming.km.common.constant.kafka.TopicConfig0100;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import kafka.server.KafkaConfig;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.requests.DescribeConfigsResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KafkaConfigConverter {
    private KafkaConfigConverter() {
    }

    public static List<KafkaConfigDetail> convert2KafkaBrokerConfigDetailList(List<String> configNameList, Properties properties) {
        List<KafkaConfigDetail> configList = new ArrayList<>();
        for (String configName: configNameList) {
            KafkaConfigDetail config = new KafkaConfigDetail();
            config.setName(configName);
            config.setValue(properties.getProperty(configName));
            config.setConfigSource(DescribeConfigsResponse.ConfigSource.TOPIC_CONFIG.ordinal());

            ConfigDef.ConfigKey configKey = KafkaConstant.KAFKA_ALL_CONFIG_DEF_MAP.get(configName);
            if (configKey != null) {
                config.setDocumentation(configKey.documentation);
                config.setDefaultValue(configKey.defaultValue == null? "": configKey.defaultValue.toString());
                config.setOverride(config.getValue() != null && config.getValue().equals(configKey.defaultValue));
                config.setConfigType(configKey.type.ordinal());
            } else {
                config.setDocumentation(null);
                config.setDefaultValue(null);
                config.setOverride(null);
                config.setConfigType(null);
            }

            config.setSensitive(null);
            config.setReadOnly(null);
            configList.add(config);
        }

        return configList;
    }

    public static List<KafkaConfigDetail> convert2KafkaTopicConfigDetailList(List<Properties> configNameAndDocList, Properties presentConfig) {
        List<KafkaConfigDetail> configList = new ArrayList<>();
        for (Properties nameAndDocProp: configNameAndDocList) {
            String configName = nameAndDocProp.getProperty(AbstractTopicConfig.KAFKA_CONFIG_KEY_NAME);
            if (ValidateUtils.isBlank(configName)) {
                continue;
            }

            KafkaConfigDetail config = new KafkaConfigDetail();

            config.setName(configName);
            config.setValue(presentConfig.getProperty(configName));
            config.setConfigSource(DescribeConfigsResponse.ConfigSource.TOPIC_CONFIG.ordinal());
            config.setDocumentation(nameAndDocProp.getProperty(AbstractTopicConfig.KAFKA_CONFIG_DOC_NAME));

            ConfigDef.ConfigKey configKey = KafkaConstant.KAFKA_ALL_CONFIG_DEF_MAP.get(configName);
            if (configKey != null) {
                config.setOverride(config.getValue() != null && !config.getValue().equals(configKey.defaultValue));
                config.setConfigType(configKey.type.ordinal());
            } else {
                config.setOverride(null);
                config.setConfigType(null);
            }

            config.setDefaultValue(null);
            config.setSensitive(null);
            config.setReadOnly(null);

            configList.add(config);
        }

        return configList;
    }

    public static List<KafkaConfigDetail> convert2KafkaConfigDetailList(Config config) {
        List<KafkaConfigDetail> configList = new ArrayList<>();
        for (ConfigEntry configEntry: config.entries()) {
            configList.add(convert2KafkaConfigDetail(configEntry, KafkaConstant.KAFKA_ALL_CONFIG_DEF_MAP));
        }

        return configList;
    }

    private static KafkaConfigDetail convert2KafkaConfigDetail(ConfigEntry configEntry, Map<String, ConfigDef.ConfigKey> configKeyMap) {
        KafkaConfigDetail config = new KafkaConfigDetail();
        config.setName(configEntry.name());
        config.setValue(configEntry.value());
        config.setConfigSource(configEntry.source().ordinal());
        config.setSensitive(configEntry.isSensitive());
        config.setReadOnly(configEntry.isReadOnly());
        config.setConfigType(configEntry.type().ordinal());

        ConfigDef.ConfigKey configKey = configKeyMap.get(configEntry.name());
        if (configKey != null) {
            config.setDocumentation(configKey.documentation);
            config.setDefaultValue(configKey.defaultValue == null? "": configKey.defaultValue.toString());
        } else {
            config.setDocumentation(configEntry.documentation());
            config.setDocumentation("");
        }

        config.setOverride(!configEntry.isDefault());
        return config;
    }

    public static List<KafkaTopicDefaultConfig> convert2KafkaTopicDefaultConfigList(List<Properties> configNameAndDocList, Map<String, String> defaultValueMap) {
        List<KafkaTopicDefaultConfig> configList = new ArrayList<>();
        for (Properties nameAndDocProp: configNameAndDocList) {
            String configName = nameAndDocProp.getProperty(AbstractTopicConfig.KAFKA_CONFIG_KEY_NAME);
            if (ValidateUtils.isBlank(configName)) {
                continue;
            }

            KafkaTopicDefaultConfig config = new KafkaTopicDefaultConfig();
            config.setName(configName);
            config.setDocumentation(nameAndDocProp.getProperty(AbstractTopicConfig.KAFKA_CONFIG_DOC_NAME));

            ConfigDef.ConfigKey configKey = KafkaConstant.KAFKA_ALL_CONFIG_DEF_MAP.get(configName);
            if (configKey != null) {
                config.setConfigType(configKey.type.ordinal());
            } else {
                config.setConfigType(null);
            }

            // 设置默认值
            config.setDefaultValue(
                    defaultValueMap.getOrDefault(configName, defaultValueMap.getOrDefault("log." + configName, null))
            );

            if (configName.equals(TopicConfig0100.RETENTION_MS_CONFIG)) {
                if (defaultValueMap.containsKey(KafkaConfig.LogRetentionTimeMillisProp())) {
                    config.setDefaultValue(defaultValueMap.get(KafkaConfig.LogRetentionTimeMillisProp()));
                } else if (defaultValueMap.containsKey(KafkaConfig.LogRetentionTimeMinutesProp())) {
                    Long val = ConvertUtil.string2Long(defaultValueMap.get(KafkaConfig.LogRetentionTimeMinutesProp()));
                    if (val != null && val.equals(-1L)) {
                        config.setDefaultValue("-1");
                    } else if (val != null ) {
                        config.setDefaultValue(String.valueOf(val * 60L * 1000L));
                    }
                } else if (defaultValueMap.containsKey(KafkaConfig.LogRetentionTimeHoursProp())) {
                    Long val = ConvertUtil.string2Long(defaultValueMap.get(KafkaConfig.LogRetentionTimeHoursProp()));
                    if (val != null && val.equals(-1L)) {
                        config.setDefaultValue("-1");
                    } else if (val != null ) {
                        config.setDefaultValue(String.valueOf(val * 60L * 60L * 1000L));
                    }
                } else {
                    config.setDefaultValue(null);
                }
            }

            config.setReadOnly(false);
            configList.add(config);
        }

        return configList;
    }
}
