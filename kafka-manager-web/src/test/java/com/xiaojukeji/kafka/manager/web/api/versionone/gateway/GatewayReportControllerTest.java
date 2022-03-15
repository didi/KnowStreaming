package com.xiaojukeji.kafka.manager.web.api.versionone.gateway;

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
public class GatewayReportControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();
    }

    @Test(description = "测试查询开启JMX采集的Topic")
    public void getDiscoveryAddress() {
        String url = baseUrl + "/gateway/api/v1/report/jmx/topics?clusterId=" + physicalClusterId;
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<String> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
    }
}
