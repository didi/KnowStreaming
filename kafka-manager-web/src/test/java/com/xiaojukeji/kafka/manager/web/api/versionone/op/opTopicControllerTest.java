package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicCreationDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicDeletionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicModificationDTO;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import com.xiaojukeji.kafka.manager.web.config.Constant;
import com.xiaojukeji.kafka.manager.web.config.HttpUtils;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wyc
 * @date 2022/1/20
 */
public class opTopicControllerTest extends BaseTest {
    private final TestRestTemplate testRestTemplate = new TestRestTemplate();


    private TopicCreationDTO getTopicCreationDTO() {
        // 在broker1上创建1分区，1副本的createTopicTest
        TopicCreationDTO creationDTO = new TopicCreationDTO();
        creationDTO.setAppId(Constant.APPID_IN_MYSQL);
        // 在broker1上创建
        creationDTO.setBrokerIdList(Arrays.asList(1));
        creationDTO.setPartitionNum(1);
        creationDTO.setReplicaNum(1);
        creationDTO.setRetentionTime(1000L * 60 * 60 * 168);
        creationDTO.setPeakBytesIn(10L * 1024 * 1024);
        // 物理集群id
        creationDTO.setClusterId(Constant.PHYSICAL_CLUSTER_ID_IN_MYSQL);
        creationDTO.setTopicName("createTopicTest");
        return creationDTO;
    }

    private TopicDeletionDTO getTopicDeletionDTO() {
        TopicDeletionDTO deletionDTO = new TopicDeletionDTO();
        deletionDTO.setClusterId(1L);
        deletionDTO.setTopicName("createTopicTest");
        deletionDTO.setUnForce(true);
        return deletionDTO;
    }

    @Test
    public void createCommonTopicTest() {
        String url = Constant.BASE_URL + "/api/v1/op/topics";

        // PARAM_ILLEGAL
        createCommonTopic1Test(url);
        // CLUSTER_NOT_EXIST
        createCommonTopic2Test(url);
        // SUCCESS
        createCommonTopic3Test(url);
    }

    private void createCommonTopic1Test(String url) {
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        TopicCreationDTO creationDTO = getTopicCreationDTO();
        creationDTO.setClusterId(null);

        HttpEntity<TopicCreationDTO> httpEntity = new HttpEntity<>(creationDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void createCommonTopic2Test(String url) {
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        TopicCreationDTO creationDTO = getTopicCreationDTO();
        creationDTO.setClusterId(-1L);

        HttpEntity<TopicCreationDTO> httpEntity = new HttpEntity<>(creationDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void createCommonTopic3Test(String url) {
        // 创建Topic
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        TopicCreationDTO creationDTO = getTopicCreationDTO();
        HttpEntity<TopicCreationDTO> httpEntity = new HttpEntity<>(creationDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());

        // 删除创建的topic
        TopicDeletionDTO topicDeletionDTO = getTopicDeletionDTO();
        HttpEntity<List<TopicDeletionDTO>> httpEntity2 = new HttpEntity<>(Arrays.asList(topicDeletionDTO), httpHeaders);
        ResponseEntity<Result> result2 = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity2, Result.class);
        Assert.assertEquals(result2.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result2.getBody());
        Assert.assertEquals(result2.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void deleteTopicsTest() {
        String url = Constant.BASE_URL + "/api/v1/op/topics";
        // PARAM_ILLEGAL
        deleteTopics1Test(url);
        // OPERATION_FAILED
        deleteTopics2Test(url);
        // SUCCESS
        deleteTopics3Test(url);
    }

    private void deleteTopics1Test(String url) {
        ArrayList<TopicDeletionDTO> deletionDTOArrayList = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            deletionDTOArrayList.add(getTopicDeletionDTO());
        }

        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<TopicDeletionDTO>> httpEntity = new HttpEntity<>(deletionDTOArrayList, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void deleteTopics2Test(String url) {
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        TopicDeletionDTO topicDeletionDTO = getTopicDeletionDTO();
        topicDeletionDTO.setClusterId(-1L);
        HttpEntity<List<TopicDeletionDTO>> httpEntity = new HttpEntity<>(Arrays.asList(topicDeletionDTO), httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.OPERATION_FAILED.getCode());
    }

    private void deleteTopics3Test(String url) {
        // 创建Topic
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        TopicCreationDTO creationDTO = getTopicCreationDTO();
        HttpEntity<TopicCreationDTO> httpEntity = new HttpEntity<>(creationDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());

        // 删除创建的topic
        TopicDeletionDTO topicDeletionDTO = getTopicDeletionDTO();
        HttpEntity<List<TopicDeletionDTO>> httpEntity2 = new HttpEntity<>(Arrays.asList(topicDeletionDTO), httpHeaders);
        ResponseEntity<Result> result2 = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity2, Result.class);
        Assert.assertEquals(result2.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result2.getBody());
        Assert.assertEquals(result2.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }


    private TopicModificationDTO getTopicModificationDTO() {
        TopicModificationDTO modificationDTO = new TopicModificationDTO();
        modificationDTO.setAppId(Constant.APPID_IN_MYSQL);
        modificationDTO.setClusterId(Constant.PHYSICAL_CLUSTER_ID_IN_MYSQL);
        modificationDTO.setTopicName("createTopicName");
        return modificationDTO;
    }

    public void modifyTopicTest() {
        String url = Constant.BASE_URL + "/api/v1/op/topics";

    }

    public void modifyTopic1Test(String url) {
        // 创建Topic
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        TopicCreationDTO creationDTO = getTopicCreationDTO();
        HttpEntity<TopicCreationDTO> httpEntity = new HttpEntity<>(creationDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());

        // 修改topic
        TopicModificationDTO topicModificationDTO = getTopicModificationDTO();
        HttpEntity<TopicModificationDTO> httpEntity2 = new HttpEntity<>(topicModificationDTO, httpHeaders);
    }

}
