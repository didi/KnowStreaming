package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.DBStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.ClusterDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.ControllerPreferredCandidate;
import com.xiaojukeji.kafka.manager.common.entity.pojo.*;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.ClusterNameDTO;
import com.xiaojukeji.kafka.manager.dao.ClusterMetricsDao;
import com.xiaojukeji.kafka.manager.dao.ControllerDao;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Mockito.when;

/**
 * @author xuguang
 * @Date 2021/12/8
 */
public class ClusterServiceTest extends BaseTest {

    @Autowired
    @InjectMocks
    private ClusterService clusterService;

    @Autowired
    private ClusterMetricsDao clusterMetricsDao;

    @Autowired
    private ControllerDao controllerDao;

    @Mock
    private RegionService regionService;

    @Mock
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Mock
    private PhysicalClusterMetadataManager physicalClusterMetadataManager;

    @Mock
    private ZookeeperService zookeeperService;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider(name = "provideClusterDO")
    public static Object[][] provideClusterDO() {
        ClusterDO clusterDO = new ClusterDO();
        clusterDO.setId(3L);
        clusterDO.setClusterName("LogiKM_moduleTest");
        clusterDO.setZookeeper("10.190.46.198:2181,10.190.14.237:2181,10.190.50.65:2181/xg");
        clusterDO.setBootstrapServers("10.190.46.198:9093,10.190.14.237:9093,10.190.50.65:9093");
        clusterDO.setSecurityProperties("{ \t\"security.protocol\": \"SASL_PLAINTEXT\", \t\"sasl.mechanism\": \"PLAIN\", \t\"sasl.jaas.config\": \"org.apache.kafka.common.security.plain.PlainLoginModule required username=\\\"dkm_admin\\\" password=\\\"km_kMl4N8as1Kp0CCY\\\";\" }");
        clusterDO.setStatus(1);
        clusterDO.setGmtCreate(new Date());
        clusterDO.setGmtModify(new Date());
        return new Object[][] {{clusterDO}};
    }

    @DataProvider(name = "provideClusterMetricsDO")
    public static Object[][] provideClusterMetricsDO() {
        ClusterMetricsDO clusterMetricsDO = new ClusterMetricsDO();
        clusterMetricsDO.setId(10L);
        clusterMetricsDO.setClusterId(1L);
        clusterMetricsDO.setMetrics("{\"PartitionNum\":52,\"BrokerNum\":0,\"CreateTime\":1638235221102,\"TopicNum\":2}");
        clusterMetricsDO.setGmtCreate(new Date());
        return new Object[][] {{clusterMetricsDO}};
    }

    @DataProvider(name = "provideControllerDO")
    public static Object[][] provideControllerDO() {
        ControllerDO controllerDO = new ControllerDO();
        controllerDO.setClusterId(1L);
        controllerDO.setBrokerId(1);
        controllerDO.setHost("127.0.0.1");
        controllerDO.setTimestamp(0L);
        controllerDO.setVersion(1);
        return new Object[][] {{controllerDO}};
    }

    @Test(dataProvider = "provideClusterDO", description = "测试新增物理集群")
    public void addNewTest(ClusterDO clusterDO) {
        // 测试新增物理集群成功
        addaddNew2SuccessTest(clusterDO);
        // 测试新增物理集群时键重复
        addaddNew2DuplicateKeyTest(clusterDO);
        // 测试新增物理集群时数据库插入失败
        addaddNew2MysqlErrorTest(clusterDO);
        // 测试新增物理集群时参数有误
        addNew2ParamIllegalTest(clusterDO);
        // 测试新增物理集群时zk无法连接
        addNew2ZookeeperConnectFailedTest(clusterDO);
    }

    private void addNew2ParamIllegalTest(ClusterDO clusterDO) {
        ResultStatus result1 = clusterService.addNew(clusterDO, null);
        Assert.assertEquals(result1.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());

        ResultStatus result2 = clusterService.addNew(null, "admin");
        Assert.assertEquals(result2.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void addNew2ZookeeperConnectFailedTest(ClusterDO clusterDO) {
        clusterDO.setZookeeper("xxx");
        ResultStatus result = clusterService.addNew(clusterDO, "admin");
        Assert.assertEquals(result.getCode(), ResultStatus.ZOOKEEPER_CONNECT_FAILED.getCode());
    }

    private void addaddNew2SuccessTest(ClusterDO clusterDO) {
        ResultStatus result = clusterService.addNew(clusterDO, "admin");
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    public void addaddNew2DuplicateKeyTest(ClusterDO clusterDO) {

        ResultStatus result = clusterService.addNew(clusterDO, "admin");
        Assert.assertEquals(result.getCode(), ResultStatus.RESOURCE_ALREADY_EXISTED.getCode());
    }

    public void addaddNew2MysqlErrorTest(ClusterDO clusterDO) {
        // operateRecord数据库插入失败
        clusterDO.setClusterName(null);
        ResultStatus result = clusterService.addNew(clusterDO, "admin");
        Assert.assertEquals(result.getCode(), ResultStatus.MYSQL_ERROR.getCode());

        // cluster数据库插入失败
        clusterDO.setClusterName("clusterTest");
        clusterDO.setBootstrapServers(null);
        ResultStatus result2 = clusterService.addNew(clusterDO, "admin");
        Assert.assertEquals(result2.getCode(), ResultStatus.MYSQL_ERROR.getCode());
    }

    @Test(dataProvider = "provideClusterDO", description = "测试由id获取ClusterDO")
    public void getById(ClusterDO clusterDO) {
        // 测试由id获取ClusterDO时，返回null
        getById2NullTest();
        // 测试由id获取ClusterDO时，返回成功
        getById2SuccessTest(clusterDO);
    }

    private void getById2NullTest() {
        ClusterDO clusterDO = clusterService.getById(null);
        Assert.assertNull(clusterDO);
    }

    private void getById2SuccessTest(ClusterDO clusterDO) {
        clusterService.addNew(clusterDO, "admin");

        ClusterDO result = clusterService.getById(clusterDO.getId());
        Assert.assertNotNull(result);
        Assert.assertEquals(result, clusterDO);
    }

    @Test(dataProvider = "provideClusterDO", description = "测试修改物理集群")
    public void updateById(ClusterDO clusterDO) {
        // 测试修改物理集群时参数有误
        updateById2ParamIllegalTest(clusterDO);
        // 测试修改物理集群时,集群不存在
        updateById2ClusterNotExistTest(clusterDO);
        // 测试修改物理集群时,zk配置不能修改
        updateById2ChangeZookeeperForbiddenTest(clusterDO);
    }

    @Test(dataProvider = "provideClusterDO", description = "测试修改物理集群时,mysqlError")
    public void updateById2mysqlErrorTest(ClusterDO clusterDO) {
        clusterService.addNew(clusterDO, "admin");

        clusterDO.setBootstrapServers(null);
        ResultStatus result1 = clusterService.updateById(clusterDO, "admin");
        Assert.assertEquals(result1.getCode(), ResultStatus.MYSQL_ERROR.getCode());
    }

    @Test(dataProvider = "provideClusterDO", description = "测试修改物理集群成功")
    public void updateById2SuccessTest(ClusterDO clusterDO) {
        clusterService.addNew(clusterDO, "admin");

        clusterDO.setJmxProperties("jmx");
        ResultStatus result1 = clusterService.updateById(clusterDO, "admin");
        Assert.assertEquals(result1.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void updateById2ParamIllegalTest(ClusterDO clusterDO) {
        ResultStatus result1 = clusterService.updateById(clusterDO, null);
        Assert.assertEquals(result1.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());

        ResultStatus result2 = clusterService.updateById(null, "admin");
        Assert.assertEquals(result2.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void updateById2ClusterNotExistTest(ClusterDO clusterDO) {
        clusterDO.setId(100L);
        ResultStatus result1 = clusterService.updateById(clusterDO, "admin");
        Assert.assertEquals(result1.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void updateById2ChangeZookeeperForbiddenTest(ClusterDO clusterDO) {
        clusterDO.setZookeeper("zzz");
        clusterDO.setId(1L);
        ResultStatus result1 = clusterService.updateById(clusterDO, "admin");
        Assert.assertEquals(result1.getCode(), ResultStatus.CHANGE_ZOOKEEPER_FORBIDDEN.getCode());
    }

    @Test(dataProvider = "provideClusterDO", description = "测试修改物理集群状态")
    public void modifyStatusTest(ClusterDO clusterDO) {
        // 测试修改物理集群状态时参数有误
        modifyStatus2ParamIllegalTest();
        // 测试修改物理集群状态时，集群不存在
        modifyStatus2ClusterNotExistTest();
        // 测试修改物理集群状态成功
        modifyStatus2SuccessTest(clusterDO);
    }

    public void modifyStatus2ParamIllegalTest() {
        ResultStatus result1 = clusterService.modifyStatus(null, 0, "admin");
        Assert.assertEquals(result1.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());

        ResultStatus result2 = clusterService.modifyStatus(1L, null,"admin");
        Assert.assertEquals(result2.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    public void modifyStatus2ClusterNotExistTest() {
        ResultStatus result1 = clusterService.modifyStatus(100L, 0, "admin");
        Assert.assertEquals(result1.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    public void modifyStatus2SuccessTest(ClusterDO clusterDO) {
        clusterService.addNew(clusterDO, "admin");

        ResultStatus result1 = clusterService.modifyStatus(clusterDO.getId(), clusterDO.getStatus(), "admin");
        Assert.assertEquals(result1.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(dataProvider = "provideClusterDO")
    public void listTest(ClusterDO clusterDO) {
        clusterService.addNew(clusterDO, "admin");

        List<ClusterDO> list = clusterService.list();
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0), clusterDO);
    }

    @Test(dataProvider = "provideClusterDO")
    public void listMapTest(ClusterDO clusterDO) {
        clusterService.addNew(clusterDO, "admin");

        Map<Long, ClusterDO> longClusterDOMap = clusterService.listMap();
        Assert.assertEquals(longClusterDOMap.size(), 1);
        Assert.assertEquals(longClusterDOMap.get(clusterDO.getId()), clusterDO);
    }

    @Test(dataProvider = "provideClusterDO")
    public void listAllTest(ClusterDO clusterDO) {
        clusterService.addNew(clusterDO, "admin");

        List<ClusterDO> list = clusterService.listAll();
        list.forEach(System.out::println);

        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0), clusterDO);
    }

    @Test(dataProvider = "provideClusterMetricsDO")
    public void getClusterMetricsFromDBTest(ClusterMetricsDO clusterMetricsDO) {
        clusterMetricsDao.batchAdd(Arrays.asList(clusterMetricsDO));

        List<ClusterMetricsDO> clusterMetricsDOList = clusterService.getClusterMetricsFromDB(
                clusterMetricsDO.getClusterId(),
                new Date(0L), new Date()
        );

        Assert.assertNotNull(clusterMetricsDOList);
        Assert.assertEquals(clusterMetricsDOList.size(), 1);
        Assert.assertTrue(clusterMetricsDOList.stream().allMatch(clusterMetricsDO1 ->
                clusterMetricsDO1.getMetrics().equals(clusterMetricsDO.getMetrics()) &&
                clusterMetricsDO1.getClusterId().equals(clusterMetricsDO.getClusterId())));

    }

    @Test(dataProvider = "provideControllerDO")
    public void getKafkaControllerHistoryTest(ControllerDO controllerDO) {
        controllerDao.insert(controllerDO);

        List<ControllerDO> kafkaControllerHistory = clusterService.getKafkaControllerHistory(controllerDO.getClusterId());
        Assert.assertNotNull(kafkaControllerHistory);
        Assert.assertTrue(kafkaControllerHistory.stream()
                .filter(controllerDO1 -> controllerDO1.getTimestamp().equals(0L))
                .allMatch(controllerDO1 ->
                    controllerDO1.getClusterId().equals(controllerDO.getClusterId()) &&
                    controllerDO1.getBrokerId().equals(controllerDO.getBrokerId()) &&
                    controllerDO1.getTimestamp().equals(controllerDO.getTimestamp()))
        );
    }

    @Test(dataProvider = "provideClusterDO", description = "参数needDetail为false")
    public void getClusterDetailDTOListWithFalseNeedDetailTest(ClusterDO clusterDO) {
        clusterService.addNew(clusterDO, "admin");

        String kafkaVersion = "2.7";
        when(physicalClusterMetadataManager.getKafkaVersionFromCache(Mockito.anyLong())).thenReturn(kafkaVersion);

        List<ClusterDetailDTO> clusterDetailDTOList = clusterService.getClusterDetailDTOList(false);
        Assert.assertNotNull(clusterDetailDTOList);
        Assert.assertTrue(clusterDetailDTOList.stream().allMatch(clusterDetailDTO ->
                clusterDetailDTO.getBootstrapServers().equals(clusterDO.getBootstrapServers()) &&
                clusterDetailDTO.getZookeeper().equals(clusterDO.getZookeeper()) &&
                clusterDetailDTO.getKafkaVersion().equals(kafkaVersion)));
    }

    @Test(dataProvider = "provideClusterDO", description = "参数needDetail为true")
    public void getClusterDetailDTOListWithTrueNeedDetailTest(ClusterDO clusterDO) {
        List<ClusterDetailDTO> clusterDetailDTOList = clusterService.getClusterDetailDTOList(true);
        Assert.assertNotNull(clusterDetailDTOList);
        Assert.assertTrue(clusterDetailDTOList.stream().allMatch(clusterDetailDTO ->
                clusterDetailDTO.getBootstrapServers().equals(clusterDO.getBootstrapServers()) &&
                        clusterDetailDTO.getZookeeper().equals(clusterDO.getZookeeper()) &&
                        clusterDetailDTO.getClusterName().equals("LogiKM_xg") &&
                        clusterDetailDTO.getBrokerNum().equals(1)));
    }

    @Test(description = "测试获取ClusterNameDTO时，无对应的逻辑集群")
    public void getClusterName2EmptyTest() {
        when(logicalClusterMetadataManager.getLogicalCluster(Mockito.anyLong())).thenReturn(null);
        ClusterNameDTO clusterName = clusterService.getClusterName(10L);
        Assert.assertEquals(clusterName.toString(), new ClusterNameDTO().toString());
    }

    @Test(dataProvider = "provideClusterDO", description = "测试获取ClusterNameDTO成功")
    public void getClusterName2SuccessTest(ClusterDO clusterDO) {
        clusterService.addNew(clusterDO, "admin");

        LogicalClusterDO logicalClusterDO = new LogicalClusterDO();
        logicalClusterDO.setIdentification("logical");
        logicalClusterDO.setClusterId(clusterDO.getId());
        logicalClusterDO.setId(1L);
        when(logicalClusterMetadataManager.getLogicalCluster(Mockito.anyLong())).thenReturn(logicalClusterDO);
        ClusterNameDTO clusterName = clusterService.getClusterName(logicalClusterDO.getId());
        Assert.assertEquals(clusterName.getLogicalClusterName(), logicalClusterDO.getName());
        Assert.assertEquals(clusterName.getLogicalClusterId(), logicalClusterDO.getId());
        Assert.assertEquals(clusterName.getPhysicalClusterId(), logicalClusterDO.getClusterId());
        Assert.assertEquals(clusterName.getPhysicalClusterName(), clusterDO.getClusterName());
    }

    @Test(description = "测试删除集群时，该集群下还有region，禁止删除")
    public void deleteById2OperationForbiddenTest() {
        when(regionService.getByClusterId(Mockito.anyLong())).thenReturn(Arrays.asList(new RegionDO()));
        ResultStatus resultStatus = clusterService.deleteById(1L, "admin");
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.OPERATION_FORBIDDEN.getCode());
    }

    @Test(dataProvider = "provideClusterDO", description = "测试删除集群成功")
    public void deleteById2SuccessTest(ClusterDO clusterDO) {
        clusterService.addNew(clusterDO, "admin");

        when(regionService.getByClusterId(Mockito.anyLong())).thenReturn(Collections.emptyList());
        ResultStatus resultStatus = clusterService.deleteById(clusterDO.getId(), "admin");
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());

    }

    @Test(description = "测试删除集群成功")
    public void deleteById2MysqlErrorTest() {
        when(regionService.getByClusterId(Mockito.anyLong())).thenReturn(Collections.emptyList());
        ResultStatus resultStatus = clusterService.deleteById(100L, "admin");
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.MYSQL_ERROR.getCode());
    }

    @Test(description = "测试从zk中获取被选举的broker")
    public void getControllerPreferredCandidatesTest() {
        // "测试从zk中获取被选举的broker失败"
        getControllerPreferredCandidates2FailedTest();
        // 测试从zk中获取被选举的broker为空
        getControllerPreferredCandidates2BrokersEmptyTest();
        // 测试从zk中获取被选举的broker的brokerMetadata为null
        getControllerPreferredCandidates2BrokerMetadataNullTest();
        // 测试从zk中获取被选举的broker成功
        getControllerPreferredCandidates2SuccessTest();
    }

    private void getControllerPreferredCandidates2FailedTest() {
        when(zookeeperService.getControllerPreferredCandidates(Mockito.anyLong())).thenReturn(new Result<>(-1, "fail"));

        Result<List<ControllerPreferredCandidate>> result = clusterService.getControllerPreferredCandidates(1L);
        Assert.assertTrue(result.getCode() != ResultStatus.SUCCESS.getCode());
    }

    private void getControllerPreferredCandidates2BrokersEmptyTest() {
        when(zookeeperService.getControllerPreferredCandidates(Mockito.anyLong())).thenReturn(new Result<>(0, new ArrayList<>(), "fail"));

        Result<List<ControllerPreferredCandidate>> result = clusterService.getControllerPreferredCandidates(1L);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
        Assert.assertTrue(result.getData().isEmpty());
    }

    private void getControllerPreferredCandidates2BrokerMetadataNullTest() {
        when(zookeeperService.getControllerPreferredCandidates(Mockito.anyLong())).thenReturn(new Result<>(0, Arrays.asList(100), "fail"));

        Result<List<ControllerPreferredCandidate>> result = clusterService.getControllerPreferredCandidates(1L);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
        Assert.assertEquals((int) result.getData().get(0).getStatus(), DBStatusEnum.DEAD.getStatus());
    }

    private void getControllerPreferredCandidates2SuccessTest() {
        when(zookeeperService.getControllerPreferredCandidates(Mockito.anyLong())).thenReturn(new Result<>(0, Arrays.asList(2), "fail"));

        Result<List<ControllerPreferredCandidate>> result = clusterService.getControllerPreferredCandidates(1L);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
        Assert.assertEquals((int) result.getData().get(0).getStatus(), DBStatusEnum.ALIVE.getStatus());
    }

    @Test(description = "增加优先被选举为controller的broker")
    public void addControllerPreferredCandidatesTest() {
        // 增加优先被选举为controller的broker时参数错误
        addControllerPreferredCandidates2ParamIllegalTest();
        // 增加优先被选举为controller的broker时broker不存活
        addControllerPreferredCandidates2BrokerNotExistTest();
        // 增加优先被选举为controller的broker失败
        addControllerPreferredCandidates2FailedTest();
        // 增加优先被选举为controller的broker成功
        addControllerPreferredCandidates2SuccessTest();
    }

    private void addControllerPreferredCandidates2ParamIllegalTest() {
        Result result1 = clusterService.addControllerPreferredCandidates(null, Arrays.asList(1));
        Assert.assertEquals(result1.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());

        Result result2 = clusterService.addControllerPreferredCandidates(1L, Collections.emptyList());
        Assert.assertEquals(result2.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void addControllerPreferredCandidates2BrokerNotExistTest() {
        Result result1 = clusterService.addControllerPreferredCandidates(1L, Arrays.asList(100));
        Assert.assertEquals(result1.getCode(), ResultStatus.BROKER_NOT_EXIST.getCode());
    }

    private void addControllerPreferredCandidates2FailedTest() {
        when(zookeeperService.addControllerPreferredCandidate(Mockito.anyLong(), Mockito.anyInt())).thenReturn(new Result(-1, "fail"));
        Result result1 = clusterService.addControllerPreferredCandidates(1L, Arrays.asList(2));
        Assert.assertNotEquals(result1.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void addControllerPreferredCandidates2SuccessTest() {
        when(zookeeperService.addControllerPreferredCandidate(Mockito.anyLong(), Mockito.anyInt())).thenReturn(new Result(0, "fail"));
        Result result1 = clusterService.addControllerPreferredCandidates(1L, Arrays.asList(2));
        Assert.assertEquals(result1.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "删除优先被选举为controller的broker")
    public void deleteControllerPreferredCandidates() {
        // 删除优先被选举为controller的broker时参数错误
        deleteControllerPreferredCandidates2ParamIllegal();
        // 删除优先被选举为controller的broker失败
        deleteControllerPreferredCandidates2FailedTest();
        // 删除优先被选举为controller的broker成功
        deleteControllerPreferredCandidates2SuccessTest();
    }

    private void deleteControllerPreferredCandidates2ParamIllegal() {
        Result result1 = clusterService.deleteControllerPreferredCandidates(null, Arrays.asList(1));
        Assert.assertEquals(result1.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());

        Result result2 = clusterService.deleteControllerPreferredCandidates(1L, Collections.emptyList());
        Assert.assertEquals(result2.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void deleteControllerPreferredCandidates2FailedTest() {
        when(zookeeperService.deleteControllerPreferredCandidate(Mockito.anyLong(), Mockito.anyInt())).thenReturn(new Result(-1, "fail"));
        Result result1 = clusterService.deleteControllerPreferredCandidates(1L, Arrays.asList(2));
        Assert.assertNotEquals(result1.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void deleteControllerPreferredCandidates2SuccessTest() {
        when(zookeeperService.deleteControllerPreferredCandidate(Mockito.anyLong(), Mockito.anyInt())).thenReturn(new Result(0, "fail"));
        Result result1 = clusterService.deleteControllerPreferredCandidates(1L, Arrays.asList(2));
        Assert.assertEquals(result1.getCode(), ResultStatus.SUCCESS.getCode());
    }

}
