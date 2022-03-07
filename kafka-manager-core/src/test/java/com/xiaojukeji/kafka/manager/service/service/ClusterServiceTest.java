package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.DBStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.ClusterDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.ControllerPreferredCandidate;
import com.xiaojukeji.kafka.manager.common.entity.pojo.*;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.ClusterNameDTO;
import com.xiaojukeji.kafka.manager.dao.ClusterDao;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * @author xuguang
 * @Date 2021/12/8
 */
public class ClusterServiceTest extends BaseTest {
    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    @Value("${test.broker.id1}")
    private Integer REAL_BROKER_ID_IN_ZK;

    @Value("${test.phyCluster.name}")
    private String REAL_PHYSICAL_CLUSTER_NAME;

    @Value("${test.ZK.address}")
    private String ZOOKEEPER_ADDRESS;

    @Value("${test.ZK.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;

    private final static String SECURITY_PROTOCOL = "{ \t\"security.protocol\": \"SASL_PLAINTEXT\", \t\"sasl.mechanism\": \"PLAIN\", \t\"sasl.jaas.config\": \"org.apache.kafka.common.security.plain.PlainLoginModule required username=\\\"dkm_admin\\\" password=\\\"km_kMl4N8as1Kp0CCY\\\";\" }";


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

    @Mock
    private OperateRecordService operateRecordService;

    @Mock
    private ClusterDao clusterDao;

    @Mock
    private ConsumerService consumerService;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private ClusterDO getClusterDO1() {
        ClusterDO clusterDO = new ClusterDO();
        clusterDO.setId(3L);
        clusterDO.setClusterName(REAL_PHYSICAL_CLUSTER_NAME);
        clusterDO.setZookeeper(ZOOKEEPER_ADDRESS);
        clusterDO.setBootstrapServers(BOOTSTRAP_SERVERS);
        clusterDO.setSecurityProperties(SECURITY_PROTOCOL);
        clusterDO.setStatus(1);
        clusterDO.setGmtCreate(new Date());
        clusterDO.setGmtModify(new Date());
        return clusterDO;
    }

    private ClusterMetricsDO getClusterMetricsDO() {
        ClusterMetricsDO clusterMetricsDO = new ClusterMetricsDO();
        clusterMetricsDO.setId(10L);
        clusterMetricsDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        clusterMetricsDO.setMetrics("{\"PartitionNum\":52,\"BrokerNum\":0,\"CreateTime\":1638235221102,\"TopicNum\":2}");
        clusterMetricsDO.setGmtCreate(new Date());
        return clusterMetricsDO;
    }

    private ControllerDO getControllerDO() {
        ControllerDO controllerDO = new ControllerDO();
        controllerDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        controllerDO.setBrokerId(REAL_BROKER_ID_IN_ZK);
        controllerDO.setHost("127.0.0.1");
        controllerDO.setTimestamp(0L);
        controllerDO.setVersion(1);
        return controllerDO;
    }

    private Map<Long, Integer> getRegionNum() {
        Map<Long, Integer> map = new HashMap<>();
        map.put(REAL_CLUSTER_ID_IN_MYSQL, 1);
        return map;
    }

    private Map<Long, Integer> getConsumerGroupNumMap() {
        Map<Long, Integer> map = new HashMap<>();
        map.put(REAL_CLUSTER_ID_IN_MYSQL, 1);
        return map;
    }

    private ClusterDO getClusterDO() {
        ClusterDO clusterDO = new ClusterDO();
        clusterDO.setId(3L);
        clusterDO.setClusterName(REAL_PHYSICAL_CLUSTER_NAME);
        clusterDO.setZookeeper("zzz");
        clusterDO.setBootstrapServers(BOOTSTRAP_SERVERS);
        clusterDO.setSecurityProperties(SECURITY_PROTOCOL);
        clusterDO.setStatus(1);
        clusterDO.setGmtCreate(new Date());
        clusterDO.setGmtModify(new Date());
        return clusterDO;
    }

    @Test(description = "测试新增物理集群")
    public void addNewTest() {
        ClusterDO clusterDO = getClusterDO1();
        // 测试新增物理集群成功
        addNew2SuccessTest(clusterDO);
        // 测试新增物理集群时键重复
        addNew2DuplicateKeyTest(clusterDO);
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

    private void addNew2SuccessTest(ClusterDO clusterDO) {
        Mockito.when(operateRecordService.insert(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(clusterDao.insert(Mockito.any())).thenReturn(1);
        ResultStatus result = clusterService.addNew(clusterDO, "admin");
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    public void addNew2DuplicateKeyTest(ClusterDO clusterDO) {
        Mockito.when(operateRecordService.insert(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(DuplicateKeyException.class);
        ResultStatus result = clusterService.addNew(clusterDO, "admin");
        Assert.assertEquals(result.getCode(), ResultStatus.RESOURCE_ALREADY_EXISTED.getCode());
    }

    @Test(description = "测试修改物理集群")
    public void updateById() {
        ClusterDO clusterDO = getClusterDO1();
        // 测试修改物理集群时参数有误
        updateById2ParamIllegalTest(clusterDO);
        // 测试修改物理集群时,集群不存在
        updateById2ClusterNotExistTest(clusterDO);
    }

    @Test(description = "测试修改物理集群时,mysqlError")
    public void updateById2mysqlErrorTest() {
        ClusterDO clusterDO = getClusterDO1();
        Mockito.when(clusterDao.getById(Mockito.any())).thenReturn(clusterDO);
        Mockito.when(operateRecordService.insert(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(clusterDao.updateById(Mockito.any())).thenReturn(0);
        ResultStatus result1 = clusterService.updateById(clusterDO, "admin");
        Assert.assertEquals(result1.getCode(), ResultStatus.MYSQL_ERROR.getCode());
    }

    @Test(description = "测试修改物理集群成功")
    public void updateById2SuccessTest() {
        ClusterDO clusterDO = getClusterDO1();
        Mockito.when(clusterDao.getById(Mockito.any())).thenReturn(clusterDO);
        Mockito.when(clusterDao.updateById(Mockito.any())).thenReturn(1);
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
        Mockito.when(clusterDao.getById(Mockito.any())).thenReturn(null);
        ResultStatus result1 = clusterService.updateById(clusterDO, "admin");
        Assert.assertEquals(result1.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    @Test()
    public void updateById2ChangeZookeeperForbiddenTest() {
        ClusterDO clusterDO = getClusterDO1();
        ClusterDO clusterDO1 = getClusterDO();
        Mockito.when(clusterDao.getById(Mockito.any())).thenReturn(clusterDO);
        ResultStatus result1 = clusterService.updateById(clusterDO1, "admin");
        Assert.assertEquals(result1.getCode(), ResultStatus.CHANGE_ZOOKEEPER_FORBIDDEN.getCode());
    }

    @Test( description = "测试修改物理集群状态")
    public void modifyStatusTest() {
        ClusterDO clusterDO = getClusterDO1();
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
        Mockito.when(clusterDao.getById(Mockito.any())).thenReturn(null);
        Assert.assertEquals(result1.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    public void modifyStatus2SuccessTest(ClusterDO clusterDO) {
        Mockito.when(clusterDao.getById(Mockito.any())).thenReturn(clusterDO);
        Mockito.when(clusterDao.updateById(Mockito.any())).thenReturn(1);
        ResultStatus result1 = clusterService.modifyStatus(clusterDO.getId(), clusterDO.getStatus(), "admin");
        Assert.assertEquals(result1.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "参数needDetail为false")
    public void getClusterDetailDTOListWithFalseNeedDetailTest() {
        ClusterDO clusterDO = getClusterDO1();
        Mockito.when(clusterDao.listAll()).thenReturn(Arrays.asList(clusterDO));
        String kafkaVersion = "2.7";
        when(physicalClusterMetadataManager.getKafkaVersionFromCache(Mockito.anyLong())).thenReturn(kafkaVersion);

        List<ClusterDetailDTO> clusterDetailDTOList = clusterService.getClusterDetailDTOList(false);
        Assert.assertNotNull(clusterDetailDTOList);
        Assert.assertTrue(clusterDetailDTOList.stream().allMatch(clusterDetailDTO ->
                clusterDetailDTO.getBootstrapServers().equals(clusterDO.getBootstrapServers()) &&
                clusterDetailDTO.getZookeeper().equals(clusterDO.getZookeeper()) &&
                clusterDetailDTO.getKafkaVersion().equals(kafkaVersion)));
    }

    @Test(description = "参数needDetail为true")
    public void getClusterDetailDTOListWithTrueNeedDetailTest() {
        ClusterDO clusterDO = getClusterDO1();
        Mockito.when(clusterDao.listAll()).thenReturn(Arrays.asList(clusterDO));
        Mockito.when(regionService.getRegionNum()).thenReturn(getRegionNum());
        Mockito.when(consumerService.getConsumerGroupNumMap(Mockito.any())).thenReturn(getConsumerGroupNumMap());
        List<ClusterDetailDTO> clusterDetailDTOList = clusterService.getClusterDetailDTOList(true);
        Assert.assertNotNull(clusterDetailDTOList);
        Assert.assertTrue(clusterDetailDTOList.stream().allMatch(clusterDetailDTO ->
                clusterDetailDTO.getBootstrapServers().equals(clusterDO.getBootstrapServers()) &&
                        clusterDetailDTO.getZookeeper().equals(clusterDO.getZookeeper()) &&
                        clusterDetailDTO.getClusterName().equals(REAL_PHYSICAL_CLUSTER_NAME)));
    }

    @Test(description = "测试获取ClusterNameDTO时，无对应的逻辑集群")
    public void getClusterName2EmptyTest() {
        when(logicalClusterMetadataManager.getLogicalCluster(Mockito.anyLong())).thenReturn(null);
        ClusterNameDTO clusterName = clusterService.getClusterName(10L);
        Assert.assertEquals(clusterName.toString(), new ClusterNameDTO().toString());
    }

    @Test(description = "测试获取ClusterNameDTO成功")
    public void getClusterName2SuccessTest() {
        ClusterDO clusterDO = getClusterDO1();
        clusterService.addNew(clusterDO, "admin");

        LogicalClusterDO logicalClusterDO = new LogicalClusterDO();
        logicalClusterDO.setIdentification("logical");
        logicalClusterDO.setClusterId(clusterDO.getId());
        logicalClusterDO.setId(1L);
        when(logicalClusterMetadataManager.getLogicalCluster(Mockito.anyLong())).thenReturn(logicalClusterDO);
        Mockito.when(clusterDao.getById(Mockito.any())).thenReturn(clusterDO);
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

    @Test(description = "测试删除集群成功")
    public void deleteById2SuccessTest() {
        ClusterDO clusterDO = getClusterDO1();
        when(regionService.getByClusterId(Mockito.anyLong())).thenReturn(Collections.emptyList());
        Mockito.when(operateRecordService.insert(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(clusterDao.deleteById(Mockito.any())).thenReturn(1);
        ResultStatus resultStatus = clusterService.deleteById(clusterDO.getId(), "admin");
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());

    }

    @Test(description = "测试MYSQL_ERROR")
    public void deleteById2MysqlErrorTest() {
        when(regionService.getByClusterId(Mockito.anyLong())).thenReturn(Collections.emptyList());
        ResultStatus resultStatus = clusterService.deleteById(100L, "admin");
        Mockito.when(operateRecordService.insert(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(clusterDao.deleteById(Mockito.any())).thenReturn(-1);
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
