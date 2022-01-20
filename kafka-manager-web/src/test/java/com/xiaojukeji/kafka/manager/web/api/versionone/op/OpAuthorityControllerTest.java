package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.openapi.common.dto.TopicAuthorityDTO;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import com.xiaojukeji.kafka.manager.web.config.Constant;
import com.xiaojukeji.kafka.manager.web.config.HttpUtils;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author xuguang
 * @Date 2022/1/11
 */
public class OpAuthorityControllerTest extends BaseTest {

    private final TestRestTemplate testRestTemplate = new TestRestTemplate();

    private TopicAuthorityDTO getTopicAuthorityDTO() {
        TopicAuthorityDTO topicAuthorityDTO = new TopicAuthorityDTO();
        topicAuthorityDTO.setClusterId(Constant.LOGICAL_CLUSTER_ID_IN_MYSQL);
        topicAuthorityDTO.setTopicName(Constant.TOPIC_NAME_IN_MYSQL);
        topicAuthorityDTO.setAppId(Constant.APPID_IN_MYSQL);
        topicAuthorityDTO.setAccess(Constant.ACCESS);
        return topicAuthorityDTO;
    }

    @Test(description = "测试权限调整")
    public void addAuthorityTest() {
        String url = Constant.BASE_URL + "/topic-authorities";

        addAuthority1Test(url);
        // cluster not exist
        addAuthority2Test(url);
        // param illegal
        addAuthority3Test(url);
        // app not exist
        addAuthority4Test(url);
        // topic not exist
        addAuthority5Test(url);
        // mysqlError, 权限一致,无法插入
        addAuthority6Test(url);
    }

    private void addAuthority1Test(String url) {
        TopicAuthorityDTO topicAuthorityDTO = getTopicAuthorityDTO();
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TopicAuthorityDTO> httpEntity =
                new HttpEntity<>(topicAuthorityDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void addAuthority2Test(String url) {
        TopicAuthorityDTO topicAuthorityDTO = getTopicAuthorityDTO();
        topicAuthorityDTO.setClusterId(Constant.INVALID_CLUSTER_ID_IN_MYSQL);
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TopicAuthorityDTO> httpEntity =
                new HttpEntity<>(topicAuthorityDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void addAuthority3Test(String url) {
        TopicAuthorityDTO topicAuthorityDTO = getTopicAuthorityDTO();
        topicAuthorityDTO.setClusterId(Constant.INVALID_ID);
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TopicAuthorityDTO> httpEntity =
                new HttpEntity<>(topicAuthorityDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void addAuthority4Test(String url) {
        TopicAuthorityDTO topicAuthorityDTO = getTopicAuthorityDTO();
        topicAuthorityDTO.setAppId(Constant.INVALID_APPID);
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TopicAuthorityDTO> httpEntity =
                new HttpEntity<>(topicAuthorityDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.APP_NOT_EXIST.getCode());
    }

    private void addAuthority5Test(String url) {
        TopicAuthorityDTO topicAuthorityDTO = getTopicAuthorityDTO();
        topicAuthorityDTO.setTopicName(Constant.INVALID_TOPIC_NAME);
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TopicAuthorityDTO> httpEntity =
                new HttpEntity<>(topicAuthorityDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.TOPIC_NOT_EXIST.getCode());
    }

    private void addAuthority6Test(String url) {
        TopicAuthorityDTO topicAuthorityDTO = getTopicAuthorityDTO();
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TopicAuthorityDTO> httpEntity =
                new HttpEntity<>(topicAuthorityDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.MYSQL_ERROR.getCode());
    }
}
