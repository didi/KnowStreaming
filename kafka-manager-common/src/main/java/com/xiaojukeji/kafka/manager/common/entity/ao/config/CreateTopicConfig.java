package com.xiaojukeji.kafka.manager.common.entity.ao.config;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/24
 */
public class CreateTopicConfig {
    private List<CreateTopicElemConfig> configList;

    public List<CreateTopicElemConfig> getConfigList() {
        return configList;
    }

    public void setConfigList(List<CreateTopicElemConfig> configList) {
        this.configList = configList;
    }

    @Override
    public String toString() {
        return "CreateTopicConfig{" +
                "configList=" + configList +
                '}';
    }
}