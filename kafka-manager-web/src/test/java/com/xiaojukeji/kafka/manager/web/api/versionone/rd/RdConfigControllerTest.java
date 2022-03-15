package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.config.ConfigDTO;
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

/**
 * @author xuguang
 * @Date 2022/2/18
 */
public class RdConfigControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();

        // 创建config
        createConfigSuccess();
    }

    @AfterClass
    public void destroy() {
        // 删除config
        deleteConfig();
    }

    @Test(description = "测试新增配置")
    public void createConfigTest() {
        // 参数错误
        createConfig1();
    }

    private void createConfig1() {
        String url = baseUrl + "/api/v1/rd/configs";
        ConfigDTO configDTO = CustomDataSource.getConfigDTO();
        configDTO.setConfigKey(null);
        HttpEntity<ConfigDTO> httpEntity = new HttpEntity<>(configDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void createConfigSuccess() {
        String url = baseUrl + "/api/v1/rd/configs";
        ConfigDTO configDTO = CustomDataSource.getConfigDTO();
        HttpEntity<ConfigDTO> httpEntity = new HttpEntity<>(configDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "删除配置")
    public void deleteConfigTest() {

    }

    private void deleteConfig() {
        String url = baseUrl + "/api/v1/rd/configs?config-key=" + ConfigConstant.CONFIG_KEY;
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试修改配置")
    public void modifyConfigTest() {
        // 参数错误
        modifyConfig1();
        // 修改成功
        modifyConfig2();
    }

    private void modifyConfig1() {
        String url = baseUrl + "/api/v1/rd/configs";
        ConfigDTO configDTO = CustomDataSource.getConfigDTO();
        configDTO.setConfigKey(null);
        HttpEntity<ConfigDTO> httpEntity = new HttpEntity<>(configDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void modifyConfig2() {
        String url = baseUrl + "/api/v1/rd/configs";
        ConfigDTO configDTO = CustomDataSource.getConfigDTO();
        HttpEntity<ConfigDTO> httpEntity = new HttpEntity<>(configDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取配置列表")
    public void getConfigList() {
        String url = baseUrl + "/api/v1/rd/configs";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获Kafka的角色列表")
    public void getKafkaRoles() {
        String url = baseUrl + "/api/v1/rd/configs/kafka-roles";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }


}
