package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.TopicModifyDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.TopicRetainDTO;
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
import java.util.List;

/**
 * @author xuguang
 * @Date 2022/2/21
 */
public class NormalTopicMineControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();

        String url = baseUrl + "/api/v1/op/topics";
        createCommonTopic(url);
    }

    @AfterClass
    public void afterTest() {
        // 删除Topic成功
        String url = baseUrl + "/api/v1/op/topics";
        deleteTopics(url);
    }

    private void createCommonTopic(String url) {
        // 创建Topic

        TopicCreationDTO creationDTO = CustomDataSource.getTopicCreationDTO(configMap);
        HttpEntity<TopicCreationDTO> httpEntity = new HttpEntity<>(creationDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void deleteTopics(String url) {
        // 删除创建的topic
        TopicDeletionDTO topicDeletionDTO = CustomDataSource.getTopicDeletionDTO(configMap);
        HttpEntity<List<TopicDeletionDTO>> httpEntity2 = new HttpEntity<>(Arrays.asList(topicDeletionDTO), httpHeaders);
        ResponseEntity<Result> result2 = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity2, Result.class);
        Assert.assertEquals(result2.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result2.getBody());
        Assert.assertEquals(result2.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取全部Topic")
    public void getAllTopicsTest() {
        String url = baseUrl + "/api/v1/normal/topics";

        HttpEntity<String> httpEntity2 = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result2 = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity2, Result.class);
        Assert.assertEquals(result2.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result2.getBody());
        Assert.assertEquals(result2.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取全部Topic")
    public void getMyTopicsTest() {
        String url = baseUrl + "/api/v1/normal/topics/mine";

        HttpEntity<String> httpEntity2 = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result2 = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity2, Result.class);
        Assert.assertEquals(result2.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result2.getBody());
        Assert.assertEquals(result2.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取全部Topic")
    public void getExpiredTopicsTest() {
        String url = baseUrl + "/api/v1/normal/topics/expired";

        HttpEntity<String> httpEntity2 = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result2 = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity2, Result.class);
        Assert.assertEquals(result2.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result2.getBody());
        Assert.assertEquals(result2.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试过期Topic保留")
    public void retainExpiredTopicsTest() {
        String url = baseUrl + "/api/v1/normal/topics/expired";

        TopicRetainDTO topicRetainDTO = new TopicRetainDTO();
        topicRetainDTO.setTopicName(configMap.get(ConfigConstant.TOPIC_NAME));
        topicRetainDTO.setRetainDays(1);
        Long logicalClusterId = Long.parseLong(configMap.get(ConfigConstant.LOGICAL_CLUSTER_ID));
        topicRetainDTO.setClusterId(logicalClusterId);
        HttpEntity<TopicRetainDTO> httpEntity2 = new HttpEntity<>(topicRetainDTO, httpHeaders);
        ResponseEntity<Result> result2 = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity2, Result.class);
        Assert.assertEquals(result2.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result2.getBody());
        Assert.assertEquals(result2.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试过期Topic保留")
    public void modifyTopicsTest() {
        String url = baseUrl + "/api/v1/normal/topics";

        TopicModifyDTO topicModifyDTO = new TopicModifyDTO();
        topicModifyDTO.setTopicName(configMap.get(ConfigConstant.TOPIC_NAME));
        Long logicalClusterId = Long.parseLong(configMap.get(ConfigConstant.LOGICAL_CLUSTER_ID));
        topicModifyDTO.setClusterId(logicalClusterId);
        topicModifyDTO.setDescription("");
        HttpEntity<TopicModifyDTO> httpEntity2 = new HttpEntity<>(topicModifyDTO, httpHeaders);
        ResponseEntity<Result> result2 = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity2, Result.class);
        Assert.assertEquals(result2.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result2.getBody());
        Assert.assertEquals(result2.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }
}
