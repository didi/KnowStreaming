package com.xiaojukeji.kafka.manager.bpm.order;

import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailGatewayConfigModifyData;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.GatewayConfigDO;
import com.xiaojukeji.kafka.manager.service.service.gateway.GatewayConfigService;
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

/**
 * @author xuguang
 * @Date 2021/12/31
 */
public class ModifyGatewayConfigOrderTest extends BaseTest {
    @Value("${test.admin}")
    private String ADMIN;

    private static final String INVALID_USER_NAME = "xxxxx";

    private static final Integer INVALID_ORDER_TYPE = -1;

    private static final Integer APPLY_TOPIC_TYPE = 0;

    private static final Long ORDER_ID = 1L;

    private static final Long INVALID_ORDER_ID = -1L;

    private static final String GATEWAY_TYPE = "SD_CLUSTER_ID";

    private static final String EXTENSIONS = "{\"id\":1,\"type\":\"SD_CLUSTER_ID\",\"name\":\"gateway\",\"value\":\"gateway\",\"description\":\"gateway\"}";

    private static final String INVALIDE_TYPE_EXTENSIONS = "{\"id\":1,\"type\":\"xxxx\",\"name\":\"gateway\",\"value\":\"gateway\",\"description\":\"gateway\"}";

    private static final String APPROVE_ORDER_APPLY_DETAIL = "{\"brokerIdList\":[3],\"partitionNum\":1,\"replicaNum\":1,\"retentionTime\":12}";

    private static final Long REAL_CLUSTER_ID_IN_MYSQL = 1L;

    private static final Long INVALID_CLUSTER_ID = -1L;

    @Value("${test.app.id}")
    private String APP_ID;

    /**
     * 工单状态, 0:待审批, 1:通过, 2:拒绝, 3:取消
     */
    private static final Integer ORDER_PASSED_STATUS = 1;


    @Autowired
    @Qualifier("modifyGatewayConfigOrder")
    @InjectMocks
    private AbstractOrder modifyGatewayConfigOrder;

    @Mock
    private GatewayConfigService gatewayConfigService;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void checkExtensionFieldsAndGenerateTitleTest() {
        // paramIllegal
        checkExtensionFieldsAndGenerateTitle2ParamIllegal1Test();
        checkExtensionFieldsAndGenerateTitle2ParamIllegal2Test();
        checkExtensionFieldsAndGenerateTitle2ParamIllegal3Test();
        checkExtensionFieldsAndGenerateTitle2ParamIllegal4Test();
        // success
        checkExtensionFieldsAndGenerateTitle2SuccessTest();
    }

    private void checkExtensionFieldsAndGenerateTitle2ParamIllegal1Test() {
        Result<String> result = modifyGatewayConfigOrder.checkExtensionFieldsAndGenerateTitle("{");
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2ParamIllegal2Test() {
        Result<String> result = modifyGatewayConfigOrder.checkExtensionFieldsAndGenerateTitle("{}");
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2ParamIllegal3Test() {
        Mockito.when(gatewayConfigService.getById(Mockito.anyLong())).thenReturn(null);
        Result<String> result = modifyGatewayConfigOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2ParamIllegal4Test() {
        Mockito.when(gatewayConfigService.getById(Mockito.anyLong())).thenReturn(new GatewayConfigDO());
        Result<String> result = modifyGatewayConfigOrder.checkExtensionFieldsAndGenerateTitle(INVALIDE_TYPE_EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2SuccessTest() {
        Mockito.when(gatewayConfigService.getById(Mockito.anyLong())).thenReturn(new GatewayConfigDO());
        Result<String> result = modifyGatewayConfigOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void handleOrderDetailTest() {
        ResultStatus resultStatus = modifyGatewayConfigOrder.handleOrderDetail(new OrderDO(), new OrderHandleBaseDTO(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void getOrderExtensionDetailDataTest() {
        // result is null
        getOrderExtensionDetailData2nullTest();
        // old config is null
        getOrderExtensionDetailData2OldConfigNullTest();
        // old config is not null
        getOrderExtensionDetailData2OldConfigNotNullTest();
    }

    private void getOrderExtensionDetailData2nullTest() {
        OrderDetailGatewayConfigModifyData data = (OrderDetailGatewayConfigModifyData)
                modifyGatewayConfigOrder.getOrderExtensionDetailData("{");
        Assert.assertNull(data);
    }

    private void getOrderExtensionDetailData2OldConfigNullTest() {
        Mockito.when(gatewayConfigService.getById(Mockito.anyLong())).thenReturn(null);

        OrderDetailGatewayConfigModifyData data = (OrderDetailGatewayConfigModifyData)
                modifyGatewayConfigOrder.getOrderExtensionDetailData(EXTENSIONS);
        Assert.assertNotNull(data);
        Assert.assertEquals(data.getNewGatewayConfig().getType(), GATEWAY_TYPE);
    }

    private void getOrderExtensionDetailData2OldConfigNotNullTest() {
        GatewayConfigDO gatewayConfigDO = new GatewayConfigDO();
        String invalid_value = "xxx";
        gatewayConfigDO.setType(invalid_value);
        gatewayConfigDO.setId(1L);
        gatewayConfigDO.setValue(invalid_value);
        gatewayConfigDO.setName(invalid_value);
        gatewayConfigDO.setVersion(1L);
        Mockito.when(gatewayConfigService.getById(Mockito.anyLong())).thenReturn(gatewayConfigDO);

        OrderDetailGatewayConfigModifyData data = (OrderDetailGatewayConfigModifyData)
                modifyGatewayConfigOrder.getOrderExtensionDetailData(EXTENSIONS);
        Assert.assertNotNull(data);
        Assert.assertEquals(data.getNewGatewayConfig().getType(), invalid_value);
    }

}
