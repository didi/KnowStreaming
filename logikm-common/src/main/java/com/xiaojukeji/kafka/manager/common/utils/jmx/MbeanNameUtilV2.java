package com.xiaojukeji.kafka.manager.common.utils.jmx;

import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.KafkaVersion;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

import java.util.*;

/**
 * kafka集群的mbean的object name集合
 * @author zengqiao
 * @date 20/6/15
 */
public class MbeanNameUtilV2 {

    private static Map<Integer, List<MbeanV2>> FIELD_NAME_MBEAN_MAP = new HashMap<>();

    private static void initMbean(MbeanV2 mbeanV2, List<Integer> interfaceIdList) {
        for (Integer interfaceId: interfaceIdList) {
            List<MbeanV2> mbeanV2List = FIELD_NAME_MBEAN_MAP.getOrDefault(interfaceId, new ArrayList<>());
            mbeanV2List.add(mbeanV2);
            FIELD_NAME_MBEAN_MAP.put(interfaceId, mbeanV2List);
        }
    }

    static {
        //社区Kafka指标-------------------------------------------------------------------------------------------

        for (String fieldName: Arrays.asList("BytesInPerSec", "BytesOutPerSec")) {
            initMbean(
                    new MbeanV2(
                            fieldName,
                            JmxAttributeEnum.RATE_ATTRIBUTE,
                            "kafka.server:type=BrokerTopicMetrics,name=" + fieldName
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.COMMON_DETAIL_METRICS,
                            KafkaMetricsCollections.BROKER_OVERVIEW_PAGE_METRICS,
                            KafkaMetricsCollections.TOPIC_METRICS_TO_DB,
                            KafkaMetricsCollections.BROKER_TO_DB_METRICS,
                            KafkaMetricsCollections.BROKER_ANALYSIS_METRICS,
                            KafkaMetricsCollections.BROKER_TOPIC_ANALYSIS_METRICS
                    )
            );
        }

        for (String fieldName: Arrays.asList(
                "MessagesInPerSec",
                "BytesRejectedPerSec",
                "TotalProduceRequestsPerSec",
                "TotalFetchRequestsPerSec"
        )) {
            initMbean(
                    new MbeanV2(
                            fieldName,
                            JmxAttributeEnum.RATE_ATTRIBUTE,
                            "kafka.server:type=BrokerTopicMetrics,name=" + fieldName
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.COMMON_DETAIL_METRICS,
                            KafkaMetricsCollections.TOPIC_METRICS_TO_DB,
                            KafkaMetricsCollections.BROKER_TO_DB_METRICS,
                            KafkaMetricsCollections.BROKER_ANALYSIS_METRICS,
                            KafkaMetricsCollections.BROKER_TOPIC_ANALYSIS_METRICS
                    )
            );
        }

        for (String fieldName: Arrays.asList(
                "FailedFetchRequestsPerSec",
                "FailedProduceRequestsPerSec"
        )) {
            initMbean(
                    new MbeanV2(
                            fieldName,
                            JmxAttributeEnum.RATE_ATTRIBUTE,
                            "kafka.server:type=BrokerTopicMetrics,name=" + fieldName
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.COMMON_DETAIL_METRICS,
                            KafkaMetricsCollections.TOPIC_METRICS_TO_DB,
                            KafkaMetricsCollections.BROKER_TO_DB_METRICS,
                            KafkaMetricsCollections.BROKER_ANALYSIS_METRICS,
                            KafkaMetricsCollections.BROKER_TOPIC_ANALYSIS_METRICS,
                            KafkaMetricsCollections.BROKER_HEALTH_SCORE_METRICS

                    )
            );
        }

        for (String fieldName: Arrays.asList("Produce", "FetchConsumer")) {
            initMbean(
                    new MbeanV2(
                            fieldName + "RequestsPerSec",
                            JmxAttributeEnum.RATE_ATTRIBUTE,
                            "kafka.network:type=RequestMetrics,name=RequestsPerSec,request=" + fieldName
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.BROKER_TO_DB_METRICS
                    )
            );
        }

        initMbean(
                new MbeanV2(
                        "RequestHandlerAvgIdlePercent",
                        JmxAttributeEnum.RATE_ATTRIBUTE,
                        "kafka.server:type=KafkaRequestHandlerPool,name=RequestHandlerAvgIdlePercent"
                ),
                Arrays.asList(
                        KafkaMetricsCollections.BROKER_TO_DB_METRICS,
                        KafkaMetricsCollections.BROKER_HEALTH_SCORE_METRICS
                )
        );

        initMbean(
                new MbeanV2(
                        "NetworkProcessorAvgIdlePercent",
                        JmxAttributeEnum.VALUE_ATTRIBUTE,
                        "kafka.network:type=SocketServer,name=NetworkProcessorAvgIdlePercent"
                ),
                Arrays.asList(
                        KafkaMetricsCollections.BROKER_TO_DB_METRICS,
                        KafkaMetricsCollections.BROKER_HEALTH_SCORE_METRICS
                )
        );

        for (String fieldName: Arrays.asList("RequestQueueSize", "ResponseQueueSize")) {
            initMbean(
                    new MbeanV2(
                            fieldName,
                            JmxAttributeEnum.VALUE_ATTRIBUTE,
                            "kafka.network:type=RequestChannel,name=" + fieldName
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.BROKER_TO_DB_METRICS, KafkaMetricsCollections.BROKER_HEALTH_SCORE_METRICS
                    )
            );
        }

        for (String fieldName: Arrays.asList("Produce", "FetchConsumer")) {
            initMbean(
                    new MbeanV2(
                            fieldName + "TotalTimeMs",
                            JmxAttributeEnum.PERCENTILE_ATTRIBUTE,
                            "kafka.network:type=RequestMetrics,name=TotalTimeMs,request=" + fieldName
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.BROKER_TO_DB_METRICS
                    )
            );
        }

        for (String fieldName: Arrays.asList("PartitionCount", "LeaderCount", "UnderReplicatedPartitions")) {
            initMbean(
                    new MbeanV2(
                            fieldName,
                            JmxAttributeEnum.VALUE_ATTRIBUTE,
                            "kafka.server:type=ReplicaManager,name=" + fieldName
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.BROKER_BASIC_PAGE_METRICS,
                            KafkaMetricsCollections.BROKER_OVERVIEW_PAGE_METRICS,
                            KafkaMetricsCollections.BROKER_STATUS_PAGE_METRICS
                    )
            );
        }

        initMbean(
                new MbeanV2(
                        "TopicCodeC",
                        JmxAttributeEnum.VALUE_ATTRIBUTE,
                        Arrays.asList(
                                new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_0_10_3, "kafka.server:type=ReplicaManager,name=TopicCodeC"),
                                new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_MAX, "kafka.server:type=AppIdTopicMetrics,name=RecordCompression,appId=")
                        )
                ),
                Arrays.asList(
                        KafkaMetricsCollections.TOPIC_BASIC_PAGE_METRICS
                )
        );




        initMbean(
                new MbeanV2(
                        "LogFlushRateAndTimeMs",
                        JmxAttributeEnum.PERCENTILE_ATTRIBUTE,
                        "kafka.log:type=LogFlushStats,name=LogFlushRateAndTimeMs"
                ),
                Arrays.asList(
                        KafkaMetricsCollections.BROKER_TO_DB_METRICS
                )
        );

        initMbean(
                new MbeanV2(
                        "LogEndOffset",
                        JmxAttributeEnum.VALUE_ATTRIBUTE,
                        "kafka.log:type=Log,name=LogEndOffset"
                ),
                Arrays.asList(
                )
        );

        for (String fieldName: Arrays.asList("Produce")) {
            initMbean(new MbeanV2(
                            fieldName + "TotalTimeMs",
                            JmxAttributeEnum.PERCENTILE_ATTRIBUTE,
                            Arrays.asList(
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_0_10_3, "kafka.network:type=TopicRequestMetrics,name=TotalTimeMs,request=" + fieldName),
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_MAX, "kafka.server:type=TopicRequestMetrics,name=TotalTimeMs,request=" + fieldName)
                            )

                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.TOPIC_REQUEST_TIME_METRICS_TO_DB,
                            KafkaMetricsCollections.TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS
                    )
            );

            initMbean(
                    new MbeanV2(
                            fieldName + "RequestQueueTimeMs",
                            JmxAttributeEnum.PERCENTILE_ATTRIBUTE,
                            Arrays.asList(
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_0_10_3, "kafka.network:type=TopicRequestMetrics,name=RequestQueueTimeMs,request=" + fieldName),
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_MAX, "kafka.server:type=TopicRequestMetrics,name=RequestQueueTimeMs,request=" + fieldName)
                            )
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS
                    )
            );

            initMbean(
                    new MbeanV2(
                            fieldName + "LocalTimeMs",
                            JmxAttributeEnum.PERCENTILE_ATTRIBUTE,
                            Arrays.asList(
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_0_10_3, "kafka.network:type=TopicRequestMetrics,name=LocalTimeMs,request=" + fieldName),
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_MAX, "kafka.server:type=TopicRequestMetrics,name=LocalTimeMs,request=" + fieldName)
                            )
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS
                    )
            );

            initMbean(
                    new MbeanV2(
                            fieldName + "RemoteTimeMs",
                            JmxAttributeEnum.PERCENTILE_ATTRIBUTE,
                            Arrays.asList(
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_0_10_3, "kafka.network:type=TopicRequestMetrics,name=RemoteTimeMs,request=" + fieldName),
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_MAX, "kafka.server:type=TopicRequestMetrics,name=RemoteTimeMs,request=" + fieldName)
                            )
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS
                    )
            );

            initMbean(
                    new MbeanV2(
                            fieldName + "ThrottleTimeMs",
                            JmxAttributeEnum.PERCENTILE_ATTRIBUTE,
                            Arrays.asList(
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_0_10_3, "kafka.network:type=TopicRequestMetrics,name=ThrottleTimeMs,request=" + fieldName),
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_MAX, "kafka.server:type=TopicRequestMetrics,name=ThrottleTimeMs,request=" + fieldName)
                            )
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS
                    )
            );

            initMbean(
                    new MbeanV2(
                            fieldName + "ResponseQueueTimeMs",
                            JmxAttributeEnum.PERCENTILE_ATTRIBUTE,
                            Arrays.asList(
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_0_10_3, "kafka.network:type=TopicRequestMetrics,name=ResponseQueueTimeMs,request=" + fieldName),
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_MAX, "kafka.server:type=TopicRequestMetrics,name=ResponseQueueTimeMs,request=" + fieldName)
                            )
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS
                    )
            );

            initMbean(
                    new MbeanV2(
                            fieldName + "ResponseSendTimeMs",
                            JmxAttributeEnum.PERCENTILE_ATTRIBUTE,
                            Arrays.asList(
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_0_10_3, "kafka.network:type=TopicRequestMetrics,name=ResponseSendTimeMs,request=" + fieldName),
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_MAX, "kafka.server:type=TopicRequestMetrics,name=ResponseSendTimeMs,request=" + fieldName)
                            )
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS
                    )
            );
        }

        for (String fieldName: Arrays.asList("Fetch")) {
            initMbean(new MbeanV2(
                    fieldName + "ConsumerTotalTimeMs",
                            JmxAttributeEnum.PERCENTILE_ATTRIBUTE,
                            Arrays.asList(
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_0_10_3, "kafka.network:type=TopicRequestMetrics,name=TotalTimeMs,request=" + fieldName + "Consumer"),
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_MAX, "kafka.server:type=TopicRequestMetrics,name=TotalTimeMs,request=" + fieldName)
                            )

                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.TOPIC_REQUEST_TIME_METRICS_TO_DB,
                            KafkaMetricsCollections.TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS
                    )
            );

            initMbean(
                    new MbeanV2(
                            fieldName + "ConsumerRequestQueueTimeMs",
                            JmxAttributeEnum.PERCENTILE_ATTRIBUTE,
                            Arrays.asList(
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_0_10_3, "kafka.network:type=TopicRequestMetrics,name=RequestQueueTimeMs,request=" + fieldName + "Consumer"),
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_MAX, "kafka.server:type=TopicRequestMetrics,name=RequestQueueTimeMs,request=" + fieldName)
                            )
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS
                    )
            );

            initMbean(
                    new MbeanV2(
                            fieldName + "ConsumerLocalTimeMs",
                            JmxAttributeEnum.PERCENTILE_ATTRIBUTE,
                            Arrays.asList(
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_0_10_3, "kafka.network:type=TopicRequestMetrics,name=LocalTimeMs,request=" + fieldName + "Consumer"),
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_MAX, "kafka.server:type=TopicRequestMetrics,name=LocalTimeMs,request=" + fieldName)
                            )
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS
                    )
            );

            initMbean(
                    new MbeanV2(
                            fieldName + "ConsumerRemoteTimeMs",
                            JmxAttributeEnum.PERCENTILE_ATTRIBUTE,
                            Arrays.asList(
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_0_10_3, "kafka.network:type=TopicRequestMetrics,name=RemoteTimeMs,request=" + fieldName + "Consumer"),
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_MAX, "kafka.server:type=TopicRequestMetrics,name=RemoteTimeMs,request=" + fieldName)
                            )
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS
                    )
            );

            initMbean(
                    new MbeanV2(
                            fieldName + "ConsumerThrottleTimeMs",
                            JmxAttributeEnum.PERCENTILE_ATTRIBUTE,
                            Arrays.asList(
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_0_10_3, "kafka.network:type=TopicRequestMetrics,name=ThrottleTimeMs,request=" + fieldName + "Consumer"),
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_MAX, "kafka.server:type=TopicRequestMetrics,name=ThrottleTimeMs,request=" + fieldName)
                            )
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS
                    )
            );

            initMbean(
                    new MbeanV2(
                            fieldName + "ConsumerResponseQueueTimeMs",
                            JmxAttributeEnum.PERCENTILE_ATTRIBUTE,
                            Arrays.asList(
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_0_10_3, "kafka.network:type=TopicRequestMetrics,name=ResponseQueueTimeMs,request=" + fieldName + "Consumer"),
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_MAX, "kafka.server:type=TopicRequestMetrics,name=ResponseQueueTimeMs,request=" + fieldName)
                            )
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS
                    )
            );

            initMbean(
                    new MbeanV2(
                            fieldName + "ConsumerResponseSendTimeMs",
                            JmxAttributeEnum.PERCENTILE_ATTRIBUTE,
                            Arrays.asList(
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_0_10_3, "kafka.network:type=TopicRequestMetrics,name=ResponseSendTimeMs,request=" + fieldName + "Consumer"),
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_MAX, "kafka.server:type=TopicRequestMetrics,name=ResponseSendTimeMs,request=" + fieldName)
                            )
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS
                    )
            );
        }

        initMbean(
                new MbeanV2(
                        "KafkaVersion",
                        JmxAttributeEnum.VERSION_ATTRIBUTE,
                        "kafka.server:type=app-info"
                ),
                Arrays.asList(
                        KafkaMetricsCollections.BROKER_VERSION
                )
        );


        //滴滴Kafka指标-------------------------------------------------------------------------------------------
        for (String fieldName: Arrays.asList("AppIdBytesInPerSec", "AppIdBytesOutPerSec", "AppIdMessagesInPerSec")) {
            initMbean(
                    new MbeanV2(
                            "Topic" + fieldName,
                            JmxAttributeEnum.RATE_ATTRIBUTE,
                            Arrays.asList(
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_0_10_3, "kafka.server:type=AppIdTopicMetrics,name=" + fieldName + ",topic=*,appId=*"),
                                    new AbstractMap.SimpleEntry<>(KafkaVersion.VERSION_MAX, "kafka.server:type=AppIdTopicMetrics,name=" + fieldName.replace("AppId", "") + ",topic=*,appId=*")
                            )
                    ),
                    Arrays.asList(
                            KafkaMetricsCollections.APP_TOPIC_METRICS_TO_DB
                    )
            );
        }
    };

    /**
     * 根据属性名，kafka版本，topic获取相应的Mbean
     */
    public static List<MbeanV2> getMbeanList(Integer interfaceId) {
        if (ValidateUtils.isNull(interfaceId)) {
            return new ArrayList<>();
        }
        return FIELD_NAME_MBEAN_MAP.getOrDefault(interfaceId, new ArrayList<>());
    }
}