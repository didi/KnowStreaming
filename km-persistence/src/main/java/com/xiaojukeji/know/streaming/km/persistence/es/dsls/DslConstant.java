package com.xiaojukeji.know.streaming.km.persistence.es.dsls;

/**
 * @author: D10865
 * @description:
 * @date: Create on 2019/2/28 上午10:23
 * @modified By D10865
 *
 * 查询语句文件名常量
 *
 * 命名规则 类名/方法名
 *
 * 在dslFiles目录下新建以类名为名称的文件夹，以方法名为名称的文件名
 *
 */
public class DslConstant {

    private DslConstant() {}

    /**************************************************** Base ****************************************************/
    public static final String GET_LATEST_METRIC_TIME                   = "BaseMetricESDAO/getLatestMetricTime";

    /**************************************************** Broker ****************************************************/
    public static final String GET_BROKER_AGG_SINGLE_METRICS            = "BrokerMetricESDAO/getAggSingleBrokerMetrics";

    public static final String GET_BROKER_AGG_LIST_METRICS              = "BrokerMetricESDAO/getAggListBrokerMetrics";

    public static final String GET_BROKER_AGG_TOP_METRICS               = "BrokerMetricESDAO/getAggTopMetricsBrokers";

    public static final String GET_BROKER_LATEST_METRICS                = "BrokerMetricESDAO/getBrokerLatestMetrics";

    /**************************************************** Topic ****************************************************/
    public static final String GET_TOPIC_AGG_LIST_METRICS               = "TopicMetricESDAO/getAggListMetrics";

    public static final String GET_TOPIC_AGG_SINGLE_METRICS             = "TopicMetricESDAO/getAggSingleMetrics";

    public static final String GET_TOPIC_MAX_OR_MIN_SINGLE_METRIC       = "TopicMetricESDAO/getMaxOrMinSingleMetric";

    public static final String GET_TOPIC_AGG_TOP_METRICS                = "TopicMetricESDAO/getAggTopMetricsTopics";

    public static final String GET_TOPIC_BROKER_LATEST_METRICS          = "TopicMetricESDAO/getTopicLatestMetricByBrokerId";

    public static final String GET_TOPIC_LATEST_METRICS                 = "TopicMetricESDAO/getTopicLatestMetric";

    public static final String LIST_TOPIC_WITH_LATEST_METRICS           = "TopicMetricESDAO/listTopicWithLatestMetrics";

    public static final String COUNT_TOPIC_METRIC_VALUE                 = "TopicMetricESDAO/countTopicMetricValue";

    public static final String COUNT_TOPIC_NOT_METRIC_VALUE             = "TopicMetricESDAO/countTopicNotMetricValue";

    /**************************************************** Cluster ****************************************************/
    public static final String GET_CLUSTER_AGG_LIST_METRICS             = "ClusterMetricESDAO/getAggListClusterMetrics";

    public static final String GET_CLUSTER_AGG_SINGLE_METRICS           = "ClusterMetricESDAO/getAggSingleClusterMetrics";

    public static final String LIST_CLUSTER_WITH_LATEST_METRICS         = "ClusterMetricESDAO/listClusterWithLatestMetrics";

    public static final String GET_CLUSTER_LATEST_METRICS               = "ClusterMetricESDAO/getClusterLatestMetrics";

    /**************************************************** Partition ****************************************************/
    public static final String GET_PARTITION_LATEST_METRICS             = "PartitionMetricESDAO/getPartitionLatestMetrics";

    public static final String LIST_PARTITION_LATEST_METRICS_BY_TOPIC   = "PartitionMetricESDAO/listPartitionLatestMetricsByTopic";

    /**************************************************** Group ****************************************************/
    public static final String GET_GROUP_TOPIC_PARTITION                = "GroupMetricESDAO/getTopicPartitionOfGroup";

    public static final String LIST_GROUP_METRICS                       = "GroupMetricESDAO/listGroupMetrics";

    public static final String LIST_GROUP_LATEST_METRICS_BY_GROUP_TOPIC = "GroupMetricESDAO/listLatestMetricsAggByGroupTopic";

    public static final String LIST_GROUP_LATEST_METRICS_OF_PARTITION   = "GroupMetricESDAO/listPartitionLatestMetrics";

    public static final String COUNT_GROUP_METRIC_VALUE                 = "GroupMetricESDAO/countGroupMetricValue";

    public static final String COUNT_GROUP_NOT_METRIC_VALUE             = "GroupMetricESDAO/countGroupNotMetricValue";

    /**************************************************** Zookeeper ****************************************************/
    public static final String GET_ZOOKEEPER_AGG_LIST_METRICS           = "ZookeeperMetricESDAO/getAggListZookeeperMetrics";

    /**************************************************** Connect-Cluster ****************************************************/
    public static final String GET_CONNECT_CLUSTER_AGG_LIST_METRICS     = "ConnectClusterMetricESDAO/getAggListConnectClusterMetrics";

    public static final String GET_CONNECT_CLUSTER_AGG_TOP_METRICS      = "ConnectClusterMetricESDAO/getAggTopMetricsConnectClusters";

    /**************************************************** Connect-Connector ****************************************************/
    public static final String GET_CONNECTOR_LATEST_METRICS             = "ConnectorMetricESDAO/getConnectorLatestMetric";

    public static final String GET_CONNECTOR_AGG_LIST_METRICS           = "ConnectorMetricESDAO/getConnectorAggListMetric";

    public static final String GET_CONNECTOR_AGG_TOP_METRICS            = "ConnectorMetricESDAO/getConnectorAggTopMetric";
}
