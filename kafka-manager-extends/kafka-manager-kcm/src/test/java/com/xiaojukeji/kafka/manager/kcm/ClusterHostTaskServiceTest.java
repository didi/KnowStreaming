package com.xiaojukeji.kafka.manager.kcm;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.CreationTaskData;
import com.xiaojukeji.kafka.manager.kcm.common.entry.dto.ClusterHostTaskDTO;
import com.xiaojukeji.kafka.manager.kcm.config.BaseTest;
import com.xiaojukeji.kafka.manager.kcm.tasks.ClusterHostTaskService;
import org.junit.Assert;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * @author xuguang
 * @Date 2022/1/5
 */
public class ClusterHostTaskServiceTest extends BaseTest {

    @Autowired
    @InjectMocks
    private ClusterHostTaskService clusterHostTaskService;

    private ClusterHostTaskDTO getClusterHostTaskDTO() {
        ClusterHostTaskDTO clusterHostTaskDTO = new ClusterHostTaskDTO();
        clusterHostTaskDTO.setClusterId(-1L);
        clusterHostTaskDTO.setHostList(Arrays.asList("127.0.0.1"));
        clusterHostTaskDTO.setTaskType("");
        clusterHostTaskDTO.setKafkaFileBaseUrl("");
        clusterHostTaskDTO.setKafkaPackageMd5("");
        clusterHostTaskDTO.setKafkaPackageName("");
        clusterHostTaskDTO.setServerPropertiesMd5("");
        clusterHostTaskDTO.setServerPropertiesName("");
        return clusterHostTaskDTO;
    }

    @Test
    public void getOperationHostsTest() {
        // cluster task host list illegal
        getOperationHosts2HostListIllegalTest();
        // success
        getOperationHosts2SuccessTest();
    }

    private void getOperationHosts2HostListIllegalTest() {
        ClusterHostTaskDTO clusterHostTaskDTO = getClusterHostTaskDTO();
        clusterHostTaskDTO.setHostList(Arrays.asList(""));
        Result<CreationTaskData> result = clusterHostTaskService.getOperationHosts(clusterHostTaskDTO);
        Assert.assertEquals(result.getCode(), ResultStatus.CLUSTER_TASK_HOST_LIST_ILLEGAL.getCode());
    }

    private void getOperationHosts2SuccessTest() {
        ClusterHostTaskDTO clusterHostTaskDTO = getClusterHostTaskDTO();
        Result<CreationTaskData> result = clusterHostTaskService.getOperationHosts(clusterHostTaskDTO);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void getCreateTaskParamDTOTest() {
        // not success
        getCreateTaskParamDTO2NotSuccessTest();
        // success
        getCreateTaskParamDTO2SuccessTest();
    }

    private void getCreateTaskParamDTO2NotSuccessTest() {
        ClusterHostTaskDTO clusterHostTaskDTO = getClusterHostTaskDTO();
        clusterHostTaskDTO.setHostList(Arrays.asList(""));
        Result<CreationTaskData> dto = clusterHostTaskService.getCreateTaskParamDTO(clusterHostTaskDTO);
        Assert.assertEquals(dto.getCode(), ResultStatus.CLUSTER_TASK_HOST_LIST_ILLEGAL.getCode());
    }

    private void getCreateTaskParamDTO2SuccessTest() {
        ClusterHostTaskDTO clusterHostTaskDTO = getClusterHostTaskDTO();
        Result<CreationTaskData> dto = clusterHostTaskService.getCreateTaskParamDTO(clusterHostTaskDTO);
        Assert.assertEquals(dto.getCode(), ResultStatus.SUCCESS.getCode());
    }
}
