package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusReassignEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.TopicReassignActionEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.reassign.ReassignStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignExecDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignExecSubDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ReassignTaskDO;
import com.xiaojukeji.kafka.manager.dao.ReassignTaskDao;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import kafka.common.TopicAndPartition;
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
 * @Date 2021/12/14
 */
public class ReassignServiceTest extends BaseTest {

    /**
     * 集群共包括三个broker:1,2,3, 该topic 2分区 3副本因子，在broker1,2,3上
     */
    @Value("${test.topic.name2}")
    private String REAL_TOPIC2_IN_ZK;

    @Value("${test.admin}")
    private String ADMIN_OPERATOR;

    @Autowired
    @InjectMocks
    private ReassignService reassignService;

    @Mock
    private RegionService regionService;

    @Mock
    private ClusterService clusterService;

    @Mock
    private ReassignTaskDao reassignTaskDao;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Value("${test.ZK.address}")
    private String ZOOKEEPER_ADDRESS;

    @Value("${test.ZK.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;

    private final static String SECURITY_PROTOCOL = "{ \t\"security.protocol\": \"SASL_PLAINTEXT\", \t\"sasl.mechanism\": \"PLAIN\", \t\"sasl.jaas.config\": \"org.apache.kafka.common.security.plain.PlainLoginModule required username=\\\"dkm_admin\\\" password=\\\"km_kMl4N8as1Kp0CCY\\\";\" }";

    private final static String REASSIGNMENTJSON =
            "{ \"version\": 1, \"partitions\": [ { \"topic\": \"reassignTest\", \"partition\": 1, \"replicas\": [ 1,2,3 ], \"log_dirs\": [ \"any\",\"any\",\"any\" ] }, { \"topic\": \"reassignTest\", \"partition\": 0, \"replicas\": [ 1,2,3 ], \"log_dirs\": [ \"any\",\"any\",\"any\" ] } ] }";

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    @Value("${test.phyCluster.name}")
    private String REAL_PHYSICAL_CLUSTER_NAME;


    private ReassignTopicDTO getReassignTopicDTO() {
        // 让分区从原本的broker1，2，3变成只落到broker2,3
        ReassignTopicDTO reassignTopicDTO = new ReassignTopicDTO();
        reassignTopicDTO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        reassignTopicDTO.setTopicName(REAL_TOPIC2_IN_ZK);
        reassignTopicDTO.setBrokerIdList(Arrays.asList(2,3));
        reassignTopicDTO.setRegionId(1000000L);
        // 原本Topic只有两个分区
        reassignTopicDTO.setPartitionIdList(Arrays.asList(0, 1));
        reassignTopicDTO.setThrottle(100000L);
        reassignTopicDTO.setMaxThrottle(100000L);
        reassignTopicDTO.setMinThrottle(100000L);
        reassignTopicDTO.setOriginalRetentionTime(10000L);
        reassignTopicDTO.setReassignRetentionTime(10000L);
        reassignTopicDTO.setBeginTime(100000L);
        reassignTopicDTO.setDescription("");
        return reassignTopicDTO;
    }

    private ReassignExecDTO getReassignExecDTO() {
        ReassignExecDTO reassignExecDTO = new ReassignExecDTO();
        reassignExecDTO.setTaskId(1L);
        reassignExecDTO.setAction("modify");
        reassignExecDTO.setBeginTime(0L);
        return reassignExecDTO;
    }

    private ReassignTaskDO getReassignTaskDO() {
        ReassignTaskDO reassignTaskDO = new ReassignTaskDO();
        reassignTaskDO.setId(1L);
        reassignTaskDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        reassignTaskDO.setStatus(0);
        reassignTaskDO.setTaskId(1L);
        reassignTaskDO.setTopicName(REAL_TOPIC2_IN_ZK);
        reassignTaskDO.setPartitions("0,1,2");
        reassignTaskDO.setReassignmentJson("");
        reassignTaskDO.setRealThrottle(1000L);
        reassignTaskDO.setMaxThrottle(1000L);
        reassignTaskDO.setMinThrottle(1000L);
        reassignTaskDO.setBeginTime(new Date());
        reassignTaskDO.setSrcBrokers("0");
        reassignTaskDO.setDestBrokers("1");
        reassignTaskDO.setReassignRetentionTime(1000L);
        reassignTaskDO.setOriginalRetentionTime(1000L);
        reassignTaskDO.setDescription("测试迁移任务");
        reassignTaskDO.setOperator(ADMIN_OPERATOR);
        return reassignTaskDO;
    }

    private ReassignExecSubDTO getReassignExecSubDTO() {
        ReassignExecSubDTO reassignExecSubDTO = new ReassignExecSubDTO();
        reassignExecSubDTO.setSubTaskId(1L);
        reassignExecSubDTO.setAction("modify");
        reassignExecSubDTO.setThrottle(100000L);
        reassignExecSubDTO.setMaxThrottle(100000L);
        reassignExecSubDTO.setMinThrottle(100000L);
        return reassignExecSubDTO;
    }

    private ClusterDO getClusterDO() {
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

    private Map<Long, ClusterDO> getMap() {
        ClusterDO clusterDO = getClusterDO();
        HashMap<Long, ClusterDO> map = new HashMap<>();
        map.put(REAL_CLUSTER_ID_IN_MYSQL, clusterDO);
        return map;
    }

    @Test(description = "创建迁移任务")
    public void createTaskTest() {
        // 参数错误
        createTask2paramIllegalTest();
        // 物理集群不存在
        createTask2ClusterNotExistTest();
        // topic不存在
        createTask2TopicNotExistTest();
        // broker数量不足
        createTask2BrokerNumNotEnoughTest();
        // broker不存在
        createTask2BrokerNotExistTest();
        // broker数量不足, checkParamLegal()方法中
        createTask2BrokerNumNotEnough2Test();
        // 参数错误， checkParamLegal()方法中
        createTask2ParamIllegal2Test();
        // 分区为空
        createTask2PartitionIdListEmptyTest();
        // 分区不存在
        // 因定时任务暂时无法跑通
        // createTask2PartitionNotExistTest();
        // 创建任务成功
        // 因定时任务暂时无法跑通
//        createTask2SuccessTest();
    }

    private void createTask2paramIllegalTest() {
        ResultStatus result = reassignService.createTask(Collections.emptyList(), ADMIN_OPERATOR);
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void createTask2ClusterNotExistTest() {
        ReassignTopicDTO reassignTopicDTO = getReassignTopicDTO();
        Mockito.when(clusterService.listMap()).thenReturn(new HashMap<>());
        ResultStatus result = reassignService.createTask(Arrays.asList(reassignTopicDTO), ADMIN_OPERATOR);
        Assert.assertEquals(result.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void createTask2TopicNotExistTest() {
        ReassignTopicDTO reassignTopicDTO = getReassignTopicDTO();
        reassignTopicDTO.setTopicName("xxx");
        Mockito.when(clusterService.listMap()).thenReturn(getMap());
        ResultStatus result = reassignService.createTask(Arrays.asList(reassignTopicDTO), ADMIN_OPERATOR);
        Assert.assertEquals(result.getCode(), ResultStatus.TOPIC_NOT_EXIST.getCode());
    }

    private void createTask2BrokerNumNotEnoughTest() {
        Mockito.when(clusterService.listMap()).thenReturn(getMap());
        Mockito.when(regionService.getFullBrokerIdList(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList())).thenReturn(null);

        ReassignTopicDTO reassignTopicDTO = getReassignTopicDTO();
        ResultStatus result = reassignService.createTask(Arrays.asList(reassignTopicDTO), ADMIN_OPERATOR);
        Assert.assertEquals(result.getCode(), ResultStatus.BROKER_NUM_NOT_ENOUGH.getCode());
    }

    private void createTask2BrokerNotExistTest() {
        Mockito.when(clusterService.listMap()).thenReturn(getMap());
        Mockito.when(regionService.getFullBrokerIdList(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList())).thenReturn(Arrays.asList(100, 2, 3));

        ReassignTopicDTO reassignTopicDTO = getReassignTopicDTO();
        ResultStatus result = reassignService.createTask(Arrays.asList(reassignTopicDTO), ADMIN_OPERATOR);
        Assert.assertEquals(result.getCode(), ResultStatus.BROKER_NOT_EXIST.getCode());
    }

    private void createTask2BrokerNumNotEnough2Test() {
        Mockito.when(clusterService.listMap()).thenReturn(getMap());
        Mockito.when(regionService.getFullBrokerIdList(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList())).thenReturn(Arrays.asList(2, 3));

        ReassignTopicDTO reassignTopicDTO = getReassignTopicDTO();
        ResultStatus result = reassignService.createTask(Arrays.asList(reassignTopicDTO), ADMIN_OPERATOR);
        Assert.assertEquals(result.getCode(), ResultStatus.BROKER_NUM_NOT_ENOUGH.getCode());
    }

    private void createTask2ParamIllegal2Test() {
        Mockito.when(clusterService.listMap()).thenReturn(getMap());
        Mockito.when(regionService.getFullBrokerIdList(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList())).thenReturn(Arrays.asList(1, 2, 3));

        ReassignTopicDTO reassignTopicDTO = getReassignTopicDTO();
        ResultStatus result = reassignService.createTask(Arrays.asList(reassignTopicDTO), ADMIN_OPERATOR);
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void createTask2PartitionIdListEmptyTest() {
        Mockito.when(clusterService.listMap()).thenReturn(getMap());
        Mockito.when(regionService.getFullBrokerIdList(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList())).thenReturn(Arrays.asList(1, 2, 3));

        ReassignTopicDTO reassignTopicDTO = getReassignTopicDTO();
        reassignTopicDTO.setOriginalRetentionTime(168 * 3600000L);
        reassignTopicDTO.setPartitionIdList(Collections.emptyList());
        ResultStatus result = reassignService.createTask(Arrays.asList(reassignTopicDTO), ADMIN_OPERATOR);
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void createTask2PartitionNotExistTest() {
        Mockito.when(clusterService.listMap()).thenReturn(getMap());
        Mockito.when(regionService.getFullBrokerIdList(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList())).thenReturn(Arrays.asList(1, 2, 3));

        ReassignTopicDTO reassignTopicDTO = getReassignTopicDTO();
        reassignTopicDTO.setClusterId(1L);
        reassignTopicDTO.setTopicName(REAL_TOPIC2_IN_ZK);
        // 注意，要求topic中数据保存时间为168小时
        reassignTopicDTO.setOriginalRetentionTime(168 * 3600000L);
        reassignTopicDTO.setPartitionIdList(Arrays.asList(100, 0));
        ResultStatus result = reassignService.createTask(Arrays.asList(reassignTopicDTO), ADMIN_OPERATOR);
        Assert.assertEquals(result.getCode(), ResultStatus.PARTITION_NOT_EXIST.getCode());
    }

    private void createTask2SuccessTest() {
        Mockito.when(clusterService.listMap()).thenReturn(getMap());
        Mockito.when(regionService.getFullBrokerIdList(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList())).thenReturn(Arrays.asList(1, 2, 3));

        ReassignTopicDTO reassignTopicDTO = getReassignTopicDTO();
        reassignTopicDTO.setTopicName(REAL_TOPIC2_IN_ZK);
        reassignTopicDTO.setOriginalRetentionTime(168 * 3600000L);
        ResultStatus result = reassignService.createTask(Arrays.asList(reassignTopicDTO), ADMIN_OPERATOR);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取迁移任务")
    public void getTaskTest() {
        // 测试获取成功
        getTaskTest2Success();
        // 测试获取失败
        getTaskTest2Exception();
    }

    private void getTaskTest2Success() {
        ReassignTaskDO reassignTask = getReassignTaskDO();
        Mockito.when(reassignTaskDao.getByTaskId(Mockito.anyLong())).thenReturn(Arrays.asList(reassignTask));

        List<ReassignTaskDO> task = reassignService.getTask(reassignTask.getTaskId());
        Assert.assertFalse(task.isEmpty());
        Assert.assertTrue(task.stream().allMatch(reassignTaskDO ->
                reassignTaskDO.getTaskId().equals(reassignTask.getTaskId()) &&
                reassignTaskDO.getClusterId().equals(reassignTask.getClusterId()) &&
                reassignTaskDO.getStatus().equals(reassignTask.getStatus())));
    }

    private void getTaskTest2Exception() {
        ReassignTaskDO reassignTask = getReassignTaskDO();
        Mockito.when(reassignTaskDao.getByTaskId(Mockito.anyLong())).thenThrow(RuntimeException.class);

        List<ReassignTaskDO> task = reassignService.getTask(reassignTask.getTaskId());
        Assert.assertNull(task);
    }

    @Test(description = "修改迁移任务")
    public void modifyTask() {
        // operation forbidden
        modifyTask2OperationForbiddenTest();
        // 修改成功
        modifyTask2Success();
        // mysqlError
        modifyTask2MysqlError();
        // 任务不存在
        modifyTask2TaskNotExistTest();
    }

    private void modifyTask2TaskNotExistTest() {
        Mockito.when(reassignTaskDao.getByTaskId(Mockito.anyLong())).thenThrow(RuntimeException.class);

        ReassignExecDTO reassignExecDTO = getReassignExecDTO();
        reassignExecDTO.setTaskId(100L);
        ResultStatus resultStatus = reassignService.modifyTask(reassignExecDTO, TopicReassignActionEnum.START);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.TASK_NOT_EXIST.getCode());
    }

    private void modifyTask2OperationForbiddenTest() {
        ReassignTaskDO reassignTask = getReassignTaskDO();
        reassignTask.setStatus(1);
        Mockito.when(reassignTaskDao.getByTaskId(Mockito.anyLong())).thenReturn(Arrays.asList(reassignTask));

        ReassignExecDTO reassignExecDTO = getReassignExecDTO();
        ResultStatus resultStatus1 = reassignService.modifyTask(reassignExecDTO, TopicReassignActionEnum.START);
        Assert.assertEquals(resultStatus1.getCode(), ResultStatus.OPERATION_FORBIDDEN.getCode());
    }

    private void modifyTask2Success() {
        ReassignTaskDO reassignTask = getReassignTaskDO();
        reassignTask.setStatus(0);
        Mockito.when(reassignTaskDao.getByTaskId(Mockito.anyLong())).thenReturn(Arrays.asList(reassignTask));

        ReassignExecDTO reassignExecDTO = getReassignExecDTO();
        // cancel action
        ResultStatus resultStatus1 = reassignService.modifyTask(reassignExecDTO, TopicReassignActionEnum.CANCEL);
        Assert.assertEquals(resultStatus1.getCode(), ResultStatus.SUCCESS.getCode());

        // start action
        reassignTask.setStatus(0);
        Mockito.when(reassignTaskDao.getByTaskId(Mockito.anyLong())).thenReturn(Arrays.asList(reassignTask));
        ResultStatus resultStatus2 = reassignService.modifyTask(reassignExecDTO, TopicReassignActionEnum.START);
        Assert.assertEquals(resultStatus2.getCode(), ResultStatus.SUCCESS.getCode());

        // modify action
        reassignTask.setStatus(0);
        Mockito.when(reassignTaskDao.getByTaskId(Mockito.anyLong())).thenReturn(Arrays.asList(reassignTask));
        ResultStatus resultStatus3 = reassignService.modifyTask(reassignExecDTO, TopicReassignActionEnum.MODIFY);
        Assert.assertEquals(resultStatus3.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void modifyTask2MysqlError() {
        ReassignTaskDO reassignTask = getReassignTaskDO();
        reassignTask.setStatus(0);
        Mockito.when(reassignTaskDao.getByTaskId(Mockito.anyLong())).thenReturn(Arrays.asList(reassignTask));

        ReassignExecDTO reassignExecDTO = getReassignExecDTO();
        // cancel action
        Mockito.doThrow(RuntimeException.class).when(reassignTaskDao).batchUpdate(Mockito.anyList());
        ResultStatus resultStatus1 = reassignService.modifyTask(reassignExecDTO, TopicReassignActionEnum.CANCEL);
        Assert.assertEquals(resultStatus1.getCode(), ResultStatus.MYSQL_ERROR.getCode());

        // start action
        reassignTask.setStatus(0);
        Mockito.when(reassignTaskDao.getByTaskId(Mockito.anyLong())).thenReturn(Arrays.asList(reassignTask));
        Mockito.doThrow(RuntimeException.class).when(reassignTaskDao).batchUpdate(Mockito.anyList());
        ResultStatus resultStatus2 = reassignService.modifyTask(reassignExecDTO, TopicReassignActionEnum.START);
        Assert.assertEquals(resultStatus2.getCode(), ResultStatus.MYSQL_ERROR.getCode());

        // modify action
        reassignTask.setStatus(0);
        Mockito.when(reassignTaskDao.getByTaskId(Mockito.anyLong())).thenReturn(Arrays.asList(reassignTask));
        Mockito.doThrow(RuntimeException.class).when(reassignTaskDao).batchUpdate(Mockito.anyList());
        ResultStatus resultStatus3 = reassignService.modifyTask(reassignExecDTO, TopicReassignActionEnum.MODIFY);
        Assert.assertEquals(resultStatus3.getCode(), ResultStatus.MYSQL_ERROR.getCode());
    }

    @Test(description = "修改子任务测试")
    public void modifySubTaskTest() {
        // 任务不存在
        modifySubTask2TaskNotExist();
        // 修改任务成功
        modifySubTask2Success();
        // 修改任务失败
        modifySubTask2MysqlError();
    }

    private void modifySubTask2TaskNotExist() {
        Mockito.when(reassignTaskDao.getSubTask(Mockito.anyLong())).thenReturn(null);
        ResultStatus resultStatus = reassignService.modifySubTask(new ReassignExecSubDTO());
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.TASK_NOT_EXIST.getCode());
    }

    private void modifySubTask2Success() {
        ReassignTaskDO reassignTask = getReassignTaskDO();
        Mockito.when(reassignTaskDao.getSubTask(Mockito.anyLong())).thenReturn(reassignTask);
        ReassignExecSubDTO reassignExecSubDTO = getReassignExecSubDTO();
        ResultStatus resultStatus = reassignService.modifySubTask(reassignExecSubDTO);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void modifySubTask2MysqlError() {
        ReassignTaskDO reassignTask = getReassignTaskDO();
        Mockito.when(reassignTaskDao.getSubTask(Mockito.anyLong())).thenReturn(reassignTask);
        Mockito.when(reassignTaskDao.updateById(Mockito.any())).thenThrow(RuntimeException.class);
        ReassignExecSubDTO reassignExecSubDTO = getReassignExecSubDTO();
        ResultStatus resultStatus = reassignService.modifySubTask(reassignExecSubDTO);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.MYSQL_ERROR.getCode());
    }

    @Test(description = "获取任务列表测试")
    public void getReassignTaskListTest() {
        // 获取成功
        getReassignTaskList2Success();
        // 获取失败
        getReassignTaskList2Empty();
    }

    private void getReassignTaskList2Success() {
        Mockito.when(reassignTaskDao.listAll()).thenReturn(Arrays.asList(new ReassignTaskDO()));
        List<ReassignTaskDO> reassignTaskList = reassignService.getReassignTaskList();
        Assert.assertFalse(reassignTaskList.isEmpty());
    }

    private void getReassignTaskList2Empty() {
        Mockito.when(reassignTaskDao.listAll()).thenThrow(RuntimeException.class);
        List<ReassignTaskDO> reassignTaskList = reassignService.getReassignTaskList();
        Assert.assertTrue(reassignTaskList.isEmpty());
    }

    @Test(description = "获取任务状态测试")
    public void getReassignStatusTest() {
        // 获取成功
        getReassignStatus2Success();
        // task不存在
        getReassignStatus2TaskNotExistTest();
    }

    private void getReassignStatus2TaskNotExistTest() {
        Mockito.when(reassignTaskDao.getByTaskId(Mockito.anyLong())).thenThrow(RuntimeException.class);
        Result<List<ReassignStatus>> reassignStatus = reassignService.getReassignStatus(1L);
        Assert.assertEquals(reassignStatus.getCode(), ResultStatus.TASK_NOT_EXIST.getCode());
    }

    private void getReassignStatus2Success() {
        ClusterDO clusterDO1 = getClusterDO();
        ClusterDO clusterDO2 = getClusterDO();
        clusterDO2.setId(100L);
        Map<Long, ClusterDO> map = new HashMap<>();
        map.put(clusterDO1.getId(), clusterDO1);
        map.put(clusterDO2.getId(), clusterDO2);
        Mockito.when(clusterService.listMap()).thenReturn(map);

        ReassignTaskDO reassignTaskDO1 = getReassignTaskDO();
        ReassignTaskDO reassignTaskDO2 = getReassignTaskDO();
        reassignTaskDO2.setStatus(TaskStatusReassignEnum.RUNNING.getCode());

        Mockito.when(reassignTaskDao.getByTaskId(Mockito.anyLong())).thenReturn(Arrays.asList(reassignTaskDO1, reassignTaskDO2));
        Result<List<ReassignStatus>> reassignStatus = reassignService.getReassignStatus(1L);
        Assert.assertFalse(reassignStatus.getData().isEmpty());
        Assert.assertEquals(reassignStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void verifyAssignmenTest() {
        Map<TopicAndPartition, TaskStatusReassignEnum> map = reassignService.verifyAssignment(ZOOKEEPER_ADDRESS, REASSIGNMENTJSON);
        Assert.assertFalse(map.isEmpty());
    }

}
