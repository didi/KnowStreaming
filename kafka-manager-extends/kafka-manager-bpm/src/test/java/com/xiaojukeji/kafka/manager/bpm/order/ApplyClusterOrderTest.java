package com.xiaojukeji.kafka.manager.bpm.order;

import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailApplyClusterDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.utils.ConfigUtils;
import org.junit.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author xuguang
 * @Date 2021/12/30
 */
public class ApplyClusterOrderTest extends BaseTest {

    @Value("${test.app.id}")
    private String APP_ID;

    private final static String IDC = "国内";

    private final static String EXTENSIONS = "{\"idc\":\"国内\",\"bytesIn\":2000,\"mode\":200,\"appId\":\"dkm_admin\"}";

    private final static String INVALID_IDC = "xxx";

    @Value("${test.admin}")
    private String ADMIN;

    @Autowired
    @Qualifier("applyClusterOrder")
    @InjectMocks
    private AbstractOrder applyClusterOrder;

    @Mock
    private AppService appService;

    @Mock
    private ConfigUtils configUtils;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void checkExtensionFieldsAndGenerateTitleTest() {
        // paramIllegal
        checkExtensionFieldsAndGenerateTitle2ParamIllegalTest();
        // app not exist
        checkExtensionFieldsAndGenerateTitle2AppNotExistTest();
        // idc not exist
        checkExtensionFieldsAndGenerateTitle2IdcNotExistTest();
        // success
        checkExtensionFieldsAndGenerateTitle2SuccessTest();
    }

    private void checkExtensionFieldsAndGenerateTitle2ParamIllegalTest() {
        Result<String> result = applyClusterOrder.checkExtensionFieldsAndGenerateTitle("{}");
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2AppNotExistTest() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(null);

        Result<String> result = applyClusterOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.APP_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2IdcNotExistTest() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(new AppDO());
        Mockito.when(configUtils.getIdc()).thenReturn(INVALID_IDC);

        Result<String> result = applyClusterOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.IDC_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2SuccessTest() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(new AppDO());
        Mockito.when(configUtils.getIdc()).thenReturn(IDC);

        Result<String> result = applyClusterOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void getOrderExtensionDetailDataTest() {
        OrderDetailApplyClusterDTO data = (OrderDetailApplyClusterDTO) applyClusterOrder.getOrderExtensionDetailData(EXTENSIONS);
        Assert.assertEquals(data.getAppId(), APP_ID);
        Assert.assertEquals(data.getIdc(), IDC);
    }

    @Test
    public void handleOrderDetail() {
        ResultStatus resultStatus = applyClusterOrder.handleOrderDetail(new OrderDO(), new OrderHandleBaseDTO(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

}
