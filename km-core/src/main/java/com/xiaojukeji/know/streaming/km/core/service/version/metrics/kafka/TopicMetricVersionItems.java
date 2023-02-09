package com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka;

import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.BaseMetricVersionMetric;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem.*;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_TOPIC;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxAttribute.*;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxName.*;
import static com.xiaojukeji.know.streaming.km.core.service.topic.impl.TopicMetricServiceImpl.*;

@Component
public class TopicMetricVersionItems extends BaseMetricVersionMetric {

    public static final String TOPIC_METRIC_HEALTH_STATE                            = "HealthState";
    public static final String TOPIC_METRIC_HEALTH_CHECK_PASSED                     = "HealthCheckPassed";
    public static final String TOPIC_METRIC_HEALTH_CHECK_TOTAL                      = "HealthCheckTotal";

    public static final String TOPIC_METRIC_TOTAL_PRODUCE_REQUESTS                  = "TotalProduceRequests";
    public static final String TOPIC_METRIC_BYTES_REJECTED                          = "BytesRejected";
    public static final String TOPIC_METRIC_FAILED_FETCH_REQ                        = "FailedFetchRequests";
    public static final String TOPIC_METRIC_FAILED_PRODUCE_REQ                      = "FailedProduceRequests";
    public static final String TOPIC_METRIC_REP_COUNT                               = "ReplicationCount";
    public static final String TOPIC_METRIC_MESSAGES                                = "Messages";
    public static final String TOPIC_METRIC_MESSAGE_IN                              = "MessagesIn";
    public static final String TOPIC_METRIC_BYTES_IN                                = "BytesIn";
    public static final String TOPIC_METRIC_BYTES_IN_MIN_5                          = "BytesIn_min_5";
    public static final String TOPIC_METRIC_BYTES_IN_MIN_15                         = "BytesIn_min_15";
    public static final String TOPIC_METRIC_BYTES_OUT                               = "BytesOut";
    public static final String TOPIC_METRIC_BYTES_OUT_MIN_5                         = "BytesOut_min_5";
    public static final String TOPIC_METRIC_BYTES_OUT_MIN_15                        = "BytesOut_min_15";
    public static final String TOPIC_METRIC_LOG_SIZE                                = "LogSize";
    public static final String TOPIC_METRIC_UNDER_REPLICA_PARTITIONS                = "PartitionURP";

    public static final String TOPIC_METRIC_MIRROR_FETCH_LAG                        = "MirrorFetchLag";
    public static final String TOPIC_METRIC_COLLECT_COST_TIME                       = Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME;

    @Override
    public int versionItemType() {
        return METRIC_TOPIC.getCode();
    }

    @Override
    public List<VersionMetricControlItem> init(){
        List<VersionMetricControlItem> itemList = new ArrayList<>();

        // HealthScore 指标
        itemList.add(buildAllVersionsItem()
                .name(TOPIC_METRIC_HEALTH_STATE).unit("0:好 1:中 2:差 3:宕机").desc("健康状态(0:好 1:中 2:差 3:宕机)").category(CATEGORY_HEALTH)
                .extendMethod( TOPIC_METHOD_GET_HEALTH_SCORE ));

        itemList.add(buildAllVersionsItem()
                .name( TOPIC_METRIC_HEALTH_CHECK_PASSED ).unit("个").desc("健康项检查通过数").category(CATEGORY_HEALTH)
                .extendMethod( TOPIC_METHOD_GET_HEALTH_SCORE ));

        itemList.add(buildAllVersionsItem()
                .name( TOPIC_METRIC_HEALTH_CHECK_TOTAL ).unit("个").desc("健康项检查总数").category(CATEGORY_HEALTH)
                .extendMethod( TOPIC_METHOD_GET_HEALTH_SCORE ));

        // TotalProduceRequests 指标
        itemList.add( buildAllVersionsItem()
                .name(TOPIC_METRIC_TOTAL_PRODUCE_REQUESTS).unit("条/s").desc("Topic 的 TotalProduceRequests").category(CATEGORY_PERFORMANCE)
                .extend( buildJMXMethodExtend( TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKER_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_PRODUCES_REQUEST ).jmxAttribute(RATE_MIN_1)));

        // BytesRejected 指标
        itemList.add( buildAllVersionsItem()
                .name(TOPIC_METRIC_BYTES_REJECTED).unit("个/s").desc("Topic 的每秒写入拒绝量").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKER_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTES_REJECTED ).jmxAttribute(RATE_MIN_1)));

        // FailedFetchRequests 指标
        itemList.add( buildAllVersionsItem()
                .name( TOPIC_METRIC_FAILED_FETCH_REQ ).unit("个/s").desc("Topic 的FailedFetchRequests").category(CATEGORY_PERFORMANCE)
                .extend( buildJMXMethodExtend( TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKER_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_FAILED_FETCH_REQUEST ).jmxAttribute(RATE_MIN_1)));

        // FailedProduceRequests 指标
        itemList.add( buildAllVersionsItem()
                .name( TOPIC_METRIC_FAILED_PRODUCE_REQ ).unit("个/s").desc("Topic 的 FailedProduceRequests").category(CATEGORY_PERFORMANCE)
                .extend( buildJMXMethodExtend( TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKER_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_FAILED_PRODUCE_REQUEST ).jmxAttribute(RATE_MIN_1)));

        // Messages 指标
        itemList.add( buildAllVersionsItem()
                .name(TOPIC_METRIC_REP_COUNT).unit("个").desc("Topic总的副本数").category(CATEGORY_PERFORMANCE)
                .extendMethod(TOPIC_METHOD_GET_REPLICAS_COUNT));

        // Messages 指标
        itemList.add( buildAllVersionsItem()
                .name(TOPIC_METRIC_MESSAGES).unit("条").desc("Topic总的消息数").category(CATEGORY_PERFORMANCE)
                .extendMethod(TOPIC_METHOD_GET_MESSAGES));

        // MessagesIn 指标
        itemList.add( buildAllVersionsItem()
                .name(TOPIC_METRIC_MESSAGE_IN).unit("条/s").desc("Topic每秒消息条数").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKER_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_MESSAGES_IN ).jmxAttribute(RATE_MIN_1)));

        // BytesInPerSec 指标
        itemList.add( buildAllVersionsItem()
                .name(TOPIC_METRIC_BYTES_IN).unit(BYTE_PER_SEC).desc("Topic每秒消息写入字节数").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKER_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_IN ).jmxAttribute(RATE_MIN_1)));

        itemList.add( buildAllVersionsItem()
                .name(TOPIC_METRIC_BYTES_IN_MIN_5).unit(BYTE_PER_SEC).desc("Topic每秒消息写入字节数，5分钟均值").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKER_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_IN ).jmxAttribute(RATE_MIN_5)));

        itemList.add( buildAllVersionsItem()
                .name(TOPIC_METRIC_BYTES_IN_MIN_15).unit(BYTE_PER_SEC).desc("Topic每秒消息写入字节数，15分钟均值").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKER_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_IN ).jmxAttribute(RATE_MIN_15)));

        // BytesOutPerSec 指标
        itemList.add( buildAllVersionsItem()
                .name(TOPIC_METRIC_BYTES_OUT).unit(BYTE_PER_SEC).desc("Topic每秒消息流出字节数").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKER_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_OUT ).jmxAttribute(RATE_MIN_1)));

        itemList.add( buildAllVersionsItem()
                .name(TOPIC_METRIC_BYTES_OUT_MIN_5).unit(BYTE_PER_SEC).desc("Topic每秒消息流出字节数，5分钟均值").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKER_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_OUT ).jmxAttribute(RATE_MIN_5)));

        itemList.add( buildAllVersionsItem()
                .name(TOPIC_METRIC_BYTES_OUT_MIN_15).unit(BYTE_PER_SEC).desc("Topic每秒消息流出字节数，15分钟均值").category(CATEGORY_FLOW)
                .extend( buildJMXMethodExtend( TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKER_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_OUT ).jmxAttribute(RATE_MIN_15)));

        // LogSize 指标
        itemList.add( buildAllVersionsItem()
                .name(TOPIC_METRIC_LOG_SIZE).unit("byte").desc("Topic 的大小").category(CATEGORY_PERFORMANCE)
                .extend( buildJMXMethodExtend( TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_PARTITION_OF_BROKER_JMX )
                        .jmxObjectName( JMX_LOG_LOG_SIZE ).jmxAttribute(VALUE)));

        // UnderReplicaPartitions 指标
        itemList.add( buildAllVersionsItem()
                .name(TOPIC_METRIC_UNDER_REPLICA_PARTITIONS).unit("个").desc("Topic未同步的副本数").category(CATEGORY_PERFORMANCE)
                .extend( buildJMXMethodExtend( TOPIC_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_PARTITION_OF_BROKER_JMX )
                        .jmxObjectName( JMX_CLUSTER_PARTITION_UNDER_REPLICATED ).jmxAttribute(VALUE)));

        itemList.add(buildAllVersionsItem()
                .name(TOPIC_METRIC_COLLECT_COST_TIME).unit("秒").desc("采集Topic指标的耗时").category(CATEGORY_PERFORMANCE)
                .extendMethod(TOPIC_METHOD_DO_NOTHING));

        itemList.add(buildItem().minVersion(VersionEnum.V_2_5_0_D_300).maxVersion(VersionEnum.V_2_5_0_D_MAX)
                .name(TOPIC_METRIC_MIRROR_FETCH_LAG).unit("条").desc("Topic复制延迟消息数").category(CATEGORY_FLOW)
                .extend(buildJMXMethodExtend(TOPIC_METHOD_GET_TOPIC_MIRROR_FETCH_LAG)
                        .jmxObjectName(JMX_SERVER_TOPIC_MIRROR).jmxAttribute(VALUE)));

        return itemList;
    }
}
