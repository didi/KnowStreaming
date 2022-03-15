package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaClientEnum;
import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionAttributeDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.PartitionState;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

/**
 * @author xuguang
 * @Date 2021/12/14
 */
public class JmxServiceTest extends BaseTest {
    /**
     * 集群共包括三个broker:1,2,3, 该topic 1分区 1副本因子，在broker1上
     */
    @Value("${test.topic.name1}")
    private String REAL_TOPIC1_IN_ZK;

    /**
     * 集群共包括三个broker:1,2,3, 该topic 2分区 3副本因子，在broker1,2,3上
     */
    @Value("${test.topic.name2}")
    private String REAL_TOPIC2_IN_ZK;

    private final static String INVALID_TOPIC = "xxxxx";

    private final static String ZK_DEFAULT_TOPIC = "_consumer_offsets";

    private final static String NO_OFFSET_CHANGE_TOPIC_IN_ZK = "NoOffsetChangeTopic";

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    @Value("${test.broker.id1}")
    private Integer REAL_BROKER_ID_IN_ZK;

    private final static Integer INVALID_BROKER_ID = -1;

    private final static Long INVALID_CLUSTER_ID = -1L;

    private final static Integer INVALID_PARTITION_ID = -1;

    @Value("${test.client-id}")
    private String CLIENT_ID;

    private final static Integer INVALID_METRICS_CODE = -1;

    @Autowired
    private JmxService jmxService;

    private PartitionState getPartitionState() {
        PartitionState partitionState = new PartitionState();
        partitionState.setPartitionId(0);
        partitionState.setLeader(2);
        return partitionState;
    }

    @Test
    public void getBrokerMetricsTest() {
        // 结果为空
        getBrokerMetrics2NullTest();
        // mbeanV2ListEmpty
        getBrokerMetrics2mbeanV2ListEmptyTest();
        // 获取成功
        getBrokerMetrics2SuccessTest();
    }

    private void getBrokerMetrics2NullTest() {
        BrokerMetrics brokerMetrics1 = jmxService.getBrokerMetrics(null, null, null);
        Assert.assertNull(brokerMetrics1);

        BrokerMetrics brokerMetrics2 = jmxService.getBrokerMetrics(
                REAL_CLUSTER_ID_IN_MYSQL,
                INVALID_BROKER_ID,
                KafkaMetricsCollections.BROKER_ANALYSIS_METRICS);
        Assert.assertNull(brokerMetrics2);
    }

    private void getBrokerMetrics2mbeanV2ListEmptyTest() {
        BrokerMetrics brokerMetrics2 = jmxService.getBrokerMetrics(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_BROKER_ID_IN_ZK,
                -1);
        Assert.assertNotNull(brokerMetrics2);
        Assert.assertEquals(brokerMetrics2.getClusterId(), REAL_CLUSTER_ID_IN_MYSQL);
        Assert.assertEquals(brokerMetrics2.getBrokerId(), REAL_BROKER_ID_IN_ZK);
        Assert.assertTrue(brokerMetrics2.getMetricsMap().isEmpty());
    }
    
    private void getBrokerMetrics2SuccessTest() {
        BrokerMetrics brokerMetrics2 = jmxService.getBrokerMetrics(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_BROKER_ID_IN_ZK,
                KafkaMetricsCollections.BROKER_ANALYSIS_METRICS);
        Assert.assertNotNull(brokerMetrics2);
        Assert.assertEquals(brokerMetrics2.getClusterId(), REAL_CLUSTER_ID_IN_MYSQL);
        Assert.assertEquals(brokerMetrics2.getBrokerId(), REAL_BROKER_ID_IN_ZK);
        Assert.assertFalse(brokerMetrics2.getMetricsMap().isEmpty());
    }

    @Test
    public void getTopicMetricsWithBrokerIdTest() {
        // 结果为空
        getTopicMetricsWithBrokerId2nullTest();
        // 获取的metrics为空
        getTopicMetricsWithBrokerId2MetricsIsNullTest();
        // 获取指标成功
        getTopicMetricsWithBrokerId2SuccessTest();
    }

    private void getTopicMetricsWithBrokerId2nullTest() {
        TopicMetrics topicMetrics1 = jmxService.getTopicMetrics(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_BROKER_ID_IN_ZK,
                REAL_TOPIC1_IN_ZK,
                -1, true);
        Assert.assertNull(topicMetrics1);

        TopicMetrics topicMetrics2 = jmxService.getTopicMetrics(
                REAL_CLUSTER_ID_IN_MYSQL,
                INVALID_BROKER_ID,
                REAL_TOPIC1_IN_ZK,
                KafkaMetricsCollections.BROKER_ANALYSIS_METRICS
                , true);
        Assert.assertNull(topicMetrics2);
    }

    private void getTopicMetricsWithBrokerId2MetricsIsNullTest() {
        // brokerId为3，不在该topic下
        TopicMetrics topicMetrics2 = jmxService.getTopicMetrics(
                REAL_CLUSTER_ID_IN_MYSQL,
                3,
                REAL_TOPIC1_IN_ZK,
                KafkaMetricsCollections.BROKER_ANALYSIS_METRICS
                , true);
        Assert.assertNotNull(topicMetrics2);
        Assert.assertEquals(topicMetrics2.getClusterId(), REAL_CLUSTER_ID_IN_MYSQL);
        Assert.assertEquals(topicMetrics2.getTopicName(), REAL_TOPIC1_IN_ZK);
        Assert.assertTrue(topicMetrics2.getMetricsMap().isEmpty());
    }

    private void getTopicMetricsWithBrokerId2SuccessTest() {
        TopicMetrics topicMetrics2 = jmxService.getTopicMetrics(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_BROKER_ID_IN_ZK,
                REAL_TOPIC1_IN_ZK,
                KafkaMetricsCollections.TOPIC_REQUEST_TIME_METRICS_TO_DB
                , true);
        Assert.assertNotNull(topicMetrics2);
        Assert.assertEquals(topicMetrics2.getClusterId(), REAL_CLUSTER_ID_IN_MYSQL);
        Assert.assertEquals(topicMetrics2.getTopicName(), REAL_TOPIC1_IN_ZK);
        Assert.assertFalse(topicMetrics2.getMetricsMap().isEmpty());
    }

    @Test
    public void getTopicMetricsWithoutBrokerId() {
        // 返回为空
        getTopicMetricsWithoutBrokerId2Null();
        // add
        getTopicMetricsWithoutBrokerId2Add();
        // max
        getTopicMetricsWithoutBrokerId2Max();
    }

    private void getTopicMetricsWithoutBrokerId2Null() {
        TopicMetrics topicMetrics = jmxService.getTopicMetrics(
                REAL_CLUSTER_ID_IN_MYSQL,
                INVALID_TOPIC,
                KafkaMetricsCollections.TOPIC_METRICS_TO_DB
                , true);
        Assert.assertNull(topicMetrics);
    }

    private void getTopicMetricsWithoutBrokerId2Add() {
        TopicMetrics topicMetrics = jmxService.getTopicMetrics(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_TOPIC1_IN_ZK,
                KafkaMetricsCollections.TOPIC_REQUEST_TIME_METRICS_TO_DB
                , true);
        Assert.assertNotNull(topicMetrics);
        Assert.assertNotNull(topicMetrics.getBrokerMetricsList());
        Assert.assertNotNull(topicMetrics.getMetricsMap());
    }

    private void getTopicMetricsWithoutBrokerId2Max() {
        TopicMetrics topicMetrics = jmxService.getTopicMetrics(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_TOPIC2_IN_ZK,
                KafkaMetricsCollections.TOPIC_REQUEST_TIME_METRICS_TO_DB
                , false);
        Assert.assertNotNull(topicMetrics);
        Assert.assertNotNull(topicMetrics.getBrokerMetricsList());
        Assert.assertNotNull(topicMetrics.getMetricsMap());
    }

    @Test(description = "测试获取集群下所有topic指标")
    public void getTopicMetricsList() {
        List<TopicMetrics> topicMetrics = jmxService.getTopicMetrics(
                REAL_CLUSTER_ID_IN_MYSQL,
                KafkaMetricsCollections.TOPIC_REQUEST_TIME_METRICS_TO_DB
                , false);
        Assert.assertFalse(topicMetrics.isEmpty());
        Assert.assertTrue(topicMetrics.stream().allMatch(topicMetric ->
                topicMetric.getClusterId().equals(REAL_CLUSTER_ID_IN_MYSQL)));
    }

    @Test(description = "测试获取broker版本")
    public void getBrokerVersion() {
        // 结果为空
        getBrokerVersion2Empty();
        // 结果不为空
        getBrokerVersion2NotEmpty();
    }

    private void getBrokerVersion2Empty() {
        String brokerVersion = jmxService.getBrokerVersion(
                REAL_CLUSTER_ID_IN_MYSQL,
                INVALID_BROKER_ID);
        Assert.assertEquals(brokerVersion, "");
    }

    private void getBrokerVersion2NotEmpty() {
        String brokerVersion = jmxService.getBrokerVersion(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_BROKER_ID_IN_ZK);
        Assert.assertNotEquals(brokerVersion, "");
    }

    @Test(description = "获取客户端限流信息")
    public void getTopicAppThrottleTest() {
        // 结果为0
        getTopicAppThrottle2ZeroTest();
        // 结果不为0
//        getTopicAppThrottle2NotZeroTest();
    }

    private void getTopicAppThrottle2ZeroTest() {
        double topicAppThrottle = jmxService.getTopicAppThrottle(
                REAL_CLUSTER_ID_IN_MYSQL,
                INVALID_BROKER_ID,
                "1",
                KafkaClientEnum.FETCH_CLIENT);
        Assert.assertEquals(topicAppThrottle, 0.0d);
    }

    private void getTopicAppThrottle2NotZeroTest() {
        double topicAppThrottle = jmxService.getTopicAppThrottle(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_BROKER_ID_IN_ZK,
                CLIENT_ID,
                KafkaClientEnum.FETCH_CLIENT);
        // 未设置限流，所以还是为0
        Assert.assertEquals(topicAppThrottle, 0.0d);
    }

    @Test(description = "获取被限流信息")
    public void getBrokerThrottleClientsTest() {
        // 结果为空
        getBrokerThrottleClients2EmptyTest();
        // 构造限流client，返回结果不为空
        // 需要流量达到限制值，比较难构造
//        getBrokerThrottleClients2NotEmptyTest();
    }

    private void getBrokerThrottleClients2EmptyTest() {
        Set<String> brokerThrottleClients = jmxService.getBrokerThrottleClients(
                REAL_CLUSTER_ID_IN_MYSQL,
                INVALID_BROKER_ID,
                KafkaClientEnum.FETCH_CLIENT);
        Assert.assertTrue(brokerThrottleClients.isEmpty());
    }

    private void getBrokerThrottleClients2NotEmptyTest() {
        Set<String> brokerThrottleClients = jmxService.getBrokerThrottleClients(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_BROKER_ID_IN_ZK,
                KafkaClientEnum.FETCH_CLIENT);
        Assert.assertFalse(brokerThrottleClients.isEmpty());
    }

    @Test(description = "测试获取topic消息压缩指标")
    public void getTopicCodeCValueTest() {
        // 结果为null
        getTopicCodeCValue2NullTest();
        // 结果不为null
        getTopicCodeCValue2SuccessTest();
    }

    private void getTopicCodeCValue2NullTest() {
        String result = jmxService.getTopicCodeCValue(REAL_CLUSTER_ID_IN_MYSQL, INVALID_TOPIC);
        Assert.assertNull(result);
    }

    private void getTopicCodeCValue2SuccessTest() {
        String result = jmxService.getTopicCodeCValue(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_TOPIC2_IN_ZK);
        Assert.assertNotNull(result);
    }

    @Test(description = "测试从JMX中获取appId维度的的流量信息")
    public void getTopicAppMetricsTest() {
        // result is empty
        getTopicAppMetrics2Empty();
        // result is not empty
        getTopicAppMetrics2NotEmpty();
    }

    private void getTopicAppMetrics2Empty() {
        List<TopicMetrics> topicAppMetrics = jmxService.getTopicAppMetrics(
                REAL_CLUSTER_ID_IN_MYSQL,
                INVALID_METRICS_CODE);
        Assert.assertTrue(topicAppMetrics.isEmpty());

        List<TopicMetrics> topicAppMetrics2 = jmxService.getTopicAppMetrics(
                INVALID_CLUSTER_ID,
                KafkaMetricsCollections.APP_TOPIC_METRICS_TO_DB);
        Assert.assertTrue(topicAppMetrics2.isEmpty());
    }

    private void getTopicAppMetrics2NotEmpty() {
        List<TopicMetrics> topicAppMetrics = jmxService.getTopicAppMetrics(
                REAL_CLUSTER_ID_IN_MYSQL,
                KafkaMetricsCollections.APP_TOPIC_METRICS_TO_DB
        );
        Assert.assertFalse(topicAppMetrics.isEmpty());
    }

//    @Test
    public void getBrokerTopicLocationTest() {
        // result is empty
        getBrokerTopicLocation2EmptyTest();
        // result is not empty
        getBrokerTopicLocation2NotEmptyTest();
    }

    private void getBrokerTopicLocation2EmptyTest() {
        Map<TopicPartition, String> brokerTopicLocation = jmxService.getBrokerTopicLocation(
                REAL_CLUSTER_ID_IN_MYSQL,
                INVALID_BROKER_ID
        );
        Assert.assertTrue(brokerTopicLocation.isEmpty());
    }

    private void getBrokerTopicLocation2NotEmptyTest() {
        Map<TopicPartition, String> brokerTopicLocation = jmxService.getBrokerTopicLocation(
                REAL_CLUSTER_ID_IN_MYSQL,
                2
        );
        Assert.assertFalse(brokerTopicLocation.isEmpty());
    }

    @Test
    public void getPartitionAttributeTest() {
        // result is empty
        getPartitionAttribute2EmptyTest();
        // result is not empty
        getPartitionAttribute2NotEmptyTest();
    }

    private void getPartitionAttribute2EmptyTest() {
        Map<Integer, PartitionAttributeDTO> list = jmxService.getPartitionAttribute(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_TOPIC2_IN_ZK,
                Collections.emptyList());
        Assert.assertTrue(list.isEmpty());
    }

    private void getPartitionAttribute2NotEmptyTest() {
        // 需要确定leader所在broker
        PartitionState partitionState1 = getPartitionState();
        PartitionState partitionState2 = getPartitionState();
        partitionState2.setLeader(3);
        partitionState2.setPartitionId(1);

        Map<Integer, PartitionAttributeDTO> list = jmxService.getPartitionAttribute(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_TOPIC2_IN_ZK,
                Arrays.asList(partitionState1, partitionState1, partitionState2)
        );
        Assert.assertFalse(list.isEmpty());
    }
}
