package com.xiaojukeji.kafka.manager.web.api.versionone.gateway;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author xuguang
 * @Date 2022/2/18
 */
public class GatewayServiceDiscoveryControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();
    }

    @Test(description = "测试获取指定集群服务地址")
    public void getDiscoveryAddress() {
        String url = baseUrl + "/gateway/api/v1/discovery/address?clusterId=" + physicalClusterId;
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<String> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
    }

    @Test(description = "测试获取最大App请求速率")
    public void getDiscoveryAppIdRate() {
        String url = baseUrl + "/gateway/api/v1/discovery/appId-rate?versionNumber=1";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取集群服务地址")
    public void getDiscoveryInit() {
        String url = baseUrl + "/gateway/api/v1/discovery/init";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取最大IP请求速率")
    public void getDiscoveryIpRate() {
        String url = baseUrl + "/gateway/api/v1/discovery/ip-rate?versionNumber=1";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取最大并发请求数")
    public void getDiscoveryMaxRequestNum() {
        String url = baseUrl + "/gateway/api/v1/discovery/max-request-num?versionNumber=1";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试最大SP请求速率")
    public void getDiscoverySpLimit() {
        String url = baseUrl + "/gateway/api/v1/discovery/sp-limit?versionNumber=1";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取集群服务地址")
    public void getDiscoveryUpdate() {
        String url = baseUrl + "/gateway/api/v1/discovery/update?versionNumber=1";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }
}
