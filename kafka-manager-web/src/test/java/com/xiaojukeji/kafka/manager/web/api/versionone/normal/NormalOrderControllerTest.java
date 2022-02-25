package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBatchDTO;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.order.OrderVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.RegionVO;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuguang
 * @Date 2022/2/18
 */
public class NormalOrderControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();

        createOrderSuccess();
    }

    @AfterClass
    public void destroy() {
        deleteOrder();
    }

    private void createOrderSuccess() {
        String url = baseUrl + "/api/v1/normal/orders";
        OrderDTO orderDTO = CustomDataSource.getOrderDTO(configMap);
        HttpEntity<OrderDTO> httpEntity = new HttpEntity<>(orderDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private Long getOrderId() {
        String url = baseUrl + "/api/v1/normal/orders?status=0";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        String s = JSON.toJSONString(result.getBody().getData());
        List<OrderVO> orders = JSON.parseArray(s, OrderVO.class);
        for (OrderVO order : orders) {
            if (order.getDescription().equals(ConfigConstant.DESCRIPTION)) {
                return order.getId();
            }
        }
        return null;
    }

    @Test(description = "测试获取工单申请列表")
    public void getOrders() {
        String url = baseUrl + "/api/v1/normal/orders?status=0";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取工单类型")
    public void getTypeEnums() {
        String url = baseUrl + "/api/v1/normal/orders/type-enums";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取工单详情")
    public void getDetailOrder() {
        String url = baseUrl + "/api/v1/normal/orders/{orderId}/detail";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("orderId", getOrderId());
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void deleteOrder() {
        String url = baseUrl + "/api/v1/normal/orders?id=" + getOrderId();

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取工单审核列表")
    public void getApprovalOrders() {
        String url = baseUrl + "/api/v1/normal/approvals?status=0";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试工单审批")
    public void handleOrderTest() {
        // 单个工单审批
        handleOrder();
//        // 工单批量处理
//        handleBatchOrders();
    }

    private void handleOrder() {
        String url = baseUrl + "/api/v1/normal/orders";

        OrderHandleBaseDTO orderHandleBaseDTO = new OrderHandleBaseDTO();
        orderHandleBaseDTO.setId(getOrderId());
        orderHandleBaseDTO.setStatus(1);
        HttpEntity<OrderHandleBaseDTO> httpEntity = new HttpEntity<>(orderHandleBaseDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNull(result.getBody());
    }

    public void handleBatchOrders() {
        String url = baseUrl + "/api/v1/normal/orders";

        OrderHandleBatchDTO orderHandleBatchDTO = new OrderHandleBatchDTO();
        orderHandleBatchDTO.setOrderIdList(Arrays.asList(getOrderId()));
        orderHandleBatchDTO.setStatus(1);
        HttpEntity<OrderHandleBatchDTO> httpEntity = new HttpEntity<>(orderHandleBatchDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

}
