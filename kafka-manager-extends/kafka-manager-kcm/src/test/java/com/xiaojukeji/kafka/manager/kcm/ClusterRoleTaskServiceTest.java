package com.xiaojukeji.kafka.manager.kcm;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaBrokerRoleEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.CreationTaskData;
import com.xiaojukeji.kafka.manager.kcm.common.entry.dto.ClusterRoleTaskDTO;
import com.xiaojukeji.kafka.manager.kcm.config.BaseTest;
import com.xiaojukeji.kafka.manager.kcm.tasks.ClusterRoleTaskService;
import org.junit.Assert;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.*;

/**
 * @author xuguang
 * @Date 2022/1/5
 */
public class ClusterRoleTaskServiceTest extends BaseTest {

    @Autowired
    @InjectMocks
    private ClusterRoleTaskService clusterRoleTaskService;

    private ClusterRoleTaskDTO getClusterRoleTaskDTO() {
        ClusterRoleTaskDTO clusterRoleTaskDTO = new ClusterRoleTaskDTO();
        clusterRoleTaskDTO.setClusterId(-1L);
        clusterRoleTaskDTO.setTaskType("");
        return clusterRoleTaskDTO;
    }

    @Test
    public void getOperationHostsTest() {
        // controller not alive
        getOperationHosts2ControllerNotAliveTest();
        // success
        getOperationHosts2SuccessTest();
    }

    private void getOperationHosts2ControllerNotAliveTest() {
        ClusterRoleTaskDTO dto = getClusterRoleTaskDTO();
        List<String> upgradeSequenceList = new ArrayList<>();
        upgradeSequenceList.add(KafkaBrokerRoleEnum.CONTROLLER.getRole());
        dto.setUpgradeSequenceList(upgradeSequenceList);
        dto.setKafkaRoleBrokerHostMap(new HashMap<>(0));

        Result<CreationTaskData> result = clusterRoleTaskService.getOperationHosts(dto);
        Assert.assertEquals(result.getCode(), ResultStatus.CONTROLLER_NOT_ALIVE.getCode());
    }

    private void getOperationHosts2SuccessTest() {
        ClusterRoleTaskDTO dto = getClusterRoleTaskDTO();
        List<String> upgradeSequenceList = new ArrayList<>();
        upgradeSequenceList.add(KafkaBrokerRoleEnum.CONTROLLER.getRole());
        upgradeSequenceList.add(KafkaBrokerRoleEnum.NORMAL.getRole());
        upgradeSequenceList.add(KafkaBrokerRoleEnum.COORDINATOR.getRole());
        dto.setUpgradeSequenceList(upgradeSequenceList);
        Map<String, List<String>> map = new HashMap<>();
        List<String> controllerList = new ArrayList<>();
        controllerList.add("127.0.0.1");
        controllerList.add("localhost");
        List<String> coordinatorList = new ArrayList<>();
        coordinatorList.add("127.0.0.1");
        coordinatorList.add("localhost");
        map.put(KafkaBrokerRoleEnum.CONTROLLER.getRole(), controllerList);
        map.put(KafkaBrokerRoleEnum.COORDINATOR.getRole(), coordinatorList);
        dto.setKafkaRoleBrokerHostMap(map);

        Result<CreationTaskData> result = clusterRoleTaskService.getOperationHosts(dto);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }
}
