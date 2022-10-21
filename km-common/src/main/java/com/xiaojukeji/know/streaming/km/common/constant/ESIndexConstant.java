package com.xiaojukeji.know.streaming.km.common.constant;

public class ESIndexConstant {

    public final static String TOPIC_INDEX = "ks_kafka_topic_metric";
    public final static String TOPIC_TEMPLATE = "{\n" +
            "    \"order\" : 10,\n" +
            "    \"index_patterns\" : [\n" +
            "      \"ks_kafka_topic_metric*\"\n" +
            "    ],\n" +
            "    \"settings\" : {\n" +
            "      \"index\" : {\n" +
            "        \"number_of_shards\" : \"10\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"mappings\" : {\n" +
            "      \"properties\" : {\n" +
            "        \"brokerId\" : {\n" +
            "          \"type\" : \"long\"\n" +
            "        },\n" +
            "        \"routingValue\" : {\n" +
            "          \"type\" : \"text\",\n" +
            "          \"fields\" : {\n" +
            "            \"keyword\" : {\n" +
            "              \"ignore_above\" : 256,\n" +
            "              \"type\" : \"keyword\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"topic\" : {\n" +
            "          \"type\" : \"keyword\"\n" +
            "        },\n" +
            "        \"clusterPhyId\" : {\n" +
            "          \"type\" : \"long\"\n" +
            "        },\n" +
            "        \"metrics\" : {\n" +
            "          \"properties\" : {\n" +
            "            \"BytesIn_min_15\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"Messages\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"BytesRejected\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"PartitionURP\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"HealthCheckTotal\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"ReplicationCount\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"ReplicationBytesOut\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"ReplicationBytesIn\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"FailedFetchRequests\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"BytesIn_min_5\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"HealthScore\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"LogSize\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"BytesOut\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"BytesOut_min_15\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"FailedProduceRequests\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"BytesIn\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"BytesOut_min_5\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"MessagesIn\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"TotalProduceRequests\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"HealthCheckPassed\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"brokerAgg\" : {\n" +
            "          \"type\" : \"keyword\"\n" +
            "        },\n" +
            "        \"key\" : {\n" +
            "          \"type\" : \"text\",\n" +
            "          \"fields\" : {\n" +
            "            \"keyword\" : {\n" +
            "              \"ignore_above\" : 256,\n" +
            "              \"type\" : \"keyword\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"timestamp\" : {\n" +
            "          \"format\" : \"yyyy-MM-dd HH:mm:ss Z||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS Z||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss,SSS||yyyy/MM/dd HH:mm:ss||yyyy-MM-dd HH:mm:ss,SSS Z||yyyy/MM/dd HH:mm:ss,SSS Z||epoch_millis\",\n" +
            "          \"index\" : true,\n" +
            "          \"type\" : \"date\",\n" +
            "          \"doc_values\" : true\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"aliases\" : { }\n" +
            "  }";

    public final static String CLUSTER_INDEX = "ks_kafka_cluster_metric";
    public final static String CLUSTER_TEMPLATE = "{\n" +
            "    \"order\" : 10,\n" +
            "    \"index_patterns\" : [\n" +
            "      \"ks_kafka_cluster_metric*\"\n" +
            "    ],\n" +
            "    \"settings\" : {\n" +
            "      \"index\" : {\n" +
            "        \"number_of_shards\" : \"10\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"mappings\" : {\n" +
            "      \"properties\" : {\n" +
            "        \"routingValue\" : {\n" +
            "          \"type\" : \"text\",\n" +
            "          \"fields\" : {\n" +
            "            \"keyword\" : {\n" +
            "              \"ignore_above\" : 256,\n" +
            "              \"type\" : \"keyword\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"clusterPhyId\" : {\n" +
            "          \"type\" : \"long\"\n" +
            "        },\n" +
            "        \"metrics\" : {\n" +
            "          \"properties\" : {\n" +
            "            \"Connections\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"BytesIn_min_15\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"PartitionURP\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"HealthScore_Topics\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"EventQueueSize\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"ActiveControllerCount\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"GroupDeads\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"BytesIn_min_5\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"HealthCheckTotal_Topics\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"Partitions\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"BytesOut\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"Groups\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"BytesOut_min_15\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"TotalRequestQueueSize\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"HealthCheckPassed_Groups\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"TotalProduceRequests\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"HealthCheckPassed\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"TotalLogSize\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"GroupEmptys\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"PartitionNoLeader\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"HealthScore_Brokers\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"Messages\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"Topics\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"PartitionMinISR_E\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"HealthCheckTotal\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"Brokers\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"Replicas\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"HealthCheckTotal_Groups\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"GroupRebalances\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"MessageIn\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"HealthScore\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"HealthCheckPassed_Topics\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"HealthCheckTotal_Brokers\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"PartitionMinISR_S\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"BytesIn\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"BytesOut_min_5\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"GroupActives\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"MessagesIn\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"GroupReBalances\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"HealthCheckPassed_Brokers\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"HealthScore_Groups\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"TotalResponseQueueSize\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"Zookeepers\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"LeaderMessages\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"HealthScore_Cluster\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"HealthCheckPassed_Cluster\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"HealthCheckTotal_Cluster\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"key\" : {\n" +
            "          \"type\" : \"text\",\n" +
            "          \"fields\" : {\n" +
            "            \"keyword\" : {\n" +
            "              \"ignore_above\" : 256,\n" +
            "              \"type\" : \"keyword\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"timestamp\" : {\n" +
            "          \"format\" : \"yyyy-MM-dd HH:mm:ss Z||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS Z||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss,SSS||yyyy/MM/dd HH:mm:ss||yyyy-MM-dd HH:mm:ss,SSS Z||yyyy/MM/dd HH:mm:ss,SSS Z||epoch_millis\",\n" +
            "          \"type\" : \"date\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"aliases\" : { }\n" +
            "   }";

    public final static String BROKER_INDEX = "ks_kafka_broker_metric";
    public final static String BROKER_TEMPLATE = "{\n" +
            "    \"order\" : 10,\n" +
            "    \"index_patterns\" : [\n" +
            "      \"ks_kafka_broker_metric*\"\n" +
            "    ],\n" +
            "    \"settings\" : {\n" +
            "      \"index\" : {\n" +
            "        \"number_of_shards\" : \"10\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"mappings\" : {\n" +
            "      \"properties\" : {\n" +
            "        \"brokerId\" : {\n" +
            "          \"type\" : \"long\"\n" +
            "        },\n" +
            "        \"routingValue\" : {\n" +
            "          \"type\" : \"text\",\n" +
            "          \"fields\" : {\n" +
            "            \"keyword\" : {\n" +
            "              \"ignore_above\" : 256,\n" +
            "              \"type\" : \"keyword\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"clusterPhyId\" : {\n" +
            "          \"type\" : \"long\"\n" +
            "        },\n" +
            "        \"metrics\" : {\n" +
            "          \"properties\" : {\n" +
            "            \"NetworkProcessorAvgIdle\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"UnderReplicatedPartitions\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"BytesIn_min_15\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"HealthCheckTotal\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"RequestHandlerAvgIdle\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"connectionsCount\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"BytesIn_min_5\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"HealthScore\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"BytesOut\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"BytesOut_min_15\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"BytesIn\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"BytesOut_min_5\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"TotalRequestQueueSize\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"MessagesIn\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"TotalProduceRequests\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"HealthCheckPassed\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"TotalResponseQueueSize\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"key\" : {\n" +
            "          \"type\" : \"text\",\n" +
            "          \"fields\" : {\n" +
            "            \"keyword\" : {\n" +
            "              \"ignore_above\" : 256,\n" +
            "              \"type\" : \"keyword\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"timestamp\" : {\n" +
            "          \"format\" : \"yyyy-MM-dd HH:mm:ss Z||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS Z||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss,SSS||yyyy/MM/dd HH:mm:ss||yyyy-MM-dd HH:mm:ss,SSS Z||yyyy/MM/dd HH:mm:ss,SSS Z||epoch_millis\",\n" +
            "          \"index\" : true,\n" +
            "          \"type\" : \"date\",\n" +
            "          \"doc_values\" : true\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"aliases\" : { }\n" +
            "  }";

    public final static String PARTITION_INDEX = "ks_kafka_partition_metric";
    public final static String PARTITION_TEMPLATE = "{\n" +
            "    \"order\" : 10,\n" +
            "    \"index_patterns\" : [\n" +
            "      \"ks_kafka_partition_metric*\"\n" +
            "    ],\n" +
            "    \"settings\" : {\n" +
            "      \"index\" : {\n" +
            "        \"number_of_shards\" : \"10\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"mappings\" : {\n" +
            "      \"properties\" : {\n" +
            "        \"brokerId\" : {\n" +
            "          \"type\" : \"long\"\n" +
            "        },\n" +
            "        \"partitionId\" : {\n" +
            "          \"type\" : \"long\"\n" +
            "        },\n" +
            "        \"routingValue\" : {\n" +
            "          \"type\" : \"text\",\n" +
            "          \"fields\" : {\n" +
            "            \"keyword\" : {\n" +
            "              \"ignore_above\" : 256,\n" +
            "              \"type\" : \"keyword\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"clusterPhyId\" : {\n" +
            "          \"type\" : \"long\"\n" +
            "        },\n" +
            "        \"topic\" : {\n" +
            "          \"type\" : \"keyword\"\n" +
            "        },\n" +
            "        \"metrics\" : {\n" +
            "          \"properties\" : {\n" +
            "            \"LogStartOffset\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"Messages\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"LogEndOffset\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"key\" : {\n" +
            "          \"type\" : \"text\",\n" +
            "          \"fields\" : {\n" +
            "            \"keyword\" : {\n" +
            "              \"ignore_above\" : 256,\n" +
            "              \"type\" : \"keyword\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"timestamp\" : {\n" +
            "          \"format\" : \"yyyy-MM-dd HH:mm:ss Z||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS Z||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss,SSS||yyyy/MM/dd HH:mm:ss||yyyy-MM-dd HH:mm:ss,SSS Z||yyyy/MM/dd HH:mm:ss,SSS Z||epoch_millis\",\n" +
            "          \"index\" : true,\n" +
            "          \"type\" : \"date\",\n" +
            "          \"doc_values\" : true\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"aliases\" : { }\n" +
            "  }";

    public final static String GROUP_INDEX = "ks_kafka_group_metric";
    public final static String GROUP_TEMPLATE = "{\n" +
            "    \"order\" : 10,\n" +
            "    \"index_patterns\" : [\n" +
            "      \"ks_kafka_group_metric*\"\n" +
            "    ],\n" +
            "    \"settings\" : {\n" +
            "      \"index\" : {\n" +
            "        \"number_of_shards\" : \"10\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"mappings\" : {\n" +
            "      \"properties\" : {\n" +
            "        \"group\" : {\n" +
            "          \"type\" : \"keyword\"\n" +
            "        },\n" +
            "        \"partitionId\" : {\n" +
            "          \"type\" : \"long\"\n" +
            "        },\n" +
            "        \"routingValue\" : {\n" +
            "          \"type\" : \"text\",\n" +
            "          \"fields\" : {\n" +
            "            \"keyword\" : {\n" +
            "              \"ignore_above\" : 256,\n" +
            "              \"type\" : \"keyword\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"clusterPhyId\" : {\n" +
            "          \"type\" : \"long\"\n" +
            "        },\n" +
            "        \"topic\" : {\n" +
            "          \"type\" : \"keyword\"\n" +
            "        },\n" +
            "        \"metrics\" : {\n" +
            "          \"properties\" : {\n" +
            "            \"HealthScore\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"Lag\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"OffsetConsumed\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"HealthCheckTotal\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"HealthCheckPassed\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"groupMetric\" : {\n" +
            "          \"type\" : \"keyword\"\n" +
            "        },\n" +
            "        \"key\" : {\n" +
            "          \"type\" : \"text\",\n" +
            "          \"fields\" : {\n" +
            "            \"keyword\" : {\n" +
            "              \"ignore_above\" : 256,\n" +
            "              \"type\" : \"keyword\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"timestamp\" : {\n" +
            "          \"format\" : \"yyyy-MM-dd HH:mm:ss Z||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS Z||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss,SSS||yyyy/MM/dd HH:mm:ss||yyyy-MM-dd HH:mm:ss,SSS Z||yyyy/MM/dd HH:mm:ss,SSS Z||epoch_millis\",\n" +
            "          \"index\" : true,\n" +
            "          \"type\" : \"date\",\n" +
            "          \"doc_values\" : true\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"aliases\" : { }\n" +
            "  }";

    public final static String REPLICATION_INDEX = "ks_kafka_replication_metric";
    public final static String REPLICATION_TEMPLATE = "{\n" +
            "    \"order\" : 10,\n" +
            "    \"index_patterns\" : [\n" +
            "      \"ks_kafka_replication_metric*\"\n" +
            "    ],\n" +
            "    \"settings\" : {\n" +
            "      \"index\" : {\n" +
            "        \"number_of_shards\" : \"10\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"mappings\" : {\n" +
            "      \"properties\" : {\n" +
            "        \"brokerId\" : {\n" +
            "          \"type\" : \"long\"\n" +
            "        },\n" +
            "        \"partitionId\" : {\n" +
            "          \"type\" : \"long\"\n" +
            "        },\n" +
            "        \"routingValue\" : {\n" +
            "          \"type\" : \"text\",\n" +
            "          \"fields\" : {\n" +
            "            \"keyword\" : {\n" +
            "              \"ignore_above\" : 256,\n" +
            "              \"type\" : \"keyword\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"clusterPhyId\" : {\n" +
            "          \"type\" : \"long\"\n" +
            "        },\n" +
            "        \"topic\" : {\n" +
            "          \"type\" : \"keyword\"\n" +
            "        },\n" +
            "        \"metrics\" : {\n" +
            "          \"properties\" : {\n" +
            "            \"LogStartOffset\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"Messages\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            },\n" +
            "            \"LogEndOffset\" : {\n" +
            "              \"type\" : \"float\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"key\" : {\n" +
            "          \"type\" : \"text\",\n" +
            "          \"fields\" : {\n" +
            "            \"keyword\" : {\n" +
            "              \"ignore_above\" : 256,\n" +
            "              \"type\" : \"keyword\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"timestamp\" : {\n" +
            "          \"format\" : \"yyyy-MM-dd HH:mm:ss Z||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS Z||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss,SSS||yyyy/MM/dd HH:mm:ss||yyyy-MM-dd HH:mm:ss,SSS Z||yyyy/MM/dd HH:mm:ss,SSS Z||epoch_millis\",\n" +
            "          \"index\" : true,\n" +
            "          \"type\" : \"date\",\n" +
            "          \"doc_values\" : true\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"aliases\" : { }\n" +
            "  }";

    public final static String ZOOKEEPER_INDEX = "ks_kafka_zookeeper_metric";
    public final static String ZOOKEEPER_TEMPLATE = "{\n" +
            "    \"order\" : 10,\n" +
            "    \"index_patterns\" : [\n" +
            "      \"ks_kafka_zookeeper_metric*\"\n" +
            "    ],\n" +
            "    \"settings\" : {\n" +
            "      \"index\" : {\n" +
            "        \"number_of_shards\" : \"10\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"mappings\" : {\n" +
            "      \"properties\" : {\n" +
            "        \"routingValue\" : {\n" +
            "          \"type\" : \"text\",\n" +
            "          \"fields\" : {\n" +
            "            \"keyword\" : {\n" +
            "              \"ignore_above\" : 256,\n" +
            "              \"type\" : \"keyword\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"clusterPhyId\" : {\n" +
            "          \"type\" : \"long\"\n" +
            "        },\n" +
            "        \"metrics\" : {\n" +
            "          \"properties\" : {\n" +
            "            \"AvgRequestLatency\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"MinRequestLatency\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"MaxRequestLatency\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"OutstandingRequests\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"NodeCount\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"WatchCount\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"NumAliveConnections\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"PacketsReceived\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"PacketsSent\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"EphemeralsCount\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"ApproximateDataSize\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"OpenFileDescriptorCount\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            },\n" +
            "            \"MaxFileDescriptorCount\" : {\n" +
            "              \"type\" : \"double\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"key\" : {\n" +
            "          \"type\" : \"text\",\n" +
            "          \"fields\" : {\n" +
            "            \"keyword\" : {\n" +
            "              \"ignore_above\" : 256,\n" +
            "              \"type\" : \"keyword\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"timestamp\" : {\n" +
            "          \"format\" : \"yyyy-MM-dd HH:mm:ss Z||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS Z||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss,SSS||yyyy/MM/dd HH:mm:ss||yyyy-MM-dd HH:mm:ss,SSS Z||yyyy/MM/dd HH:mm:ss,SSS Z||epoch_millis\",\n" +
            "          \"type\" : \"date\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"aliases\" : { }\n" +
            "  }";
}
