package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

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
 * @Date 2022/2/23
 */
public class RdBrokerControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();
    }

    @Test(description = "测试获取broker基本信息列表")
    public void getBrokersBasicInfo() {
        String url = baseUrl + "/api/v1/rd/clusters/{clusterId}/brokers/basic-info";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> variableUrls = new HashMap<>();
        variableUrls.put("clusterId", physicalClusterId);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, variableUrls);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());

    }

    @Test(description = "测试获取broker元信息")
    public void getBrokerMetadata() {
        String url = baseUrl + "/api/v1/rd/{clusterId}/brokers/broker-metadata";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> variableUrls = new HashMap<>();
        variableUrls.put("clusterId", physicalClusterId);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, variableUrls);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试brokerTopic分析")
    public void getBrokerTopicAnalysis() {
        String url = baseUrl + "/api/v1/rd/{clusterId}/brokers/{brokerId}/analysis";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> variableUrls = new HashMap<>();
        variableUrls.put("clusterId", physicalClusterId);
        variableUrls.put("brokerId", configMap.get(ConfigConstant.ALIVE_BROKER_ID));
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, variableUrls);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取broker基本信息")
    public void getBrokerBasicInfo() {
        String url = baseUrl + "/api/v1/rd/{clusterId}/brokers/{brokerId}/basic-info";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> variableUrls = new HashMap<>();
        variableUrls.put("clusterId", physicalClusterId);
        variableUrls.put("brokerId", configMap.get(ConfigConstant.ALIVE_BROKER_ID));
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, variableUrls);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取broker实时流量")
    public void getBrokerMetrics() {
        String url = baseUrl + "/api/v1/rd/{clusterId}/brokers/{brokerId}/metrics";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> variableUrls = new HashMap<>();
        variableUrls.put("clusterId", physicalClusterId);
        variableUrls.put("brokerId", configMap.get(ConfigConstant.ALIVE_BROKER_ID));
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, variableUrls);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取broker历史指标")
    public void getBrokerHistoryMetrics() {
        long now = System.currentTimeMillis();
        String url = baseUrl + "/api/v1/rd/{clusterId}/brokers/{brokerId}/metrics-history?startTime=0&endTime=" + now;

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> variableUrls = new HashMap<>();
        variableUrls.put("clusterId", physicalClusterId);
        variableUrls.put("brokerId", configMap.get(ConfigConstant.ALIVE_BROKER_ID));
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, variableUrls);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取broker分区信息")
    public void getBrokerPartitions() {
        String url = baseUrl + "/api/v1/rd/{clusterId}/brokers/{brokerId}/partitions";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> variableUrls = new HashMap<>();
        variableUrls.put("clusterId", physicalClusterId);
        variableUrls.put("brokerId", configMap.get(ConfigConstant.ALIVE_BROKER_ID));
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, variableUrls);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取broker磁盘分区")
    public void getBrokerPartitionsLocation() {
        String url = baseUrl + "/api/v1/rd/{clusterId}/brokers/{brokerId}/partitions-location";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> variableUrls = new HashMap<>();
        variableUrls.put("clusterId", physicalClusterId);
        variableUrls.put("brokerId", configMap.get(ConfigConstant.ALIVE_BROKER_ID));
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, variableUrls);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取brokerTopic信息")
    public void getBrokerPartitionsTopic() {
        String url = baseUrl + "/api/v1/rd/{clusterId}/brokers/{brokerId}/topics";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> variableUrls = new HashMap<>();
        variableUrls.put("clusterId", physicalClusterId);
        variableUrls.put("brokerId", configMap.get(ConfigConstant.ALIVE_BROKER_ID));
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, variableUrls);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试删除broker")
    public void deleteBroker() {
        String url = baseUrl + "/api/v1/rd/{clusterId}/brokers?brokerId=-1";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> variableUrls = new HashMap<>();
        variableUrls.put("clusterId", physicalClusterId);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Result.class, variableUrls);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.OPERATION_FAILED.getCode());
    }
}
