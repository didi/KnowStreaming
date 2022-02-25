package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskTypeEnum;
import com.xiaojukeji.kafka.manager.kcm.common.entry.dto.ClusterHostTaskDTO;
import com.xiaojukeji.kafka.manager.kcm.common.entry.dto.ClusterTaskActionDTO;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuguang
 * @Date 2022/2/23
 */
public class OpClusterTaskControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();
    }

    @Test(description = "测试创建集群任务")
    public void createClusterTaskTest() {
        createClusterTask();
    }

    private ClusterHostTaskDTO getClusterHostTaskDTO() {
        ClusterHostTaskDTO clusterHostTaskDTO = new ClusterHostTaskDTO();
        clusterHostTaskDTO.setClusterId(physicalClusterId);
        clusterHostTaskDTO.setHostList(Arrays.asList("127.0.0.1"));
        clusterHostTaskDTO.setTaskType(ClusterTaskTypeEnum.HOST_UPGRADE.getName());
        clusterHostTaskDTO.setKafkaFileBaseUrl("127.0.0.1");
        clusterHostTaskDTO.setKafkaPackageMd5("md5");
        clusterHostTaskDTO.setKafkaPackageName("name");
        clusterHostTaskDTO.setServerPropertiesMd5("md5");
        clusterHostTaskDTO.setServerPropertiesName("name");
        return clusterHostTaskDTO;
    }

    private void createClusterTask() {
        String url = baseUrl + "/api/v1/op/cluster-tasks";

        ClusterHostTaskDTO clusterHostTaskDTO = getClusterHostTaskDTO();
        HttpEntity<ClusterHostTaskDTO> httpEntity = new HttpEntity<>(clusterHostTaskDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试触发集群任务")
    public void startTaskTest() {
        String url = baseUrl + "/api/v1/op/cluster-tasks";

        ClusterTaskActionDTO clusterHostTaskDTO = new ClusterTaskActionDTO();
        clusterHostTaskDTO.setTaskId(-1L);
        clusterHostTaskDTO.setAction("action");
        HttpEntity<ClusterTaskActionDTO> httpEntity = new HttpEntity<>(clusterHostTaskDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.RESOURCE_NOT_EXIST.getCode());
    }

    @Test(description = "测试触发集群任务")
    public void listClusterTasksTest() {
        String url = baseUrl + "/api/v1/op/cluster-tasks";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取集群任务类型")
    public void listTaskEnumsTest() {
        String url = baseUrl + "/api/v1/op/cluster-tasks/enums";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试文件选择")
    public void listKafkaFilesTest() {
        String url = baseUrl + "/api/v1/op/cluster-tasks/kafka-files?clusterId=" + physicalClusterId;

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取集群任务日志")
    public void getKafkaTaskLogsTest() {
        String url = baseUrl + "/api/v1/op/cluster-tasks/{taskId}/log?hostname=127.0.0.1";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("taskId", -1L);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.TASK_NOT_EXIST.getCode());
    }

    @Test(description = "测试获取集群任务元信息")
    public void getTaskMetadataTest() {
        String url = baseUrl + "/api/v1/op/cluster-tasks/{taskId}/metadata";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("taskId", -1L);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.RESOURCE_NOT_EXIST.getCode());
    }

    @Test(description = "测试获取集群任务状态")
    public void getTaskStatusTest() {
        String url = baseUrl + "/api/v1/op/cluster-tasks/{taskId}/metadata";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("taskId", -1L);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.RESOURCE_NOT_EXIST.getCode());
    }
}
