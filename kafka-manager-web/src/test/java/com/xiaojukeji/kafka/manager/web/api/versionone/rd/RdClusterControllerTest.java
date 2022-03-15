package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
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
public class RdClusterControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();
    }

    @Test(description = "获取集群基本信息列表")
    public void getClustersBasicInfo() {
        String url = baseUrl + "/api/v1/rd/clusters/basic-info?need-detail=true";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "获取集群基本信息")
    public void getClusterBasicInfo() {
        String url = baseUrl + "/api/v1/rd/clusters/{clusterId}/basic-info?need-detail=true";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", physicalClusterId);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "获取集群Broker列表")
    public void getClusterBrokers() {
        String url = baseUrl + "/api/v1/rd/clusters/{clusterId}/brokers";

        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", physicalClusterId);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "获取集群Broker状态")
    public void getClusterBrokersStatus() {
        String url = baseUrl + "/api/v1/rd/clusters/{clusterId}/brokers-status";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", physicalClusterId);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "获取集群Controller变更历史")
    public void getClusterControllerHistory() {
        String url = baseUrl + "/api/v1/rd/clusters/{clusterId}/controller-history";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", physicalClusterId);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "获取集群Controller优先候选的Broker")
    public void getClusterControllerPreferredCandidates() {
        String url = baseUrl + "/api/v1/rd/clusters/{clusterId}/controller-preferred-candidates";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", physicalClusterId);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "获取集群实时流量")
    public void getClusterMetrics() {
        String url = baseUrl + "/api/v1/rd/clusters/{clusterId}/metrics";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", physicalClusterId);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "获取集群历史流量")
    public void getClusterHistoryMetrics() {
        long now = System.currentTimeMillis();
        String url = baseUrl + "/api/v1/rd/clusters/{clusterId}/metrics-history?startTime=0&endTime=" + now;

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", physicalClusterId);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "获取集群限流信息")
    public void getClusterThrottles() {
        String url = baseUrl + "/api/v1/rd/clusters/{clusterId}/throttles";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", physicalClusterId);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "获取集群Topic元信息列表")
    public void getClusterTopicMetadata() {
        String url = baseUrl + "/api/v1/rd/clusters/{clusterId}/topic-metadata";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", physicalClusterId);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "获取集群Topic列表")
    public void getClusterTopics() {
        String url = baseUrl + "/api/v1/rd/clusters/{clusterId}/topics";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", physicalClusterId);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }
}
