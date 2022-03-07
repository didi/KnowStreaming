package com.xiaojukeji.kafka.manager.bpm.order;

import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailDeleteAppDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.service.service.gateway.TopicConnectionService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * @author xuguang
 * @Date 2021/12/29
 */
public class DeleteAppOrderTest extends BaseTest {

    private static final String EXTENSIONS = "{\"appId\":\"dkm_admin\"}";

    private static final Long ORDER_ID = 1L;

    @Value("${test.admin}")
    private String ADMIN;

    private static final Integer APPLY_TOPIC_TYPE = 0;

    /**
     * 工单状态, 0:待审批, 1:通过, 2:拒绝, 3:取消
     */
    private static final Integer ORDER_PASSED_STATUS = 1;
    @Autowired
    @Qualifier("deleteAppOrder")
    @InjectMocks
    private AbstractOrder deleteAppOrder;

    @Mock
    private AppService appService;

    @Mock
    private AuthorityService authorityService;

    @Mock
    private TopicConnectionService connectionService;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private OrderDO getOrderDO() {
        OrderDO orderDO = new OrderDO();
        orderDO.setId(ORDER_ID);
        orderDO.setType(APPLY_TOPIC_TYPE);
        orderDO.setTitle("apply topic");
        orderDO.setApplicant(ADMIN);
        orderDO.setDescription("测试的OrderDO");
        orderDO.setApprover(ADMIN);
        orderDO.setGmtHandle(new Date());
        orderDO.setGmtCreate(new Date());
        orderDO.setExtensions(EXTENSIONS);
        orderDO.setStatus(ORDER_PASSED_STATUS);
        return orderDO;
    }

    private AppDO getAppDO() {
        AppDO appDO = new AppDO();
        appDO.setId(4L);
        appDO.setAppId("moduleTestAppId");
        appDO.setName("moduleTestApp");
        appDO.setPassword("moduleTestApp");
        appDO.setType(1);
        appDO.setApplicant("admin");
        appDO.setPrincipals("admin");
        appDO.setDescription("moduleTestApp");
        appDO.setCreateTime(new Date(1638786493173L));
        appDO.setModifyTime(new Date(1638786493173L));
        return appDO;
    }

    @Test
    public void checkExtensionFieldsAndGenerateTitleTest() {
        // paramIllegal
        checkExtensionFieldsAndGenerateTitle2ParamIllegalTest();
        // app not exist
        checkExtensionFieldsAndGenerateTitle2AppNotExistTest();
        // success
        checkExtensionFieldsAndGenerateTitle2SuccessTest();
    }

    private void checkExtensionFieldsAndGenerateTitle2ParamIllegalTest() {
        Result<String> result = deleteAppOrder.checkExtensionFieldsAndGenerateTitle("{}");
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2AppNotExistTest() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(null);

        Result<String> result = deleteAppOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.APP_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2SuccessTest() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(new AppDO());

        Result<String> result = deleteAppOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void handleOrderDetailTest() {
        // appNotExist
        handleOrderDetail2AppNotExistTest();
        // app offline forbidden
        handleOrderDetail2AppOfflineForbiddenTest();
        // success
        handleOrderDetail2SuccesssTest();
        // failed
        handleOrderDetail2FailedTest();
    }

    private void handleOrderDetail2AppNotExistTest() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(null);

        OrderDO orderDO = getOrderDO();
        ResultStatus resultStatus = deleteAppOrder.handleOrderDetail(
                orderDO, new OrderHandleBaseDTO(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.APP_NOT_EXIST.getCode());
    }

    private void handleOrderDetail2AppOfflineForbiddenTest() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(new AppDO());
        Mockito.when(authorityService.getAuthority(Mockito.any())).thenReturn(Arrays.asList(new AuthorityDO()));

        OrderDO orderDO = getOrderDO();
        ResultStatus resultStatus = deleteAppOrder.handleOrderDetail(
                orderDO, new OrderHandleBaseDTO(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.APP_OFFLINE_FORBIDDEN.getCode());
    }

    private void handleOrderDetail2SuccesssTest() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(new AppDO());
        Mockito.when(authorityService.getAuthority(Mockito.any())).thenReturn(Collections.emptyList());
        Mockito.when(appService.deleteApp(Mockito.any(), Mockito.any())).thenReturn(1);

        OrderDO orderDO = getOrderDO();
        ResultStatus resultStatus = deleteAppOrder.handleOrderDetail(
                orderDO, new OrderHandleBaseDTO(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void handleOrderDetail2FailedTest() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(new AppDO());
        Mockito.when(authorityService.getAuthority(Mockito.any())).thenReturn(Collections.emptyList());
        Mockito.when(appService.deleteApp(Mockito.any(), Mockito.any())).thenReturn(-1);

        OrderDO orderDO = getOrderDO();
        ResultStatus resultStatus = deleteAppOrder.handleOrderDetail(
                orderDO, new OrderHandleBaseDTO(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.OPERATION_FAILED.getCode());
    }

    @Test
    public void getOrderExtensionDetailDataTest() {
        // app is null
        getOrderExtensionDetailData2AppDOIsNullTest();
        // success
        getOrderExtensionDetailData2SuccessTest();
    }

    private void getOrderExtensionDetailData2AppDOIsNullTest() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(null);

        OrderDetailDeleteAppDTO data = (OrderDetailDeleteAppDTO) deleteAppOrder.getOrderExtensionDetailData(EXTENSIONS);

        Assert.assertNotNull(data);
        Assert.assertNull(data.getName());
    }

    private void getOrderExtensionDetailData2SuccessTest() {
        AppDO appDO = getAppDO();
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(appDO);
        Mockito.when(connectionService.getByAppId(
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Collections.emptyList());

        OrderDetailDeleteAppDTO data = (OrderDetailDeleteAppDTO) deleteAppOrder.getOrderExtensionDetailData(EXTENSIONS);

        Assert.assertNotNull(data);
        Assert.assertNotNull(data.getName());
        Assert.assertTrue(data.getConnectionList().isEmpty());
    }
}
