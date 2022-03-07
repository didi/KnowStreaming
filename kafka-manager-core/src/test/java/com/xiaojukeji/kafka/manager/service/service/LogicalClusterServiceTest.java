package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.LogicalCluster;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.LogicalClusterMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.dao.LogicalClusterDao;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import org.apache.kafka.clients.Metadata;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

/**
 * @author xuguang
 * @Date 2021/12/10
 */
public class LogicalClusterServiceTest extends BaseTest {

    private final static Long INVALID_CLUSTER_ID = -1L;

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    @Autowired
    @InjectMocks
    private LogicalClusterService logicalClusterService;

    @Mock
    private LogicalClusterDao logicalClusterDao;

    @Mock
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Mock
    private AppService appService;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider(name = "provideLogicalClusterDO")
    public Object[][] provideLogicalClusterDO() {
        LogicalClusterDO logicalClusterDO = new LogicalClusterDO();
        logicalClusterDO.setId(INVALID_CLUSTER_ID);
        logicalClusterDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        logicalClusterDO.setIdentification("moduleTestLogicalCluster");
        logicalClusterDO.setName("moduleTestLogicalCluster");
        logicalClusterDO.setMode(1);
        logicalClusterDO.setRegionList("2,3");
        logicalClusterDO.setAppId("moduleTest");
        logicalClusterDO.setGmtCreate(new Date());
        logicalClusterDO.setGmtModify(new Date());
        return new Object[][] {{logicalClusterDO}};
    }

    private LogicalClusterDO getLogicalClusterDO() {
        LogicalClusterDO logicalClusterDO = new LogicalClusterDO();
        logicalClusterDO.setId(INVALID_CLUSTER_ID);
        logicalClusterDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        logicalClusterDO.setIdentification("moduleTestLogicalCluster");
        logicalClusterDO.setName("moduleTestLogicalCluster");
        logicalClusterDO.setMode(0);
        logicalClusterDO.setRegionList("2,3");
        logicalClusterDO.setAppId("");
        logicalClusterDO.setGmtCreate(new Date());
        logicalClusterDO.setGmtModify(new Date());
        return logicalClusterDO;
    }

    public AppDO getAppDO() {
        AppDO appDO = new AppDO();
        appDO.setId(4L);
        appDO.setAppId("moduleTest");
        appDO.setName("moduleTestApp");
        appDO.setPassword("moduleTestApp");
        appDO.setType(1);
        appDO.setApplicant("admin");
        appDO.setPrincipals("module");
        appDO.setDescription("moduleTestApp");
        appDO.setCreateTime(new Date(1638786493173L));
        appDO.setModifyTime(new Date(1638786493173L));
        return appDO;
    }

    @Test(description = "测试创建逻辑集群")
    public void createLogicalCluster() {
        // 创建逻辑集群时参数错误
        createLogicalCluster2paramIllegalTest();
        // 创建逻辑集群时,region已使用
        createLogicalCluster2existRegionAlreadyInUseTest();
        // 创建逻辑集群时,物理集群不存在
        createLogicalCluster2PhysicalClusterNotExistTest();
        // 创建逻辑集群时,不存在region已使用
        createLogicalCluster2NotexistRegionAlreadyInUseTest();
        // 创建逻辑集群成功
        createLogicalCluster2SuccessTest();
    }

    private void createLogicalCluster2paramIllegalTest() {
        ResultStatus result = logicalClusterService.createLogicalCluster(null);
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void createLogicalCluster2existRegionAlreadyInUseTest() {
        LogicalClusterDO logicalClusterDO = getLogicalClusterDO();
        // 物理集群Id为null
        logicalClusterDO.setClusterId(null);
        ResultStatus result1 = logicalClusterService.createLogicalCluster(logicalClusterDO);
        Assert.assertEquals(result1.getCode(), ResultStatus.RESOURCE_ALREADY_USED.getCode());

        // regionList为空情况
        logicalClusterDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        logicalClusterDO.setRegionList("");
        ResultStatus result2 = logicalClusterService.createLogicalCluster(logicalClusterDO);
        Assert.assertEquals(result2.getCode(), ResultStatus.RESOURCE_ALREADY_USED.getCode());

        // region已存在使用
        logicalClusterDao.insert(logicalClusterDO);
        ResultStatus result3 = logicalClusterService.createLogicalCluster(logicalClusterDO);
        Assert.assertEquals(result3.getCode(), ResultStatus.RESOURCE_ALREADY_USED.getCode());
    }

    private void createLogicalCluster2PhysicalClusterNotExistTest() {
        LogicalClusterDO logicalClusterDO = getLogicalClusterDO();
        Mockito.when(logicalClusterDao.insert(Mockito.any())).thenReturn(1);
        // 不存在该物理集群情况
        logicalClusterDO.setClusterId(INVALID_CLUSTER_ID);
        ResultStatus result1 = logicalClusterService.createLogicalCluster(logicalClusterDO);
        Assert.assertNotEquals(result1.getCode(), ResultStatus.RESOURCE_ALREADY_USED.getCode());
        Assert.assertEquals(result1.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void createLogicalCluster2NotexistRegionAlreadyInUseTest() {
        LogicalClusterDO logicalClusterDO = getLogicalClusterDO();
        Mockito.when(logicalClusterDao.insert(Mockito.any())).thenReturn(1);
        // region没有存在使用
        ResultStatus result2 = logicalClusterService.createLogicalCluster(logicalClusterDO);
        Assert.assertNotEquals(result2.getCode(), ResultStatus.RESOURCE_ALREADY_USED.getCode());
        Assert.assertEquals(result2.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "创建逻辑集群时,不存在region已使用(键重复)")
    private void createLogicalCluster2DuplicateKeyTest() {
        LogicalClusterDO logicalClusterDO = getLogicalClusterDO();
        Mockito.when(logicalClusterDao.insert(Mockito.any())).thenThrow(DuplicateKeyException.class);
        logicalClusterDO.setRegionList("100");
        ResultStatus result3 = logicalClusterService.createLogicalCluster(logicalClusterDO);
        Assert.assertEquals(result3.getCode(), ResultStatus.RESOURCE_ALREADY_EXISTED.getCode());
    }

    private void createLogicalCluster2SuccessTest() {
        LogicalClusterDO logicalClusterDO = getLogicalClusterDO();
        Mockito.when(logicalClusterDao.insert(Mockito.any())).thenReturn(1);

        ResultStatus result3 = logicalClusterService.createLogicalCluster(logicalClusterDO);
        Assert.assertEquals(result3.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试删除集群")
    public void deleteByIdTest() {
        // 删除集群成功
        deleteById2SuccessTest();
        // 删除集群时参数错误
        deleteById2paramIllegalTest();
        // 删除集群时无该集群
        deleteById2ResourceNotExistTest();
        // 删除集群时，mysqlError
        deleteById2MysqlErrorTest();
    }

    private void deleteById2paramIllegalTest() {
        ResultStatus resultStatus = logicalClusterService.deleteById(null);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void deleteById2ResourceNotExistTest() {
        Mockito.when(logicalClusterDao.deleteById(Mockito.anyLong())).thenReturn(-1);

        ResultStatus resultStatus = logicalClusterService.deleteById(INVALID_CLUSTER_ID);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.RESOURCE_NOT_EXIST.getCode());
    }

    private void deleteById2MysqlErrorTest() {
        Mockito.when(logicalClusterDao.deleteById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        ResultStatus resultStatus = logicalClusterService.deleteById(7L);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.MYSQL_ERROR.getCode());
    }

    private void deleteById2SuccessTest() {
        Mockito.when(logicalClusterDao.deleteById(Mockito.anyLong())).thenReturn(1);
        ResultStatus resultStatus = logicalClusterService.deleteById(7L);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(dataProvider = "provideLogicalClusterDO", description = "修改集群时参数错误")
    public void updateById2paramIllegalTest(LogicalClusterDO logicalClusterDO) {
        logicalClusterDO.setId(null);
        ResultStatus resultStatus = logicalClusterService.updateById(logicalClusterDO);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());

        logicalClusterDO = null;
        ResultStatus resultStatus2 = logicalClusterService.updateById(logicalClusterDO);
        Assert.assertEquals(resultStatus2.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    @Test(dataProvider = "provideLogicalClusterDO", description = "修改集群时无对应逻辑集群")
    public void updateById2ResourceNotExistTest(LogicalClusterDO logicalClusterDO) {
        logicalClusterDO.setId(INVALID_CLUSTER_ID);
        ResultStatus resultStatus2 = logicalClusterService.updateById(logicalClusterDO);
        Assert.assertEquals(resultStatus2.getCode(), ResultStatus.RESOURCE_NOT_EXIST.getCode());
    }

    @Test(dataProvider = "provideLogicalClusterDO", description = "修改集群时,region已在使用")
    public void updateById2existRegionAlreadyInUseTest(LogicalClusterDO logicalClusterDO) {
        Mockito.when(logicalClusterDao.getById(Mockito.anyLong())).thenReturn(logicalClusterDO);

        // 物理集群Id为null
        logicalClusterDO.setClusterId(null);
        ResultStatus result1 = logicalClusterService.updateById(logicalClusterDO);
        Assert.assertEquals(result1.getCode(), ResultStatus.RESOURCE_ALREADY_USED.getCode());

        // regionList为空情况
        logicalClusterDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        logicalClusterDO.setRegionList("");
        ResultStatus result2 = logicalClusterService.updateById(logicalClusterDO);
        Assert.assertEquals(result2.getCode(), ResultStatus.RESOURCE_ALREADY_USED.getCode());

        // region已存在使用
        logicalClusterDao.insert(logicalClusterDO);
        ResultStatus result3 = logicalClusterService.updateById(logicalClusterDO);
        Assert.assertEquals(result3.getCode(), ResultStatus.RESOURCE_ALREADY_USED.getCode());
    }

    @Test(dataProvider = "provideLogicalClusterDO", description = "修改集群成功")
    public void updateById2SuccessTest(LogicalClusterDO logicalClusterDO) {
        Mockito.when(logicalClusterDao.updateById(Mockito.any())).thenReturn(1);
        Mockito.when(logicalClusterDao.getById(Mockito.anyLong())).thenReturn(logicalClusterDO);

        ResultStatus result3 = logicalClusterService.updateById(logicalClusterDO);
        Assert.assertEquals(result3.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(dataProvider = "provideLogicalClusterDO", description = "测试获取所有逻辑集群")
    public void listAllTest(LogicalClusterDO logicalClusterDO) {
        Mockito.when(logicalClusterDao.listAll()).thenReturn(Arrays.asList(logicalClusterDO));

        List<LogicalClusterDO> logicalClusterDOS = logicalClusterService.listAll();
        Assert.assertFalse(logicalClusterDOS.isEmpty());
        Assert.assertTrue(logicalClusterDOS.stream().allMatch(logicalClusterDO1 ->
                logicalClusterDO1.getIdentification().equals(logicalClusterDO.getIdentification())));
    }

    @Test(dataProvider = "provideLogicalClusterDO", description = "从缓存中获取所有的逻辑集群")
    public void getAllLogicalClusterTest(LogicalClusterDO logicalClusterDO) {
        // 从缓存中获取所有的逻辑集群为空
        getAllLogicalCluster2NullTest(logicalClusterDO);
        // 从缓存中获取所有的逻辑集群不为空
        getAllLogicalCluster2NotNullTest(logicalClusterDO);
    }

    @Test(dataProvider = "provideLogicalClusterDO", description = "从缓存中获取所有的逻辑集群为空")
    private void getAllLogicalCluster2NullTest(LogicalClusterDO logicalClusterDO) {
        Mockito.when(logicalClusterMetadataManager.getLogicalClusterList()).thenReturn(Collections.emptyList());

        List<LogicalCluster> allLogicalCluster = logicalClusterService.getAllLogicalCluster();
        Assert.assertNotNull(allLogicalCluster);
        Assert.assertTrue(allLogicalCluster.isEmpty());
    }

    @Test(dataProvider = "provideLogicalClusterDO", description = "从缓存中获取所有的逻辑集群不为空")
    private void getAllLogicalCluster2NotNullTest(LogicalClusterDO logicalClusterDO) {
        Mockito.when(logicalClusterMetadataManager.getLogicalClusterList()).thenReturn(Arrays.asList(logicalClusterDO));

        List<LogicalCluster> allLogicalCluster = logicalClusterService.getAllLogicalCluster();
        Assert.assertNotNull(allLogicalCluster);
        Assert.assertEquals(allLogicalCluster.get(0).getLogicalClusterIdentification(), logicalClusterDO.getIdentification());
    }

    @Test(dataProvider = "provideLogicalClusterDO", description = "获取逻辑集群信息测试")
    public void getLogicalClusterTest(LogicalClusterDO logicalClusterDO) {
        // 获取逻辑集群信息失败
        getLogicalCluster2NullTest();
        // 测试获取逻辑集群成功
        getLogicalCluster2SuccessTest(logicalClusterDO);
    }

    private void getLogicalCluster2NullTest() {
        LogicalCluster logicalCluster = logicalClusterService.getLogicalCluster(INVALID_CLUSTER_ID);
        Assert.assertNull(logicalCluster);
    }

    private void getLogicalCluster2SuccessTest(LogicalClusterDO logicalClusterDO) {
        Mockito.when(logicalClusterMetadataManager.getLogicalCluster(Mockito.anyLong())).thenReturn(logicalClusterDO);

        LogicalCluster logicalCluster = logicalClusterService.getLogicalCluster(logicalClusterDO.getId());
        Assert.assertNotNull(logicalCluster);
        Assert.assertEquals(logicalCluster.getLogicalClusterIdentification(), logicalClusterDO.getIdentification());
    }

    @Test(dataProvider = "provideLogicalClusterDO", description = "获取逻辑集群信息测试")
    public void getLogicalClusterListByPrincipal(LogicalClusterDO logicalClusterDO) {
        // 责任人为空
        getLogicalClusterListByPrincipal2PrincipalIsBlankTest();
        // 获取的appDOList为空
        getLogicalClusterListByPrincipal2AppIsEmptyTest();
        // 完整流程
        getLogicalClusterListByPrincipal2Test(logicalClusterDO);
    }

    private void getLogicalClusterListByPrincipal2PrincipalIsBlankTest() {
        Mockito.when(logicalClusterMetadataManager.getLogicalClusterList()).thenReturn(Collections.emptyList());

        List<LogicalCluster> list = logicalClusterService.getLogicalClusterListByPrincipal("");
        Assert.assertNotNull(list);
        Assert.assertTrue(list.isEmpty());
    }

    private void getLogicalClusterListByPrincipal2AppIsEmptyTest() {
        Mockito.when(logicalClusterMetadataManager.getLogicalClusterList()).thenReturn(Collections.emptyList());
        Mockito.when(appService.getByPrincipal(Mockito.anyString())).thenReturn(Collections.emptyList());

        List<LogicalCluster> list = logicalClusterService.getLogicalClusterListByPrincipal("admin");
        Assert.assertNotNull(list);
        Assert.assertTrue(list.isEmpty());
    }

    private void getLogicalClusterListByPrincipal2Test(LogicalClusterDO logicalClusterDO) {
        List<LogicalClusterDO> LogicalClusterDOList = new ArrayList<>();
        LogicalClusterDOList.add(logicalClusterDO);
        LogicalClusterDOList.add(getLogicalClusterDO());
        Mockito.when(logicalClusterMetadataManager.getLogicalClusterList()).thenReturn(LogicalClusterDOList);
        Mockito.when(appService.getByPrincipal(Mockito.anyString())).thenReturn(Arrays.asList(getAppDO()));

        List<LogicalCluster> list = logicalClusterService.getLogicalClusterListByPrincipal("module");
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), 2);
        Assert.assertTrue(list.stream().allMatch(logicalCluster ->
                logicalCluster.getLogicalClusterName().equals(logicalClusterDO.getName())));
    }

    @Test(dataProvider = "provideLogicalClusterDO", description = "逻辑集群下Topic元信息测试")
    public void getTopicMetadatasTest(LogicalClusterDO logicalClusterDO) {
        // 传入的logicalClusterDO为空
        getTopicMetadatas2ParamisNullTest();
        // 获取逻辑集群下Topic元信息成功
        getTopicMetadatas2SuccessTest(logicalClusterDO);
    }

    private void getTopicMetadatas2ParamisNullTest() {
        List<TopicMetadata> topicMetadatas = logicalClusterService.getTopicMetadatas(null);
        Assert.assertTrue(topicMetadatas.isEmpty());
    }

    private void getTopicMetadatas2SuccessTest(LogicalClusterDO logicalClusterDO) {
        Set<String> set = new HashSet<>();
        set.add("xgTest");
        set.add("topicTest");
        Mockito.when(logicalClusterMetadataManager.getTopicNameSet(Mockito.anyLong()))
                .thenReturn(set);

        List<TopicMetadata> topicMetadatas = logicalClusterService.getTopicMetadatas(logicalClusterDO);
        Assert.assertFalse(topicMetadatas.isEmpty());
        Assert.assertTrue(topicMetadatas.stream().allMatch(topicMetadata ->
                topicMetadata.getTopic().equals("xgTest")));
    }

    @Test(dataProvider = "provideLogicalClusterDO", description = "逻辑集群下broker元信息测试")
    public void getBrokerMetadatasTest(LogicalClusterDO logicalClusterDO) {
        // 传入的logicalClusterDO为空
        getBrokerMetadatas2ParamisNullTest();
        // 获取逻辑集群下broker元信息成功
        getTopicBroker2SuccessTest(logicalClusterDO);
    }

    private void getBrokerMetadatas2ParamisNullTest() {
        List<BrokerMetadata> brokerMetadatas = logicalClusterService.getBrokerMetadatas(null);
        Assert.assertTrue(brokerMetadatas.isEmpty());
    }

    private void getTopicBroker2SuccessTest(LogicalClusterDO logicalClusterDO) {
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(111);
        Mockito.when(logicalClusterMetadataManager.getBrokerIdSet(Mockito.anyLong()))
                .thenReturn(set);

        List<BrokerMetadata> brokerMetadatas = logicalClusterService.getBrokerMetadatas(logicalClusterDO);
        Assert.assertFalse(brokerMetadatas.isEmpty());
        Assert.assertTrue(brokerMetadatas.stream().allMatch(brokerMetadata ->
                brokerMetadata.getBrokerId() == 1));
    }

    @Test(dataProvider = "provideLogicalClusterDO", description = "获取逻辑集群流量测试")
    public void getLogicalClusterMetricsFromDBTest(LogicalClusterDO logicalClusterDO) {
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(111);
        Mockito.when(logicalClusterMetadataManager.getBrokerIdSet(Mockito.anyLong()))
                .thenReturn(set);

        long startTime = 1639360565000L;
        long endTime = new Date().getTime();
        List<LogicalClusterMetrics> list = logicalClusterService.getLogicalClusterMetricsFromDB(
                logicalClusterDO, new Date(startTime), new Date(endTime));
        Assert.assertFalse(list.isEmpty());
        Assert.assertTrue(list.stream().allMatch(logicalClusterMetrics ->
                logicalClusterMetrics.getGmtCreate().compareTo(startTime) > 0 &&
                logicalClusterMetrics.getGmtCreate().compareTo(endTime) < 0));
    }


}
