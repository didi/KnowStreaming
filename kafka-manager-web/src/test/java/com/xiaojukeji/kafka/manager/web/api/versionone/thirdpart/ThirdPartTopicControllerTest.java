package com.xiaojukeji.kafka.manager.web.api.versionone.thirdpart;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicCreationDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicDeletionDTO;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import com.xiaojukeji.kafka.manager.web.config.ConfigConstant;
import com.xiaojukeji.kafka.manager.web.config.CustomDataSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuguang
 * @Date 2022/2/18
 */
public class ThirdPartTopicControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();

        // 成功创建Topic
        String url = baseUrl + "/api/v1/op/topics";
        createCommonTopic3Test(url);
    }


    @AfterClass
    public void afterTest() {
        // 删除Topic成功
        String url = baseUrl + "/api/v1/op/topics";
        deleteTopics3Test(url);
    }

    private void createCommonTopic3Test(String url) {
        // 创建Topic

        TopicCreationDTO creationDTO = CustomDataSource.getTopicCreationDTO(configMap);
        HttpEntity<TopicCreationDTO> httpEntity = new HttpEntity<>(creationDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void deleteTopics3Test(String url) {
        // 删除创建的topic
        TopicDeletionDTO topicDeletionDTO = CustomDataSource.getTopicDeletionDTO(configMap);
        HttpEntity<List<TopicDeletionDTO>> httpEntity2 = new HttpEntity<>(Arrays.asList(topicDeletionDTO), httpHeaders);
        ResponseEntity<Result> result2 = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity2, Result.class);
        Assert.assertEquals(result2.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result2.getBody());
        Assert.assertEquals(result2.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取Topic元信息")
    public void getTopicMetadataTest() {
        String url = baseUrl + "/api/v1/third-part/clusters/{clusterId}/topics/{topicName}/metadata";
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", physicalClusterId);
        urlVariables.put("topicName", configMap.get(ConfigConstant.TOPIC_NAME));
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取Topic应用信息")
    public void getTopicAppsTest() {
        String url = baseUrl + "/api/v1/third-part/{physicalClusterId}/topics/{topicName}/apps";
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("physicalClusterId", physicalClusterId);
        urlVariables.put("topicName", configMap.get(ConfigConstant.TOPIC_NAME));
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试查询Topic的消费组列表")
    public void getTopicConsumerGroupsTest() {
        String url = baseUrl + "/api/v1/third-part/{physicalClusterId}/topics/{topicName}/consumer-groups";
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("physicalClusterId", physicalClusterId);
        urlVariables.put("topicName", configMap.get(ConfigConstant.TOPIC_NAME));
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试查询Topic实时流量信息")
    public void getTopicMetricsTest() {
        String url = baseUrl + "/api/v1/third-part/{physicalClusterId}/topics/{topicName}/metrics";
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("physicalClusterId", physicalClusterId);
        urlVariables.put("topicName", configMap.get(ConfigConstant.TOPIC_NAME));
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试查询Topic是否有流量")
    public void getTopicOffsetChangedTest() {
        long now = System.currentTimeMillis();
        String url = baseUrl + "/api/v1/third-part/{physicalClusterId}/topics/{topicName}/offset-changed?latest-time=" + now;
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("physicalClusterId", physicalClusterId);
        urlVariables.put("topicName", configMap.get(ConfigConstant.TOPIC_NAME));
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试查询Topic实时请求耗时信息")
    public void getTopicRequestTimeTest() {
        String url = baseUrl + "/api/v1/third-part/{physicalClusterId}/topics/{topicName}/request-time";
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("physicalClusterId", physicalClusterId);
        urlVariables.put("topicName", configMap.get(ConfigConstant.TOPIC_NAME));
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

}
