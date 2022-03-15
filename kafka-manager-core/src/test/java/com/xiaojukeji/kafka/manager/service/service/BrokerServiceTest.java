package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.BrokerBasicDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.BrokerOverviewDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.TopicDiskLocation;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BrokerDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BrokerMetricsDO;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.PartitionState;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.apache.kafka.common.TopicPartition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

/**
 * @author xuguang
 * @Date 2021/12/10
 */
public class BrokerServiceTest extends BaseTest {
    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    @Value("${test.broker.id1}")
    private Integer REAL_BROKER_ID_IN_ZK;

    @Value("${test.sasl-plaintext}")
    private String END_POINTS_IN_BROKER;

    @Autowired
    @InjectMocks
    private BrokerService brokerService;

    @Mock
    private JmxService jmxService;

    @Mock
    private TopicService topicService;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private BrokerDO getBrokerDO() {
        BrokerDO brokerDO = new BrokerDO();
        brokerDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        brokerDO.setBrokerId(100);
        brokerDO.setHost("127.0.0.1");
        brokerDO.setPort(9093);
        brokerDO.setTimestamp(1638605696062L);
        brokerDO.setMaxAvgBytesIn(0d);
        brokerDO.setStatus(0);
        brokerDO.setGmtCreate(new Date(1638605696062L));
        brokerDO.setGmtModify(new Date(1638605696062L));
        return brokerDO;
    }

    private BrokerMetadata getBrokerMetadata() {
        BrokerMetadata brokerMetadata = new BrokerMetadata();
        brokerMetadata.setBrokerId(REAL_BROKER_ID_IN_ZK);
        brokerMetadata.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        brokerMetadata.setHost("127.0.0.1");
        brokerMetadata.setPort(9092);
        brokerMetadata.setEndpoints(Arrays.asList(END_POINTS_IN_BROKER));
        brokerMetadata.setTimestamp(1638605696062L);
        brokerMetadata.setJmxPort(9999);
        brokerMetadata.setRack("CY");
        brokerMetadata.setVersion("2");
        return brokerMetadata;
    }

    private TopicDiskLocation getTopicDiskLocation() {
        TopicDiskLocation topicDiskLocation = new TopicDiskLocation();
        topicDiskLocation.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        topicDiskLocation.setBrokerId(1);
        topicDiskLocation.setTopicName("testTopic");
        topicDiskLocation.setDiskName("disk");
        topicDiskLocation.setLeaderPartitions(new ArrayList<>());
        topicDiskLocation.setFollowerPartitions(Arrays.asList(0));
        topicDiskLocation.setUnderReplicatedPartitions(new ArrayList<>());
        topicDiskLocation.setUnderReplicated(false);

        return topicDiskLocation;
    }

    private TopicPartition getTopicPartition() {
        TopicPartition topicPartition = new TopicPartition("testTopic", 0);
        return topicPartition;
    }

    private Map<TopicPartition, String> getDiskNameMap() {
        Map<TopicPartition, String> diskNameMap = new HashMap<>();
        TopicPartition topicPartition = getTopicPartition();
        diskNameMap.put(topicPartition, "disk");
        return diskNameMap;
    }

    private PartitionState getPartitionState() {
        PartitionState partitionState = new PartitionState();
        return partitionState;
    }

    private Map<String, List<PartitionState>> getStateMap() {
        PartitionState partitionState = getPartitionState();
        Map<String, List<PartitionState>> stateMap = new HashMap<>();
        stateMap.put("string", Arrays.asList(partitionState));
        return stateMap;
    }

    public BrokerMetrics getBrokerMetrics() {
        BrokerMetrics brokerMetrics = new BrokerMetrics(1L, 1);
        Map<String, Object> metricsMap = new HashMap<>();
        metricsMap.put("PartitionCountValue", 100);
        metricsMap.put("LeaderCountValue", 100);
        brokerMetrics.setMetricsMap(metricsMap);
        return brokerMetrics;
    }

    @Test
    public void getBrokerVersionTest() {
        String version = "1.4";
        Mockito.when(jmxService.getBrokerVersion(Mockito.anyLong(), Mockito.anyInt())).thenReturn(version);

        String brokerVersion = brokerService.getBrokerVersion(1L, 1);
        Assert.assertNotNull(brokerVersion);
        Assert.assertEquals(brokerVersion, version);
    }

    @Test(description = "根据Cluster和brokerId获取broker的具体信息测试")
    public void getBrokerBasicDTO() {
        // 测试结果为null
        getBrokerBasicDTO2nullTest();
        // 获取的brokerMetrics为空
        getBrokerBasicDTO2brokerMetricsNullTest();
        // 获取的brokerMetrics不为空
        getBrokerBasicDTO2brokerMetricsNotNullTest();
    }

    private void getBrokerBasicDTO2nullTest() {
        BrokerBasicDTO result1 = brokerService.getBrokerBasicDTO(null, 1);
        Assert.assertNull(result1);

        BrokerBasicDTO result2 = brokerService.getBrokerBasicDTO(1L, null);
        Assert.assertNull(result2);

        BrokerBasicDTO result3 = brokerService.getBrokerBasicDTO(100L, 100);
        Assert.assertNull(result3);
    }

    private void getBrokerBasicDTO2brokerMetricsNullTest() {
        BrokerBasicDTO result1 = brokerService.getBrokerBasicDTO(1L, 1);
        Assert.assertNotNull(result1);
        Assert.assertNull(result1.getPartitionCount());
        Assert.assertNull(result1.getLeaderCount());
    }

    private void getBrokerBasicDTO2brokerMetricsNotNullTest() {
        Mockito.when(jmxService.getBrokerMetrics(
                Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(getBrokerMetrics());

        BrokerBasicDTO result1 = brokerService.getBrokerBasicDTO(1L, 1);
        Assert.assertNotNull(result1);
        Assert.assertNotNull(result1.getPartitionCount());
        Assert.assertNotNull(result1.getLeaderCount());
    }

    @Test
    public void getBrokerTopicLocationTest() {
        Map<TopicPartition, String> diskNameMap = getDiskNameMap();
        Mockito.when(jmxService.getBrokerTopicLocation(Mockito.any(), Mockito.any())).thenReturn(diskNameMap);
        Map<String, List<PartitionState>> stateMap = getStateMap();
        Mockito.when(topicService.getTopicPartitionState(Mockito.any(), Mockito.any())).thenReturn(stateMap);
        TopicDiskLocation topicDiskLocation = getTopicDiskLocation();
        List<TopicDiskLocation> expectedResult = Arrays.asList(topicDiskLocation);
        List<TopicDiskLocation> actualResult = brokerService.getBrokerTopicLocation(1L, 1);
        Assert.assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test(description = "计算Broker的峰值均值流量测试")
    public void calBrokerMaxAvgBytesInTest() {
        // 参数异常
        calBrokerMaxAvgBytesIn2ParamIllegalTest();
        // 获取的指标为空
        calBrokerMaxAvgBytesIn2ZeroTest();
        // 整个流程
        calBrokerMaxAvgBytesIn2Success();
    }

    private void calBrokerMaxAvgBytesIn2ParamIllegalTest() {
        Double result1 = brokerService.calBrokerMaxAvgBytesIn(null, 1, 1, new Date(), new Date());
        Assert.assertEquals(result1, -1.0);
        Double result2 = brokerService.calBrokerMaxAvgBytesIn(1L, null, 1, new Date(), new Date());
        Assert.assertEquals(result2, -1.0);
        Double result3 = brokerService.calBrokerMaxAvgBytesIn(1L, 1, null, new Date(), new Date());
        Assert.assertEquals(result3, -1.0);
        Double result4 = brokerService.calBrokerMaxAvgBytesIn(1L, 1, 1, null, new Date());
        Assert.assertEquals(result4, -1.0);
        Double result5 = brokerService.calBrokerMaxAvgBytesIn(1L, 1, 1, new Date(), null);
        Assert.assertEquals(result5, -1.0);
    }

    private void calBrokerMaxAvgBytesIn2ZeroTest() {
        Double result = brokerService.calBrokerMaxAvgBytesIn(1L, 100, 100, new Date(), new Date());
        Assert.assertEquals(result, 0.0);
    }

    private void calBrokerMaxAvgBytesIn2Success() {
        // 此测试需要brokerId=1的broker上有真实的流量
        long startTime = 0L;
        long endTime = new Date().getTime();
        Double result = brokerService.calBrokerMaxAvgBytesIn(
                1L, 1, 2, new Date(startTime), new Date(endTime));
        Assert.assertTrue(result > 0.0);
    }

    @Test(description = "获取BrokerMetrics信息测试，单个broker")
    public void getBrokerMetricsFromJmxTest() {
        // 参数错误
        getBrokerMetricsFromJmx2ParamIllegalTest();
        // 返回为null
        getBrokerMetricsFromJmx2nullTest();
        // 获取成功
        getBrokerMetricsFromJmx2SuccessTest();
    }

    private void getBrokerMetricsFromJmx2ParamIllegalTest() {
        BrokerMetrics result1 = brokerService.getBrokerMetricsFromJmx(null, 1, 200);
        Assert.assertNull(result1);

        BrokerMetrics result3 = brokerService.getBrokerMetricsFromJmx(1L, 1, null);
        Assert.assertNull(result3);
    }

    private void getBrokerMetricsFromJmx2nullTest() {
        BrokerMetrics result1 = brokerService.getBrokerMetricsFromJmx(1L, 1, 200);
        Assert.assertNull(result1);
    }

    private void getBrokerMetricsFromJmx2SuccessTest() {
        Mockito.when(jmxService.getBrokerMetrics(
                Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(new BrokerMetrics(1L, 1));
        BrokerMetrics result1 = brokerService.getBrokerMetricsFromJmx(1L, 1, 200);
        Assert.assertNotNull(result1);
        Assert.assertEquals(Optional.ofNullable(result1.getClusterId()), Optional.ofNullable(1L));
        Assert.assertEquals(Optional.ofNullable(result1.getBrokerId()), Optional.ofNullable(1));
    }

    @Test(description = "获取BrokerMetrics信息测试，多个broker")
    public void getBrokerMetricsFromJmxWithMoreBrokersTest() {
        Mockito.when(jmxService.getBrokerMetrics(
                Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(new BrokerMetrics(1L, 1));

        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
        List<BrokerMetrics> result = brokerService.getBrokerMetricsFromJmx(1L, set, 200);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.stream().allMatch(brokerMetric ->
                brokerMetric.getClusterId().equals(1L)));
    }

    @Test(description = "获取Broker列表信息")
    public void getBrokerOverviewListTest() {
        // brokerIdSet为空时
        getBrokerOverviewList2BrokerIdSetIsNullTest();
        // brokerIdSet不为空时
        getBrokerOverviewList2BrokerIdSetNotNullTest();
    }

    private void getBrokerOverviewList2BrokerIdSetIsNullTest() {
        List<BrokerOverviewDTO> brokerOverviewList = brokerService.getBrokerOverviewList(1L, null);
        Assert.assertFalse(brokerOverviewList.isEmpty());
        Assert.assertTrue(brokerOverviewList.stream().allMatch(brokerOverviewDTO ->
                brokerOverviewDTO.getPort().equals(9093)));
    }

    private void getBrokerOverviewList2BrokerIdSetNotNullTest() {
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        List<BrokerOverviewDTO> brokerOverviewList = brokerService.getBrokerOverviewList(1L, set);
        Assert.assertFalse(brokerOverviewList.isEmpty());
        Assert.assertTrue(brokerOverviewList.stream().allMatch(brokerOverviewDTO ->
                brokerOverviewDTO.getPort().equals(9093)));
    }


}
