package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaClientEnum;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicThrottledMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicThrottledMetricsDO;
import com.xiaojukeji.kafka.manager.dao.TopicThrottledMetricsDao;
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

import java.util.*;

/**
 * @author wyc
 * @date 2021/12/24
 */
public class ThrottleServiceTest extends BaseTest {

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    @Value("${test.topic.name1}")
    private String REAL_TOPIC_IN_ZK;

    @Value("${test.app.id}")
    private String KAFKA_MANAGER_APP_ID;


    private final static Set<Integer> REAL_BROKER_ID_SET = new HashSet<>();

    static {
        REAL_BROKER_ID_SET.add(1);
        REAL_BROKER_ID_SET.add(2);
    }

    @Autowired
    @InjectMocks
    private ThrottleService throttleService;

    @Mock
    private TopicThrottledMetricsDao topicThrottleDao;

    @Mock
    private JmxService jmxService;


    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    private TopicThrottledMetricsDO getTopicThrottledMetricsDO() {
        TopicThrottledMetricsDO metricsDO = new TopicThrottledMetricsDO();
        metricsDO.setAppId(KAFKA_MANAGER_APP_ID);
        metricsDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        metricsDO.setTopicName(REAL_TOPIC_IN_ZK);
        return metricsDO;
    }

    private TopicThrottledMetrics getTopicThrottledMetrics() {
        TopicThrottledMetrics metrics = new TopicThrottledMetrics();
        metrics.setClientType(KafkaClientEnum.PRODUCE_CLIENT);
        metrics.setTopicName(REAL_TOPIC_IN_ZK);
        metrics.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        return metrics;
    }

    @Test
    public void insertBatchTest() {
        // dataList为空测试
        Assert.assertEquals(throttleService.insertBatch(new ArrayList<>()), 0);

        // 成功测试
        insertBatch2SuccessTest();
    }
    private void insertBatch2SuccessTest() {
        TopicThrottledMetricsDO metricsDO = getTopicThrottledMetricsDO();
        List<TopicThrottledMetricsDO> doList = new ArrayList<>();
        doList.add(metricsDO);

        Mockito.when(topicThrottleDao.insertBatch(Mockito.anyList())).thenReturn(3);
        Assert.assertTrue(throttleService.insertBatch(doList) > 0);
    }

    @Test
    public void getTopicThrottleFromDBTest() {
        // 返回空集合测试
        getTopicThrottleFromDB2TopicThrottleDOListIsEmptyTest();

        // 成功测试
        getTopicThrottleFromDB2SuccessTest();
    }

    private void getTopicThrottleFromDB2SuccessTest() {
        TopicThrottledMetricsDO metricsDO = getTopicThrottledMetricsDO();
        List<TopicThrottledMetricsDO> metricsDOList = new ArrayList<>();
        metricsDOList.add(metricsDO);

        Mockito.when(topicThrottleDao.getTopicThrottle(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(metricsDOList);
        Assert.assertTrue(throttleService.getTopicThrottleFromDB(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK, KAFKA_MANAGER_APP_ID, new Date(), new Date()).stream().allMatch(metricsDO1 ->
                metricsDO1.getAppId().equals(metricsDO.getAppId()) &&
                metricsDO1.getClusterId().equals(metricsDO.getClusterId()) &&
                metricsDO1.getTopicName().equals(metricsDO.getTopicName())));

    }

    private void getTopicThrottleFromDB2TopicThrottleDOListIsEmptyTest() {
        Mockito.when(topicThrottleDao.getAppIdThrottle(Mockito.anyLong(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
        Assert.assertTrue(throttleService.getTopicThrottleFromDB(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK, KAFKA_MANAGER_APP_ID, new Date(), new Date()).isEmpty());
    }

    @Test
    public void getThrottledTopicsFromJmxTest() {
        // 返回空集合测试
        getThrottledTopicsFromJmx2ReturnEmptyTest();

        // 返回成功测试
        getThrottledTopicsFromJmx2SuccessTest();
    }

    private void getThrottledTopicsFromJmx2ReturnEmptyTest() {
        Assert.assertTrue(throttleService.getThrottledTopicsFromJmx(REAL_CLUSTER_ID_IN_MYSQL, REAL_BROKER_ID_SET, new ArrayList<>()).isEmpty());
    }

    private void getThrottledTopicsFromJmx2SuccessTest() {
        List<KafkaClientEnum> clientList = new ArrayList<>();
        clientList.add(KafkaClientEnum.PRODUCE_CLIENT);

        Assert.assertTrue(throttleService.getThrottledTopicsFromJmx(REAL_CLUSTER_ID_IN_MYSQL, REAL_BROKER_ID_SET, clientList).isEmpty());
    }
}
