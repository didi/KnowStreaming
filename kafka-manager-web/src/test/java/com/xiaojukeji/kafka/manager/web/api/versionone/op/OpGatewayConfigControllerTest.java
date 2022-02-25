package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway.OrderExtensionAddGatewayConfigDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway.OrderExtensionDeleteGatewayConfigDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway.OrderExtensionModifyGatewayConfigDTO;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.GatewayConfigVO;
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

import java.util.List;

/**
 * @author xuguang
 * @Date 2022/2/21
 */
public class OpGatewayConfigControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();

        createGatewayConfigSuccess();
    }

    @AfterClass
    public void destroy() {
        // 删除成功
        deleteGatewayConfigSuccess();
    }

    @Test(description = "测试创建Gateway配置")
    public void createGatewayConfigTest() {
        // paramIllegal
        createGatewayConfig2ParamIllegal();
    }

    private void createGatewayConfigSuccess() {
        String url = baseUrl + "/api/v1/op/gateway-configs";
        OrderExtensionAddGatewayConfigDTO dto = CustomDataSource.getOrderExtensionAddGatewayConfigDTO(configMap);
        HttpEntity<OrderExtensionAddGatewayConfigDTO> httpEntity = new HttpEntity<>(dto, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void createGatewayConfig2ParamIllegal() {
        String url = baseUrl + "/api/v1/op/gateway-configs";
        OrderExtensionAddGatewayConfigDTO dto = CustomDataSource.getOrderExtensionAddGatewayConfigDTO(configMap);
        dto.setName(null);
        HttpEntity<OrderExtensionAddGatewayConfigDTO> httpEntity = new HttpEntity<>(dto, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private Long getGatewayConfigId() {
        String url = baseUrl + "/api/v1/rd/gateway-configs";
        String gatewayName = configMap.get(ConfigConstant.GATEWAY_NAME);
        String gatewayType = configMap.get(ConfigConstant.GATEWAY_TYPE);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        String s = JSON.toJSONString(result.getBody().getData());
        List<GatewayConfigVO> GatewayConfigVOS = JSON.parseArray(s, GatewayConfigVO.class);
        for (GatewayConfigVO gatewayConfigVO : GatewayConfigVOS) {
            if (gatewayConfigVO.getName().equals(gatewayName) && gatewayConfigVO.getType().equals(gatewayType)) {
                return gatewayConfigVO.getId();
            }
        }
        return null;
    }

    @Test(description = "测试修改Gateway配置")
    public void modifyGatewayConfigTest() {
        String url = baseUrl + "/api/v1/op/gateway-configs";
        OrderExtensionModifyGatewayConfigDTO dto =
                CustomDataSource.getOrderExtensionModifyGatewayConfigDTO(configMap);
        dto.setId(getGatewayConfigId());
        HttpEntity<OrderExtensionModifyGatewayConfigDTO> httpEntity = new HttpEntity<>(dto, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试删除Gateway配置")
    public void deleteGatewayConfigTest() {

    }

    private void deleteGatewayConfigSuccess() {
        String url = baseUrl + "/api/v1/op/gateway-configs";
        OrderExtensionDeleteGatewayConfigDTO dto = new OrderExtensionDeleteGatewayConfigDTO();
        dto.setId(getGatewayConfigId());
        HttpEntity<OrderExtensionDeleteGatewayConfigDTO> httpEntity = new HttpEntity<>(dto, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取Gateway配置类型")
    public void getTypeEnumsTest() {
        String url = baseUrl + "/api/v1/op/gateway-configs/type-enums";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

}
