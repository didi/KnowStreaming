package com.xiaojukeji.kafka.manager.web.api.versionone.thirdpart;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicCreationDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicDeletionDTO;
import com.xiaojukeji.kafka.manager.openapi.common.dto.ConsumeHealthDTO;
import com.xiaojukeji.kafka.manager.openapi.common.dto.OffsetResetDTO;
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

import java.util.*;

/**
 * @author xuguang
 * @Date 2022/2/24
 */
public class ThirdPartConsumerControllerTest extends BaseTest {

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

    @Test(description = "测c消费组健康")
    public void consumerHealthTest() {
        ConsumeHealthDTO consumeHealthDTO = new ConsumeHealthDTO();
        consumeHealthDTO.setClusterId(physicalClusterId);
        consumeHealthDTO.setTopicNameList(Arrays.asList(configMap.get(ConfigConstant.TOPIC_NAME)));
        consumeHealthDTO.setConsumerGroup("test");
        consumeHealthDTO.setMaxDelayTime(System.currentTimeMillis());

        String url = baseUrl + "/api/v1/third-part/clusters/consumer-health";
        HttpEntity<ConsumeHealthDTO> httpEntity = new HttpEntity<>(consumeHealthDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试重置消费组")
    public void resetOffsetTest() {

    }

    private void resetOffset() {
        OffsetResetDTO offsetResetDTO = new OffsetResetDTO();
        offsetResetDTO.setClusterId(physicalClusterId);
        offsetResetDTO.setTopicName(configMap.get(ConfigConstant.TOPIC_NAME));
        offsetResetDTO.setConsumerGroup("test");
        offsetResetDTO.setLocation("broker");
        offsetResetDTO.setOffsetResetType(0);
        offsetResetDTO.setAppId(configMap.get(ConfigConstant.APPID));
        offsetResetDTO.setOperator(ConfigConstant.ADMIN_USER);
        offsetResetDTO.setPassword(ConfigConstant.ADMIN_USER);
        offsetResetDTO.setSubscribeReset(true);
        offsetResetDTO.setPartitionOffsetDTOList(new ArrayList<>());
        offsetResetDTO.setTimestamp(System.currentTimeMillis());
        offsetResetDTO.setSystemCode("kafka-manager");

        String url = "/api/v1/third-part/consumers/offsets";
        HttpEntity<OffsetResetDTO> httpEntity = new HttpEntity<>(offsetResetDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    @Test(description = "测试查询消费组的消费详情")
    public void getConsumeDetailTest() {
        String url = baseUrl + "/api/v1/third-part/{physicalClusterId}" +
                "/consumers/{consumerGroup}/topics/{topicName}/consume-details?location=broker";

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("physicalClusterId", physicalClusterId);
        urlVariables.put("consumerGroup", "test");
        urlVariables.put("topicName", configMap.get(ConfigConstant.TOPIC_NAME));
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.OPERATION_FAILED.getCode());
    }
}
