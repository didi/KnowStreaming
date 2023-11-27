package com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka;

import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.BaseMetricVersionMetric;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem.*;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_CLUSTER;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxAttribute.*;
import static com.xiaojukeji.know.streaming.km.common.jmx.JmxName.*;
import static com.xiaojukeji.know.streaming.km.core.service.cluster.impl.ClusterMetricServiceImpl.*;

/**
 * @author didi
 */
@Component
public class ClusterMetricVersionItems extends BaseMetricVersionMetric {
    /**
     * 整体的健康指标
     */
    public static final String CLUSTER_METRIC_HEALTH_STATE                          = "HealthState";
    public static final String CLUSTER_METRIC_HEALTH_CHECK_PASSED                   = "HealthCheckPassed";
    public static final String CLUSTER_METRIC_HEALTH_CHECK_TOTAL                    = "HealthCheckTotal";

    /**
     * Topics健康指标
     */
    public static final String CLUSTER_METRIC_HEALTH_STATE_TOPICS                   = "HealthState_Topics";
    public static final String CLUSTER_METRIC_HEALTH_CHECK_PASSED_TOPICS            = "HealthCheckPassed_Topics";
    public static final String CLUSTER_METRIC_HEALTH_CHECK_TOTAL_TOPICS             = "HealthCheckTotal_Topics";

    /**
     * Brokers健康指标
     */
    public static final String CLUSTER_METRIC_HEALTH_STATE_BROKERS                  = "HealthState_Brokers";
    public static final String CLUSTER_METRIC_HEALTH_CHECK_PASSED_BROKERS           = "HealthCheckPassed_Brokers";
    public static final String CLUSTER_METRIC_HEALTH_CHECK_TOTAL_BROKERS            = "HealthCheckTotal_Brokers";

    /**
     * Groups健康指标
     */
    public static final String CLUSTER_METRIC_HEALTH_STATE_GROUPS                   = "HealthState_Groups";
    public static final String CLUSTER_METRIC_HEALTH_CHECK_PASSED_GROUPS            = "HealthCheckPassed_Groups";
    public static final String CLUSTER_METRIC_HEALTH_CHECK_TOTAL_GROUPS             = "HealthCheckTotal_Groups";

    /**
     * Cluster健康指标
     */
    public static final String CLUSTER_METRIC_HEALTH_STATE_CLUSTER                  = "HealthState_Cluster";
    public static final String CLUSTER_METRIC_HEALTH_CHECK_PASSED_CLUSTER           = "HealthCheckPassed_Cluster";
    public static final String CLUSTER_METRIC_HEALTH_CHECK_TOTAL_CLUSTER            = "HealthCheckTotal_Cluster";

    /**
     * connector健康指标
     */
    public static final String CLUSTER_METRIC_HEALTH_STATE_CONNECTOR                = "HealthState_Connector";
    public static final String CLUSTER_METRIC_HEALTH_CHECK_PASSED_CONNECTOR         = "HealthCheckPassed_Connector";
    public static final String CLUSTER_METRIC_HEALTH_CHECK_TOTAL_CONNECTOR          = "HealthCheckTotal_Connector";

    /**
     * mm2健康指标
     */
    public static final String CLUSTER_METRIC_HEALTH_STATE_MIRROR_MAKER             = "HealthState_MirrorMaker";
    public static final String CLUSTER_METRIC_HEALTH_CHECK_PASSED_MIRROR_MAKER      = "HealthCheckPassed_MirrorMaker";
    public static final String CLUSTER_METRIC_HEALTH_CHECK_TOTAL_MIRROR_MAKER       = "HealthCheckTotal_MirrorMaker";



    public static final String CLUSTER_METRIC_TOTAL_REQ_QUEUE_SIZE                  = "TotalRequestQueueSize";
    public static final String CLUSTER_METRIC_TOTAL_RES_QUEUE_SIZE                  = "TotalResponseQueueSize";
    public static final String CLUSTER_METRIC_EVENT_QUEUE_SIZE                      = "EventQueueSize";
    public static final String CLUSTER_METRIC_ACTIVE_CONTROLLER_COUNT               = "ActiveControllerCount";
    public static final String CLUSTER_METRIC_TOTAL_PRODUCE_REQ                     = "TotalProduceRequests";
    public static final String CLUSTER_METRIC_TOTAL_LOG_SIZE                        = "TotalLogSize";
    public static final String CLUSTER_METRIC_CONNECTIONS                           = "ConnectionsCount";
    public static final String CLUSTER_METRIC_ZOOKEEPERS                            = "Zookeepers";
    public static final String CLUSTER_METRIC_ZOOKEEPERS_AVAILABLE                  = "ZookeepersAvailable";
    public static final String CLUSTER_METRIC_BROKERS                               = "Brokers";
    public static final String CLUSTER_METRIC_BROKERS_ALIVE                         = "BrokersAlive";
    public static final String CLUSTER_METRIC_BROKERS_NOT_ALIVE                     = "BrokersNotAlive";
    public static final String CLUSTER_METRIC_REPLICAS                              = "Replicas";
    public static final String CLUSTER_METRIC_TOPICS                                = "Topics";
    public static final String CLUSTER_METRIC_PARTITIONS                            = "Partitions";
    public static final String CLUSTER_METRIC_PARTITIONS_NO_LEADER                  = "PartitionNoLeader";
    public static final String CLUSTER_METRIC_PARTITION_MIN_ISR_S                   = "PartitionMinISR_S";
    public static final String CLUSTER_METRIC_PARTITION_MIN_ISR_E                   = "PartitionMinISR_E";
    public static final String CLUSTER_METRIC_PARTITION_URP                         = "PartitionURP";
    public static final String CLUSTER_METRIC_MESSAGES_IN                           = "MessagesIn";
    public static final String CLUSTER_METRIC_LEADER_MESSAGES                       = "LeaderMessages";
    public static final String CLUSTER_METRIC_BYTES_IN                              = "BytesIn";
    public static final String CLUSTER_METRIC_BYTES_IN_5_MIN                        = "BytesIn_min_5";
    public static final String CLUSTER_METRIC_BYTES_IN_15_MIN                       = "BytesIn_min_15";
    public static final String CLUSTER_METRIC_BYTES_OUT                             = "BytesOut";
    public static final String CLUSTER_METRIC_BYTES_OUT_5_MIN                       = "BytesOut_min_5";
    public static final String CLUSTER_METRIC_BYTES_OUT_15_MIN                      = "BytesOut_min_15";

    public static final String CLUSTER_METRIC_GROUP                                 = "Groups";
    public static final String CLUSTER_METRIC_GROUP_ACTIVES                         = "GroupActives";
    public static final String CLUSTER_METRIC_GROUP_EMPTYS                          = "GroupEmptys";
    public static final String CLUSTER_METRIC_GROUP_REBALANCES                      = "GroupRebalances";
    public static final String CLUSTER_METRIC_GROUP_DEADS                           = "GroupDeads";

    public static final String CLUSTER_METRIC_ALIVE                                 = "Alive";

    public static final String CLUSTER_METRIC_ACL_ENABLE                            = "AclEnable";
    public static final String CLUSTER_METRIC_ACLS                                  = "Acls";
    public static final String CLUSTER_METRIC_ACL_USERS                             = "AclUsers";
    public static final String CLUSTER_METRIC_ACL_TOPICS                            = "AclTopics";
    public static final String CLUSTER_METRIC_ACL_GROUPS                            = "AclGroups";

    public static final String CLUSTER_METRIC_JOB                                   = "Jobs";
    public static final String CLUSTER_METRIC_JOB_RUNNING                           = "JobsRunning";
    public static final String CLUSTER_METRIC_JOB_WAITING                           = "JobsWaiting";
    public static final String CLUSTER_METRIC_JOB_SUCCESS                           = "JobsSuccess";
    public static final String CLUSTER_METRIC_JOB_FAILED                            = "JobsFailed";

    public static final String CLUSTER_METRIC_COLLECT_COST_TIME                     = Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME;

    public ClusterMetricVersionItems(){}

    @Override
    public int versionItemType() {
        return METRIC_CLUSTER.getCode();
    }

    @Override
    public List<VersionMetricControlItem> init(){
        List<VersionMetricControlItem> itemList = new ArrayList<>();

        // HealthScore 指标
        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_HEALTH_STATE).unit("0:好 1:中 2:差 3:宕").desc("集群健康状态(0:好 1:中 2:差 3:宕)").category(CATEGORY_HEALTH)
                .extendMethod(CLUSTER_METHOD_GET_HEALTH_METRICS));

        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_HEALTH_CHECK_PASSED).unit("个").desc("集群总体健康检查通过数").category(CATEGORY_HEALTH)
                .extendMethod( CLUSTER_METHOD_GET_HEALTH_METRICS ));

        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_HEALTH_CHECK_TOTAL).unit("个").desc("集群总体健康检查总数").category(CATEGORY_HEALTH)
                .extendMethod( CLUSTER_METHOD_GET_HEALTH_METRICS ));

        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_HEALTH_STATE_TOPICS).unit("0:好 1:中 2:差 3:宕机").desc("集群Topics健康状态").category(CATEGORY_HEALTH)
                .extendMethod(CLUSTER_METHOD_GET_HEALTH_METRICS));

        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_HEALTH_CHECK_PASSED_TOPICS).unit("个").desc("集群Topics健康检查通过数").category(CATEGORY_HEALTH)
                .extendMethod( CLUSTER_METHOD_GET_HEALTH_METRICS ));

        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_TOPICS).unit("个").desc("集群Topics健康检查总数").category(CATEGORY_HEALTH)
                .extendMethod( CLUSTER_METHOD_GET_HEALTH_METRICS ));

        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_HEALTH_STATE_BROKERS).unit("0:好 1:中 2:差 3:宕机").desc("集群Brokers健康状态").category(CATEGORY_HEALTH)
                .extendMethod(CLUSTER_METHOD_GET_HEALTH_METRICS));

        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_HEALTH_CHECK_PASSED_BROKERS).unit("个").desc("集群Brokers健康检查通过数").category(CATEGORY_HEALTH)
                .extendMethod( CLUSTER_METHOD_GET_HEALTH_METRICS ));

        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_BROKERS).unit("个").desc("集群Brokers健康检查总数").category(CATEGORY_HEALTH)
                .extendMethod( CLUSTER_METHOD_GET_HEALTH_METRICS ));

        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_HEALTH_STATE_GROUPS).unit("0:好 1:中 2:差 3:宕机").desc("集群Groups健康状态").category(CATEGORY_HEALTH)
                .extendMethod(CLUSTER_METHOD_GET_HEALTH_METRICS));

        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_HEALTH_CHECK_PASSED_GROUPS).unit("个").desc("集群Groups健康检查通过数").category(CATEGORY_HEALTH)
                .extendMethod( CLUSTER_METHOD_GET_HEALTH_METRICS ));

        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_GROUPS).unit("个").desc("集群Groups健康检查总数").category(CATEGORY_HEALTH)
                .extendMethod( CLUSTER_METHOD_GET_HEALTH_METRICS ));

        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_HEALTH_STATE_CLUSTER).unit("0:好 1:中 2:差 3:宕机").desc("集群自身健康状态").category(CATEGORY_HEALTH)
                .extendMethod(CLUSTER_METHOD_GET_HEALTH_METRICS));

        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_HEALTH_CHECK_PASSED_CLUSTER).unit("个").desc("集群自身健康检查通过数").category(CATEGORY_HEALTH)
                .extendMethod( CLUSTER_METHOD_GET_HEALTH_METRICS ));

        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_CLUSTER).unit("个").desc("集群自身健康检查总数").category(CATEGORY_HEALTH)
                .extendMethod( CLUSTER_METHOD_GET_HEALTH_METRICS ));

        // TotalRequestQueueSize 指标
        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_TOTAL_REQ_QUEUE_SIZE).unit("个").desc("集群的中总的请求队列数").category(CATEGORY_CLUSTER)
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX )
                        .jmxObjectName( JMX_NETWORK_REQUEST_QUEUE ).jmxAttribute(VALUE)));

        // TotalResponseQueueSize 指标
        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_TOTAL_RES_QUEUE_SIZE).unit("个").desc("集群的 TotalResponseQueueSize").category(CATEGORY_CLUSTER)
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX )
                        .jmxObjectName( JMX_NETWORK_RESPONSE_QUEUE ).jmxAttribute(VALUE)));

        // EventQueueSize 指标
        itemList.add(buildItem().minVersion(VersionEnum.V_2_0_0).maxVersion(VersionEnum.V_MAX).category(CATEGORY_CLUSTER)
                .name(CLUSTER_METRIC_EVENT_QUEUE_SIZE).unit("个").desc("集群中controller的EventQueueSize大小")
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_CONTROLLER_JMX )
                        .jmxObjectName( JMX_CONTROLLER_EVENT_QUEUE_SIZE ).jmxAttribute(VALUE)));

        // ActiveControllerCount 指标
        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_ACTIVE_CONTROLLER_COUNT).unit("个").desc("集群中ActiveControllerCount大小").category(CATEGORY_CLUSTER)
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX )
                        .jmxObjectName( JMX_CONTROLLER_ACTIVE_COUNT ).jmxAttribute(VALUE)));

        // TotalProduceRequests 指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_TOTAL_PRODUCE_REQ).unit("个/s").desc("集群中的Produce每秒请求数").category(CATEGORY_CLUSTER)
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_PRODUCES_REQUEST ).jmxAttribute(RATE_MIN_1)));

        // TotalLogSize 指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_TOTAL_LOG_SIZE).unit("byte").desc("集群总的已使用的磁盘大小").category(CATEGORY_CLUSTER)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_TOTAL_LOG_SIZE )));

        // Connections 指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_CONNECTIONS).unit("个").desc("集群的连接(Connections)个数").category(CATEGORY_CLUSTER)
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX )
                        .jmxObjectName(JMX_SERVER_SOCKET_CONNECTIONS).jmxAttribute(VALUE)));

        // ZKs 个数指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_ZOOKEEPERS).unit("个").desc("集群中存活的zk节点个数").category(CATEGORY_CLUSTER)
                .extend(buildMethodExtend( CLUSTER_METHOD_GET_ZK_COUNT )));

        // ZK 是否合法，即可连接
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_ZOOKEEPERS_AVAILABLE).unit("是/否").desc("ZK地址是否合法").category(CATEGORY_CLUSTER)
                .extend(buildMethodExtend(CLUSTER_METHOD_GET_ZK_AVAILABLE)));

        // Brokers 总数指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_BROKERS).unit("个").desc("集群的broker的总数").category(CATEGORY_BROKER)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_BROKERS_COUNT )));

        // Brokers 存活数指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_BROKERS_ALIVE).unit("个").desc("集群的broker的存活数").category(CATEGORY_BROKER)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_BROKERS_ALIVE_COUNT )));

        // Brokers 未存活数指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_BROKERS_NOT_ALIVE).unit("个").desc("集群的broker的未存活数").category(CATEGORY_BROKER)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_BROKERS_NOT_ALIVE_COUNT )));

        // Replicas 指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_REPLICAS).unit("个").desc("集群中Replica的总数").category(CATEGORY_CLUSTER)
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_REPLICAS_COUNT )));

        // Topics 个数指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_TOPICS).unit("个").desc("集群中Topic的总数").category(CATEGORY_CLUSTER)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_TOPIC_SIZE )));

        // Partitions 个数指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_PARTITIONS).unit("个").desc("集群的Partitions总数").category(CATEGORY_CLUSTER)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_PARTITION_SIZE )));

        // PartitionNoLeader 个数指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_PARTITIONS_NO_LEADER).unit("个").desc("集群中的PartitionNoLeader总数").category(CATEGORY_CLUSTER)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_PARTITION_NO_LEADER_SIZE )));

        // 小于 PartitionMinISR 个数指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_PARTITION_MIN_ISR_S).unit("个").desc("集群中的小于PartitionMinISR总数").category(CATEGORY_CLUSTER)
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX )
                        .jmxObjectName( JMX_SERVER_UNDER_MIN_ISR_PARTITIONS ).jmxAttribute(VALUE)));

        // 等于 PartitionMinISR 个数指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_PARTITION_MIN_ISR_E).unit("个").desc("集群中的PartitionMinISR总数").category(CATEGORY_CLUSTER)
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX )
                        .jmxObjectName( JMX_SERVER_AT_MIN_ISR_PARTITIONS ).jmxAttribute(VALUE)));

        // PartitionURP 个数指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_PARTITION_URP).unit("个").desc("集群中的未同步的总数").category(CATEGORY_CLUSTER)
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX )
                        .jmxObjectName( JMX_SERVER_UNDER_REP_PARTITIONS ).jmxAttribute(VALUE)));

        // MessagesIn 指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_MESSAGES_IN).unit("条/s").desc("集群每秒消息写入条数").category(CATEGORY_CLUSTER)
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_MESSAGES_IN ).jmxAttribute(RATE_MIN_1)));

        // LeaderMessages 指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_LEADER_MESSAGES).unit("条").desc("集群中leader总的消息条数").category(CATEGORY_CLUSTER)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_MESSAGE_SIZE )));

        // BytesInPerSec 指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_BYTES_IN).unit(BYTE_PER_SEC).desc("集群的每秒写入字节数").category(CATEGORY_CLUSTER)
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_IN ).jmxAttribute(RATE_MIN_1)));

        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_BYTES_IN_5_MIN).unit(BYTE_PER_SEC).desc("集群的每秒写入字节数，5分钟均值").category(CATEGORY_CLUSTER)
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_IN ).jmxAttribute(RATE_MIN_5)));

        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_BYTES_IN_15_MIN).unit(BYTE_PER_SEC).desc("集群的每秒写入字节数，15分钟均值").category(CATEGORY_CLUSTER)
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_IN ).jmxAttribute(RATE_MIN_15)));

        // BytesOutPerSec 指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_BYTES_OUT).unit(BYTE_PER_SEC).desc("集群的每秒流出字节数").category(CATEGORY_CLUSTER)
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_OUT ).jmxAttribute(RATE_MIN_1)));

        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_BYTES_OUT_5_MIN).unit(BYTE_PER_SEC).desc("集群的每秒流出字节数，5分钟均值").category(CATEGORY_CLUSTER)
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_OUT ).jmxAttribute(RATE_MIN_5)));

        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_BYTES_OUT_15_MIN).unit(BYTE_PER_SEC).desc("集群的每秒流出字节数，15分钟均值").category(CATEGORY_CLUSTER)
                .extend( buildJMXMethodExtend( CLUSTER_METHOD_GET_METRIC_FROM_KAFKA_BY_TOTAL_BROKERS_JMX )
                        .jmxObjectName( JMX_SERVER_BROKER_BYTE_OUT ).jmxAttribute(RATE_MIN_15)));

        // 集群维度-Group相关指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_GROUP).unit("个").desc("集群中Group的总数").category(CATEGORY_CONSUMER)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_GROUP_COUNT )));

        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_GROUP_ACTIVES).unit("个").desc("集群中ActiveGroup的总数").category(CATEGORY_CONSUMER)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_GROUP_ACTIVE_COUNT )));

        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_GROUP_EMPTYS).unit("个").desc("集群中EmptyGroup的总数").category(CATEGORY_CONSUMER)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_GROUP_EMPTY_COUNT )));

        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_GROUP_REBALANCES).unit("个").desc("集群中reBalanceGroup的总数").category(CATEGORY_CONSUMER)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_GROUP_REBALANCED_COUNT )));

        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_GROUP_DEADS).unit("个").desc("集群中DeadGroup的总数").category(CATEGORY_CONSUMER)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_GROUP_DEAD_COUNT )));

        // 集群维度-alive
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_ALIVE).unit("是/否").desc("集群是否存活，1：存活；0：没有存活").category(CATEGORY_CLUSTER)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_ALIVE )));

        // 集群维度-ACL相关指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_ACL_ENABLE).unit("是/否").desc("集群是否开启Acl，1：是；0：否").category(CATEGORY_SECURITY)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_ACL_ENABLE )));

        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_ACLS).unit("个").desc("ACL数").category(CATEGORY_SECURITY)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_ACLS )));

        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_ACL_USERS).unit("个").desc("ACL-KafkaUser数").category(CATEGORY_SECURITY)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_ACL_USERS )));

        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_ACL_TOPICS).unit("个").desc("ACL-Topic数").category(CATEGORY_SECURITY)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_ACL_TOPICS )));

        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_ACL_GROUPS).unit("个").desc("ACL-Group数").category(CATEGORY_SECURITY)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_ACL_GROUPS )));

        // 集群维度-Jobs相关指标
        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_JOB).unit("个").desc("集群任务总数").category(CATEGORY_JOB)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_JOBS )));

        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_JOB_RUNNING).unit("个").desc("集群running任务总数").category(CATEGORY_JOB)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_JOBS_RUNNING )));

        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_JOB_WAITING).unit("个").desc("集群waiting任务总数").category(CATEGORY_JOB)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_JOBS_WAITING )));

        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_JOB_SUCCESS).unit("个").desc("集群success任务总数").category(CATEGORY_JOB)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_JOBS_SUCCESS )));

        itemList.add( buildAllVersionsItem()
                .name(CLUSTER_METRIC_JOB_FAILED).unit("个").desc("集群failed任务总数").category(CATEGORY_JOB)
                .extend( buildMethodExtend( CLUSTER_METHOD_GET_JOBS_FAILED )));

        itemList.add(buildAllVersionsItem()
                .name(CLUSTER_METRIC_COLLECT_COST_TIME).unit("秒").desc("采集Cluster指标的耗时").category(CATEGORY_PERFORMANCE)
                .extendMethod(CLUSTER_METHOD_DO_NOTHING));
        return itemList;
    }
}
