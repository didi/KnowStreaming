package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.CustomScheduledTaskDTO;
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
public class RdScheduledControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();
    }

    @Test(description = "测试获取调度任务列表")
    public void listScheduledTasksTest() {
        String url = baseUrl + "/api/v1/rd/scheduled-tasks";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试修改任务调度周期")
    public void modifyScheduledTasksTest() {
        String url = baseUrl + "/api/v1/rd/scheduled-tasks";

        CustomScheduledTaskDTO customScheduledTaskDTO = new CustomScheduledTaskDTO();
        customScheduledTaskDTO.setCron("");
        customScheduledTaskDTO.setName(ConfigConstant.ADMIN_USER);
        HttpEntity<CustomScheduledTaskDTO> httpEntity = new HttpEntity<>(customScheduledTaskDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试触发执行调度任务")
    public void runScheduledTask() {
        String url = baseUrl + "/api/v1/rd/scheduled-tasks/{scheduledName}/run";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("scheduledName", ConfigConstant.ADMIN_USER);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNull(result.getBody());
    }
}
