package com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka;

import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.BaseMetricVersionMetric;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem.*;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_ZOOKEEPER;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxAttribute.*;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxName.*;
import static com.xiaojukeji.know.streaming.km.core.service.zookeeper.impl.ZookeeperMetricServiceImpl.*;

@Component
public class ZookeeperMetricVersionItems extends BaseMetricVersionMetric {
    /**
     * 健康状态
     */
    public static final String ZOOKEEPER_METRIC_HEALTH_STATE                        = "HealthState";
    public static final String ZOOKEEPER_METRIC_HEALTH_CHECK_PASSED                 = "HealthCheckPassed";
    public static final String ZOOKEEPER_METRIC_HEALTH_CHECK_TOTAL                  = "HealthCheckTotal";

    /**
     * 性能
     */
    public static final String ZOOKEEPER_METRIC_AVG_REQUEST_LATENCY                 = "AvgRequestLatency";
    public static final String ZOOKEEPER_METRIC_MIN_REQUEST_LATENCY                 = "MinRequestLatency";
    public static final String ZOOKEEPER_METRIC_MAX_REQUEST_LATENCY                 = "MaxRequestLatency";
    public static final String ZOOKEEPER_METRIC_OUTSTANDING_REQUESTS                = "OutstandingRequests";
    public static final String ZOOKEEPER_METRIC_NODE_COUNT                          = "ZnodeCount";
    public static final String ZOOKEEPER_METRIC_WATCH_COUNT                         = "WatchCount";
    public static final String ZOOKEEPER_METRIC_NUM_ALIVE_CONNECTIONS               = "NumAliveConnections";
    public static final String ZOOKEEPER_METRIC_PACKETS_RECEIVED                    = "PacketsReceived";
    public static final String ZOOKEEPER_METRIC_PACKETS_SENT                        = "PacketsSent";
    public static final String ZOOKEEPER_METRIC_EPHEMERALS_COUNT                    = "EphemeralsCount";
    public static final String ZOOKEEPER_METRIC_APPROXIMATE_DATA_SIZE               = "ApproximateDataSize";
    public static final String ZOOKEEPER_METRIC_OPEN_FILE_DESCRIPTOR_COUNT          = "OpenFileDescriptorCount";
    public static final String ZOOKEEPER_METRIC_MAX_FILE_DESCRIPTOR_COUNT           = "MaxFileDescriptorCount";

    public static final String ZOOKEEPER_METRIC_KAFKA_ZK_DISCONNECTS_PER_SEC        = "KafkaZKDisconnectsPerSec";
    public static final String ZOOKEEPER_METRIC_KAFKA_ZK_SYNC_CONNECTS_PER_SEC      = "KafkaZKSyncConnectsPerSec";
    public static final String ZOOKEEPER_METRIC_KAFKA_ZK_REQUEST_LATENCY_99TH       = "KafkaZKRequestLatencyMs_99thPercentile";
    public static final String ZOOKEEPER_METRIC_KAFKA_ZK_REQUEST_LATENCY_MAX        = "KafkaZKRequestLatencyMs_Max";
    public static final String ZOOKEEPER_METRIC_KAFKA_ZK_REQUEST_LATENCY_MEAN       = "KafkaZKRequestLatencyMs_Mean";
    public static final String ZOOKEEPER_METRIC_KAFKA_ZK_REQUEST_LATENCY_MIN        = "KafkaZKRequestLatencyMs_Min";


    public static final String ZOOKEEPER_METRIC_COLLECT_COST_TIME                   = Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME;

    @Override
    public int versionItemType() {
        return METRIC_ZOOKEEPER.getCode();
    }

    @Override
    public List<VersionMetricControlItem> init(){
        List<VersionMetricControlItem> items = new ArrayList<>();

        // 健康状态
        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_HEALTH_STATE).unit("0:好 1:中 2:差 3:宕机").desc("健康状态(0:好 1:中 2:差 3:宕机)").category(CATEGORY_HEALTH)
                .extendMethod(ZOOKEEPER_METHOD_GET_METRIC_FROM_HEALTH_SERVICE));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_HEALTH_CHECK_PASSED).unit("个").desc("健康巡检通过数").category(CATEGORY_HEALTH)
                .extendMethod(ZOOKEEPER_METHOD_GET_METRIC_FROM_HEALTH_SERVICE));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_HEALTH_CHECK_TOTAL).unit("个").desc("健康巡检总数").category(CATEGORY_HEALTH)
                .extendMethod(ZOOKEEPER_METHOD_GET_METRIC_FROM_HEALTH_SERVICE));


        // 性能指标
        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_AVG_REQUEST_LATENCY).unit("ms").desc("平均响应延迟").category(CATEGORY_PERFORMANCE)
                .extendMethod(ZOOKEEPER_METHOD_GET_METRIC_FROM_SERVER_CMD));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_MIN_REQUEST_LATENCY).unit("ms").desc("最小响应延迟").category(CATEGORY_PERFORMANCE)
                .extendMethod(ZOOKEEPER_METHOD_GET_METRIC_FROM_SERVER_CMD));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_MAX_REQUEST_LATENCY).unit("ms").desc("最大响应延迟").category(CATEGORY_PERFORMANCE)
                .extendMethod(ZOOKEEPER_METHOD_GET_METRIC_FROM_SERVER_CMD));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_OUTSTANDING_REQUESTS).unit("个").desc("堆积请求数").category(CATEGORY_PERFORMANCE)
                .extendMethod(ZOOKEEPER_METHOD_GET_METRIC_FROM_SERVER_CMD));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_NODE_COUNT).unit("个").desc("ZNode数量").category(CATEGORY_PERFORMANCE)
                .extendMethod(ZOOKEEPER_METHOD_GET_METRIC_FROM_SERVER_CMD));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_WATCH_COUNT).unit("个").desc("Watch数量").category(CATEGORY_PERFORMANCE)
                .extendMethod(ZOOKEEPER_METHOD_GET_METRIC_FROM_MONITOR_CMD));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_NUM_ALIVE_CONNECTIONS).unit("个").desc("客户端连接数量").category(CATEGORY_PERFORMANCE)
                .extendMethod(ZOOKEEPER_METHOD_GET_METRIC_FROM_SERVER_CMD));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_PACKETS_RECEIVED).unit("个").desc("接受包的数量").category(CATEGORY_PERFORMANCE)
                .extendMethod(ZOOKEEPER_METHOD_GET_METRIC_FROM_SERVER_CMD));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_PACKETS_SENT).unit("个").desc("发送包的数量").category(CATEGORY_PERFORMANCE)
                .extendMethod(ZOOKEEPER_METHOD_GET_METRIC_FROM_SERVER_CMD));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_EPHEMERALS_COUNT).unit("个").desc("临时节点数").category(CATEGORY_PERFORMANCE)
                .extendMethod(ZOOKEEPER_METHOD_GET_METRIC_FROM_MONITOR_CMD));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_APPROXIMATE_DATA_SIZE).unit("byte").desc("文件大小(近似值)").category(CATEGORY_PERFORMANCE)
                .extendMethod(ZOOKEEPER_METHOD_GET_METRIC_FROM_MONITOR_CMD));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_OPEN_FILE_DESCRIPTOR_COUNT).unit("个").desc("已打开的文件描述符数").category(CATEGORY_PERFORMANCE)
                .extendMethod(ZOOKEEPER_METHOD_GET_METRIC_FROM_MONITOR_CMD));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_MAX_FILE_DESCRIPTOR_COUNT).unit("个").desc("允许打开的最大文件描述符数").category(CATEGORY_PERFORMANCE)
                .extendMethod(ZOOKEEPER_METHOD_GET_METRIC_FROM_MONITOR_CMD));

        // JMX指标
        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_KAFKA_ZK_REQUEST_LATENCY_99TH).unit("ms").desc("ZK请求99分位延迟").category(CATEGORY_CLIENT)
                .extend( buildJMXMethodExtend( ZOOKEEPER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_ZK_REQUEST_LATENCY_MS ).jmxAttribute(PERCENTILE_99)));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_KAFKA_ZK_REQUEST_LATENCY_MAX).unit("ms").desc("ZK请求最大延迟").category(CATEGORY_CLIENT)
                .extend( buildJMXMethodExtend( ZOOKEEPER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_ZK_REQUEST_LATENCY_MS ).jmxAttribute(MAX)));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_KAFKA_ZK_REQUEST_LATENCY_MIN).unit("ms").desc("ZK请求最小延迟").category(CATEGORY_CLIENT)
                .extend( buildJMXMethodExtend( ZOOKEEPER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_ZK_REQUEST_LATENCY_MS ).jmxAttribute(MIN)));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_KAFKA_ZK_REQUEST_LATENCY_MEAN).unit("ms").desc("ZK请求平均延迟").category(CATEGORY_CLIENT)
                .extend( buildJMXMethodExtend( ZOOKEEPER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_ZK_REQUEST_LATENCY_MS ).jmxAttribute(MEAN)));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_KAFKA_ZK_DISCONNECTS_PER_SEC).unit("个").desc("断开连接数").category(CATEGORY_CLIENT)
                .extend( buildJMXMethodExtend( ZOOKEEPER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_ZK_DISCONNECTORS_PER_SEC ).jmxAttribute(RATE_MIN_1)));

        items.add(buildAllVersionsItem()
                .name(ZOOKEEPER_METRIC_KAFKA_ZK_SYNC_CONNECTS_PER_SEC).unit("个").desc("同步连接数").category(CATEGORY_CLIENT)
                .extend( buildJMXMethodExtend( ZOOKEEPER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_ZK_SYNC_CONNECTS_PER_SEC ).jmxAttribute(RATE_MIN_1)));
        return items;
    }
}

