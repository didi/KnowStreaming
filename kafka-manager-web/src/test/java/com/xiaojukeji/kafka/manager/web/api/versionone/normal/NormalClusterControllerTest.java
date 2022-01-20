package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import com.xiaojukeji.kafka.manager.web.config.Constant;
import com.xiaojukeji.kafka.manager.web.config.HttpUtils;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author xuguang
 * @Date 2022/1/14
 */
public class NormalClusterControllerTest extends BaseTest {

    private final TestRestTemplate testRestTemplate = new TestRestTemplate();

    @Test(description = "测试获取集群列表")
    public void getLogicClusterVOListTest() {
        String url = Constant.BASE_URL + "/api/v1/normal/clusters/basic-info";

        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());

        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试由逻辑集群id获取集群")
    public void getLogicClusterVOByIdTest() {
        String url = Constant.BASE_URL + "/api/v1/normal/clusters/{logicalClusterId}/basic-info";

        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }
}
