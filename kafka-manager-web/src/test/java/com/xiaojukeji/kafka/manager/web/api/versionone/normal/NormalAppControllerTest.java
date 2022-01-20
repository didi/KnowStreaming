package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.AppDTO;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import com.xiaojukeji.kafka.manager.web.config.Constant;
import com.xiaojukeji.kafka.manager.web.config.HttpUtils;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xuguang
 * @Date 2022/1/7
 */
public class NormalAppControllerTest extends BaseTest {


    private final TestRestTemplate testRestTemplate = new TestRestTemplate();

    private AppDTO getAppDTO() {
        AppDTO appDTO = new AppDTO();
        appDTO.setAppId(Constant.APPID_IN_MYSQL);
        appDTO.setName("KM管理员");
        appDTO.setPrincipals("admin");
        appDTO.setDescription("KM管理员应用-谨慎对外提供");
        return appDTO;
    }

    @Test(description = "测试获取App列表")
    public void getAppsTest() {
        String url = Constant.BASE_URL + "/api/v1/normal/apps";

        // 有headers登陆
        getAppsWithHeadersTest(url);
        // 无headers登陆
        getAppsWithoutHeadersTest(url);
    }

    private void getAppsWithHeadersTest(String url) {
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
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
        String url = Constant.BASE_URL + "/api/v1/normal/apps/{appId}/basic-info";

        // 查询结果不为空
        getAppBasicInfo2ResultNotEmptyTest(url);
        // 查询结果为空
        getAppBasicInfo2ResultEmptyTest(url);
    }

    private void getAppBasicInfo2ResultNotEmptyTest(String url) {
        HttpEntity<String> httpEntity = new HttpEntity<>("", HttpUtils.getHttpHeaders());
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("appId", Constant.APPID_IN_MYSQL);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
        Assert.assertNotNull(result.getBody().getData());
    }

    private void getAppBasicInfo2ResultEmptyTest(String url) {
        HttpEntity<String> httpEntity = new HttpEntity<>("", HttpUtils.getHttpHeaders());
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("appId", Constant.INVALID_APPID);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
        Assert.assertNull(result.getBody().getData());
    }

    @Test(description = "测试修改app")
    public void modifyApp() {
        String url = Constant.BASE_URL + "/api/v1/normal/apps";
        // 修改成功
        modifyApp2SuccessTest(url);
        // 传的dto为空, 修改不成功
        modifyApp2FailureTest(url);
    }

    private void modifyApp2SuccessTest(String url) {
        AppDTO appDTO = getAppDTO();
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
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
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
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
        String url = Constant.BASE_URL + "/api/v1/normal/apps/{appId}/topics";
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
        HttpEntity<String> httpEntity = new HttpEntity<>(null, HttpUtils.getHttpHeaders());
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("appId", Constant.APPID_IN_MYSQL);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void getAppTopics2Test(String url) {
        url = url + "?mine=true";

        HttpEntity<String> httpEntity = new HttpEntity<>(null, HttpUtils.getHttpHeaders());
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", Constant.APPID_IN_MYSQL);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void getAppTopics3Test(String url) {
        url = url + "?mine=false";
        HttpEntity<String> httpEntity = new HttpEntity<>(null, HttpUtils.getHttpHeaders());
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("appId", Constant.APPID_IN_MYSQL);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void getAppTopics4Test(String url) {
        url = url + "?mine=false";
        HttpEntity<String> httpEntity = new HttpEntity<>(null, HttpUtils.getHttpHeaders());
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("appId", Constant.INVALID_APPID);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试Quota查询")
    public void getAppIdQuotaTest() {
        String url = Constant.BASE_URL + "/api/v1/normal/apps/{appId}/quotas";
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
        url = url + "?clusterId=" + Constant.PHYSICAL_CLUSTER_ID_IN_MYSQL + "&topicName=" +
                Constant.TOPIC_NAME_IN_MYSQL + "&isPhysicalClusterId=true";
        HttpEntity<String> httpEntity = new HttpEntity<>(null, HttpUtils.getHttpHeaders());
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", Constant.APPID_IN_MYSQL);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void getAppIdQuota2Test(String url) {
        url = url + "?clusterId=" + Constant.PHYSICAL_CLUSTER_ID_IN_MYSQL + "&topicName=" +
                Constant.TOPIC_NAME_IN_MYSQL + "&isPhysicalClusterId=true";
        HttpEntity<String> httpEntity = new HttpEntity<>(null, HttpUtils.getHttpHeaders());
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", Constant.INVALID_APPID);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void getAppIdQuota3Test(String url) {
        url = url + "?clusterId=" + Constant.PHYSICAL_CLUSTER_ID_IN_MYSQL + "&topicName=" +
                Constant.INVALID_TOPIC_NAME + "&isPhysicalClusterId=true";
        HttpEntity<String> httpEntity = new HttpEntity<>(null, HttpUtils.getHttpHeaders());
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", Constant.APPID_IN_MYSQL);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.TOPIC_NOT_EXIST.getCode());
    }

    private void getAppIdQuota4Test(String url) {
        url = url + "?clusterId=" + Constant.INVALID_CLUSTER_ID_IN_MYSQL + "&topicName=" +
                Constant.TOPIC_NAME_IN_MYSQL + "&isPhysicalClusterId=false";
        HttpEntity<String> httpEntity = new HttpEntity<>(null, HttpUtils.getHttpHeaders());
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", Constant.APPID_IN_MYSQL);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    @Test(description = "测试获取应用连接信息")
    public void getAppIdConnectionsTest() {
        String url = Constant.BASE_URL + "/api/v1/normal/apps/{appId}/connections";
        // appId存在数据库
        getAppIdConnections1Test(url);
        // appId不存在数据库
        getAppIdConnections2Test(url);
    }

    public void getAppIdConnections1Test(String url) {
        HttpEntity<String> httpEntity = new HttpEntity<>("", HttpUtils.getHttpHeaders());
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("appId", Constant.APPID_IN_MYSQL);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
        Assert.assertNotNull(result.getBody().getData());
    }

    public void getAppIdConnections2Test(String url) {
        HttpEntity<String> httpEntity = new HttpEntity<>("", HttpUtils.getHttpHeaders());
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("appId", Constant.INVALID_APPID);
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
        String url = Constant.BASE_URL + "/api/v1/normal/apps/{appId}/authorities";
        url = url + "?clusterId=" + Constant.LOGICAL_CLUSTER_ID_IN_MYSQL + "&topicName=" +
                Constant.TOPIC_NAME_IN_MYSQL;
        HttpEntity<String> httpEntity = new HttpEntity<>(null, HttpUtils.getHttpHeaders());
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", Constant.APPID_IN_MYSQL);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void getAppIdAuthority2Test() {
        String url = Constant.BASE_URL + "/api/v1/normal/apps/{appId}/authorities";
        url = url + "?clusterId=" + Constant.LOGICAL_CLUSTER_ID_IN_MYSQL + "&topicName=" +
                Constant.TOPIC_NAME_IN_MYSQL;
        HttpEntity<String> httpEntity = new HttpEntity<>(null, HttpUtils.getHttpHeaders());
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", Constant.INVALID_APPID);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void getAppIdAuthority3Test() {
        String url = Constant.BASE_URL + "/api/v1/normal/apps/{appId}/authorities";
        url = url + "?clusterId=" + Constant.INVALID_CLUSTER_ID_IN_MYSQL + "&topicName=" +
                Constant.TOPIC_NAME_IN_MYSQL;
        HttpEntity<String> httpEntity = new HttpEntity<>(null, HttpUtils.getHttpHeaders());
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", Constant.APPID_IN_MYSQL);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void getAppIdAuthority4Test() {
        String url = Constant.BASE_URL + "/api/v1/normal/apps/{appId}/authorities";
        url = url + "?clusterId=" + Constant.LOGICAL_CLUSTER_ID_IN_MYSQL + "&topicName=" +
                Constant.INVALID_TOPIC_NAME;
        HttpEntity<String> httpEntity = new HttpEntity<>(null, HttpUtils.getHttpHeaders());
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("appId", Constant.APPID_IN_MYSQL);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.TOPIC_NOT_EXIST.getCode());
    }

}
