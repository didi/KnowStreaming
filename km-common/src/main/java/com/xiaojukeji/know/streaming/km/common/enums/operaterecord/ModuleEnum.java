package com.xiaojukeji.know.streaming.km.common.enums.operaterecord;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;

import java.util.List;
import java.util.Map;

/**
 * 操作记录模块枚举
 * @author d06679
 * @date 2017/7/14
 */
public enum ModuleEnum {
    UNKNOWN(Constant.INVALID_CODE, "unknown"),

    KAFKA_CLUSTER(1, "Kafka集群"),

    KAFKA_BROKER(20, "KafkaBroker"),
    KAFKA_BROKER_CONFIG(20, "KafkaBroker配置"),

    KAFKA_TOPIC(30, "KafkaTopic"),
    KAFKA_TOPIC_CONFIG(31, "KafkaTopic配置"),
    KAFKA_TOPIC_DATA(32, "KafkaTopic数据"),
    KAFKA_TOPIC_REASSIGN(33, "KafkaTopic迁移"),

    KAFKA_USER(40, "KafkaUser"),

    KAFKA_ACL(50, "KafkaACL"),

    KAFKA_GROUP(60, "KafkaGroup"),

    KAFKA_CONTROLLER(70, "KafkaController"),

    KAFKA_CONNECT_CLUSTER(80, "KafkaConnectCluster"),
    KAFKA_CONNECT_CONNECTOR(81, "KafkaConnectConnector"),

    PLATFORM_CONFIG(100, "平台配置"),

    JOB_KAFKA_REPLICA_REASSIGN(110, "Job-KafkaReplica迁移"),

    ;

    ModuleEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final int    code;

    private final String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", code);
        map.put("desc", desc);
        return map;
    }

    public static ModuleEnum valueOf(Integer code) {
        if (code == null) {
            return ModuleEnum.UNKNOWN;
        }
        for (ModuleEnum state : ModuleEnum.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }

        return ModuleEnum.UNKNOWN;
    }

    public static boolean validate(Integer code) {
        if (code == null) {
            return false;
        }
        for (ModuleEnum state : ModuleEnum.values()) {
            if (state.getCode() == code) {
                return true;
            }
        }

        return false;
    }

    public static List<Map<String, Object>> getAllAriusConfigs() {
        List<Map<String, Object>> objects = Lists.newArrayList();
        for (ModuleEnum moduleEnum : ModuleEnum.values()) {
            if (moduleEnum.getCode() == -1) {
                continue;
            }

            objects.add(moduleEnum.toMap());
        }

        return objects;
    }
}
