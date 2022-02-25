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
 * @Date 2022/2/21
 */
public class RdConsumerControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();
    }

    @Test(description = "测试获取集群ConsumerGroup列表")
    public void getConsumerGroupsTest() {
        String url = baseUrl + "/api/v1/rd/{clusterId}/consumer-groups";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> variableUrls = new HashMap<>();
        variableUrls.put("clusterId", configMap.get(ConfigConstant.PHYSICAL_CLUSTER_ID));
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, variableUrls);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取消费组消费的Topic列表")
    public void getTopicConsumerGroupsTest() {
        String url = baseUrl + "/api/v1/rd/{clusterId}/consumer-groups/{consumerGroup}/topics?location=broker";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> variableUrls = new HashMap<>();
        variableUrls.put("clusterId", configMap.get(ConfigConstant.PHYSICAL_CLUSTER_ID));
        variableUrls.put("consumerGroup", "test");
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, variableUrls);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }
}
