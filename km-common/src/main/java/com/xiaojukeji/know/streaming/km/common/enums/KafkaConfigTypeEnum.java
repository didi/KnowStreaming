package com.xiaojukeji.know.streaming.km.common.enums;

import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import kafka.server.ConfigType;
import lombok.Getter;

@Getter
public enum KafkaConfigTypeEnum {
    UNKNOWN(-1, "unknown"),

    TOPIC(0, ConfigType.Topic()),

    CLIENT(1, ConfigType.Client()),

    USER(2, ConfigType.User()),

    BROKER(3, ConfigType.Broker()),

    IP(4, ConfigType.Ip()),

    ;

    private final int configCode;

    private final String configName;

    KafkaConfigTypeEnum(int configCode, String configName) {
        this.configCode = configCode;
        this.configName = configName;
    }

    public static ModuleEnum getByConfigName(String configName) {
        if (TOPIC.configName.equals(configName)) {
            return ModuleEnum.KAFKA_TOPIC_CONFIG;
        }

        if (BROKER.configName.equals(configName)) {
            return ModuleEnum.KAFKA_BROKER_CONFIG;
        }

        if (USER.configName.equals(configName)) {
            return ModuleEnum.KAFKA_USER;
        }

        return ModuleEnum.UNKNOWN;
    }
}
