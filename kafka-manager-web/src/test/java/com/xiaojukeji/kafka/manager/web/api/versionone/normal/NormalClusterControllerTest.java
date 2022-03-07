package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
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
 * @Date 2022/1/14
 */
public class NormalClusterControllerTest extends BaseTest {

    private Long logicalClusterId;

    @BeforeClass
    public void init() {
        super.init();

        logicalClusterId = Long.parseLong(configMap.get(ConfigConstant.LOGICAL_CLUSTER_ID));
    }

    @Test(description = "测试获取集群列表")
    public void getLogicClusterVOListTest() {
        String url = baseUrl + "/api/v1/normal/clusters/basic-info";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());

        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试由逻辑集群id获取集群")
    public void getLogicClusterVOByIdTest() {
        // 获取成功
        getLogicClusterVOById1();
        // 集群不存在
        getLogicClusterVOById2();
    }

    private void getLogicClusterVOById1() {
        String url = baseUrl + "/api/v1/normal/clusters/{logicalClusterId}/basic-info";

        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("logicalClusterId", logicalClusterId);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void getLogicClusterVOById2() {
        String url = baseUrl + "/api/v1/normal/clusters/{logicalClusterId}/basic-info";

        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("logicalClusterId", ConfigConstant.INVALID_CLUSTER_ID);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    @Test(description = "测试由逻辑集群id获取集群元信息列表")
    public void getBrokerMetadataTest() {
        String url = baseUrl + "/api/v1/normal/clusters/{logicalClusterId}/broker-metadata";

        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("logicalClusterId", logicalClusterId);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试由逻辑集群id获取集群")
    public void getBrokersTest() {
        String url = baseUrl + "/api/v1/normal/clusters/{logicalClusterId}/brokers";

        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("logicalClusterId", logicalClusterId);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试由逻辑集群id获取集群实时流量")
    public void getMetricsTest() {
        String url = baseUrl + "/api/v1/normal/clusters/{logicalClusterId}/metrics";

        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("logicalClusterId", logicalClusterId);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试由逻辑集群id获取集群历史流量")
    public void getMetricsHistoryTest() {
        long now = System.currentTimeMillis();
        String url = baseUrl + "/api/v1/normal/clusters/{logicalClusterId}/metrics-history?startTime=0&endTime=" + now;

        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("logicalClusterId", logicalClusterId);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试由逻辑集群id获取集群限流信息")
    public void getThrottlesTest() {
        String url = baseUrl + "/api/v1/normal/clusters/{logicalClusterId}/throttles";

        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("logicalClusterId", logicalClusterId);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试由逻辑集群id获取集群topic元信息列表")
    public void getTopicMetadataTest() {
        String url = baseUrl + "/api/v1/normal/clusters/{logicalClusterId}/topic-metadata";

        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("logicalClusterId", logicalClusterId);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试由逻辑集群id获取集群列表")
    public void getTopicsTest() {
        String url = baseUrl + "/api/v1/normal/clusters/{logicalClusterId}/topics";

        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("logicalClusterId", logicalClusterId);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }
}
