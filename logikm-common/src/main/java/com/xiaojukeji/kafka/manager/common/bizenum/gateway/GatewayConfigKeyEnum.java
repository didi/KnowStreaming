package com.xiaojukeji.kafka.manager.common.bizenum.gateway;

/**
 * @author zengqiao
 * @date 20/7/28
 */
public enum GatewayConfigKeyEnum {
    SD_CLUSTER_ID("SD_CLUSTER_ID", "SD_CLUSTER_ID"),
    SD_QUEUE_SIZE("SD_QUEUE_SIZE", "SD_QUEUE_SIZE"),
    SD_APP_RATE("SD_APP_RATE", "SD_APP_RATE"),
    SD_IP_RATE("SD_IP_RATE", "SD_IP_RATE"),
    SD_SP_RATE("SD_SP_RATE", "SD_SP_RATE"),

    ;

    private String configType;

    private String configName;

    GatewayConfigKeyEnum(String configType, String configName) {
        this.configType = configType;
        this.configName = configName;
    }

    public String getConfigType() {
        return configType;
    }

    public String getConfigName() {
        return configName;
    }

    @Override
    public String toString() {
        return "GatewayConfigKeyEnum{" +
                "configType='" + configType + '\'' +
                ", configName='" + configName + '\'' +
                '}';
    }

    public static GatewayConfigKeyEnum getByConfigType(String configType) {
        for (GatewayConfigKeyEnum configKeyEnum: GatewayConfigKeyEnum.values()) {
            if (configKeyEnum.getConfigType().equals(configType)) {
                return configKeyEnum;
            }
        }
        return null;
    }
}