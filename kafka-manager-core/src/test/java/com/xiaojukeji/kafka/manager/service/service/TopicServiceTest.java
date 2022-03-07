package com.xiaojukeji.kafka.manager.service.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.common.bizenum.OffsetPosEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.TopicOffsetChangedEnum;
import com.xiaojukeji.kafka.manager.common.constant.TopicSampleConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionAttributeDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionOffsetDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.*;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.TopicDataSampleDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BaseMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicMetricsDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicThrottledMetricsDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.PartitionState;
import com.xiaojukeji.kafka.manager.dao.TopicAppMetricsDao;
import com.xiaojukeji.kafka.manager.dao.TopicMetricsDao;
import com.xiaojukeji.kafka.manager.dao.TopicRequestMetricsDao;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import org.apache.kafka.common.TopicPartition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

/**
 * @author xuguang
 * @Date 2021/12/20
 */
public class TopicServiceTest extends BaseTest {

    /**
     * 集群共包括三个broker:1,2,3, 该topic 1分区 1副本因子，在broker1上
     * 要求测试之前，moduleTest这个topic需要有过生产者生产和消费者消费moduleTest
     */
    @Value("${test.topic.name1}")
    private String REAL_TOPIC1_IN_ZK;

    /**
     * 集群共包括三个broker:1,2,3, 该topic 2分区 3副本因子，在broker1,2,3上
     */
    @Value("${test.topic.name2}")
    private String REAL_TOPIC2_IN_ZK;

    private final static String INVALID_TOPIC = "xxxxx";

    @Value("${test.topic.name6}")
    private String ZK_DEFAULT_TOPIC;

    /**
     * 该topic同样需要被创建，但是不能有流量
     */
    @Value("${test.topic.name5}")
    private String NO_OFFSET_CHANGE_TOPIC_IN_ZK;

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    @Value("${test.broker.id3}")
    private Integer REAL_BROKER_ID_IN_ZK;

    private final static Long INVALID_CLUSTER_ID = -1L;

    private final static Integer INVALID_PARTITION_ID = -1;

    @Value("${test.phyCluster.name}")
    private String REAL_PHYSICAL_CLUSTER_NAME;

    @Value("${test.ZK.address}")
    private String ZOOKEEPER_ADDRESS;

    @Value("${test.ZK.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;

    private final static String SECURITY_PROTOCOL = "{ \t\"security.protocol\": \"SASL_PLAINTEXT\", \t\"sasl.mechanism\": \"PLAIN\", \t\"sasl.jaas.config\": \"org.apache.kafka.common.security.plain.PlainLoginModule required username=\\\"dkm_admin\\\" password=\\\"km_kMl4N8as1Kp0CCY\\\";\" }";

    @Autowired
    @InjectMocks
    private TopicService topicService;

    @Mock
    private TopicManagerService topicManagerService;

    @Mock
    private AppService appService;

    @Mock
    private JmxService jmxService;

    @Mock
    private TopicMetricsDao topicMetricsDao;

    @Mock
    private ThrottleService topicThrottleService;

    @Mock
    private TopicAppMetricsDao topicAppMetricsDao;

    @Mock
    private TopicRequestMetricsDao topicRequestMetricsDao;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private TopicMetricsDO getTopicMetricsDO() {
        TopicMetricsDO topicMetricsDO = new TopicMetricsDO();
        topicMetricsDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        topicMetricsDO.setAppId("moduleTestAppId");
        topicMetricsDO.setTopicName(REAL_TOPIC1_IN_ZK);
        topicMetricsDO.setMetrics("");
        topicMetricsDO.setGmtCreate(new Date());
        return topicMetricsDO;
    }

    private TopicMetricsDO getTopicMetricsDO1() {
        TopicMetricsDO topicMetricsDO = new TopicMetricsDO();
        topicMetricsDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        topicMetricsDO.setAppId("moduleTestAppId");
        topicMetricsDO.setTopicName(REAL_TOPIC1_IN_ZK);
        String metrics = "{\"TotalFetchRequestsPerSecFiveMinuteRate\":4.132236103122026,\"BytesRejectedPerSecFiveMinuteRate\":0.0,\"TotalFetchRequestsPerSecFifteenMinuteRate\":1.5799208507558833,\"ProduceTotalTimeMs98thPercentile\":0.0,\"MessagesInPerSecMeanRate\":0.0,\"ProduceTotalTimeMs75thPercentile\":0.0,\"ProduceTotalTimeMs99thPercentile\":0.0,\"TotalProduceRequestsPerSecOneMinuteRate\":0.0,\"FailedProduceRequestsPerSecFifteenMinuteRate\":0.0,\"BytesInPerSecMeanRate\":0.0,\"TotalProduceRequestsPerSecFiveMinuteRate\":0.0,\"FetchConsumerTotalTimeMs999thPercentile\":0.0,\"FetchConsumerTotalTimeMs98thPercentile\":0.0,\"FetchConsumerTotalTimeMsMean\":0.0,\"FetchConsumerTotalTimeMs99thPercentile\":0.0,\"FailedFetchRequestsPerSecFifteenMinuteRate\":0.0,\"MessagesInPerSecFiveMinuteRate\":0.0,\"RequestHandlerAvgIdlePercentOneMinuteRate\":0.999221766772746,\"ProduceTotalTimeMsMean\":0.0,\"BytesInPerSecFiveMinuteRate\":0.0,\"FailedProduceRequestsPerSecMeanRate\":0.0,\"FailedFetchRequestsPerSecMeanRate\":0.0,\"FailedProduceRequestsPerSecFiveMinuteRate\":0.0,\"BytesOutPerSecFifteenMinuteRate\":0.0,\"BytesInPerSecOneMinuteRate\":0.0,\"BytesOutPerSecFiveMinuteRate\":0.0,\"HealthScore\":90,\"FailedFetchRequestsPerSecOneMinuteRate\":0.0,\"MessagesInPerSecOneMinuteRate\":0.0,\"BytesRejectedPerSecFifteenMinuteRate\":0.0,\"FailedFetchRequestsPerSecFiveMinuteRate\":0.0,\"RequestHandlerAvgIdlePercentFiveMinuteRate\":0.999803118809842,\"BytesOutPerSecOneMinuteRate\":0.0,\"ResponseQueueSizeValue\":0,\"MessagesInPerSecFifteenMinuteRate\":0.0,\"TotalProduceRequestsPerSecMeanRate\":0.0,\"BytesRejectedPerSecMeanRate\":0.0,\"TotalFetchRequestsPerSecMeanRate\":1.2674449706628523,\"NetworkProcessorAvgIdlePercentValue\":1.0,\"TotalFetchRequestsPerSecOneMinuteRate\":10.457259856316893,\"BytesInPerSecFifteenMinuteRate\":0.0,\"BytesOutPerSecMeanRate\":0.0,\"TotalProduceRequestsPerSecFifteenMinuteRate\":0.0,\"FetchConsumerTotalTimeMs50thPercentile\":0.0,\"RequestHandlerAvgIdlePercentFifteenMinuteRate\":0.9999287809186348,\"FetchConsumerTotalTimeMs95thPercentile\":0.0,\"FailedProduceRequestsPerSecOneMinuteRate\":0.0,\"CreateTime\":1638792321071,\"FetchConsumerTotalTimeMs75thPercentile\":0.0,\"ProduceTotalTimeMs999thPercentile\":0.0,\"RequestQueueSizeValue\":0,\"ProduceTotalTimeMs50thPercentile\":0.0,\"BytesRejectedPerSecOneMinuteRate\":0.0,\"RequestHandlerAvgIdlePercentMeanRate\":0.9999649184090593,\"ProduceTotalTimeMs95thPercentile\":0.0}";

        topicMetricsDO.setMetrics(metrics);
        topicMetricsDO.setGmtCreate(new Date(0L));
        return topicMetricsDO;
    }

    private TopicDO getTopicDO() {
        TopicDO topicDO = new TopicDO();
        topicDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        topicDO.setTopicName(REAL_TOPIC1_IN_ZK);
        topicDO.setAppId("moduleTestAppId");
        topicDO.setDescription(INVALID_TOPIC);
        topicDO.setPeakBytesIn(100000L);
        return topicDO;
    }

    private AppDO getAppDO() {
        AppDO appDO = new AppDO();
        appDO.setId(4L);
        appDO.setAppId("moduleTestAppId");
        appDO.setName("moduleTestApp");
        appDO.setPassword("moduleTestApp");
        appDO.setType(1);
        appDO.setApplicant("admin");
        appDO.setPrincipals("admin");
        appDO.setDescription("moduleTestApp");
        appDO.setCreateTime(new Date(1638786493173L));
        appDO.setModifyTime(new Date(1638786493173L));
        return appDO;
    }

    public ClusterDO getClusterDO() {
        ClusterDO clusterDO = new ClusterDO();
        clusterDO.setId(REAL_CLUSTER_ID_IN_MYSQL);
        clusterDO.setClusterName(REAL_PHYSICAL_CLUSTER_NAME);
        clusterDO.setZookeeper(ZOOKEEPER_ADDRESS);
        clusterDO.setBootstrapServers(BOOTSTRAP_SERVERS);
        clusterDO.setSecurityProperties(SECURITY_PROTOCOL);
        clusterDO.setStatus(1);
        clusterDO.setGmtCreate(new Date());
        clusterDO.setGmtModify(new Date());
        return clusterDO;
    }

    private TopicDataSampleDTO getTopicDataSampleDTO() {
        TopicDataSampleDTO topicDataSampleDTO = new TopicDataSampleDTO();
        topicDataSampleDTO.setPartitionId(0);
        topicDataSampleDTO.setOffset(0L);
        topicDataSampleDTO.setTimeout(5000);
        topicDataSampleDTO.setTruncate(true);
        topicDataSampleDTO.setMaxMsgNum(90);
        return topicDataSampleDTO;
    }

    private TopicMetrics getTopicMetrics() {
        TopicMetrics topicMetrics = new TopicMetrics(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC1_IN_ZK);
        String metrics = "{\"TotalFetchRequestsPerSecFiveMinuteRate\":4.132236103122026,\"BytesRejectedPerSecFiveMinuteRate\":0.0,\"TotalFetchRequestsPerSecFifteenMinuteRate\":1.5799208507558833,\"ProduceTotalTimeMs98thPercentile\":0.0,\"MessagesInPerSecMeanRate\":0.0,\"ProduceTotalTimeMs75thPercentile\":0.0,\"ProduceTotalTimeMs99thPercentile\":0.0,\"TotalProduceRequestsPerSecOneMinuteRate\":0.0,\"FailedProduceRequestsPerSecFifteenMinuteRate\":0.0,\"BytesInPerSecMeanRate\":0.0,\"TotalProduceRequestsPerSecFiveMinuteRate\":0.0,\"FetchConsumerTotalTimeMs999thPercentile\":0.0,\"FetchConsumerTotalTimeMs98thPercentile\":0.0,\"FetchConsumerTotalTimeMsMean\":0.0,\"FetchConsumerTotalTimeMs99thPercentile\":0.0,\"FailedFetchRequestsPerSecFifteenMinuteRate\":0.0,\"MessagesInPerSecFiveMinuteRate\":0.0,\"RequestHandlerAvgIdlePercentOneMinuteRate\":0.999221766772746,\"ProduceTotalTimeMsMean\":0.0,\"BytesInPerSecFiveMinuteRate\":0.0,\"FailedProduceRequestsPerSecMeanRate\":0.0,\"FailedFetchRequestsPerSecMeanRate\":0.0,\"FailedProduceRequestsPerSecFiveMinuteRate\":0.0,\"BytesOutPerSecFifteenMinuteRate\":0.0,\"BytesInPerSecOneMinuteRate\":0.0,\"BytesOutPerSecFiveMinuteRate\":0.0,\"HealthScore\":90,\"FailedFetchRequestsPerSecOneMinuteRate\":0.0,\"MessagesInPerSecOneMinuteRate\":0.0,\"BytesRejectedPerSecFifteenMinuteRate\":0.0,\"FailedFetchRequestsPerSecFiveMinuteRate\":0.0,\"RequestHandlerAvgIdlePercentFiveMinuteRate\":0.999803118809842,\"BytesOutPerSecOneMinuteRate\":0.0,\"ResponseQueueSizeValue\":0,\"MessagesInPerSecFifteenMinuteRate\":0.0,\"TotalProduceRequestsPerSecMeanRate\":0.0,\"BytesRejectedPerSecMeanRate\":0.0,\"TotalFetchRequestsPerSecMeanRate\":1.2674449706628523,\"NetworkProcessorAvgIdlePercentValue\":1.0,\"TotalFetchRequestsPerSecOneMinuteRate\":10.457259856316893,\"BytesInPerSecFifteenMinuteRate\":0.0,\"BytesOutPerSecMeanRate\":0.0,\"TotalProduceRequestsPerSecFifteenMinuteRate\":0.0,\"FetchConsumerTotalTimeMs50thPercentile\":0.0,\"RequestHandlerAvgIdlePercentFifteenMinuteRate\":0.9999287809186348,\"FetchConsumerTotalTimeMs95thPercentile\":0.0,\"FailedProduceRequestsPerSecOneMinuteRate\":0.0,\"CreateTime\":1638792321071,\"FetchConsumerTotalTimeMs75thPercentile\":0.0,\"ProduceTotalTimeMs999thPercentile\":0.0,\"RequestQueueSizeValue\":0,\"ProduceTotalTimeMs50thPercentile\":0.0,\"BytesRejectedPerSecOneMinuteRate\":0.0,\"RequestHandlerAvgIdlePercentMeanRate\":0.9999649184090593,\"ProduceTotalTimeMs95thPercentile\":0.0}";
        JSONObject jsonObject = JSON.parseObject(metrics);
        Map<String, Object> metricsMap = new HashMap<>();
        for (Map.Entry<String, Object> stringObjectEntry : jsonObject.entrySet()) {
            metricsMap.put(stringObjectEntry.getKey(), stringObjectEntry.getValue());
        }
        topicMetrics.setMetricsMap(metricsMap);
        return topicMetrics;
    }

    private TopicThrottledMetricsDO getTopicThrottledMetricsDO() {
        TopicThrottledMetricsDO throttledMetricsDO = new TopicThrottledMetricsDO();
        throttledMetricsDO.setGmtCreate(new Date(1638792321071L));
        throttledMetricsDO.setFetchThrottled(100);
        throttledMetricsDO.setProduceThrottled(100);
        return throttledMetricsDO;
    }


    @Test
    public void getTopicMetricsFromDBWithAppIdTest() {
        Mockito.when(topicMetricsDao.getTopicMetrics(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(getTopicMetricsDO1()));
        Mockito.when(topicThrottleService.getTopicThrottleFromDB(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(getTopicThrottledMetricsDO()));
        Mockito.when(topicAppMetricsDao.getTopicAppMetrics(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(getTopicMetricsDO1()));

        List<TopicMetricsDTO> list = topicService.getTopicMetricsFromDB("moduleTestAppId", REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC1_IN_ZK, new Date(0L), new Date());
        Assert.assertFalse(list.isEmpty());
        Assert.assertTrue(list.stream().allMatch(topicMetricsDTO -> topicMetricsDTO.getConsumeThrottled() && topicMetricsDTO.getProduceThrottled()));
    }

    @Test(description = "测试获取指定时间段内的峰值的均值流量")
    public void getMaxAvgBytesInFromDBTest() {
        // 为空
        getMaxAvgBytesInFromDB2NullTest();
        // 获取成功
        getMaxAvgBytesInFromDB2SuccessTest();
    }

    private void getMaxAvgBytesInFromDB2NullTest() {
        Double result = topicService.getMaxAvgBytesInFromDB(REAL_CLUSTER_ID_IN_MYSQL, INVALID_TOPIC, new Date(0L), new Date());
        Assert.assertNull(result);
    }

    private void getMaxAvgBytesInFromDB2SuccessTest() {
        Mockito.when(topicMetricsDao.getTopicMetrics(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(getTopicMetricsDO1()));
        Double result = topicService.getMaxAvgBytesInFromDB(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC1_IN_ZK, new Date(0L), new Date());
        Assert.assertNotNull(result);
    }

    @Test(description = "获取brokerId下所有的Topic及其对应的PartitionId")
    public void getTopicPartitionIdMapTest() {
        Map<String, List<Integer>> topicPartitionIdMap = topicService.getTopicPartitionIdMap(REAL_CLUSTER_ID_IN_MYSQL, 1);
        Assert.assertFalse(topicPartitionIdMap.isEmpty());
        Assert.assertTrue(topicPartitionIdMap.containsKey(REAL_TOPIC1_IN_ZK));
    }

    @Test(description = "测试获取 Topic 的 basic-info 信息")
    public void getTopicBasicDTOTest() {
        // TopicMetadata is Null
        getTopicBasicDTO2TopicMetadataIsNull();
        // TopicDO is Null
        getTopicBasicDTO2TopicMetadata2TopicDOIsNull();
        // TopicDO is not Null
        getTopicBasicDTO2TopicMetadata2TopicDOIsNotNull();
    }

    private void getTopicBasicDTO2TopicMetadataIsNull() {
        TopicBasicDTO result = topicService.getTopicBasicDTO(REAL_CLUSTER_ID_IN_MYSQL, INVALID_TOPIC);
        Assert.assertEquals(result.getClusterId(), Long.valueOf(REAL_CLUSTER_ID_IN_MYSQL));
        Assert.assertEquals(result.getTopicName(), INVALID_TOPIC);
        Assert.assertNull(result.getAppId());
    }

    private void getTopicBasicDTO2TopicMetadata2TopicDOIsNull() {
        Mockito.when(topicManagerService.getByTopicName(Mockito.anyLong(), Mockito.anyString())).thenReturn(null);
        TopicBasicDTO result = topicService.getTopicBasicDTO(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC1_IN_ZK);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getClusterId(), Long.valueOf(REAL_CLUSTER_ID_IN_MYSQL));
        Assert.assertEquals(result.getTopicName(), REAL_TOPIC1_IN_ZK);
        Assert.assertNull(result.getDescription());
        Assert.assertNull(result.getAppId());
    }

    private void getTopicBasicDTO2TopicMetadata2TopicDOIsNotNull() {
        TopicDO topicDO = getTopicDO();
        Mockito.when(topicManagerService.getByTopicName(Mockito.anyLong(), Mockito.anyString())).thenReturn(topicDO);
        Mockito.when(appService.getByAppId(Mockito.anyString())).thenReturn(null);
        TopicBasicDTO result = topicService.getTopicBasicDTO(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC1_IN_ZK);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getClusterId(), Long.valueOf(REAL_CLUSTER_ID_IN_MYSQL));
        Assert.assertEquals(result.getTopicName(), REAL_TOPIC1_IN_ZK);
        Assert.assertEquals(result.getDescription(), topicDO.getDescription());
        // appId不存在
        Assert.assertNull(result.getAppId());
        // appId存在
        topicDO.setAppId("moduleTestAppId");
        Mockito.when(topicManagerService.getByTopicName(Mockito.anyLong(), Mockito.anyString())).thenReturn(topicDO);
        Mockito.when(appService.getByAppId(Mockito.anyString())).thenReturn(getAppDO());
        TopicBasicDTO result2 = topicService.getTopicBasicDTO(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC1_IN_ZK);
        Assert.assertNotNull(result2);
        Assert.assertEquals(result2.getClusterId(), Long.valueOf(REAL_CLUSTER_ID_IN_MYSQL));
        Assert.assertEquals(result2.getTopicName(), REAL_TOPIC1_IN_ZK);
        Assert.assertEquals(result2.getDescription(), topicDO.getDescription());
        Assert.assertEquals(result2.getAppId(), topicDO.getAppId());
    }

    @Test(description = "获取Topic的PartitionState信息")
    public void getTopicPartitionDTOTest() {
        // result is emptyList
        getTopicPartitionDTO2EmptyTest();
        // needDetail is false
        getTopicPartitionDTO2NeedDetailFalseTest();
        // needDetail is true
        getTopicPartitionDTO2NeedDetailTrueTest();
    }

    private void getTopicPartitionDTO2EmptyTest() {
        List<TopicPartitionDTO> list = topicService.getTopicPartitionDTO(null, null, true);
        Assert.assertTrue(list.isEmpty());

        ClusterDO clusterDO = new ClusterDO();
        clusterDO.setId(REAL_CLUSTER_ID_IN_MYSQL);
        List<TopicPartitionDTO> list2 = topicService.getTopicPartitionDTO(clusterDO, INVALID_TOPIC, true);
        Assert.assertTrue(list2.isEmpty());
    }

    private void getTopicPartitionDTO2NeedDetailFalseTest() {
        Map<Integer, PartitionAttributeDTO> map = new HashMap<>();
        PartitionAttributeDTO partitionAttributeDTO1 = new PartitionAttributeDTO();
        partitionAttributeDTO1.setLogSize(0L);
        map.put(0, partitionAttributeDTO1);
        map.put(1, null);
        Mockito.when(jmxService.getPartitionAttribute(
                Mockito.anyLong(), Mockito.anyString(), Mockito.anyList())).thenReturn(map);

        ClusterDO clusterDO = getClusterDO();
        List<TopicPartitionDTO> list = topicService.getTopicPartitionDTO(clusterDO, REAL_TOPIC1_IN_ZK, false);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 1);
        Assert.assertTrue(list.stream().allMatch(topicPartitionDTO ->
                topicPartitionDTO.getBeginningOffset() == null &&
                        topicPartitionDTO.getEndOffset() == null));
    }

    private void getTopicPartitionDTO2NeedDetailTrueTest() {
        Map<Integer, PartitionAttributeDTO> map = new HashMap<>();
        PartitionAttributeDTO partitionAttributeDTO1 = new PartitionAttributeDTO();
        partitionAttributeDTO1.setLogSize(0L);
        map.put(0, partitionAttributeDTO1);
        map.put(1, null);
        Mockito.when(jmxService.getPartitionAttribute(
                Mockito.anyLong(), Mockito.anyString(), Mockito.anyList())).thenReturn(map);

        ClusterDO clusterDO = getClusterDO();
        List<TopicPartitionDTO> list = topicService.getTopicPartitionDTO(clusterDO, REAL_TOPIC1_IN_ZK, true);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 1);
        Assert.assertTrue(list.stream().allMatch(topicPartitionDTO ->
                topicPartitionDTO.getBeginningOffset() != null &&
                topicPartitionDTO.getEndOffset() != null));
    }

    @Test
    public void getTopicMetricsFromJMXTest() {
        Mockito.when(jmxService.getTopicMetrics(
                Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyBoolean())).thenReturn(new TopicMetrics(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC1_IN_ZK));
        BaseMetrics result = topicService.getTopicMetricsFromJMX(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC1_IN_ZK, 200, true);
        Assert.assertNotNull(result);
    }

    @Test(description = "测试获取Topic的分区的offset")
    public void getPartitionOffsetTest() {
        // 结果为空
        getPartitionOffset2EmptyTest();
        // 获取成功
        getPartitionOffset2SuccessTest();
    }

    private void getPartitionOffset2EmptyTest() {
        ClusterDO clusterDO = getClusterDO();
        Map<TopicPartition, Long> partitionOffset = topicService.getPartitionOffset(
                null, null, OffsetPosEnum.BEGINNING);
        Assert.assertTrue(partitionOffset.isEmpty());

        Map<TopicPartition, Long> partitionOffset2 = topicService.getPartitionOffset(
                clusterDO, INVALID_TOPIC, OffsetPosEnum.BEGINNING);
        Assert.assertTrue(partitionOffset2.isEmpty());
    }

    private void getPartitionOffset2SuccessTest() {
        ClusterDO clusterDO = getClusterDO();
        // 获取beginning offset
        Map<TopicPartition, Long> partitionOffset1 = topicService.getPartitionOffset(
                clusterDO, REAL_TOPIC1_IN_ZK, OffsetPosEnum.BEGINNING);
        Assert.assertFalse(partitionOffset1.isEmpty());
        // 获取end offset
        Map<TopicPartition, Long> partitionOffset2 = topicService.getPartitionOffset(
                clusterDO, REAL_TOPIC1_IN_ZK, OffsetPosEnum.END);
        Assert.assertFalse(partitionOffset2.isEmpty());
    }

    @Test(description = "测试获取Topic概览信息，参数clusterId, brokerId")
    public void getTopicOverviewListTest() {
        // 结果为空
        getTopicOverviewList2EmptyTest();
        // 获取成功
        getTopicOverviewList2SuccessTest();
    }

    private void getTopicOverviewList2EmptyTest() {
        List<TopicOverview> topicOverviewList = topicService.getTopicOverviewList(null, 1);
        Assert.assertTrue(topicOverviewList.isEmpty());
    }

    private void getTopicOverviewList2SuccessTest() {
        List<TopicOverview> topicOverviewList = topicService.getTopicOverviewList(REAL_CLUSTER_ID_IN_MYSQL, 1);
        Assert.assertFalse(topicOverviewList.isEmpty());
    }

    @Test(description = "测试获取Topic概览信息，参数clusterId, topicNameList")
    public void getTopicOverviewListWithTopicList() {
        // 结果为空
        getTopicOverviewListWithTopicList2EmptyTest();

        // topicDOList is null，appDOList is null, metrics is null
        getTopicOverviewListWithTopicList2TopicAndApp1Test();

        // topicDOList is null，appDOList is null, metrics is not null
        getTopicOverviewListWithTopicList2TopicAndApp2Test();

        // topicDOList is null，appDOList is not null, metrics is null
        getTopicOverviewListWithTopicList2TopicAndApp3Test();

        // topicDOList is null，appDOList is not null, metrics is not null
        getTopicOverviewListWithTopicList2TopicAndApp4Test();

        // topicDOList is not null，appDOList is null, metrics is null
        getTopicOverviewListWithTopicList2TopicAndApp5Test();

        // topicDOList is not null，appDOList is null, metrics is not null
        getTopicOverviewListWithTopicList2TopicAndApp6Test();

        // topicDOList is not null，appDOList is not null, metrics is null
        getTopicOverviewListWithTopicList2TopicAndApp7Test();

        // topicDOList is not null，appDOList is not null, metrics is not null
        getTopicOverviewListWithTopicList2TopicAndApp8Test();

    }

    private void getTopicOverviewListWithTopicList2EmptyTest() {
        List<TopicOverview> topicOverviewList = topicService.getTopicOverviewList(null, Arrays.asList(REAL_TOPIC1_IN_ZK, ZK_DEFAULT_TOPIC, INVALID_TOPIC));
        Assert.assertTrue(topicOverviewList.isEmpty());
    }

    private void getTopicOverviewListWithTopicList2TopicAndApp1Test() {
        Mockito.when(topicManagerService.getByClusterIdFromCache(Mockito.anyLong())).thenReturn(null);
        Mockito.when(appService.listAll()).thenReturn(null);
        Mockito.when(jmxService.getTopicMetrics(
                Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyBoolean())).thenReturn(null);

        List<String> topics = Arrays.asList(REAL_TOPIC1_IN_ZK, ZK_DEFAULT_TOPIC, INVALID_TOPIC);
        List<TopicOverview> topicOverviewList = topicService.getTopicOverviewList(REAL_CLUSTER_ID_IN_MYSQL, topics);
        Assert.assertFalse(topicOverviewList.isEmpty());
        Assert.assertTrue(topicOverviewList.stream().allMatch(topicOverview ->
                topicOverview.getClusterId().equals(REAL_CLUSTER_ID_IN_MYSQL) &&
                topics.contains(topicOverview.getTopicName()) &&
                topicOverview.getAppId() == null &&
                topicOverview.getAppName() == null &&
                topicOverview.getByteIn() == null));
    }

    private void getTopicOverviewListWithTopicList2TopicAndApp2Test() {
        Mockito.when(topicManagerService.getByClusterIdFromCache(Mockito.anyLong())).thenReturn(null);
        Mockito.when(appService.listAll()).thenReturn(null);
        TopicMetrics topicMetrics = getTopicMetrics();
        Mockito.when(jmxService.getTopicMetrics(
                Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyBoolean())).
                thenReturn(topicMetrics);

        List<String> topics = Arrays.asList(REAL_TOPIC1_IN_ZK, ZK_DEFAULT_TOPIC, INVALID_TOPIC);
        List<TopicOverview> topicOverviewList = topicService.getTopicOverviewList(REAL_CLUSTER_ID_IN_MYSQL, topics);
        Assert.assertFalse(topicOverviewList.isEmpty());
        Assert.assertTrue(topicOverviewList.stream().allMatch(topicOverview ->
                topicOverview.getClusterId().equals(REAL_CLUSTER_ID_IN_MYSQL) &&
                        topics.contains(topicOverview.getTopicName()) &&
                        topicOverview.getAppId() == null &&
                        topicOverview.getAppName() == null &&
                        topicOverview.getByteIn() != null));
    }

    private void getTopicOverviewListWithTopicList2TopicAndApp3Test() {
        Mockito.when(topicManagerService.getByClusterIdFromCache(Mockito.anyLong())).thenReturn(null);
        AppDO appDO = getAppDO();
        Mockito.when(appService.listAll()).thenReturn(Arrays.asList(appDO));

        Mockito.when(jmxService.getTopicMetrics(
                Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyBoolean())).
                thenReturn(null);

        List<String> topics = Arrays.asList(REAL_TOPIC1_IN_ZK, ZK_DEFAULT_TOPIC, INVALID_TOPIC);
        List<TopicOverview> topicOverviewList = topicService.getTopicOverviewList(REAL_CLUSTER_ID_IN_MYSQL, topics);
        Assert.assertFalse(topicOverviewList.isEmpty());
        Assert.assertTrue(topicOverviewList.stream().allMatch(topicOverview ->
                topicOverview.getClusterId().equals(REAL_CLUSTER_ID_IN_MYSQL) &&
                        topics.contains(topicOverview.getTopicName()) &&
                        topicOverview.getAppId() == null &&
                        topicOverview.getAppName() == null &&
                        topicOverview.getByteIn() == null));
    }

    private void getTopicOverviewListWithTopicList2TopicAndApp4Test() {
        Mockito.when(topicManagerService.getByClusterIdFromCache(Mockito.anyLong())).thenReturn(null);
        AppDO appDO = getAppDO();
        Mockito.when(appService.listAll()).thenReturn(Arrays.asList(appDO));

        TopicMetrics topicMetrics = getTopicMetrics();
        Mockito.when(jmxService.getTopicMetrics(
                Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyBoolean())).
                thenReturn(topicMetrics);

        List<String> topics = Arrays.asList(REAL_TOPIC1_IN_ZK, ZK_DEFAULT_TOPIC, INVALID_TOPIC);
        List<TopicOverview> topicOverviewList = topicService.getTopicOverviewList(REAL_CLUSTER_ID_IN_MYSQL, topics);
        Assert.assertFalse(topicOverviewList.isEmpty());
        Assert.assertTrue(topicOverviewList.stream().allMatch(topicOverview ->
                topicOverview.getClusterId().equals(REAL_CLUSTER_ID_IN_MYSQL) &&
                        topics.contains(topicOverview.getTopicName()) &&
                        topicOverview.getAppId() == null &&
                        topicOverview.getAppName() == null &&
                        topicOverview.getByteIn() != null));
    }

    private void getTopicOverviewListWithTopicList2TopicAndApp5Test() {
        TopicDO topicDO = getTopicDO();
        Mockito.when(topicManagerService.getByClusterIdFromCache(Mockito.anyLong())).thenReturn(Arrays.asList(topicDO));
        Mockito.when(appService.listAll()).thenReturn(null);
        Mockito.when(jmxService.getTopicMetrics(
                Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyBoolean())).thenReturn(null);

        List<String> topics = Arrays.asList(REAL_TOPIC1_IN_ZK, ZK_DEFAULT_TOPIC, INVALID_TOPIC);
        List<TopicOverview> topicOverviewList = topicService.getTopicOverviewList(REAL_CLUSTER_ID_IN_MYSQL, topics);
        Assert.assertFalse(topicOverviewList.isEmpty());
        Assert.assertTrue(topicOverviewList.stream().allMatch(topicOverview ->
                topicOverview.getClusterId().equals(REAL_CLUSTER_ID_IN_MYSQL) &&
                        topics.contains(topicOverview.getTopicName()) &&
                        topicOverview.getAppId().equals(topicDO.getAppId()) &&
                        topicOverview.getAppName() == null &&
                        topicOverview.getByteIn() == null));
    }

    private void getTopicOverviewListWithTopicList2TopicAndApp6Test() {
        TopicDO topicDO = getTopicDO();
        Mockito.when(topicManagerService.getByClusterIdFromCache(Mockito.anyLong())).thenReturn(Arrays.asList(topicDO));
        Mockito.when(appService.listAll()).thenReturn(null);
        TopicMetrics topicMetrics = getTopicMetrics();
        Mockito.when(jmxService.getTopicMetrics(
                Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyBoolean())).
                thenReturn(topicMetrics);

        List<String> topics = Arrays.asList(REAL_TOPIC1_IN_ZK, ZK_DEFAULT_TOPIC, INVALID_TOPIC);
        List<TopicOverview> topicOverviewList = topicService.getTopicOverviewList(REAL_CLUSTER_ID_IN_MYSQL, topics);
        Assert.assertFalse(topicOverviewList.isEmpty());
        Assert.assertTrue(topicOverviewList.stream().allMatch(topicOverview ->
                topicOverview.getClusterId().equals(REAL_CLUSTER_ID_IN_MYSQL) &&
                        topics.contains(topicOverview.getTopicName()) &&
                        topicOverview.getAppId().equals(topicDO.getAppId()) &&
                        topicOverview.getAppName() == null &&
                        topicOverview.getByteIn() != null));
    }

    private void getTopicOverviewListWithTopicList2TopicAndApp7Test() {
        TopicDO topicDO = getTopicDO();
        Mockito.when(topicManagerService.getByClusterIdFromCache(Mockito.anyLong())).thenReturn(Arrays.asList(topicDO));
        AppDO appDO = getAppDO();
        Mockito.when(appService.listAll()).thenReturn(Arrays.asList(appDO));

        Mockito.when(jmxService.getTopicMetrics(
                Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyBoolean())).
                thenReturn(null);

        List<String> topics = Arrays.asList(REAL_TOPIC1_IN_ZK, ZK_DEFAULT_TOPIC, INVALID_TOPIC);
        List<TopicOverview> topicOverviewList = topicService.getTopicOverviewList(REAL_CLUSTER_ID_IN_MYSQL, topics);
        Assert.assertFalse(topicOverviewList.isEmpty());
        Assert.assertTrue(topicOverviewList.stream().allMatch(topicOverview ->
                topicOverview.getClusterId().equals(REAL_CLUSTER_ID_IN_MYSQL) &&
                        topics.contains(topicOverview.getTopicName()) &&
                        topicOverview.getAppId().equals(topicDO.getAppId()) &&
                        topicOverview.getAppName().equals(appDO.getName()) &&
                        topicOverview.getByteIn() == null));
    }

    private void getTopicOverviewListWithTopicList2TopicAndApp8Test() {
        TopicDO topicDO = getTopicDO();
        Mockito.when(topicManagerService.getByClusterIdFromCache(Mockito.anyLong())).thenReturn(Arrays.asList(topicDO));
        AppDO appDO = getAppDO();
        Mockito.when(appService.listAll()).thenReturn(Arrays.asList(appDO));

        TopicMetrics topicMetrics = getTopicMetrics();
        Mockito.when(jmxService.getTopicMetrics(
                Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyBoolean())).
                thenReturn(topicMetrics);

        List<String> topics = Arrays.asList(REAL_TOPIC1_IN_ZK, ZK_DEFAULT_TOPIC, INVALID_TOPIC);
        List<TopicOverview> topicOverviewList = topicService.getTopicOverviewList(REAL_CLUSTER_ID_IN_MYSQL, topics);
        Assert.assertFalse(topicOverviewList.isEmpty());
        Assert.assertTrue(topicOverviewList.stream().allMatch(topicOverview ->
                topicOverview.getClusterId().equals(REAL_CLUSTER_ID_IN_MYSQL) &&
                        topics.contains(topicOverview.getTopicName()) &&
                        topicOverview.getAppId().equals(topicDO.getAppId()) &&
                        topicOverview.getAppName().equals(appDO.getName()) &&
                        topicOverview.getByteIn() != null));
    }

    @Test(description = "测试获取指定时间的offset信息")
    public void getPartitionOffsetListTest() {
        // 结果为空
        getPartitionOffsetList2EmptyTest();
        // 获取成功
        getPartitionOffsetList2SuccessTest();
    }

    private void getPartitionOffsetList2EmptyTest() {
        ClusterDO clusterDO = getClusterDO();
        List<PartitionOffsetDTO> list = topicService.getPartitionOffsetList(clusterDO, INVALID_TOPIC, 0L);
        Assert.assertTrue(list.isEmpty());
    }

    private void getPartitionOffsetList2SuccessTest() {
        ClusterDO clusterDO = getClusterDO();
        List<PartitionOffsetDTO> list = topicService.getPartitionOffsetList(clusterDO, REAL_TOPIC1_IN_ZK, 0L);
        Assert.assertFalse(list.isEmpty());
    }

    @Test()
    public void getTopicPartitionStateTest() {
        // 结果为空
        getTopicPartitionState2EmptyTest();

        // 获取结果成功
        getTopicPartitionState2SuccessTest();
    }

    private void getTopicPartitionState2EmptyTest() {
        Map<String, List<PartitionState>> map1 =
                topicService.getTopicPartitionState(null, REAL_BROKER_ID_IN_ZK);
        Assert.assertTrue(map1.isEmpty());

        Map<String, List<PartitionState>> map2 =
                topicService.getTopicPartitionState(INVALID_CLUSTER_ID, REAL_BROKER_ID_IN_ZK);
        Assert.assertTrue(map2.isEmpty());
    }

    /**
     * 共有三个topic, REAL_TOPIC1_IN_ZK, REAL_TOPIC2_IN_ZK, ZK_DEFAULT_TOPIC
     */
    private void getTopicPartitionState2SuccessTest() {
        Map<String, List<PartitionState>> map1 =
                topicService.getTopicPartitionState(REAL_CLUSTER_ID_IN_MYSQL, REAL_BROKER_ID_IN_ZK);
        Assert.assertFalse(map1.isEmpty());
    }

    @Test(description = "测试数据采样")
    public void fetchTopicDataTest() {
        // invalid partitionId
        fetchTopicData2InvalidPartitionId();
        // 指定了offset,截断
        fetchTopicData2OffsetAndTruncate();
        // 指定了offset,未截断
        fetchTopicData2OffsetAndNoTruncate();
        // 未指定offset, 返回空
        fetchTopicData2NoOffset2Empty();
        // 未指定offset,截断
        fetchTopicData2NoOffsetAndTruncate();
        // 未指定offset,未截断
        fetchTopicData2NoOffsetAndNoTruncate();
    }

    private void fetchTopicData2InvalidPartitionId() {
        ClusterDO clusterDO = getClusterDO();
        TopicDataSampleDTO topicDataSampleDTO = getTopicDataSampleDTO();
        topicDataSampleDTO.setPartitionId(INVALID_PARTITION_ID);
        List<String> result = topicService.fetchTopicData(clusterDO, REAL_TOPIC1_IN_ZK, topicDataSampleDTO);
        Assert.assertTrue(result.isEmpty());
    }

    private void fetchTopicData2NoOffsetAndTruncate() {
        ClusterDO clusterDO = getClusterDO();
        TopicDataSampleDTO topicDataSampleDTO = getTopicDataSampleDTO();
        topicDataSampleDTO.setOffset(null);
        List<String> result = topicService.fetchTopicData(clusterDO, REAL_TOPIC1_IN_ZK, topicDataSampleDTO);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(result.stream().allMatch(
                value -> value.length() <= TopicSampleConstant.MAX_DATA_LENGTH_UNIT_BYTE));
    }

    private void fetchTopicData2NoOffsetAndNoTruncate() {
        ClusterDO clusterDO = getClusterDO();
        TopicDataSampleDTO topicDataSampleDTO = getTopicDataSampleDTO();
        topicDataSampleDTO.setOffset(null);
        topicDataSampleDTO.setTruncate(false);
        List<String> result = topicService.fetchTopicData(clusterDO, REAL_TOPIC1_IN_ZK, topicDataSampleDTO);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(result.stream().allMatch(
                value -> value.length() != TopicSampleConstant.MAX_DATA_LENGTH_UNIT_BYTE));
    }

    private void fetchTopicData2OffsetAndTruncate() {
        ClusterDO clusterDO = getClusterDO();
        TopicDataSampleDTO topicDataSampleDTO = getTopicDataSampleDTO();
        List<String> result = topicService.fetchTopicData(clusterDO, REAL_TOPIC1_IN_ZK, topicDataSampleDTO);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(result.stream().allMatch(
                value -> value.length() <= TopicSampleConstant.MAX_DATA_LENGTH_UNIT_BYTE));
    }

    private void fetchTopicData2OffsetAndNoTruncate() {
        ClusterDO clusterDO = getClusterDO();
        TopicDataSampleDTO topicDataSampleDTO = getTopicDataSampleDTO();
        topicDataSampleDTO.setTruncate(false);
        List<String> result = topicService.fetchTopicData(clusterDO, REAL_TOPIC1_IN_ZK, topicDataSampleDTO);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(result.stream().allMatch(
                value -> value.length() != TopicSampleConstant.MAX_DATA_LENGTH_UNIT_BYTE));
    }

    private void fetchTopicData2NoOffset2Empty() {
        ClusterDO clusterDO = getClusterDO();
        TopicDataSampleDTO topicDataSampleDTO = getTopicDataSampleDTO();
        topicDataSampleDTO.setOffset(null);
        topicDataSampleDTO.setTimeout(-1);
        List<String> result = topicService.fetchTopicData(clusterDO, REAL_TOPIC1_IN_ZK, topicDataSampleDTO);
        Assert.assertTrue(result.isEmpty());
    }

    @Test(description = "测试获取topic的broker列表")
    public void getTopicBrokerListTest() {
        List<TopicBrokerDTO> topicBrokerList = topicService.getTopicBrokerList(
                REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC2_IN_ZK);
        Assert.assertFalse(topicBrokerList.isEmpty());
    }

    @Test(description = "测试topic是否有数据写入")
    public void checkTopicOffsetChangedTest() {
        // physicalCluster does not exist
        checkTopicOffsetChanged2ClusterNotExistTest();
        // endOffsetMap is empty
        checkTopicOffsetChanged2UnknownTest();
        // dtoList is not empty and result is Yes
        checkTopicOffsetChanged2dtoListNotNullAndYesTest();
        // dtoList is empty and result is No
        checkTopicOffsetChanged2NoTest();
    }

    private void checkTopicOffsetChanged2ClusterNotExistTest() {
        Result<TopicOffsetChangedEnum> result =
                topicService.checkTopicOffsetChanged(INVALID_CLUSTER_ID, REAL_TOPIC1_IN_ZK, 0L);
        Assert.assertEquals(result.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void checkTopicOffsetChanged2UnknownTest() {
        Result<TopicOffsetChangedEnum> result =
                topicService.checkTopicOffsetChanged(REAL_CLUSTER_ID_IN_MYSQL, INVALID_TOPIC, 0L);
        Assert.assertEquals(result.getData().getCode(), TopicOffsetChangedEnum.UNKNOWN.getCode());
    }

    private void checkTopicOffsetChanged2dtoListNotNullAndYesTest() {
        Result<TopicOffsetChangedEnum> result = topicService.checkTopicOffsetChanged(
                REAL_CLUSTER_ID_IN_MYSQL,
                REAL_TOPIC1_IN_ZK,
                System.currentTimeMillis());
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getData().getCode(), TopicOffsetChangedEnum.YES.getCode());
    }

    private void checkTopicOffsetChanged2NoTest() {
        Result<TopicOffsetChangedEnum> result = topicService.checkTopicOffsetChanged(
                REAL_CLUSTER_ID_IN_MYSQL,
                NO_OFFSET_CHANGE_TOPIC_IN_ZK,
                System.currentTimeMillis());
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getData().getCode(), TopicOffsetChangedEnum.NO.getCode());
    }

}
