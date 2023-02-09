package com.xiaojukeji.know.streaming.km.common.enums.health;

import lombok.Getter;

/**
 * @author zengqiao
 * @date 22/03/01
 */
@Getter
public enum HealthCheckDimensionEnum {
    UNKNOWN(-1, "未知", "未知"),

    CLUSTER(0, "Cluster", "Cluster"),

    BROKER(1, "Broker", "Broker"),

    TOPIC(2, "Topic", "Topic"),

    GROUP(3, "Group", "Group"),

    ZOOKEEPER(4, "Zookeeper", "Zookeeper"),

    CONNECT_CLUSTER(5, "ConnectCluster", "Connect"),

    CONNECTOR(6, "Connector", "Connect"),

    MIRROR_MAKER(7,"MirrorMaker","MirrorMaker"),

    MAX_VAL(100, "所有的dimension的值需要小于MAX_VAL", "Ignore")

    ;

    private final int dimension;

    private final String message;

    private final String dimensionDisplayName;

    HealthCheckDimensionEnum(int dimension, String message, String dimensionDisplayName) {
        this.dimension = dimension;
        this.message = message;
        this.dimensionDisplayName=dimensionDisplayName;
    }

    public static HealthCheckDimensionEnum getByCode(Integer dimension) {
        if (dimension == null) {
            return UNKNOWN;
        }

        for (HealthCheckDimensionEnum dimensionEnum: HealthCheckDimensionEnum.values()) {
            if (dimensionEnum.getDimension() == dimension) {
                return dimensionEnum;
            }
        }

        return UNKNOWN;
    }
}
