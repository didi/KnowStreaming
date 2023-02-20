package com.xiaojukeji.know.streaming.km.common.enums.health;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.HealthAmountRatioConfig;
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
            BaseClusterHealthConfig.class,
            false
    ),

    CLUSTER_NO_CONTROLLER(
            HealthCheckDimensionEnum.CLUSTER,
            "Controller",
            Constant.HC_CONFIG_NAME_PREFIX + "CLUSTER_NO_CONTROLLER",
            "集群Controller数正常",
            HealthCompareValueConfig.class,
            true
    ),

    BROKER_REQUEST_QUEUE_FULL(
            HealthCheckDimensionEnum.BROKER,
            "RequestQueueSize",
            Constant.HC_CONFIG_NAME_PREFIX + "BROKER_REQUEST_QUEUE_FULL",
            "Broker-RequestQueueSize指标",
            HealthCompareValueConfig.class,
            false
    ),

    BROKER_NETWORK_PROCESSOR_AVG_IDLE_TOO_LOW(
            HealthCheckDimensionEnum.BROKER,
            "NetworkProcessorAvgIdlePercent",
            Constant.HC_CONFIG_NAME_PREFIX + "BROKER_NETWORK_PROCESSOR_AVG_IDLE_TOO_LOW",
            "Broker-NetworkProcessorAvgIdlePercent指标",
            HealthCompareValueConfig.class,
            false
    ),

    GROUP_RE_BALANCE_TOO_FREQUENTLY(
            HealthCheckDimensionEnum.GROUP,
            "Group Re-Balance",
            Constant.HC_CONFIG_NAME_PREFIX + "GROUP_RE_BALANCE_TOO_FREQUENTLY",
            "Group re-balance频率",
            HealthDetectedInLatestMinutesConfig.class,
            false
    ),

    TOPIC_NO_LEADER(
            HealthCheckDimensionEnum.TOPIC,
            "NoLeader",
            Constant.HC_CONFIG_NAME_PREFIX + "TOPIC_NO_LEADER",
            "Topic 无Leader数",
            HealthCompareValueConfig.class,
            false
    ),

    TOPIC_UNDER_REPLICA_TOO_LONG(
            HealthCheckDimensionEnum.TOPIC,
            "UnderReplicaTooLong",
            Constant.HC_CONFIG_NAME_PREFIX + "TOPIC_UNDER_REPLICA_TOO_LONG",
            "Topic 未同步持续时间",
            HealthDetectedInLatestMinutesConfig.class,
            false
    ),

    ZK_BRAIN_SPLIT(
            HealthCheckDimensionEnum.ZOOKEEPER,
            "BrainSplit",
            Constant.HC_CONFIG_NAME_PREFIX + "ZK_BRAIN_SPLIT",
            "ZK 脑裂",
            HealthCompareValueConfig.class,
            true
    ),

    ZK_OUTSTANDING_REQUESTS(
            HealthCheckDimensionEnum.ZOOKEEPER,
            "OutstandingRequests",
            Constant.HC_CONFIG_NAME_PREFIX + "ZK_OUTSTANDING_REQUESTS",
            "ZK Outstanding 请求堆积数",
            HealthAmountRatioConfig.class,
            false
    ),

    ZK_WATCH_COUNT(
            HealthCheckDimensionEnum.ZOOKEEPER,
            "WatchCount",
            Constant.HC_CONFIG_NAME_PREFIX + "ZK_WATCH_COUNT",
            "ZK WatchCount 数",
            HealthAmountRatioConfig.class,
            false
    ),

    ZK_ALIVE_CONNECTIONS(
            HealthCheckDimensionEnum.ZOOKEEPER,
            "AliveConnections",
            Constant.HC_CONFIG_NAME_PREFIX + "ZK_ALIVE_CONNECTIONS",
            "ZK 连接数",
            HealthAmountRatioConfig.class,
            false
    ),

    ZK_APPROXIMATE_DATA_SIZE(
            HealthCheckDimensionEnum.ZOOKEEPER,
            "ApproximateDataSize",
            Constant.HC_CONFIG_NAME_PREFIX + "ZK_APPROXIMATE_DATA_SIZE",
            "ZK 数据大小(Byte)",
            HealthAmountRatioConfig.class,
            false
    ),

    ZK_SENT_RATE(
            HealthCheckDimensionEnum.ZOOKEEPER,
            "SentRate",
            Constant.HC_CONFIG_NAME_PREFIX + "ZK_SENT_RATE",
            "ZK 发包数",
            HealthAmountRatioConfig.class,
            false
    ),

    CONNECT_CLUSTER_TASK_STARTUP_FAILURE_PERCENTAGE(
            HealthCheckDimensionEnum.CONNECT_CLUSTER,
            "TaskStartupFailurePercentage",
            Constant.HC_CONFIG_NAME_PREFIX+"CONNECT_CLUSTER_TASK_STARTUP_FAILURE_PERCENTAGE",
            "Connect集群任务启动失败概率",
            HealthCompareValueConfig.class,
            false
    ),

    CONNECTOR_FAILED_TASK_COUNT(
            HealthCheckDimensionEnum.CONNECTOR,
            "ConnectorFailedTaskCount",
            Constant.HC_CONFIG_NAME_PREFIX+"CONNECTOR_FAILED_TASK_COUNT",
            "Connector失败状态的任务数量",
            HealthCompareValueConfig.class,
            false
    ),

    CONNECTOR_UNASSIGNED_TASK_COUNT(
            HealthCheckDimensionEnum.CONNECTOR,
            "ConnectorUnassignedTaskCount",
            Constant.HC_CONFIG_NAME_PREFIX+"CONNECTOR_UNASSIGNED_TASK_COUNT",
            "Connector未被分配的任务数量",
            HealthCompareValueConfig.class,
            false
    ),

    MIRROR_MAKER_FAILED_TASK_COUNT(
            HealthCheckDimensionEnum.MIRROR_MAKER,
            "MirrorMakerFailedTaskCount",
            Constant.HC_CONFIG_NAME_PREFIX+"MIRROR_MAKER_FAILED_TASK_COUNT",
            "MirrorMaker失败状态的任务数量",
            HealthCompareValueConfig.class,
            false
    ),

    MIRROR_MAKER_UNASSIGNED_TASK_COUNT(
            HealthCheckDimensionEnum.MIRROR_MAKER,
            "MirrorMakerUnassignedTaskCount",
            Constant.HC_CONFIG_NAME_PREFIX+"MIRROR_MAKER_UNASSIGNED_TASK_COUNT",
            "MirrorMaker未被分配的任务数量",
            HealthCompareValueConfig.class,
            false
    ),

    MIRROR_MAKER_TOTAL_RECORD_ERRORS(
            HealthCheckDimensionEnum.MIRROR_MAKER,
            "TotalRecord-errors",
            Constant.HC_CONFIG_NAME_PREFIX + "MIRROR_MAKER_TOTAL_RECORD_ERRORS",
            "MirrorMaker消息处理错误的次数",
            HealthCompareValueConfig.class,
            false
    ),

    MIRROR_MAKER_REPLICATION_LATENCY_MS_MAX(
            HealthCheckDimensionEnum.MIRROR_MAKER,
            "ReplicationLatencyMsMax",
            Constant.HC_CONFIG_NAME_PREFIX + "MIRROR_MAKER_REPLICATION_LATENCY_MS_MAX",
            "MirrorMaker消息复制最大延迟时间",
            HealthCompareValueConfig.class,
            false
    )




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

    /**
     * 是可用性检查？
     */
    private final boolean availableChecker;

    HealthCheckNameEnum(HealthCheckDimensionEnum dimensionEnum, String configItem, String configName, String configDesc, Class configClazz, boolean availableChecker) {
        this.dimensionEnum = dimensionEnum;
        this.configItem = configItem;
        this.configName = configName;
        this.configDesc = configDesc;
        this.configClazz = configClazz;
        this.availableChecker = availableChecker;
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
