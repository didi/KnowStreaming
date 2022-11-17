package com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka;

import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.BaseMetricVersionMetric;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem.*;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.*;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_BROKER;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxAttribute.*;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxName.*;
import static com.xiaojukeji.know.streaming.km.core.service.broker.impl.BrokerMetricServiceImpl.*;

@Component
public class BrokerMetricVersionItems extends BaseMetricVersionMetric {

    public static final String BROKER_METRIC_HEALTH_STATE                           = "HealthState";
    public static final String BROKER_METRIC_HEALTH_CHECK_PASSED                    = "HealthCheckPassed";
    public static final String BROKER_METRIC_HEALTH_CHECK_TOTAL                     = "HealthCheckTotal";
    public static final String BROKER_METRIC_TOTAL_REQ_QUEUE                        = "TotalRequestQueueSize";
    public static final String BROKER_METRIC_TOTAL_RES_QUEUE                        = "TotalResponseQueueSize";
    public static final String BROKER_METRIC_REP_BYTES_IN                           = "ReplicationBytesIn";
    public static final String BROKER_METRIC_REP_BYTES_OUT                          = "ReplicationBytesOut";
    public static final String BROKER_METRIC_MESSAGE_IN                             = "MessagesIn";
    public static final String BROKER_METRIC_TOTAL_PRODUCE_REQ                      = "TotalProduceRequests";
    public static final String BROKER_METRIC_NETWORK_RPO_AVG_IDLE                   = "NetworkProcessorAvgIdle";
    public static final String BROKER_METRIC_REQ_AVG_IDLE                           = "RequestHandlerAvgIdle";
    public static final String BROKER_METRIC_UNDER_REPLICATE_PARTITION              = "PartitionURP";
    public static final String BROKER_METRIC_CONNECTION_COUNT                       = "ConnectionsCount";
    public static final String BROKER_METRIC_BYTES_IN                               = "BytesIn";
    public static final String BROKER_METRIC_BYTES_IN_5_MIN                         = "BytesIn_min_5";
    public static final String BROKER_METRIC_BYTES_IN_15_MIN                        = "BytesIn_min_15";
    public static final String BROKER_METRIC_REASSIGNMENT_BYTES_IN                  = "ReassignmentBytesIn";
    public static final String BROKER_METRIC_BYTES_OUT                              = "BytesOut";
    public static final String BROKER_METRIC_BYTES_OUT_5_MIN                        = "BytesOut_min_5";
    public static final String BROKER_METRIC_BYTES_OUT_15_MIN                       = "BytesOut_min_15";
    public static final String BROKER_METRIC_REASSIGNMENT_BYTES_OUT                 = "ReassignmentBytesOut";
    public static final String BROKER_METRIC_PARTITIONS                             = "Partitions";
    public static final String BROKER_METRIC_PARTITIONS_SKEW                        = "PartitionsSkew";
    public static final String BROKER_METRIC_LEADERS                                = "Leaders";
    public static final String BROKER_METRIC_LEADERS_SKEW                           = "LeadersSkew";
    public static final String BROKER_METRIC_LOG_SIZE                               = "LogSize";
    public static final String BROKER_METRIC_ACTIVE_CONTROLLER_COUNT                = "ActiveControllerCount";
    public static final String BROKER_METRIC_ALIVE                                  = "Alive";
    public static final String BROKER_METRIC_COLLECT_COST_TIME                      = Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME;

    @Override
    public int versionItemType() {
        return METRIC_BROKER.getCode();
    }

    @Override
    public List<VersionMetricControlItem> init(){
        List<VersionMetricControlItem> items = new ArrayList<>();

        // HealthScore 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_HEALTH_STATE).unit("0:好 1:中 2:差 3:宕机").desc("健康状态(0:好 1:中 2:差 3:宕机)").category(CATEGORY_HEALTH)
                .extendMethod( BROKER_METHOD_GET_HEALTH_SCORE ));

        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_HEALTH_CHECK_PASSED ).unit("个").desc("健康检查通过数").category(CATEGORY_HEALTH)
                .extendMethod(BROKER_METHOD_GET_HEALTH_SCORE));

        items.add(buildAllVersionsItem()
                .name( BROKER_METRIC_HEALTH_CHECK_TOTAL ).unit("个").desc("健康检查总数").category(CATEGORY_HEALTH)
                .extendMethod( BROKER_METHOD_GET_HEALTH_SCORE ));

        // TotalRequestQueueSize 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_TOTAL_REQ_QUEUE).unit("个").desc("Broker的请求队列大小").category(CATEGORY_PERFORMANCE)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                    .jmxObjectName( JMX_NETWORK_REQUEST_QUEUE ).jmxAttribute(VALUE)));

        // TotalResponseQueueSize 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_TOTAL_RES_QUEUE).unit("个").desc("Broker的应答队列大小").category(CATEGORY_PERFORMANCE)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_NETWORK_RESPONSE_QUEUE ).jmxAttribute(VALUE)));

        // MessagesIn 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_MESSAGE_IN).unit("条/s").desc("Broker的每秒消息流入条数").category(CATEGORY_PERFORMANCE)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_MESSAGES_IN ).jmxAttribute(RATE_MIN_1)));

        // TotalProduceRequests 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_TOTAL_PRODUCE_REQ).unit("个/s").desc("Broker上Produce的每秒请求数").category(CATEGORY_PERFORMANCE)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_PRODUCES_REQUEST ).jmxAttribute(RATE_MIN_1)));

        // NetworkProcessorAvgIdle 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_NETWORK_RPO_AVG_IDLE).unit("%").desc("Broker的网络处理器的空闲百分比").category(CATEGORY_PERFORMANCE)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_NETWORK_PROCESSOR_AVG_IDLE ).jmxAttribute(VALUE)));

        // RequestHandlerAvgIdle 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_REQ_AVG_IDLE).unit("%").desc("Broker上请求处理器的空闲百分比").category(CATEGORY_PERFORMANCE)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_REQUEST_AVG_IDLE ).jmxAttribute(RATE_MIN_1)));

        // PartitionURP 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_UNDER_REPLICATE_PARTITION).unit("个").desc("Broker上的未同步的副本的个数").category(CATEGORY_PARTITION)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_UNDER_REP_PARTITIONS ).jmxAttribute(VALUE)));

        // connectionsCount 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_CONNECTION_COUNT).unit("个").desc("Broker上网络链接的个数").category(CATEGORY_PERFORMANCE)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_CONNECTION_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_SOCKET_CONNECTIONS ).jmxAttribute(CONNECTION_COUNT)));

        // BytesInPerSec 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_BYTES_IN).unit(BYTE_PER_SEC).desc("Broker的每秒数据写入量").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_IN ).jmxAttribute(RATE_MIN_1)));

        items.add(buildAllVersionsItem(BROKER_METRIC_BYTES_IN_5_MIN, BYTE_PER_SEC)
                .desc("Broker的每秒数据写入量，5分钟均值").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_IN ).jmxAttribute(RATE_MIN_5)));

        items.add(buildAllVersionsItem(BROKER_METRIC_BYTES_IN_15_MIN, BYTE_PER_SEC)
                .desc("Broker的每秒数据写入量，15分钟均值").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_IN ).jmxAttribute(RATE_MIN_15)));

        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_REASSIGNMENT_BYTES_IN).unit(BYTE_PER_SEC)
                .desc("Broker的每秒数据迁移写入量").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_REASSIGNMENT_BYTE_IN ).jmxAttribute(RATE_MIN_1)));

        // BytesInPerSec 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_BYTES_OUT).unit(BYTE_PER_SEC).desc("Broker的每秒数据流出量").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_OUT ).jmxAttribute(RATE_MIN_1)));

        items.add(buildAllVersionsItem(BROKER_METRIC_BYTES_OUT_5_MIN, BYTE_PER_SEC)
                .desc("Broker的每秒数据流出量，5分钟均值").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_OUT ).jmxAttribute(RATE_MIN_5)));

        items.add(buildAllVersionsItem(BROKER_METRIC_BYTES_OUT_15_MIN, BYTE_PER_SEC)
                .desc("Broker的每秒数据流出量，15分钟均值").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_OUT ).jmxAttribute(RATE_MIN_15)));

        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_REASSIGNMENT_BYTES_OUT).unit(BYTE_PER_SEC)
                .desc("Broker的每秒数据迁移流出量").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_REASSIGNMENT_BYTE_OUT ).jmxAttribute(RATE_MIN_1)));

        // Partitions 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_PARTITIONS).unit("个").desc("Broker上的Partition个数").category(CATEGORY_PARTITION)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_PARTITIONS ).jmxAttribute(VALUE)));

        // PartitionsSkew 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_PARTITIONS_SKEW).unit("%").desc("Broker上的Partitions倾斜度").category(CATEGORY_PARTITION)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_PARTITIONS_SKEW )
                        .jmxObjectName( JMX_SERVER_PARTITIONS ).jmxAttribute(VALUE)));

        // Leaders 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_LEADERS).unit("个").desc("Broker上的Leaders个数").category(CATEGORY_PARTITION)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_LEADERS ).jmxAttribute(VALUE)));

        // LeadersSkew 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_LEADERS_SKEW).unit("%").desc("Broker上的Leaders倾斜度").category(CATEGORY_PARTITION)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_LEADERS_SKEW )
                        .jmxObjectName( JMX_SERVER_PARTITIONS ).jmxAttribute(VALUE)));

        // LogSize 指标
        items.add(buildItem().minVersion(V_0_10_0_0).maxVersion(V_1_0_0)
                .name(BROKER_METRIC_LOG_SIZE).unit("byte").desc("Broker上的消息容量大小").category(CATEGORY_PARTITION)
                .extendMethod(BROKER_METHOD_GET_LOG_SIZE_FROM_JMX));
        items.add(buildItem().minVersion(V_1_0_0).maxVersion(V_MAX)
                .name(BROKER_METRIC_LOG_SIZE).unit("byte").desc("Broker上的消息容量大小").category(CATEGORY_PARTITION)
                .extendMethod(BROKER_METHOD_GET_LOG_SIZE_FROM_CLIENT));

        // ActiveControllerCount 指标
        items.add(buildAllVersionsItem(BROKER_METRIC_ACTIVE_CONTROLLER_COUNT, "个").desc("Broker是否为controller").category(CATEGORY_PERFORMANCE)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_CONTROLLER_ACTIVE_COUNT ).jmxAttribute(VALUE)));

        // ActiveControllerCount 指标
        items.add(buildAllVersionsItem(BROKER_METRIC_ALIVE, "是/否").desc("Broker是否存活，1：存活；0：没有存活").category(CATEGORY_PERFORMANCE)
                .extend( buildMethodExtend( BROKER_METHOD_IS_BROKER_ALIVE )));

        // BROKER_METRIC_REP_BYTES_IN 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_REP_BYTES_IN).unit(BYTE_PER_SEC).desc("Broker 的 ReplicationBytesIn").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_REPLICATION_BYTES_IN ).jmxAttribute(RATE_MIN_1)));

        // BROKER_METRIC_REP_BYTES_OUT 指标
        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_REP_BYTES_OUT).unit(BYTE_PER_SEC).desc("Broker 的 ReplicationBytesOut").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( BROKER_METHOD_GET_METRIC_FROM_KAFKA_BY_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_REPLICATION_BYTES_OUT ).jmxAttribute(RATE_MIN_1)));

        items.add(buildAllVersionsItem()
                .name(BROKER_METRIC_COLLECT_COST_TIME).unit("秒").desc("采集Broker指标的耗时").category(CATEGORY_PERFORMANCE)
                .extendMethod(BROKER_METHOD_DO_NOTHING));
        return items;
    }
}

