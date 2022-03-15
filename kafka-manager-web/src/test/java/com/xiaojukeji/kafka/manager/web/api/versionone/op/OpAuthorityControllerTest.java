package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicCreationDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicDeletionDTO;
import com.xiaojukeji.kafka.manager.openapi.common.dto.TopicAuthorityDTO;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import com.xiaojukeji.kafka.manager.web.config.ConfigConstant;
import com.xiaojukeji.kafka.manager.web.config.CustomDataSource;
import org.springframework.boot.test.web.client.TestRestTemplate;
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
 * @Date 2022/1/11
 */
public class OpAuthorityControllerTest extends BaseTest {

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

    private final TestRestTemplate testRestTemplate = new TestRestTemplate();

    private TopicAuthorityDTO getTopicAuthorityDTO() {
        TopicAuthorityDTO topicAuthorityDTO = new TopicAuthorityDTO();
        long logicalClusterId = Long.parseLong(configMap.get(ConfigConstant.LOGICAL_CLUSTER_ID));
        topicAuthorityDTO.setClusterId(logicalClusterId);
        topicAuthorityDTO.setTopicName(configMap.get(ConfigConstant.TOPIC_NAME));
        topicAuthorityDTO.setAppId(configMap.get(ConfigConstant.APPID));
        topicAuthorityDTO.setAccess(ConfigConstant.ACCESS);
        return topicAuthorityDTO;
    }

    @Test(description = "测试权限调整")
    public void addAuthorityTest() {
        String url = baseUrl + "/topic-authorities";

        addAuthority1Test(url);
    }

    private void addAuthority1Test(String url) {
        TopicAuthorityDTO topicAuthorityDTO = getTopicAuthorityDTO();
        HttpEntity<TopicAuthorityDTO> httpEntity =
                new HttpEntity<>(topicAuthorityDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }
}
