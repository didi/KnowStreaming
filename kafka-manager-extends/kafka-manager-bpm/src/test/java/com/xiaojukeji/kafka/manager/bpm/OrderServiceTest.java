package com.xiaojukeji.kafka.manager.bpm;

import com.xiaojukeji.kafka.manager.account.AccountService;
import com.xiaojukeji.kafka.manager.bpm.common.OrderResult;
import com.xiaojukeji.kafka.manager.bpm.common.OrderStatusEnum;
import com.xiaojukeji.kafka.manager.bpm.common.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBatchDTO;
import com.xiaojukeji.kafka.manager.bpm.component.AbstractOrderStorageService;
import com.xiaojukeji.kafka.manager.bpm.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author xuguang
 * @Date 2021/12/27
 */
public class OrderServiceTest extends BaseTest {

    @Value("${test.admin}")
    private String ADMIN;

    private static final Integer INVALID_ORDER_TYPE = -1;

    private static final Integer APPLY_TOPIC_TYPE = 0;

    private static final Long ORDER_ID = 1L;

    private static final Long INVALID_ORDER_ID = -1L;

    // EXTENSIONS中的clusterId需要是自己数据库中真实的逻辑集群id，这样createOrder才能跑通
    private static final String EXTENSIONS = "{\"clusterId\":15,\"topicName\":\"moduleTest2\",\"appId\":\"dkm_admin\",\"peakBytesIn\":104857600000}";

    /**
     * 工单状态, 0:待审批, 1:通过, 2:拒绝, 3:取消
     */
    private static final Integer ORDER_PASSED_STATUS = 1;

    private static final Integer ORDER_REFUSED_STATUS = 2;

    @Autowired
    @InjectMocks
    private OrderService orderService;

    @Mock
    private AbstractOrderStorageService orderStorageService;

    @Mock
    private AccountService accountService;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private OrderDTO getOrderDTO() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setApplicant(ADMIN);
        orderDTO.setType(APPLY_TOPIC_TYPE);
        orderDTO.setExtensions(EXTENSIONS);
        orderDTO.setDescription("测试用测试用");
        return orderDTO;
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

    private OrderHandleBaseDTO getOrderHandleBaseDTO() {
        OrderHandleBaseDTO orderHandleBaseDTO = new OrderHandleBaseDTO();
        orderHandleBaseDTO.setId(ORDER_ID);
        orderHandleBaseDTO.setDetail("");
        orderHandleBaseDTO.setStatus(ORDER_PASSED_STATUS);
        orderHandleBaseDTO.setOpinion("");
        return orderHandleBaseDTO;
    }

    @Test(description = "测试创建工单")
    public void createOrder() {
        // paramIllegal
        createOrder2ParamIllegal();
        // checkExtensionFieldsAndGenerateTitle false
        createOrder2Success();
        // mysql error
        createOrder2MysqlError();
    }

    private void createOrder2ParamIllegal() {
        Result order1 = orderService.createOrder(null);
        Assert.assertEquals(order1.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());

        OrderDTO orderDTO = getOrderDTO();
        orderDTO.setType(INVALID_ORDER_TYPE);
        Result order2 = orderService.createOrder(orderDTO);
        Assert.assertEquals(order2.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void createOrder2Success() {
        Mockito.when(orderStorageService.save(Mockito.any())).thenReturn(true);

        OrderDTO orderDTO = getOrderDTO();
        Result order2 = orderService.createOrder(orderDTO);
        Assert.assertEquals(order2.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void createOrder2MysqlError() {
        Mockito.when(orderStorageService.save(Mockito.any())).thenReturn(false);

        OrderDTO orderDTO = getOrderDTO();
        Result order2 = orderService.createOrder(orderDTO);
        Assert.assertEquals(order2.getCode(), ResultStatus.MYSQL_ERROR.getCode());
    }

    @Test(description = "测试工单详情")
    public void getOrderDetailDataTest() {
        // order not exist
        getOrderDetailData2OrderNotExistTest();
        // param illegal
        getOrderDetailData2ParamIllegalTest();
        // success
        getOrderDetailData2SuccessTest();
    }

    private void getOrderDetailData2OrderNotExistTest() {
        Mockito.when(orderStorageService.getById(Mockito.anyLong())).thenReturn(null);

        Result orderDetailData = orderService.getOrderDetailData(ORDER_ID);
        Assert.assertEquals(orderDetailData.getCode(), ResultStatus.ORDER_NOT_EXIST.getCode());
    }

    private void getOrderDetailData2ParamIllegalTest() {
        Mockito.when(orderStorageService.getById(Mockito.anyLong())).thenReturn(new OrderDO());

        Result orderDetailData = orderService.getOrderDetailData(ORDER_ID);
        Assert.assertEquals(orderDetailData.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void getOrderDetailData2SuccessTest() {
        OrderDO orderDO = getOrderDO();
        Mockito.when(orderStorageService.getById(Mockito.anyLong())).thenReturn(orderDO);

        Result orderDetailData = orderService.getOrderDetailData(ORDER_ID);
        Assert.assertEquals(orderDetailData.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试处理工单")
    public void handleOrderTest() {
        // paramIllegal
        handleOrder2ParamIllegalTest();
        // orderNotExist
        handleOrder2OrderNotExistTest();
        // orderAlreadyHandled,
        // SpringTool.getUserName() 需要构建上下文对象
        handleOrder2OrderAlreadyHandledTest();
    }

    private void handleOrder2ParamIllegalTest() {
        ResultStatus result1 = orderService.handleOrder(null);
        Assert.assertEquals(result1.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());

        OrderDO orderDO = getOrderDO();
        orderDO.setType(INVALID_ORDER_TYPE);
        Mockito.when(orderStorageService.getById(Mockito.anyLong())).thenReturn(orderDO);
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus result2 = orderService.handleOrder(orderHandleBaseDTO);
        Assert.assertEquals(result2.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void handleOrder2OrderNotExistTest() {
        Mockito.when(orderStorageService.getById(Mockito.anyLong())).thenReturn(null);
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus result2 = orderService.handleOrder(orderHandleBaseDTO);
        Assert.assertEquals(result2.getCode(), ResultStatus.ORDER_NOT_EXIST.getCode());
    }

    private void handleOrder2OrderAlreadyHandledTest() {
        OrderDO orderDO = getOrderDO();
        Mockito.when(orderStorageService.getById(Mockito.anyLong())).thenReturn(orderDO);
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus result2 = orderService.handleOrder(orderHandleBaseDTO);
        Assert.assertEquals(result2.getCode(), ResultStatus.ORDER_ALREADY_HANDLED.getCode());
    }

    @Test(description = "测试获取全部的工单审核列表")
    public void getApprovalListTest() {
        Mockito.when(accountService.isAdminOrderHandler(Mockito.any())).thenReturn(false);
        OrderDO orderDO = getOrderDO();
        Mockito.when(orderStorageService.getByApproverAndStatus(Mockito.anyString(), Mockito.any()))
                .thenReturn(Arrays.asList(orderDO));

    }

    @Test
    public void handleOrderBatchTest() {
        // 要通过所有审批
        handleOrderBatchWithPassedTest();
        // 要拒绝所有审批
        handleOrderBatchWithRefusedTest();
    }

    private void handleOrderBatchWithPassedTest() {
        /*
        构造数据，尽量走handleOrderBatch的每个流程
         */
        OrderDO orderDO1 = getOrderDO();
        Mockito.when(orderStorageService.getById(ORDER_ID)).thenReturn(orderDO1);
        Mockito.when(orderStorageService.getById(INVALID_ORDER_ID)).thenReturn(null);

        OrderDO orderDO2 = getOrderDO();
        orderDO2.setId(2L);
        orderDO2.setType(OrderTypeEnum.APPLY_APP.getCode());
        Mockito.when(orderStorageService.getById(2L)).thenReturn(orderDO2);

        OrderHandleBatchDTO orderHandleBatchDTO = new OrderHandleBatchDTO();
        orderHandleBatchDTO.setStatus(OrderStatusEnum.PASSED.getCode());
        orderHandleBatchDTO.setOrderIdList(Arrays.asList(ORDER_ID, INVALID_ORDER_ID, 2L));
        List<OrderResult> orderResults = orderService.handleOrderBatch(orderHandleBatchDTO, ADMIN);
        Assert.assertFalse(orderResults.isEmpty());
    }

    private void handleOrderBatchWithRefusedTest() {
        /*
        构造数据，尽量走handleOrderBatch的每个流程
         */
        OrderDO orderDO1 = getOrderDO();
        Mockito.when(orderStorageService.getById(ORDER_ID)).thenReturn(orderDO1);
        Mockito.when(orderStorageService.getById(INVALID_ORDER_ID)).thenReturn(null);

        OrderDO orderDO2 = getOrderDO();
        orderDO2.setId(2L);
        orderDO2.setType(OrderTypeEnum.APPLY_APP.getCode());
        Mockito.when(orderStorageService.getById(2L)).thenReturn(orderDO2);

        OrderHandleBatchDTO orderHandleBatchDTO = new OrderHandleBatchDTO();
        orderHandleBatchDTO.setStatus(OrderStatusEnum.REFUSED.getCode());
        orderHandleBatchDTO.setOrderIdList(Arrays.asList(ORDER_ID, INVALID_ORDER_ID, 2L));
        List<OrderResult> orderResults = orderService.handleOrderBatch(orderHandleBatchDTO, ADMIN);
        Assert.assertFalse(orderResults.isEmpty());
    }

}
