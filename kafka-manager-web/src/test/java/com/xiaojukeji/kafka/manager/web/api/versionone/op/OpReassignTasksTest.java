package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignExecDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignExecSubDTO;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import com.xiaojukeji.kafka.manager.web.config.ConfigConstant;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xuguang
 * @Date 2022/2/24
 */
public class OpReassignTasksTest extends BaseTest {
    @BeforeClass
    public void init() {
        super.init();
    }

    @Test(description = "测试创建迁移任务")
    public void createReassignTasksTest() {
        String url = baseUrl + "/api/v1/op/reassign-tasks";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取迁移任务列表")
    public void getReassignTaskListTest() {
        String url = baseUrl + "/api/v1/op/reassign-tasks";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取迁移任务信息")
    public void getReassignTaskDetailTest() {
        String url = baseUrl + "/api/v1/op/reassign-tasks/{taskId}/detail";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("taskId", ConfigConstant.INVALID_ID);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取迁移任务信息")
    public void getReassignTaskStatusTest() {
        String url = baseUrl + "/api/v1/op/reassign-tasks/{taskId}/status";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("taskId", ConfigConstant.INVALID_ID);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试操作迁移任务")
    public void operateReassignTaskTest() {
        String url = baseUrl + "/api/v1/op/reassign-tasks/{taskId}/status";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("taskId", ConfigConstant.INVALID_ID);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试操作迁移任务")
    public void reassignTasks() {
        String url = baseUrl + "/api/v1/op/reassign-tasks";

        ReassignExecDTO reassignExecDTO = new ReassignExecDTO();
        reassignExecDTO.setTaskId(-1L);
        reassignExecDTO.setAction("cancel");
        long now = System.currentTimeMillis();
        reassignExecDTO.setBeginTime(now);
        HttpEntity<ReassignExecDTO> httpEntity = new HttpEntity<>(reassignExecDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.OPERATION_FORBIDDEN.getCode());
    }

    @Test(description = "测试操作迁移子任务")
    public void reassignSubTasks() {
        String url = baseUrl + "/api/v1/op/reassign-tasks/sub-tasks";

        ReassignExecSubDTO reassignExecSubDTO = new ReassignExecSubDTO();
        reassignExecSubDTO.setAction("cancel");
        reassignExecSubDTO.setSubTaskId(-1L);

        HttpEntity<ReassignExecSubDTO> httpEntity = new HttpEntity<>(reassignExecSubDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }
}
