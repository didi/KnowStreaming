package com.xiaojukeji.kafka.manager.web.config;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import java.io.IOException;
import java.util.Map;

public class BaseTest extends AbstractTestNGSpringContextTests {

    protected final TestRestTemplate testRestTemplate = new TestRestTemplate();

    protected Map<String, String> configMap;

    protected HttpHeaders httpHeaders;

    protected String baseUrl = "http://localhost:8080";

    // 默认物理集群Id为1
    protected Long physicalClusterId = 1L;

    public void init() {
        // 加载本地配置
        try {
            configMap = CommonUtils.readSettings();

            httpHeaders = CommonUtils.getHttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            baseUrl = configMap.get(ConfigConstant.BASE_URL);
            physicalClusterId = Long.parseLong(configMap.get(ConfigConstant.PHYSICAL_CLUSTER_ID));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
