package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.LogicalClusterMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BrokerMetricsDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.KafkaBillDO;
import com.xiaojukeji.kafka.manager.dao.KafkaBillDao;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author xuguang
 * @Date 2021/12/14
 */
public class KafkaBillServiceTest extends BaseTest {

    @Autowired
    @InjectMocks
    private KafkaBillService kafkaBillService;

    @Mock
    private KafkaBillDao kafkaBillDao;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    @Value("${test.admin}")
    private String ADMIN;

    private KafkaBillDO getKafkaBillDO() {
        KafkaBillDO kafkaBillDO = new KafkaBillDO();
        kafkaBillDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        kafkaBillDO.setCost(100.0d);
        kafkaBillDO.setGmtCreate(new Date(1638605696062L));
        kafkaBillDO.setGmtDay("10");
        kafkaBillDO.setPrincipal(ADMIN);
        kafkaBillDO.setQuota(1000.0d);
        kafkaBillDO.setTopicName("moduleTest");
        return kafkaBillDO;
    }

    private BrokerMetricsDO getBrokerMetricsDO() {
        BrokerMetricsDO metricsDO = new BrokerMetricsDO();
        metricsDO.setMetrics("");
        return metricsDO;
    }

    @Test()
    public void replaceTest() {
        KafkaBillDO kafkaBillDO = getKafkaBillDO();
        // 插入成功
        replace2SuccessTest(kafkaBillDO);
        // 插入失败
        replace2ExceptionTest(kafkaBillDO);
    }

    private void replace2SuccessTest(KafkaBillDO kafkaBillDO) {
        Mockito.when(kafkaBillDao.replace(Mockito.any())).thenReturn(1);
        int result = kafkaBillService.replace(kafkaBillDO);
        Assert.assertEquals(result, 1);
    }

    private void replace2ExceptionTest(KafkaBillDO kafkaBillDO) {
        Mockito.when(kafkaBillDao.replace(Mockito.any())).thenThrow(RuntimeException.class);
        int result = kafkaBillService.replace(kafkaBillDO);
        Assert.assertEquals(result, 0);
    }

    @Test()
    public void getByTopicNameTest() {
        KafkaBillDO kafkaBillDO = getKafkaBillDO();
        // 查询成功
        getByTopicName2SuccessTest(kafkaBillDO);
        // 查询异常
        getByTopicName2ExceptionTest();

    }

    private void getByTopicName2SuccessTest(KafkaBillDO kafkaBillDO) {
        Mockito.when(kafkaBillDao.getByTopicName(
                Mockito.anyLong(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(kafkaBillDO));
        List<KafkaBillDO> result = kafkaBillService.getByTopicName(1L, "moudleTest", new Date(0L), new Date());
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(result.stream().allMatch(kafkaBillDO1 ->
                kafkaBillDO1.getTopicName().equals(kafkaBillDO.getTopicName()) &&
                        kafkaBillDO1.getClusterId().equals(kafkaBillDO.getClusterId())));
    }

    private void getByTopicName2ExceptionTest() {
        Mockito.when(kafkaBillDao.getByTopicName(
                Mockito.anyLong(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenThrow(RuntimeException.class);
        List<KafkaBillDO> result = kafkaBillService.getByTopicName(1L, "moudleTest", new Date(0L), new Date());
        Assert.assertTrue(result.isEmpty());
    }

    @Test()
    public void getByPrincipalTest() {
        KafkaBillDO kafkaBillDO = getKafkaBillDO();
        // 查询成功
        getByPrincipal2SuccessTest(kafkaBillDO);
        // 查询失败
        getByPrincipal2ExceptionTest();
    }

    private void getByPrincipal2SuccessTest(KafkaBillDO kafkaBillDO) {
        Mockito.when(kafkaBillDao.getByPrincipal(
                Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(kafkaBillDO));
        List<KafkaBillDO> result = kafkaBillService.getByPrincipal("admin", new Date(0L), new Date());
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(result.stream().allMatch(kafkaBillDO1 ->
                kafkaBillDO1.getTopicName().equals(kafkaBillDO.getTopicName()) &&
                        kafkaBillDO1.getClusterId().equals(kafkaBillDO.getClusterId())));
    }

    private void getByPrincipal2ExceptionTest() {
        Mockito.when(kafkaBillDao.getByPrincipal(
                Mockito.anyString(), Mockito.any(), Mockito.any())).thenThrow(RuntimeException.class);
        List<KafkaBillDO> result = kafkaBillService.getByPrincipal("admin", new Date(0L), new Date());
        Assert.assertTrue(result.isEmpty());
    }

    @Test()
    public void getByTimeBetweenTest() {
        KafkaBillDO kafkaBillDO = getKafkaBillDO();
        // 查询成功
        getByTimeBetween2SuccessTest(kafkaBillDO);
        // 查询失败
        getByTimeBetween2ExceptionTest();
    }

    private void getByTimeBetween2SuccessTest(KafkaBillDO kafkaBillDO) {
        Mockito.when(kafkaBillDao.getByTimeBetween(
                Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(kafkaBillDO));
        List<KafkaBillDO> result = kafkaBillService.getByTimeBetween(new Date(0L), new Date());
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(result.stream().allMatch(kafkaBillDO1 ->
                kafkaBillDO1.getTopicName().equals(kafkaBillDO.getTopicName()) &&
                        kafkaBillDO1.getClusterId().equals(kafkaBillDO.getClusterId())));
    }

    private void getByTimeBetween2ExceptionTest() {
        Mockito.when(kafkaBillDao.getByTimeBetween(
                Mockito.any(), Mockito.any())).thenThrow(RuntimeException.class);
        List<KafkaBillDO> result = kafkaBillService.getByTimeBetween(new Date(0L), new Date());
        Assert.assertTrue(result.isEmpty());
    }

    @Test()
    public void getByGmtDayTest() {
        KafkaBillDO kafkaBillDO = getKafkaBillDO();
        // 查询成功
        getByGmtDay2SuccessTest(kafkaBillDO);
        // 查询失败
        getByGmtDay2ExceptionTest();
    }

    private void getByGmtDay2SuccessTest(KafkaBillDO kafkaBillDO) {
        Mockito.when(kafkaBillDao.getByGmtDay(
                Mockito.anyString())).thenReturn(Arrays.asList(kafkaBillDO));
        List<KafkaBillDO> result = kafkaBillService.getByGmtDay("10");
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(result.stream().allMatch(kafkaBillDO1 ->
                kafkaBillDO1.getTopicName().equals(kafkaBillDO.getTopicName()) &&
                        kafkaBillDO1.getClusterId().equals(kafkaBillDO.getClusterId())));
    }

    private void getByGmtDay2ExceptionTest() {
        Mockito.when(kafkaBillDao.getByGmtDay(
                Mockito.anyString())).thenThrow(RuntimeException.class);
        List<KafkaBillDO> result = kafkaBillService.getByGmtDay("10");
        Assert.assertTrue(result.isEmpty());
    }

}
