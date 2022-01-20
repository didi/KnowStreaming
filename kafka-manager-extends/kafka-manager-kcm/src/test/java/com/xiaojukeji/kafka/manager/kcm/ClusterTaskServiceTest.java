package com.xiaojukeji.kafka.manager.kcm;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterTaskDO;
import com.xiaojukeji.kafka.manager.dao.ClusterTaskDao;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskActionEnum;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskStateEnum;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskTypeEnum;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.ClusterTaskLog;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.ClusterTaskStatus;
import com.xiaojukeji.kafka.manager.kcm.common.entry.dto.AbstractClusterTaskDTO;
import com.xiaojukeji.kafka.manager.kcm.common.entry.dto.ClusterHostTaskDTO;
import com.xiaojukeji.kafka.manager.kcm.component.agent.AbstractAgent;
import com.xiaojukeji.kafka.manager.kcm.config.BaseTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * @author xuguang
 * @Date 2021/12/27
 */
public class ClusterTaskServiceTest extends BaseTest {

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    @Value("${test.admin}")
    private String ADMIN;

    private static final String BASEURL = "127.0.0.1";

    private static final String MD5 = "md5";

    private static final Long REAL_TASK_ID_IN_MYSQL = 1L;

    private static final Long INVALID_TASK_ID = -1L;


    @Autowired
    @InjectMocks
    private ClusterTaskService clusterTaskService;

    @Mock
    private AbstractAgent abstractAgent;

    @Mock
    private ClusterTaskDao clusterTaskDao;

    private ClusterHostTaskDTO getClusterHostTaskDTO() {
        ClusterHostTaskDTO clusterHostTaskDTO = new ClusterHostTaskDTO();
        clusterHostTaskDTO.setClusterId(-1L);
        clusterHostTaskDTO.setHostList(Arrays.asList(BASEURL));
        clusterHostTaskDTO.setTaskType(ClusterTaskTypeEnum.HOST_UPGRADE.getName());
        clusterHostTaskDTO.setKafkaFileBaseUrl(BASEURL);
        clusterHostTaskDTO.setKafkaPackageMd5(MD5);
        clusterHostTaskDTO.setKafkaPackageName("name");
        clusterHostTaskDTO.setServerPropertiesMd5(MD5);
        clusterHostTaskDTO.setServerPropertiesName("name");
        return clusterHostTaskDTO;
    }

    private ClusterTaskDO getClusterTaskDO() {
        ClusterTaskDO clusterTaskDO = new ClusterTaskDO();
        clusterTaskDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        clusterTaskDO.setId(REAL_TASK_ID_IN_MYSQL);
        clusterTaskDO.setAgentTaskId(-1L);
        clusterTaskDO.setAgentRollbackTaskId(-1L);
        clusterTaskDO.setTaskStatus(0);
        return clusterTaskDO;
    }

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(description = "测试创建任务")
    public void createTaskTest() {
        // paramIllegal
        createTask2ParamIllegalTest();
        // not success
        createTask2NotSuccessTest();
        // CallClusterTaskAgentFailed
        createTask2CallClusterTaskAgentFailedTest();
        // success
        createTask2SuccessTest();
        // mysqlError
        createTask2MysqlErrorTest();
    }

    private void createTask2ParamIllegalTest() {
        AbstractClusterTaskDTO dto = getClusterHostTaskDTO();
        dto.setTaskType(null);
        Result result1 = clusterTaskService.createTask(dto, ADMIN);
        Assert.assertEquals(result1.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());

        dto.setTaskType("");
        Result result2 = clusterTaskService.createTask(dto, ADMIN);
        Assert.assertEquals(result2.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void createTask2NotSuccessTest() {
        ClusterHostTaskDTO clusterHostTaskDTO = getClusterHostTaskDTO();
        clusterHostTaskDTO.setHostList(Arrays.asList("host"));
        Result result = clusterTaskService.createTask(clusterHostTaskDTO, ADMIN);
        Assert.assertEquals(result.getCode(), ResultStatus.CLUSTER_TASK_HOST_LIST_ILLEGAL.getCode());
    }

    private void createTask2CallClusterTaskAgentFailedTest() {
        Mockito.when(abstractAgent.createTask(Mockito.any())).thenReturn(null);

        ClusterHostTaskDTO clusterHostTaskDTO = getClusterHostTaskDTO();
        Result result = clusterTaskService.createTask(clusterHostTaskDTO, ADMIN);
        Assert.assertEquals(result.getCode(), ResultStatus.CALL_CLUSTER_TASK_AGENT_FAILED.getCode());
    }

    private void createTask2SuccessTest() {
        Mockito.when(abstractAgent.createTask(Mockito.any())).thenReturn(Result.buildSuc(1L));
        Mockito.when(clusterTaskDao.insert(Mockito.any())).thenReturn(1);

        ClusterHostTaskDTO clusterHostTaskDTO = getClusterHostTaskDTO();
        Result result = clusterTaskService.createTask(clusterHostTaskDTO, ADMIN);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void createTask2MysqlErrorTest() {
        Mockito.when(abstractAgent.createTask(Mockito.any())).thenReturn(Result.buildSuc(1L));
        Mockito.when(clusterTaskDao.insert(Mockito.any())).thenThrow(RuntimeException.class);

        ClusterHostTaskDTO clusterHostTaskDTO = getClusterHostTaskDTO();
        Result result = clusterTaskService.createTask(clusterHostTaskDTO, ADMIN);
        Assert.assertEquals(result.getCode(), ResultStatus.MYSQL_ERROR.getCode());
    }

    @Test
    public void executeTaskTest() {
        // task not exist
        executeTask2TaskNotExistTest();
        // CallClusterTaskAgentFailed
        executeTask2CallClusterTaskAgentFailedTest();
        // 暂停状态, 可以执行开始
        executeTask2StartTest();
        // 运行状态, 可以执行暂停
        executeTask2PauseTest();
        // 忽略
        executeTask2IgnoreTest();
        // 取消
        executeTask2CancelTest();
        // operation failed
        executeTask2OperationFailedTest();
        // 回滚 operation forbidden
        executeTask2RollbackForbiddenTest();
    }

    private void executeTask2TaskNotExistTest() {
        ResultStatus resultStatus = clusterTaskService.executeTask(INVALID_TASK_ID, ClusterTaskActionEnum.START.getAction(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.RESOURCE_NOT_EXIST.getCode());
    }

    private void executeTask2CallClusterTaskAgentFailedTest() {
        Mockito.when(abstractAgent.getTaskExecuteState(Mockito.anyLong())).thenReturn(null);
        ClusterTaskDO clusterTaskDO = getClusterTaskDO();
        Mockito.when(clusterTaskDao.getById(Mockito.anyLong())).thenReturn(clusterTaskDO);

        ResultStatus resultStatus = clusterTaskService.executeTask(REAL_TASK_ID_IN_MYSQL, ClusterTaskActionEnum.START.getAction(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.CALL_CLUSTER_TASK_AGENT_FAILED.getCode());
    }

    private void executeTask2StartTest() {
        Mockito.when(abstractAgent.getTaskExecuteState(Mockito.anyLong())).thenReturn(Result.buildSuc(ClusterTaskStateEnum.BLOCKED));
        ClusterTaskDO clusterTaskDO = getClusterTaskDO();
        Mockito.when(clusterTaskDao.getById(Mockito.anyLong())).thenReturn(clusterTaskDO);

        // success
        Mockito.when(abstractAgent.actionTask(Mockito.anyLong(), Mockito.any())).thenReturn(true);
        ResultStatus resultStatus = clusterTaskService.executeTask(REAL_TASK_ID_IN_MYSQL, ClusterTaskActionEnum.START.getAction(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());

        // operation failed
        Mockito.when(abstractAgent.actionTask(Mockito.anyLong(), Mockito.any())).thenReturn(false);
        ResultStatus resultStatus2 = clusterTaskService.executeTask(REAL_TASK_ID_IN_MYSQL, ClusterTaskActionEnum.START.getAction(), ADMIN);
        Assert.assertEquals(resultStatus2.getCode(), ResultStatus.OPERATION_FAILED.getCode());
    }

    private void executeTask2PauseTest() {
        Mockito.when(abstractAgent.getTaskExecuteState(Mockito.anyLong())).thenReturn(Result.buildSuc(ClusterTaskStateEnum.RUNNING));
        ClusterTaskDO clusterTaskDO = getClusterTaskDO();
        Mockito.when(clusterTaskDao.getById(Mockito.anyLong())).thenReturn(clusterTaskDO);

        // success
        Mockito.when(abstractAgent.actionTask(Mockito.anyLong(), Mockito.any())).thenReturn(true);
        ResultStatus resultStatus = clusterTaskService.executeTask(REAL_TASK_ID_IN_MYSQL, ClusterTaskActionEnum.PAUSE.getAction(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());

        // operation failed
        Mockito.when(abstractAgent.actionTask(Mockito.anyLong(), Mockito.any())).thenReturn(false);
        ResultStatus resultStatus2 = clusterTaskService.executeTask(REAL_TASK_ID_IN_MYSQL, ClusterTaskActionEnum.PAUSE.getAction(), ADMIN);
        Assert.assertEquals(resultStatus2.getCode(), ResultStatus.OPERATION_FAILED.getCode());
    }

    private void executeTask2IgnoreTest() {
        Mockito.when(abstractAgent.getTaskExecuteState(Mockito.anyLong())).thenReturn(Result.buildSuc(ClusterTaskStateEnum.RUNNING));
        ClusterTaskDO clusterTaskDO = getClusterTaskDO();
        Mockito.when(clusterTaskDao.getById(Mockito.anyLong())).thenReturn(clusterTaskDO);

        // success
        Mockito.when(abstractAgent.actionTask(Mockito.anyLong(), Mockito.any())).thenReturn(true);
        ResultStatus resultStatus = clusterTaskService.executeTask(REAL_TASK_ID_IN_MYSQL, ClusterTaskActionEnum.IGNORE.getAction(), "");
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());

        // operation failed
        Mockito.when(abstractAgent.actionTask(Mockito.anyLong(), Mockito.any())).thenReturn(false);
        ResultStatus resultStatus2 = clusterTaskService.executeTask(REAL_TASK_ID_IN_MYSQL, ClusterTaskActionEnum.IGNORE.getAction(), "");
        Assert.assertEquals(resultStatus2.getCode(), ResultStatus.OPERATION_FAILED.getCode());
    }

    private void executeTask2CancelTest() {
        Mockito.when(abstractAgent.getTaskExecuteState(Mockito.anyLong())).thenReturn(Result.buildSuc(ClusterTaskStateEnum.RUNNING));
        ClusterTaskDO clusterTaskDO = getClusterTaskDO();
        Mockito.when(clusterTaskDao.getById(Mockito.anyLong())).thenReturn(clusterTaskDO);

        // success
        Mockito.when(abstractAgent.actionTask(Mockito.anyLong(), Mockito.any())).thenReturn(true);
        ResultStatus resultStatus = clusterTaskService.executeTask(REAL_TASK_ID_IN_MYSQL, ClusterTaskActionEnum.CANCEL.getAction(), "");
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());

        // operation failed
        Mockito.when(abstractAgent.actionTask(Mockito.anyLong(), Mockito.any())).thenReturn(false);
        ResultStatus resultStatus2 = clusterTaskService.executeTask(REAL_TASK_ID_IN_MYSQL, ClusterTaskActionEnum.CANCEL.getAction(), "");
        Assert.assertEquals(resultStatus2.getCode(), ResultStatus.OPERATION_FAILED.getCode());
    }

    private void executeTask2OperationFailedTest() {
        Mockito.when(abstractAgent.getTaskExecuteState(Mockito.anyLong())).thenReturn(Result.buildSuc(ClusterTaskStateEnum.RUNNING));
        ClusterTaskDO clusterTaskDO = getClusterTaskDO();
        Mockito.when(clusterTaskDao.getById(Mockito.anyLong())).thenReturn(clusterTaskDO);

        // operation failed
        ResultStatus resultStatus2 = clusterTaskService.executeTask(REAL_TASK_ID_IN_MYSQL, ClusterTaskActionEnum.START.getAction(), ADMIN);
        Assert.assertEquals(resultStatus2.getCode(), ResultStatus.OPERATION_FAILED.getCode());
    }

    private void executeTask2RollbackForbiddenTest() {
        Mockito.when(abstractAgent.getTaskExecuteState(Mockito.anyLong())).thenReturn(Result.buildSuc(ClusterTaskStateEnum.RUNNING));
        ClusterTaskDO clusterTaskDO = getClusterTaskDO();
        clusterTaskDO.setAgentRollbackTaskId(1L);
        Mockito.when(clusterTaskDao.getById(Mockito.anyLong())).thenReturn(clusterTaskDO);

        // operation failed
        ResultStatus resultStatus2 = clusterTaskService.executeTask(REAL_TASK_ID_IN_MYSQL, ClusterTaskActionEnum.ROLLBACK.getAction(), ADMIN);
        Assert.assertEquals(resultStatus2.getCode(), ResultStatus.OPERATION_FORBIDDEN.getCode());
    }

    @Test
    public void getTaskLogTest() {
        // task not exist
        getTaskLog2TaskNotExistTest();
        // call cluster task agent failed
        getTaskLog2CallClusterTaskAgentFailedTest();
        // success
        getTaskLog2SuccessTest();
    }

    private void getTaskLog2TaskNotExistTest() {
        Mockito.when(clusterTaskDao.getById(Mockito.anyLong())).thenReturn(null);

        Result<String> result = clusterTaskService.getTaskLog(REAL_TASK_ID_IN_MYSQL, ADMIN);
        Assert.assertEquals(result.getCode(), ResultStatus.TASK_NOT_EXIST.getCode());
    }

    private void getTaskLog2CallClusterTaskAgentFailedTest() {
        ClusterTaskDO clusterTaskDO = getClusterTaskDO();
        Mockito.when(clusterTaskDao.getById(Mockito.anyLong())).thenReturn(clusterTaskDO);
        Mockito.when(abstractAgent.getTaskLog(Mockito.anyLong(), Mockito.any())).thenReturn(null);

        Result<String> result = clusterTaskService.getTaskLog(REAL_TASK_ID_IN_MYSQL, ADMIN);
        Assert.assertEquals(result.getCode(), ResultStatus.CALL_CLUSTER_TASK_AGENT_FAILED.getCode());
    }

    private void getTaskLog2SuccessTest() {
        ClusterTaskDO clusterTaskDO = getClusterTaskDO();
        Mockito.when(clusterTaskDao.getById(Mockito.anyLong())).thenReturn(clusterTaskDO);
        Mockito.when(abstractAgent.getTaskLog(Mockito.anyLong(), Mockito.any()))
                .thenReturn(Result.buildSuc(new ClusterTaskLog("")));

        Result<String> result = clusterTaskService.getTaskLog(REAL_TASK_ID_IN_MYSQL, ADMIN);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void getTaskStateTest() {
        // null
        getTaskState2NullTest();
        //
        getTaskState2NotNullTest();
    }

    private void getTaskState2NullTest() {
        Mockito.when(abstractAgent.getTaskExecuteState(Mockito.anyLong())).thenReturn(null);

        ClusterTaskStateEnum taskState = clusterTaskService.getTaskState(REAL_TASK_ID_IN_MYSQL);
        Assert.assertNull(taskState);
    }

    private void getTaskState2NotNullTest() {

        Mockito.when(abstractAgent.getTaskExecuteState(Mockito.anyLong())).thenReturn(Result.buildSuc(ClusterTaskStateEnum.RUNNING));

        ClusterTaskStateEnum taskState = clusterTaskService.getTaskState(REAL_TASK_ID_IN_MYSQL);
        Assert.assertNotNull(taskState);
    }

    @Test
    public void getTaskStatusTest() {
        // task not exist
        getTaskStatus2TaskNotExistTest();
        // get failed
        getTaskStatus2FailedTest();
        // success
        getTaskStatus2SuccessTest();
    }

    private void getTaskStatus2TaskNotExistTest() {
        Mockito.when(clusterTaskDao.getById(Mockito.anyLong())).thenReturn(null);

        Result<ClusterTaskStatus> result = clusterTaskService.getTaskStatus(REAL_TASK_ID_IN_MYSQL);
        Assert.assertEquals(result.getCode(), ResultStatus.TASK_NOT_EXIST.getCode());
    }

    private void getTaskStatus2FailedTest() {
        ClusterTaskDO clusterTaskDO = getClusterTaskDO();
        Mockito.when(clusterTaskDao.getById(Mockito.anyLong())).thenReturn(clusterTaskDO);
        Mockito.when(abstractAgent.getTaskExecuteState(Mockito.anyLong())).thenReturn(Result.buildFailure(""));

        Result<ClusterTaskStatus> result = clusterTaskService.getTaskStatus(REAL_TASK_ID_IN_MYSQL);
        Assert.assertEquals(result.getCode(), ResultStatus.FAIL.getCode());
    }

    private void getTaskStatus2SuccessTest() {
        ClusterTaskDO clusterTaskDO = getClusterTaskDO();
        Mockito.when(clusterTaskDao.getById(Mockito.anyLong())).thenReturn(clusterTaskDO);
        Mockito.when(abstractAgent.getTaskExecuteState(Mockito.anyLong()))
                .thenReturn(Result.buildSuc(ClusterTaskStateEnum.RUNNING));

        Result<ClusterTaskStatus> result = clusterTaskService.getTaskStatus(REAL_TASK_ID_IN_MYSQL);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }
}
