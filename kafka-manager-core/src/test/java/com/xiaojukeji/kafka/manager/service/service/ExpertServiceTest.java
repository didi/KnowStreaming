package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.ao.config.expert.RegionTopicHotConfig;
import com.xiaojukeji.kafka.manager.common.entity.ao.config.expert.TopicExpiredConfig;
import com.xiaojukeji.kafka.manager.common.entity.ao.config.expert.TopicInsufficientPartitionConfig;
import com.xiaojukeji.kafka.manager.common.entity.ao.expert.TopicInsufficientPartition;
import com.xiaojukeji.kafka.manager.common.entity.ao.expert.TopicRegionHot;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicExpiredDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicMetricsDO;
import com.xiaojukeji.kafka.manager.dao.TopicMetricsDao;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.constraints.AssertTrue;
import java.util.*;

/**
 * @author wyc
 * @date 2021/12/27
 */
public class ExpertServiceTest extends BaseTest {
    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    @Value("${test.topic.name4}")
    private String REAL_TOPIC_IN_ZK;

    private final static Set<Integer> REAL_BROKER_ID_SET = new HashSet<>();

    private String metrics = "{\"TotalFetchRequestsPerSecFiveMinuteRate\":4.132236103122026,\"BytesRejectedPerSecFiveMinuteRate\":0.0,\"TotalFetchRequestsPerSecFifteenMinuteRate\":1.5799208507558833,\"ProduceTotalTimeMs98thPercentile\":0.0,\"MessagesInPerSecMeanRate\":0.0,\"ProduceTotalTimeMs75thPercentile\":0.0,\"ProduceTotalTimeMs99thPercentile\":0.0,\"TotalProduceRequestsPerSecOneMinuteRate\":0.0,\"FailedProduceRequestsPerSecFifteenMinuteRate\":0.0,\"BytesInPerSecMeanRate\":0.0,\"TotalProduceRequestsPerSecFiveMinuteRate\":0.0,\"FetchConsumerTotalTimeMs999thPercentile\":0.0,\"FetchConsumerTotalTimeMs98thPercentile\":0.0,\"FetchConsumerTotalTimeMsMean\":0.0,\"FetchConsumerTotalTimeMs99thPercentile\":0.0,\"FailedFetchRequestsPerSecFifteenMinuteRate\":0.0,\"MessagesInPerSecFiveMinuteRate\":0.0,\"RequestHandlerAvgIdlePercentOneMinuteRate\":0.999221766772746,\"ProduceTotalTimeMsMean\":0.0,\"BytesInPerSecFiveMinuteRate\":0.0,\"FailedProduceRequestsPerSecMeanRate\":0.0,\"FailedFetchRequestsPerSecMeanRate\":0.0,\"FailedProduceRequestsPerSecFiveMinuteRate\":0.0,\"BytesOutPerSecFifteenMinuteRate\":0.0,\"BytesInPerSecOneMinuteRate\":100.0,\"BytesOutPerSecFiveMinuteRate\":0.0,\"HealthScore\":90,\"FailedFetchRequestsPerSecOneMinuteRate\":0.0,\"MessagesInPerSecOneMinuteRate\":0.0,\"BytesRejectedPerSecFifteenMinuteRate\":0.0,\"FailedFetchRequestsPerSecFiveMinuteRate\":0.0,\"RequestHandlerAvgIdlePercentFiveMinuteRate\":0.999803118809842,\"BytesOutPerSecOneMinuteRate\":0.0,\"ResponseQueueSizeValue\":0,\"MessagesInPerSecFifteenMinuteRate\":0.0,\"TotalProduceRequestsPerSecMeanRate\":0.0,\"BytesRejectedPerSecMeanRate\":0.0,\"TotalFetchRequestsPerSecMeanRate\":1.2674449706628523,\"NetworkProcessorAvgIdlePercentValue\":1.0,\"TotalFetchRequestsPerSecOneMinuteRate\":10.457259856316893,\"BytesInPerSecFifteenMinuteRate\":0.0,\"BytesOutPerSecMeanRate\":0.0,\"TotalProduceRequestsPerSecFifteenMinuteRate\":0.0,\"FetchConsumerTotalTimeMs50thPercentile\":0.0,\"RequestHandlerAvgIdlePercentFifteenMinuteRate\":0.9999287809186348,\"FetchConsumerTotalTimeMs95thPercentile\":0.0,\"FailedProduceRequestsPerSecOneMinuteRate\":0.0,\"CreateTime\":1638792321071,\"FetchConsumerTotalTimeMs75thPercentile\":0.0,\"ProduceTotalTimeMs999thPercentile\":0.0,\"RequestQueueSizeValue\":0,\"ProduceTotalTimeMs50thPercentile\":0.0,\"BytesRejectedPerSecOneMinuteRate\":0.0,\"RequestHandlerAvgIdlePercentMeanRate\":0.9999649184090593,\"ProduceTotalTimeMs95thPercentile\":0.0}";
    static {
        REAL_BROKER_ID_SET.add(1);
        REAL_BROKER_ID_SET.add(2);
    }

    @Autowired
    @InjectMocks
    private ExpertService expertService;

    @Mock
    private ConfigService configService;

    @Mock
    private RegionService regionService;

    @Mock
    private ClusterService clusterService;

    @Mock
    private TopicManagerService topicManagerService;

    @Autowired
    private TopicMetricsDao topicMetricsDao;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    private ClusterDO getClusterDO() {
        ClusterDO clusterDO = new ClusterDO();
        clusterDO.setId(REAL_CLUSTER_ID_IN_MYSQL);

        return clusterDO;
    }

    private RegionTopicHotConfig getRegionTopicHotConfig() {
        RegionTopicHotConfig config = new RegionTopicHotConfig();
        config.setMaxDisPartitionNum(-1);// 为了通过检测
        // ignoreClusterIdList字段不用设置
        config.setMinTopicBytesInUnitB(-1L);// 为了通过检测
        return config;
    }
    private TopicRegionHot getTopicRegionHot() {
        ClusterDO clusterDO = getClusterDO();
        TopicRegionHot hotTopic = new TopicRegionHot(clusterDO, REAL_TOPIC_IN_ZK, null, new HashMap<>());
        return hotTopic;
    }

    private Map<String, Set<Integer>> getTopicNameRegionBrokerIdMap() {
        Map<String, Set<Integer>> map = new HashMap<>();
        map.put(REAL_TOPIC_IN_ZK, REAL_BROKER_ID_SET);
        return map;
    }

    private Map<String, List<Double>> getMaxAvgBytesInMap() {
        Map<String, List<Double>> map = new HashMap<>();
        map.put(REAL_TOPIC_IN_ZK, new ArrayList<>());
        return map;
    }

    private TopicInsufficientPartitionConfig getTopicInsufficientPartitionConfig() {
        TopicInsufficientPartitionConfig config = new TopicInsufficientPartitionConfig();
        config.setMinTopicBytesInUnitB(-1L);// 为了通过测试
        config.setMaxBytesInPerPartitionUnitB(10L);// 为了通过测试
        return config;
    }

    private TopicExpiredDO getTopicExpiredDO() {
        TopicExpiredDO topicExpiredDO = new TopicExpiredDO();
        topicExpiredDO.setTopicName(REAL_TOPIC_IN_ZK);
        topicExpiredDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        return topicExpiredDO;
    }

    private TopicExpiredConfig getTopicExpiredConfig() {
        TopicExpiredConfig config = new TopicExpiredConfig();
        config.setIgnoreClusterIdList(new ArrayList<>());
        return config;
    }

    private TopicMetricsDO getTopicMetricsDO() {
        TopicMetricsDO topicMetricsDO = new TopicMetricsDO();
        topicMetricsDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        topicMetricsDO.setTopicName(REAL_TOPIC_IN_ZK);
        topicMetricsDO.setMetrics(metrics);
        return topicMetricsDO;
    }

    private TopicInsufficientPartition getTopicInsufficientPartition() {
        ClusterDO clusterDO = getClusterDO();

        return new TopicInsufficientPartition(
                clusterDO,
                REAL_TOPIC_IN_ZK,
                null,
                null,
                null,
                null,
                new ArrayList<>(REAL_BROKER_ID_SET)
        );
    }

    @Test
    public void getRegionHotTopicsTest() {
        // 返回空集合测试
        getRegionHotTopics2EmptyTest();

        getRegionHotTopics2SuccessTest();
    }

    private void getRegionHotTopics2EmptyTest() {
        Mockito.when(configService.getByKey(Mockito.anyString(), Mockito.any())).thenReturn(null);
        Assert.assertTrue(expertService.getRegionHotTopics().isEmpty());
    }

    private void getRegionHotTopics2SuccessTest() {
        RegionTopicHotConfig config = getRegionTopicHotConfig();
        Mockito.when(configService.getByKey(Mockito.anyString(), Mockito.any())).thenReturn(config);

        ClusterDO clusterDO = getClusterDO();
        List<ClusterDO> clusterDOList = new ArrayList<>();
        clusterDOList.add(clusterDO);
        Mockito.when(clusterService.list()).thenReturn(clusterDOList);

        Map<String, Set<Integer>> map = getTopicNameRegionBrokerIdMap();
        Mockito.when(regionService.getTopicNameRegionBrokerIdMap(Mockito.anyLong())).thenReturn(map);

        Assert.assertTrue(expertService.getRegionHotTopics().stream().allMatch(hotTopic -> hotTopic.getClusterDO().getId().equals(clusterDO.getId())));

    }



    @Test
    public void getPartitionInsufficientTopicsTest() {
        // 返回空集合测试
        getPartitionInsufficientTopic2EmptyTest();

        // 成功测试
        getPartitionInsufficientTopicsSuccessTest();
    }

    private void getPartitionInsufficientTopic2EmptyTest() {
        TopicInsufficientPartitionConfig config = getTopicInsufficientPartitionConfig();
        Mockito.when(configService.getByKey(Mockito.anyString(), Mockito.any())).thenReturn(config);
        Mockito.when(clusterService.list()).thenReturn(new ArrayList<>());
        Assert.assertTrue(expertService.getPartitionInsufficientTopics().isEmpty());
    }

    private void getPartitionInsufficientTopicsSuccessTest() {
        // 先向数据库中插入
        List<TopicMetricsDO> topicMetricsDOList = new ArrayList<>();
        topicMetricsDOList.add(getTopicMetricsDO());
        topicMetricsDao.batchAdd(topicMetricsDOList);

        TopicInsufficientPartitionConfig config = getTopicInsufficientPartitionConfig();
        Mockito.when(configService.getByKey(Mockito.anyString(), Mockito.any())).thenReturn(config);

        ClusterDO clusterDO = getClusterDO();
        List<ClusterDO> clusterDOList = new ArrayList<>();
        clusterDOList.add(clusterDO);
        Mockito.when(clusterService.list()).thenReturn(clusterDOList);

        Map<String, Set<Integer>> map = getTopicNameRegionBrokerIdMap();
        Mockito.when(regionService.getTopicNameRegionBrokerIdMap(Mockito.anyLong())).thenReturn(map);

        Map<String, List<Double>> maxAvgBytesInMap = getMaxAvgBytesInMap();
        Mockito.when(topicManagerService.getTopicMaxAvgBytesIn(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyDouble())).thenReturn(maxAvgBytesInMap);

        TopicInsufficientPartition expectResult = getTopicInsufficientPartition();
        Assert.assertTrue(expertService.getPartitionInsufficientTopics().stream().allMatch(topic -> topic.getClusterDO().getId().equals(expectResult.getClusterDO().getId()) &&
                topic.getTopicName().equals(expectResult.getTopicName())));
    }

    @Test
    public void getExpiredTopicsTest() {
        // 返回空集合测试
        getExpiredTopics2EmptyTest();

        // 成功测试
        getExpiredTopics2SuccessTest();
    }

    private void getExpiredTopics2EmptyTest() {
        TopicExpiredConfig topicExpiredConfig = getTopicExpiredConfig();
        Mockito.when(configService.getByKey(Mockito.anyString(), Mockito.any())).thenReturn(topicExpiredConfig);

        Mockito.when(topicManagerService.getExpiredTopics(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Assert.assertTrue(expertService.getExpiredTopics().isEmpty());
    }

    public void getExpiredTopics2SuccessTest() {
        TopicExpiredConfig topicExpiredConfig = getTopicExpiredConfig();
        Mockito.when(configService.getByKey(Mockito.anyString(), Mockito.any())).thenReturn(topicExpiredConfig);

        TopicExpiredDO topicExpiredDO = getTopicExpiredDO();
        List<TopicExpiredDO> topicExpiredDOList = new ArrayList<>();
        topicExpiredDOList.add(topicExpiredDO);
        Mockito.when(topicManagerService.getExpiredTopics(Mockito.anyInt())).thenReturn(topicExpiredDOList);

        Assert.assertTrue(expertService.getExpiredTopics().stream().allMatch(expiredDO -> expiredDO.getClusterId().equals(topicExpiredDO.getClusterId()) &&
        expiredDO.getTopicName().equals(topicExpiredDO.getTopicName())));

    }

}
