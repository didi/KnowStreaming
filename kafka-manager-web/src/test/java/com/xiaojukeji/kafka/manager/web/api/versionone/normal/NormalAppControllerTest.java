package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.AppDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicCreationDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicDeletionDTO;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import com.xiaojukeji.kafka.manager.web.config.ConfigConstant;
import org.springframework.http.*;
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
 * @Date 2022/1/7
 */
public class NormalAppControllerTest extends BaseTest {
    
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
        deleteTopic(url);
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
        creationDTO.setRetentionTime(1000L * 60 * 60 * 168);
        creationDTO.setPeakBytesIn(10L * 1024 * 1024);
        // 物理集群id
        
        creationDTO.setClusterId(physicalClusterId);
        creationDTO.setTopicName(configMap.get(ConfigConstant.TOPIC_NAME));
        return creationDTO;
    }

    private void createCommonTopic(String url) {
        // 创建Topic
        TopicCreationDTO creationDTO = getTopicCreationDTO();
        HttpEntity<TopicCreationDTO> httpEntity = new HttpEntity<>(creationDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void deleteTopic(String url) {
        // 删除创建的topic
        TopicDeletionDTO topicDeletionDTO = getTopicDeletionDTO();
        HttpEntity<List<TopicDeletionDTO>> httpEntity2 = new HttpEntity<>(Arrays.asList(topicDeletionDTO), httpHeaders);
        ResponseEntity<Result> result2 = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity2, Result.class);
        Assert.assertEquals(result2.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result2.getBody());
        Assert.assertEquals(result2.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private TopicDeletionDTO getTopicDeletionDTO() {
        TopicDeletionDTO deletionDTO = new TopicDeletionDTO();
        
        deletionDTO.setClusterId(physicalClusterId);
        deletionDTO.setTopicName(configMap.get(ConfigConstant.TOPIC_NAME));
        deletionDTO.setUnForce(true);
        return deletionDTO;
    }

    private AppDTO getAppDTO() {
        AppDTO appDTO = new AppDTO();
        appDTO.setAppId(configMap.get(ConfigConstant.APPID));
        appDTO.setName("KM管理员");
        appDTO.setPrincipals(configMap.get(ConfigConstant.ADMIN_USER));
        appDTO.setDescription("KM管理员应用-谨慎对外提供");
        return appDTO;
    }

    @Test(description = "测试获取App列表")
    public void getAppsTest() {
        String url = baseUrl + "/api/v1/normal/apps";

        // 有headers登陆
        getAppsWithHeadersTest(url);
        // 无headers登陆
        getAppsWithoutHeadersTest(url);
    }

    private void getAppsWithHeadersTest(String url) {
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void getAppsWithoutHeadersTest(String url) {
        HttpEntity<String> httpEntity = new HttpEntity<>("", new HttpHeaders());
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.UNAUTHORIZED.value());
    }

    @Test(description = "测试由appId获取app")
    public void getAppBasicInfoTest() {
        String url = baseUrl + "/api/v1/normal/apps/{appId}/basic-info";

        // 查询结果不为空
        getAppBasicInfo2ResultNotEmptyTest(url);
        // 查询结果为空
        getAppBasicInfo2ResultEmptyTest(url);
    }

    private void getAppBasicInfo2ResultNotEmptyTest(String url) {
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("appId", configMap.get(ConfigConstant.APPID));
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
        Assert.assertNotNull(result.getBody().getData());
    }

    private void getAppBasicInfo2ResultEmptyTest(String url) {
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("appId", ConfigConstant.INVALID_STRING);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
        Assert.assertNull(result.getBody().getData());
    }

    @Test(description = "测试修改app")
    public void modifyApp() {
        String url = baseUrl + "/api/v1/normal/apps";
        // 修改成功
        modifyApp2SuccessTest(url);
        // 传的dto为空, 修改不成功
        modifyApp2FailureTest(url);
    }

    private void modifyApp2SuccessTest(String url) {
        AppDTO appDTO = getAppDTO();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AppDTO> httpEntity =
                new HttpEntity<>(appDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void modifyApp2FailureTest(String url) {
        AppDTO appDTO = new AppDTO();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AppDTO> httpEntity =
                new HttpEntity<>(appDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertNotEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取有权限的Topic信息")
    public void getAppTopicsTest() {
        String url = baseUrl + "/api/v1/normal/apps/{appId}/topics";
        // 参数appId
        getAppTopics1Test(url);
        // 参数有appId，mine=true
        getAppTopics2Test(url);
        // 参数有appId，mine=false
        getAppTopics3Test(url);
        // appId无效，mine=false
        getAppTopics4Test(url);
    }

    private void getAppTopics1Test(String url) {
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("appId", configMap.get(ConfigConstant.APPID));
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void getAppTopics2Test(String url) {
        url = url + "?mine=true";

        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", configMap.get(ConfigConstant.APPID));
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void getAppTopics3Test(String url) {
        url = url + "?mine=false";
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("appId", configMap.get(ConfigConstant.APPID));
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void getAppTopics4Test(String url) {
        url = url + "?mine=false";
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("appId", configMap.get(ConfigConstant.APPID));
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试Quota查询")
    public void getAppIdQuotaTest() {
        String url = baseUrl + "/api/v1/normal/apps/{appId}/quotas";
        // appId不为空，clusterId和topicName在数据库中真实存在，isPhysicalClusterId=true
        getAppIdQuota1Test(url);
        // appId无效
        getAppIdQuota2Test(url);
        // topicName无效
        getAppIdQuota3Test(url);
        // clusterId无效
        getAppIdQuota4Test(url);
    }

    private void getAppIdQuota1Test(String url) {
        url = url + "?clusterId=" + physicalClusterId + "&topicName=" +
                configMap.get(ConfigConstant.TOPIC_NAME) + "&isPhysicalClusterId=true";
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", configMap.get(ConfigConstant.APPID));
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void getAppIdQuota2Test(String url) {
        url = url + "?clusterId=" + physicalClusterId + "&topicName=" +
                configMap.get(ConfigConstant.TOPIC_NAME) + "&isPhysicalClusterId=true";
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", configMap.get(ConfigConstant.APPID));
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void getAppIdQuota3Test(String url) {
        url = url + "?clusterId=" + physicalClusterId + "&topicName=" +
                ConfigConstant.INVALID_STRING + "&isPhysicalClusterId=true";
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", configMap.get(ConfigConstant.APPID));
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.TOPIC_NOT_EXIST.getCode());
    }

    private void getAppIdQuota4Test(String url) {
        url = url + "?clusterId=" + ConfigConstant.INVALID_CLUSTER_ID + "&topicName=" +
                configMap.get(ConfigConstant.TOPIC_NAME) + "&isPhysicalClusterId=false";
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", configMap.get(ConfigConstant.APPID));
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    @Test(description = "测试获取应用连接信息")
    public void getAppIdConnectionsTest() {
        String url = baseUrl + "/api/v1/normal/apps/{appId}/connections";
        // appId存在数据库
        getAppIdConnections1Test(url);
        // appId不存在数据库
        getAppIdConnections2Test(url);
    }

    public void getAppIdConnections1Test(String url) {
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("appId", configMap.get(ConfigConstant.APPID));
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
        Assert.assertNotNull(result.getBody().getData());
    }

    public void getAppIdConnections2Test(String url) {
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("appId", configMap.get(ConfigConstant.APPID));
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
        Assert.assertNotNull(result.getBody().getData());
    }

    @Test(description = "测试app对Topic权限信息")
    public void getAppIdAuthorityTest() {
        // appId, clusterId, topicName在数据库中存在
        getAppIdAuthority1Test();
        // appId无效
        getAppIdAuthority2Test();
        // clusterId无效
        getAppIdAuthority3Test();
        // topicName无效
        getAppIdAuthority4Test();
    }

    private void getAppIdAuthority1Test() {
        String url = baseUrl + "/api/v1/normal/apps/{appId}/authorities";
        url = url + "?clusterId=" + configMap.get(ConfigConstant.LOGICAL_CLUSTER_ID) + "&topicName=" +
                configMap.get(ConfigConstant.TOPIC_NAME);
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", configMap.get(ConfigConstant.APPID));
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void getAppIdAuthority2Test() {
        String url = baseUrl + "/api/v1/normal/apps/{appId}/authorities";
        url = url + "?clusterId=" + configMap.get(ConfigConstant.LOGICAL_CLUSTER_ID) + "&topicName=" +
                configMap.get(ConfigConstant.TOPIC_NAME);
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", configMap.get(ConfigConstant.APPID));
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void getAppIdAuthority3Test() {
        String url = baseUrl + "/api/v1/normal/apps/{appId}/authorities";
        url = url + "?clusterId=" +ConfigConstant.INVALID_CLUSTER_ID + "&topicName=" +
                configMap.get(ConfigConstant.TOPIC_NAME);
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", configMap.get(ConfigConstant.APPID));
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void getAppIdAuthority4Test() {
        String url = baseUrl + "/api/v1/normal/apps/{appId}/authorities";
        url = url + "?clusterId=" + configMap.get(ConfigConstant.LOGICAL_CLUSTER_ID) + "&topicName=" +
                ConfigConstant.INVALID_STRING;
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", configMap.get(ConfigConstant.APPID));
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.TOPIC_NOT_EXIST.getCode());
    }

}
