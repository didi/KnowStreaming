package com.xiaojukeji.kafka.manager.service.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xuguang
 * @Date 2021/12/15
 */
public class AbstractHealthScoreStrategyTest extends BaseTest {

    private BrokerMetrics getBrokerMetrics() {
        BrokerMetrics brokerMetrics = new BrokerMetrics(1L, 1);
        String metrics = "{\"TotalFetchRequestsPerSecFiveMinuteRate\":4.132236103122026,\"BytesRejectedPerSecFiveMinuteRate\":0.0,\"TotalFetchRequestsPerSecFifteenMinuteRate\":1.5799208507558833,\"ProduceTotalTimeMs98thPercentile\":0.0,\"MessagesInPerSecMeanRate\":0.0,\"ProduceTotalTimeMs75thPercentile\":0.0,\"ProduceTotalTimeMs99thPercentile\":0.0,\"TotalProduceRequestsPerSecOneMinuteRate\":0.0,\"FailedProduceRequestsPerSecFifteenMinuteRate\":0.0,\"BytesInPerSecMeanRate\":0.0,\"TotalProduceRequestsPerSecFiveMinuteRate\":0.0,\"FetchConsumerTotalTimeMs999thPercentile\":0.0,\"FetchConsumerTotalTimeMs98thPercentile\":0.0,\"FetchConsumerTotalTimeMsMean\":0.0,\"FetchConsumerTotalTimeMs99thPercentile\":0.0,\"FailedFetchRequestsPerSecFifteenMinuteRate\":0.0,\"MessagesInPerSecFiveMinuteRate\":0.0,\"RequestHandlerAvgIdlePercentOneMinuteRate\":0.999221766772746,\"ProduceTotalTimeMsMean\":0.0,\"BytesInPerSecFiveMinuteRate\":0.0,\"FailedProduceRequestsPerSecMeanRate\":0.0,\"FailedFetchRequestsPerSecMeanRate\":0.0,\"FailedProduceRequestsPerSecFiveMinuteRate\":0.0,\"BytesOutPerSecFifteenMinuteRate\":0.0,\"BytesInPerSecOneMinuteRate\":0.0,\"BytesOutPerSecFiveMinuteRate\":0.0,\"HealthScore\":90,\"FailedFetchRequestsPerSecOneMinuteRate\":0.0,\"MessagesInPerSecOneMinuteRate\":0.0,\"BytesRejectedPerSecFifteenMinuteRate\":0.0,\"FailedFetchRequestsPerSecFiveMinuteRate\":0.0,\"RequestHandlerAvgIdlePercentFiveMinuteRate\":0.999803118809842,\"BytesOutPerSecOneMinuteRate\":0.0,\"ResponseQueueSizeValue\":0,\"MessagesInPerSecFifteenMinuteRate\":0.0,\"TotalProduceRequestsPerSecMeanRate\":0.0,\"BytesRejectedPerSecMeanRate\":0.0,\"TotalFetchRequestsPerSecMeanRate\":1.2674449706628523,\"NetworkProcessorAvgIdlePercentValue\":1.0,\"TotalFetchRequestsPerSecOneMinuteRate\":10.457259856316893,\"BytesInPerSecFifteenMinuteRate\":0.0,\"BytesOutPerSecMeanRate\":0.0,\"TotalProduceRequestsPerSecFifteenMinuteRate\":0.0,\"FetchConsumerTotalTimeMs50thPercentile\":0.0,\"RequestHandlerAvgIdlePercentFifteenMinuteRate\":0.9999287809186348,\"FetchConsumerTotalTimeMs95thPercentile\":0.0,\"FailedProduceRequestsPerSecOneMinuteRate\":0.0,\"CreateTime\":1638792321071,\"FetchConsumerTotalTimeMs75thPercentile\":0.0,\"ProduceTotalTimeMs999thPercentile\":0.0,\"RequestQueueSizeValue\":0,\"ProduceTotalTimeMs50thPercentile\":0.0,\"BytesRejectedPerSecOneMinuteRate\":0.0,\"RequestHandlerAvgIdlePercentMeanRate\":0.9999649184090593,\"ProduceTotalTimeMs95thPercentile\":0.0}";
        JSONObject jsonObject = JSON.parseObject(metrics);
        Map<String, Object> metricsMap = new HashMap<>();
        for (Map.Entry<String, Object> stringObjectEntry : jsonObject.entrySet()) {
            metricsMap.put(stringObjectEntry.getKey(), stringObjectEntry.getValue());
        }
        brokerMetrics.setMetricsMap(metricsMap);
        return brokerMetrics;
    }

    @Autowired
    @InjectMocks
    private AbstractHealthScoreStrategy didiHealthScoreStrategy;

    @Mock
    private JmxService jmxService;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(description = "测试计算broker健康分")
    public void calBrokerHealthScoreWithBrokerMetricsTest() {
        // brokerMetrics为空时
        calBrokerHealthScoreWithBrokerMetrics2InvideCodeTest();
        // HEALTH_SCORE_VERY_BAD
        calBrokerHealthScoreWithBrokerMetrics2HealthScoreVeryBadTest();
        // requestQueueSizeValue is Null or responseQueueSizeValue is Null
        calBrokerHealthScoreWithBrokerMetrics2requestQueueSizeValueNullTest();
        // HEALTH_SCORE_BAD
        calBrokerHealthScoreWithBrokerMetrics2HealthScoreBadTest();
        // requestHandlerAvgIdlePercentOneMinuteRate is null
        calBrokerHealthScoreWithBrokerMetrics2InvideCode3Test();
        // HEALTH_SCORE_NORMAL
        calBrokerHealthScoreWithBrokerMetrics2HealthScoreNormalTest();
        // HEALTH_SCORE_Healthy
        calBrokerHealthScoreWithBrokerMetrics2HealthScoreHealthyTest();
        // exception
        calBrokerHealthScoreWithBrokerMetrics2ExceptionTest();
    }

    private void calBrokerHealthScoreWithBrokerMetrics2InvideCodeTest() {
        Integer result1 = didiHealthScoreStrategy.calBrokerHealthScore(1L, 1, null);
        Assert.assertEquals(result1, Constant.INVALID_CODE);

        Integer result2 = didiHealthScoreStrategy.calBrokerHealthScore(1L, 1, new BrokerMetrics(1L, 1));
        Assert.assertEquals(result2, Constant.INVALID_CODE);
    }

    private void calBrokerHealthScoreWithBrokerMetrics2HealthScoreVeryBadTest() {
        BrokerMetrics brokerMetrics = getBrokerMetrics();
        Map<String, Object> metricsMap = brokerMetrics.getMetricsMap();
        metricsMap.put("FailedFetchRequestsPerSecOneMinuteRate", 0.02);
        metricsMap.put("FailedProduceRequestsPerSecOneMinuteRate", 0.02);

        Integer result = didiHealthScoreStrategy.calBrokerHealthScore(1L, 1, brokerMetrics);
        Assert.assertEquals(result, Integer.valueOf(30));
    }

    private void calBrokerHealthScoreWithBrokerMetrics2requestQueueSizeValueNullTest() {
        BrokerMetrics brokerMetrics = getBrokerMetrics();
        Map<String, Object> metricsMap = brokerMetrics.getMetricsMap();
        metricsMap.put("FailedFetchRequestsPerSecOneMinuteRate", 0.0);
        metricsMap.put("FailedProduceRequestsPerSecOneMinuteRate", 0.0);
        metricsMap.put("RequestQueueSizeValue", null);
        metricsMap.put("ResponseQueueSizeValue", null);

        Integer result = didiHealthScoreStrategy.calBrokerHealthScore(1L, 1, brokerMetrics);
        Assert.assertEquals(result, Constant.INVALID_CODE);
    }

    private void calBrokerHealthScoreWithBrokerMetrics2HealthScoreBadTest() {
        BrokerMetrics brokerMetrics = getBrokerMetrics();
        Map<String, Object> metricsMap = brokerMetrics.getMetricsMap();
        metricsMap.put("FailedFetchRequestsPerSecOneMinuteRate", 0.0);
        metricsMap.put("FailedProduceRequestsPerSecOneMinuteRate", 0.0);
        metricsMap.put("RequestQueueSizeValue", 500);
        metricsMap.put("ResponseQueueSizeValue", 500);

        Integer result = didiHealthScoreStrategy.calBrokerHealthScore(1L, 1, brokerMetrics);
        Assert.assertEquals(result, Integer.valueOf(60));
    }

    private void calBrokerHealthScoreWithBrokerMetrics2InvideCode3Test() {
        BrokerMetrics brokerMetrics = getBrokerMetrics();
        Map<String, Object> metricsMap = brokerMetrics.getMetricsMap();
        metricsMap.put("FailedFetchRequestsPerSecOneMinuteRate", 0.0);
        metricsMap.put("FailedProduceRequestsPerSecOneMinuteRate", 0.0);
        metricsMap.put("RequestQueueSizeValue", 300);
        metricsMap.put("ResponseQueueSizeValue", 300);
        metricsMap.put("RequestHandlerAvgIdlePercentOneMinuteRate", null);
        metricsMap.put("NetworkProcessorAvgIdlePercentValue", null);

        Integer result = didiHealthScoreStrategy.calBrokerHealthScore(1L, 1, brokerMetrics);
        Assert.assertEquals(result, Constant.INVALID_CODE);
    }

    private void calBrokerHealthScoreWithBrokerMetrics2HealthScoreNormalTest() {
        BrokerMetrics brokerMetrics = getBrokerMetrics();
        Map<String, Object> metricsMap = brokerMetrics.getMetricsMap();
        metricsMap.put("FailedFetchRequestsPerSecOneMinuteRate", 0.0);
        metricsMap.put("FailedProduceRequestsPerSecOneMinuteRate", 0.0);
        metricsMap.put("RequestQueueSizeValue", 300);
        metricsMap.put("ResponseQueueSizeValue", 300);
        metricsMap.put("RequestHandlerAvgIdlePercentOneMinuteRate", 0.0);
        metricsMap.put("NetworkProcessorAvgIdlePercentValue", 0.0);

        Integer result = didiHealthScoreStrategy.calBrokerHealthScore(1L, 1, brokerMetrics);
        Assert.assertEquals(result, Integer.valueOf(90));
    }

    private void calBrokerHealthScoreWithBrokerMetrics2HealthScoreHealthyTest() {
        BrokerMetrics brokerMetrics = getBrokerMetrics();
        Map<String, Object> metricsMap = brokerMetrics.getMetricsMap();
        metricsMap.put("FailedFetchRequestsPerSecOneMinuteRate", 0.0);
        metricsMap.put("FailedProduceRequestsPerSecOneMinuteRate", 0.0);
        metricsMap.put("RequestQueueSizeValue", 300);
        metricsMap.put("ResponseQueueSizeValue", 300);
        metricsMap.put("RequestHandlerAvgIdlePercentOneMinuteRate", 100.0);
        metricsMap.put("NetworkProcessorAvgIdlePercentValue", 100.0);

        Integer result = didiHealthScoreStrategy.calBrokerHealthScore(1L, 1, brokerMetrics);
        Assert.assertEquals(result, Integer.valueOf(100));
    }

    private void calBrokerHealthScoreWithBrokerMetrics2ExceptionTest() {
        BrokerMetrics brokerMetrics = getBrokerMetrics();
        Map<String, Object> metricsMap = brokerMetrics.getMetricsMap();
        metricsMap.put("FailedFetchRequestsPerSecOneMinuteRate", 0.0);
        metricsMap.put("FailedProduceRequestsPerSecOneMinuteRate", 0.0);
        metricsMap.put("RequestQueueSizeValue", 300);
        metricsMap.put("ResponseQueueSizeValue", 300);
        // Integer转Double出现异常
        metricsMap.put("RequestHandlerAvgIdlePercentOneMinuteRate", 100);
        metricsMap.put("NetworkProcessorAvgIdlePercentValue", 100);

        Integer result = didiHealthScoreStrategy.calBrokerHealthScore(1L, 1, brokerMetrics);
        Assert.assertEquals(result, Constant.INVALID_CODE);
    }

    @Test(description = "测试计算broker健康分")
    public void calBrokerHealthScoreTest() {
        // BrokerMetadata is Null
        calBrokerHealthScore2BrokerMetadataIsNullTest();
        // INVALID_CODE
        calBrokerHealthScore2InvideCodeTest();
        // success
        calBrokerHealthScore2SuccessTest();
    }

    private void calBrokerHealthScore2BrokerMetadataIsNullTest() {
        Integer result = didiHealthScoreStrategy.calBrokerHealthScore(1L, 100);
        Assert.assertEquals(result, Integer.valueOf(100));
    }

    private void calBrokerHealthScore2InvideCodeTest() {
        Mockito.when(jmxService.getBrokerMetrics(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);
        Integer result = didiHealthScoreStrategy.calBrokerHealthScore(1L, 1);
        Assert.assertEquals(result, Constant.INVALID_CODE);
    }

    private void calBrokerHealthScore2SuccessTest() {
        BrokerMetrics brokerMetrics = getBrokerMetrics();
        Map<String, Object> metricsMap = brokerMetrics.getMetricsMap();
        metricsMap.put("FailedFetchRequestsPerSecOneMinuteRate", 0.0);
        metricsMap.put("FailedProduceRequestsPerSecOneMinuteRate", 0.0);
        metricsMap.put("RequestQueueSizeValue", 500);
        metricsMap.put("ResponseQueueSizeValue", 500);

        Mockito.when(jmxService.getBrokerMetrics(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(brokerMetrics);
        Integer result = didiHealthScoreStrategy.calBrokerHealthScore(1L, 1);
        Assert.assertEquals(result, Integer.valueOf(60));
    }

    @Test(description = "测试计算健康分")
    public void calTopicHealthScore() {
        // TopicMetadata为空
        calTopicHealthScore2InvadeCodeTest();
        // 测试计算topic健康分成功
        calTopicHealthScore2SuccessTest();
    }

    private void calTopicHealthScore2InvadeCodeTest() {
        Integer result = didiHealthScoreStrategy.calTopicHealthScore(1L, "xxx");
        Assert.assertEquals(result, Constant.INVALID_CODE);
    }

    private void calTopicHealthScore2SuccessTest() {
        BrokerMetrics brokerMetrics = getBrokerMetrics();
        Map<String, Object> metricsMap = brokerMetrics.getMetricsMap();
        metricsMap.put("FailedFetchRequestsPerSecOneMinuteRate", 0.0);
        metricsMap.put("FailedProduceRequestsPerSecOneMinuteRate", 0.0);
        metricsMap.put("RequestQueueSizeValue", 500);
        metricsMap.put("ResponseQueueSizeValue", 500);

        Mockito.when(jmxService.getBrokerMetrics(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(brokerMetrics);

        Integer result = didiHealthScoreStrategy.calTopicHealthScore(1L, "xgTest");
        Assert.assertNotEquals(result, Constant.INVALID_CODE);
    }

}
