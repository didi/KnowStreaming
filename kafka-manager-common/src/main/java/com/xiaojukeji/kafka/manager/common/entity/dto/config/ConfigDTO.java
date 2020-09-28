package com.xiaojukeji.kafka.manager.common.entity.dto.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/3/20
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "配置")
public class ConfigDTO {
    @ApiModelProperty(value = "配置key")
    private String configKey;

    @ApiModelProperty(value = "配置value")
    private String configValue;

    @ApiModelProperty(value = "备注")
    private String configDescription;

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigDescription() {
        return configDescription;
    }

    public void setConfigDescription(String configDescription) {
        this.configDescription = configDescription;
    }

    @Override
    public String toString() {
        return "ConfigDTO{" +
                "configKey='" + configKey + '\'' +
                ", configValue='" + configValue + '\'' +
                ", configDescription='" + configDescription + '\'' +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isExistBlank(configKey)
                || ValidateUtils.isBlank(configValue)
                || ValidateUtils.isBlank(configDescription)) {
            return false;
        }
        return true;
    }
}