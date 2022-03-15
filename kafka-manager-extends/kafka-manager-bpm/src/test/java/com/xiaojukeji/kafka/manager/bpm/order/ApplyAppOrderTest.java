package com.xiaojukeji.kafka.manager.bpm.order;

import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailApplyAppDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.utils.ConfigUtils;
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

import java.util.Date;

/**
 * @author xuguang
 * @Date 2021/12/29
 */
public class ApplyAppOrderTest extends BaseTest {

    private static final String EXTENSIONS = "{\"name\":\"dkm_admin\",\"idc\":\"country\",\"principals\":\"admin\"}";

    private static final Long ORDER_ID = 1L;

    @Value("${test.admin}")
    private String ADMIN;

    private static final Integer APPLY_TOPIC_TYPE = 0;

    /**
     * 工单状态, 0:待审批, 1:通过, 2:拒绝, 3:取消
     */
    private static final Integer ORDER_PASSED_STATUS = 1;

    @Value("${test.app.id}")
    private String APP_ID;

    @Autowired
    @Qualifier("applyAppOrder")
    @InjectMocks
    private AbstractOrder applyAppOrder;

    @Mock
    private AppService appService;

    @Mock
    private ConfigUtils configUtils;

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

    @Test
    public void checkExtensionFieldsAndGenerateTitleTest() {
        // paramIllegal
        checkExtensionFieldsAndGenerateTitle2ParamIllegalTest();
        // resourceAlreadyExist
        checkExtensionFieldsAndGenerateTitle2ResourceAlreadyExistTest();
        // idc not exist
        checkExtensionFieldsAndGenerateTitle2IdcNotExistTest();
        // success
        checkExtensionFieldsAndGenerateTitle2SuccessTest();
    }

    private void checkExtensionFieldsAndGenerateTitle2ParamIllegalTest() {
        Result<String> result = applyAppOrder.checkExtensionFieldsAndGenerateTitle("{}");
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2ResourceAlreadyExistTest() {
        Mockito.when(appService.getByName(Mockito.any())).thenReturn(new AppDO());

        Result<String> result = applyAppOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.RESOURCE_ALREADY_EXISTED.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2IdcNotExistTest() {
        Mockito.when(appService.getByName(Mockito.any())).thenReturn(null);
        Mockito.when(configUtils.getIdc()).thenReturn("");

        Result<String> result = applyAppOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.IDC_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2SuccessTest() {
        Mockito.when(appService.getByName(Mockito.any())).thenReturn(null);
        Mockito.when(configUtils.getIdc()).thenReturn("country");

        Result<String> result = applyAppOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "")
    public void handleOrderDetailTest() {
        Mockito.when(appService.addApp(Mockito.any(), Mockito.any())).thenReturn(ResultStatus.SUCCESS);

        OrderDO orderDO = getOrderDO();
        ResultStatus resultStatus = applyAppOrder.handleOrderDetail(orderDO, new OrderHandleBaseDTO(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "")
    public void getOrderExtensionDetailDataTest() {
        AppDO appDO = new AppDO();
        appDO.setAppId(APP_ID);
        appDO.setPassword("password");
        Mockito.when(appService.getByName(Mockito.any())).thenReturn(appDO);
        OrderDetailApplyAppDTO data = (OrderDetailApplyAppDTO) applyAppOrder.getOrderExtensionDetailData(EXTENSIONS);
        Assert.assertNotNull(data);
        Assert.assertEquals(data.getAppId(), APP_ID);
    }
}
