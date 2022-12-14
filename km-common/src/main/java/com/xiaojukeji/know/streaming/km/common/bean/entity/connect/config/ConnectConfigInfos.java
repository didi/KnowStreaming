package com.xiaojukeji.know.streaming.km.common.bean.entity.connect.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.connect.runtime.rest.entities.ConfigInfo;
import org.apache.kafka.connect.runtime.rest.entities.ConfigInfos;

import java.util.*;

import static com.xiaojukeji.know.streaming.km.common.constant.Constant.CONNECTOR_CONFIG_ACTION_RELOAD_NAME;
import static com.xiaojukeji.know.streaming.km.common.constant.Constant.CONNECTOR_CONFIG_ERRORS_TOLERANCE_NAME;

/**
 * @see ConfigInfos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectConfigInfos {

    private static final Map<String, List<String>> recommendValuesMap = new HashMap<>();

    static {
        recommendValuesMap.put(CONNECTOR_CONFIG_ACTION_RELOAD_NAME, Arrays.asList("none", "restart"));
        recommendValuesMap.put(CONNECTOR_CONFIG_ERRORS_TOLERANCE_NAME, Arrays.asList("none", "all"));
    }
    private String name;

    private int errorCount;

    private List<String> groups;

    private List<ConnectConfigInfo> configs;

    public ConnectConfigInfos(ConfigInfos configInfos) {
        this.name = configInfos.name();
        this.errorCount = configInfos.errorCount();
        this.groups = configInfos.groups();

        this.configs = new ArrayList<>();
        for (ConfigInfo configInfo: configInfos.values()) {
            ConnectConfigKeyInfo definition = new ConnectConfigKeyInfo();
            definition.setName(configInfo.configKey().name());
            definition.setType(configInfo.configKey().type());
            definition.setRequired(configInfo.configKey().required());
            definition.setDefaultValue(configInfo.configKey().defaultValue());
            definition.setImportance(configInfo.configKey().importance());
            definition.setDocumentation(configInfo.configKey().documentation());
            definition.setGroup(configInfo.configKey().group());
            definition.setOrderInGroup(configInfo.configKey().orderInGroup());
            definition.setWidth(configInfo.configKey().width());
            definition.setDisplayName(configInfo.configKey().displayName());
            definition.setDependents(configInfo.configKey().dependents());

            ConnectConfigValueInfo value = new ConnectConfigValueInfo();
            value.setName(configInfo.configValue().name());
            value.setValue(configInfo.configValue().value());
            value.setRecommendedValues(recommendValuesMap.getOrDefault(configInfo.configValue().name(), configInfo.configValue().recommendedValues()));
            value.setErrors(configInfo.configValue().errors());
            value.setVisible(configInfo.configValue().visible());

            ConnectConfigInfo connectConfigInfo = new ConnectConfigInfo();
            connectConfigInfo.setDefinition(definition);
            connectConfigInfo.setValue(value);

            this.configs.add(connectConfigInfo);
        }
    }
}
