package com.xiaojukeji.know.streaming.km.common.enums.health;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.HealthCompareValueConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.HealthDetectedInLatestMinutesConfig;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 健康检查-配置名枚举
 */
@Getter
public enum HealthCheckNameEnum {
    UNKNOWN(
            HealthCheckDimensionEnum.UNKNOWN,
            "未知",
            Constant.HC_CONFIG_NAME_PREFIX + "UNKNOWN",
            "未知",
            BaseClusterHealthConfig.class
    ),

    CLUSTER_NO_CONTROLLER(
            HealthCheckDimensionEnum.CLUSTER,
            "Controller",
            Constant.HC_CONFIG_NAME_PREFIX + "CLUSTER_NO_CONTROLLER",
            "集群Controller数错误",
            HealthCompareValueConfig.class
    ),

    BROKER_REQUEST_QUEUE_FULL(
            HealthCheckDimensionEnum.BROKER,
            "RequestQueueSize",
            Constant.HC_CONFIG_NAME_PREFIX + "BROKER_REQUEST_QUEUE_FULL",
            "Broker-RequestQueueSize被打满",
            HealthCompareValueConfig.class
    ),

    BROKER_NETWORK_PROCESSOR_AVG_IDLE_TOO_LOW(
            HealthCheckDimensionEnum.BROKER,
            "NetworkProcessorAvgIdlePercent",
            Constant.HC_CONFIG_NAME_PREFIX + "BROKER_NETWORK_PROCESSOR_AVG_IDLE_TOO_LOW",
            "Broker-NetworkProcessorAvgIdlePercent的Idle过低",
            HealthCompareValueConfig.class
    ),

    GROUP_RE_BALANCE_TOO_FREQUENTLY(
            HealthCheckDimensionEnum.GROUP,
            "Group Re-Balance",
            Constant.HC_CONFIG_NAME_PREFIX + "GROUP_RE_BALANCE_TOO_FREQUENTLY",
            "Group re-balance太频繁",
            HealthDetectedInLatestMinutesConfig.class
    ),

    TOPIC_NO_LEADER(
            HealthCheckDimensionEnum.TOPIC,
            "NoLeader",
            Constant.HC_CONFIG_NAME_PREFIX + "TOPIC_NO_LEADER",
            "Topic 无Leader数",
            HealthCompareValueConfig.class
    ),

    TOPIC_UNDER_REPLICA_TOO_LONG(
            HealthCheckDimensionEnum.TOPIC,
            "UnderReplicaTooLong",
            Constant.HC_CONFIG_NAME_PREFIX + "TOPIC_UNDER_REPLICA_TOO_LONG",
            "Topic 长期处于未同步状态",
            HealthDetectedInLatestMinutesConfig.class
    ),

    ;

    /**
     * 配置维度
     */
    private final HealthCheckDimensionEnum dimensionEnum;

    /**
     * 检查项
     */
    private final String configItem;

    /**
     * 配置名
     */
    private final String configName;

    /**
     * 配置说明
     */
    private final String configDesc;

    /**
     * 配置类
     */
    private final Class configClazz;

    HealthCheckNameEnum(HealthCheckDimensionEnum dimensionEnum, String configItem, String configName, String configDesc, Class configClazz) {
        this.dimensionEnum = dimensionEnum;
        this.configItem = configItem;
        this.configName = configName;
        this.configDesc = configDesc;
        this.configClazz = configClazz;
    }

    public static HealthCheckNameEnum getByName(String configName) {
        for (HealthCheckNameEnum nameEnum: HealthCheckNameEnum.values()) {
            if (nameEnum.configName.equals(configName)) {
                return nameEnum;
            }
        }

        return UNKNOWN;
    }

    public static List<HealthCheckNameEnum> getByDimension(HealthCheckDimensionEnum dimensionEnum) {
        List<HealthCheckNameEnum> nameEnumList = new ArrayList<>();
        for (HealthCheckNameEnum nameEnum: HealthCheckNameEnum.values()) {
            if (dimensionEnum == null || nameEnum.dimensionEnum.equals(dimensionEnum)) {
                nameEnumList.add(nameEnum);
            }
        }

        return nameEnumList;
    }

    public static List<HealthCheckNameEnum> getByDimensionCode(Integer dimension) {
        List<HealthCheckNameEnum> nameEnumList = new ArrayList<>();
        for (HealthCheckNameEnum nameEnum: HealthCheckNameEnum.values()) {
            if (dimension == null || nameEnum.dimensionEnum.getDimension() == dimension) {
                nameEnumList.add(nameEnum);
            }
        }

        return nameEnumList;
    }
}
