package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicCreationDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicDeletionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicExpansionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicModificationDTO;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import com.xiaojukeji.kafka.manager.web.config.ConfigConstant;
import com.xiaojukeji.kafka.manager.web.config.CustomDataSource;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wyc
 * @date 2022/1/20
 */
public class OpTopicControllerTest extends BaseTest {

    /**
     * Topic保存时间
     */
    private static final Long RETENTION_TIME = 1000L * 60 * 60 * 168;

    @BeforeClass
    public void init() {
        super.init();

        // 成功创建Topic
        String url = baseUrl + "/api/v1/op/topics";
        createCommonTopic(url);
    }

    @AfterClass
    public void afterTest() {
        // 删除Topic成功
        String url = baseUrl + "/api/v1/op/topics";
        deleteTopics(url);
    }

    private TopicCreationDTO getTopicCreationDTO() {
        // 在broker1上创建1分区，1副本的createTopicTest
        TopicCreationDTO creationDTO = new TopicCreationDTO();
        creationDTO.setAppId(configMap.get(ConfigConstant.APPID));
        // 在broker1上创建
        Integer brokerId = Integer.parseInt(configMap.get(ConfigConstant.ALIVE_BROKER_ID));
        creationDTO.setBrokerIdList(Arrays.asList(brokerId));
        creationDTO.setPartitionNum(1);
        creationDTO.setReplicaNum(1);
        creationDTO.setRetentionTime(RETENTION_TIME);
        creationDTO.setPeakBytesIn(10L * 1024 * 1024);
        // 物理集群id
        creationDTO.setClusterId(physicalClusterId);
        creationDTO.setTopicName(configMap.get(ConfigConstant.TOPIC_NAME));
        return creationDTO;
    }

    private TopicDeletionDTO getTopicDeletionDTO() {
        TopicDeletionDTO deletionDTO = new TopicDeletionDTO();
        deletionDTO.setClusterId(physicalClusterId);
        deletionDTO.setTopicName(configMap.get(ConfigConstant.TOPIC_NAME));
        deletionDTO.setUnForce(true);
        return deletionDTO;
    }

    @Test(description = "测试创建Topic")
    public void createCommonTopicTest() {
        String url = baseUrl + "/api/v1/op/topics";

        // PARAM_ILLEGAL
        createCommonTopic1Test(url);
        // CLUSTER_NOT_EXIST
        createCommonTopic2Test(url);
    }

    private void createCommonTopic1Test(String url) {
        TopicCreationDTO creationDTO = getTopicCreationDTO();
        creationDTO.setClusterId(null);

        HttpEntity<TopicCreationDTO> httpEntity = new HttpEntity<>(creationDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void createCommonTopic2Test(String url) {
        TopicCreationDTO creationDTO = getTopicCreationDTO();
        creationDTO.setClusterId(-1L);

        HttpEntity<TopicCreationDTO> httpEntity = new HttpEntity<>(creationDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
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

    @Test(description = "测试删除Topic")
    public void deleteTopicsTest() {
        String url = baseUrl + "/api/v1/op/topics";
        // PARAM_ILLEGAL
        deleteTopics1Test(url);
        // OPERATION_FAILED
        deleteTopics2Test(url);
    }

    private void deleteTopics1Test(String url) {
        ArrayList<TopicDeletionDTO> deletionDTOArrayList = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            deletionDTOArrayList.add(getTopicDeletionDTO());
        }

        HttpEntity<List<TopicDeletionDTO>> httpEntity = new HttpEntity<>(deletionDTOArrayList, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void deleteTopics2Test(String url) {
        TopicDeletionDTO topicDeletionDTO = getTopicDeletionDTO();
        topicDeletionDTO.setClusterId(ConfigConstant.INVALID_CLUSTER_ID);
        HttpEntity<List<TopicDeletionDTO>> httpEntity = new HttpEntity<>(Arrays.asList(topicDeletionDTO), httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.OPERATION_FAILED.getCode());
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


    private TopicModificationDTO getTopicModificationDTO() {
        TopicModificationDTO modificationDTO = new TopicModificationDTO();
        modificationDTO.setAppId(configMap.get(ConfigConstant.APPID));
        modificationDTO.setClusterId(physicalClusterId);
        modificationDTO.setTopicName(configMap.get(ConfigConstant.TOPIC_NAME));
        modificationDTO.setRetentionTime(RETENTION_TIME);
        modificationDTO.setDescription("");
        return modificationDTO;
    }

    @Test(description = "测试修改Topic")
    public void modifyTopicTest() {
        String url = baseUrl + "/api/v1/op/topics";

        // paramIllegal
        modifyTopic1Test(url);
        // cluster not exist
        modifyTopic2Test(url);
        // topic未知
        modifyTopic3Test(url);
        // 修改成功
        modifyTopic4Test(url);
    }

    public void modifyTopic1Test(String url) {
        // 修改topic
        TopicModificationDTO topicModificationDTO = getTopicModificationDTO();
        topicModificationDTO.setTopicName(null);
        HttpEntity<TopicModificationDTO> httpEntity = new HttpEntity<>(topicModificationDTO, httpHeaders);

        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    public void modifyTopic2Test(String url) {
        // 修改topic
        TopicModificationDTO topicModificationDTO = getTopicModificationDTO();
        topicModificationDTO.setClusterId(ConfigConstant.INVALID_CLUSTER_ID);
        HttpEntity<TopicModificationDTO> httpEntity = new HttpEntity<>(topicModificationDTO, httpHeaders);

        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    public void modifyTopic3Test(String url) {
        // 修改topic
        TopicModificationDTO topicModificationDTO = getTopicModificationDTO();
        topicModificationDTO.setTopicName(ConfigConstant.INVALID_STRING);
        HttpEntity<TopicModificationDTO> httpEntity = new HttpEntity<>(topicModificationDTO, httpHeaders);

        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.TOPIC_OPERATION_UNKNOWN_TOPIC_PARTITION.getCode());
    }

    public void modifyTopic4Test(String url) {
        // 修改topic
        TopicModificationDTO topicModificationDTO = getTopicModificationDTO();
        HttpEntity<TopicModificationDTO> httpEntity = new HttpEntity<>(topicModificationDTO, httpHeaders);

        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private TopicExpansionDTO getTopicExpansionDTO() {
        TopicExpansionDTO topicExpansionDTO= new TopicExpansionDTO();
        Long physicalClusterId = Long.parseLong(configMap.get(ConfigConstant.PHYSICAL_CLUSTER_ID));
        topicExpansionDTO.setClusterId(physicalClusterId);
        topicExpansionDTO.setTopicName(configMap.get(ConfigConstant.TOPIC_NAME));
        topicExpansionDTO.setPartitionNum(2);
        Integer brokerId = Integer.parseInt(configMap.get(ConfigConstant.ALIVE_BROKER_ID));
        topicExpansionDTO.setBrokerIdList(Arrays.asList(brokerId));
        return topicExpansionDTO;
    }

    @Test(description = "测试Topic扩分区")
    public void expandTopicPartitionTest() {
        String url = baseUrl + "/api/v1/op/topics/expand-partitions";

        // operation failed
        expandTopicPartition1Test(url);
        // success
        expandTopicPartition2Test(url);
    }

    private void expandTopicPartition1Test(String url) {
        // topic扩分区
        TopicExpansionDTO topicExpansionDTO = getTopicExpansionDTO();
        topicExpansionDTO.setClusterId(null);
        HttpEntity<List<TopicExpansionDTO>> httpEntity = new HttpEntity<>(Arrays.asList(topicExpansionDTO), httpHeaders);

        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.OPERATION_FAILED.getCode());
    }

    private void expandTopicPartition2Test(String url) {
        // topic扩分区
        TopicExpansionDTO topicExpansionDTO = getTopicExpansionDTO();
        HttpEntity<List<TopicExpansionDTO>> httpEntity = new HttpEntity<>(Arrays.asList(topicExpansionDTO), httpHeaders);

        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

}
