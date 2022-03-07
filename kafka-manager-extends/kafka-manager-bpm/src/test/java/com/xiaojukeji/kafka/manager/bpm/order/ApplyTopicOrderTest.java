package com.xiaojukeji.kafka.manager.bpm.order;

import com.xiaojukeji.kafka.manager.bpm.OrderService;
import com.xiaojukeji.kafka.manager.bpm.common.OrderStatusEnum;
import com.xiaojukeji.kafka.manager.bpm.common.entry.BaseOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailApplyTopicDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.AdminService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
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
 * @Date 2021/12/27
 */
public class ApplyTopicOrderTest extends BaseTest {
    @Value("${test.admin}")
    private String ADMIN;

    private static final String INVALID_USER_NAME = "xxxxx";

    private static final Integer INVALID_ORDER_TYPE = -1;

    private static final Integer APPLY_TOPIC_TYPE = 0;

    private static final Long ORDER_ID = 1L;

    private static final Long INVALID_ORDER_ID = -1L;

    private static final String EXTENSIONS = "{\"clusterId\":7,\"topicName\":\"moduleTest2\",\"appId\":\"dkm_admin\",\"peakBytesIn\":104857600000}";

    private static final String TOPIC_EXIST_EXTENSIONS = "{\"clusterId\":7,\"topicName\":\"moduleTest\",\"appId\":\"dkm_admin\",\"peakBytesIn\":104857600000}";

    private static final String APPROVE_ORDER_APPLY_DETAIL = "{\"brokerIdList\":[3],\"partitionNum\":1,\"replicaNum\":1,\"retentionTime\":12}";

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    /**
     * 工单状态, 0:待审批, 1:通过, 2:拒绝, 3:取消
     */
    private static final Integer ORDER_PASSED_STATUS = 1;


    @Autowired
    @Qualifier("applyTopicOrder")
    @InjectMocks
    private AbstractTopicOrder applyTopicOrder;

    @Mock
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Mock
    private AdminService adminService;

    @Mock
    private ClusterService clusterService;

    @Mock
    private AppService appService;

    @Mock
    private OrderService orderService;

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
        orderHandleBaseDTO.setStatus(ORDER_PASSED_STATUS);
        orderHandleBaseDTO.setDetail(APPROVE_ORDER_APPLY_DETAIL);
        return orderHandleBaseDTO;
    }

    private LogicalClusterDO getLogicalClusterDO() {
        LogicalClusterDO logicalClusterDO = new LogicalClusterDO();
        logicalClusterDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        logicalClusterDO.setIdentification("moduleTestLogicalCluster");
        logicalClusterDO.setName("moduleTestLogicalCluster");
        logicalClusterDO.setMode(0);
        logicalClusterDO.setRegionList("2,3");
        logicalClusterDO.setAppId("");
        logicalClusterDO.setGmtCreate(new Date());
        logicalClusterDO.setGmtModify(new Date());
        return logicalClusterDO;
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

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(description = "测试获取工单详情")
    public void getOrderDetailTest() {
        // null
        getOrderDetail2NullTest();
        // success
        getOrderDetail2SuccessTest();
    }

    private void getOrderDetail2NullTest() {
        BaseOrderDetailData orderDetail = applyTopicOrder.getOrderDetail(null);
        Assert.assertNull(orderDetail);
    }

    private void getOrderDetail2SuccessTest() {
        BaseOrderDetailData orderDetail = applyTopicOrder.getOrderDetail(getOrderDO());
        Assert.assertNotNull(orderDetail);
    }


    @Test
    public void getOrderExtensionDetailDataTest() {
        //  app is null
        getOrderExtensionDetailData2AppIsNullTest();
        // app is not null
        getOrderExtensionDetailData2AppIsNotNullTest();
    }

    private void getOrderExtensionDetailData2AppIsNullTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        LogicalClusterDO logicalClusterDO = getLogicalClusterDO();
        Mockito.when(logicalClusterMetadataManager.getLogicalCluster(
                Mockito.anyLong(), Mockito.any())).thenReturn(logicalClusterDO);

        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(null);

        OrderDetailApplyTopicDTO data = (OrderDetailApplyTopicDTO) applyTopicOrder.getOrderExtensionDetailData(EXTENSIONS);
        Assert.assertNotNull(data);
        Assert.assertNull(data.getAppName());
    }

    private void getOrderExtensionDetailData2AppIsNotNullTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        LogicalClusterDO logicalClusterDO = getLogicalClusterDO();
        Mockito.when(logicalClusterMetadataManager.getLogicalCluster(
                Mockito.anyLong(), Mockito.any())).thenReturn(logicalClusterDO);
        AppDO appDO = getAppDO();
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(appDO);

        OrderDetailApplyTopicDTO data = (OrderDetailApplyTopicDTO) applyTopicOrder.getOrderExtensionDetailData(EXTENSIONS);
        Assert.assertNotNull(data);
        Assert.assertNotNull(data.getAppId());
        Assert.assertEquals(data.getAppName(), appDO.getName());
    }

    @Test(description = "测试检查扩展字段并生成工单的Title")
    public void checkExtensionFieldsAndGenerateTitle() {
        // paramIllegal
        checkExtensionFieldsAndGenerateTitle2ParamIllegal();
        // cluster not exist
        checkExtensionFieldsAndGenerateTitle2ClusterNotExist();
        // topic already exist
        checkExtensionFieldsAndGenerateTitle2TopicAlreadyExist();
        // success
        checkExtensionFieldsAndGenerateTitle2Success();
    }

    private void checkExtensionFieldsAndGenerateTitle2ParamIllegal() {
        Result<String> result = applyTopicOrder.checkExtensionFieldsAndGenerateTitle("{}");
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2ClusterNotExist() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(null);
        Result<String> result = applyTopicOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2TopicAlreadyExist() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Result<String> result = applyTopicOrder.checkExtensionFieldsAndGenerateTitle(TOPIC_EXIST_EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.TOPIC_ALREADY_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2Success() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Result<String> result = applyTopicOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试处理具体的订单信息")
    public void handleOrderDetailTest() {
        // paramIllegal
        handleOrderDetail2ParamIllegalTest();
        // app not exist
        handleOrderDetail2AppNotExistTest();
        // cluster not exist
        handleOrderDetail2ClusterNotExistTest();
        // success
        handleOrderDetail2SuccessTest();
    }

    private void handleOrderDetail2ParamIllegalTest() {
        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        orderHandleBaseDTO.setDetail("{}");
        ResultStatus resultStatus = applyTopicOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void handleOrderDetail2AppNotExistTest() {
        OrderDO orderDO = getOrderDO();
        orderDO.setExtensions("{}");
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = applyTopicOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.APP_NOT_EXIST.getCode());
    }

    private void handleOrderDetail2ClusterNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(null);
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(new AppDO());

        OrderDO orderDO = getOrderDO();
        orderDO.setExtensions("{\"appId\":\"dkm_admin\"}");
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = applyTopicOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());

        orderDO.setExtensions("{\"appId\":\"dkm_admin\", \"clusterId\":\"-1\"}");
        ResultStatus resultStatus2 = applyTopicOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus2.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void handleOrderDetail2SuccessTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);

        Mockito.when(clusterService.getById(Mockito.anyLong())).thenReturn(new ClusterDO());
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(new AppDO());

        Mockito.when(adminService.createTopic(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())
        ).thenReturn(ResultStatus.SUCCESS);

        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = applyTopicOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试处理工单")
    public void handleOrderTest() {
        // not wait deal
        handleOrder2OrderAlreadyHandledTest();
        // check authority and no authority
        handleOrder2WithoutAuthorityTest();
        // refuse
        handleOrder2RefuseTest();
    }

    private void handleOrder2OrderAlreadyHandledTest() {
        OrderDO orderDO = getOrderDO();
        orderDO.setStatus(OrderStatusEnum.PASSED.getCode());
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = applyTopicOrder.handleOrder(orderDO, orderHandleBaseDTO, ADMIN, true);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.ORDER_ALREADY_HANDLED.getCode());
    }

    private void handleOrder2WithoutAuthorityTest() {
        OrderDO orderDO = getOrderDO();
        orderDO.setStatus(OrderStatusEnum.WAIT_DEAL.getCode());
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = applyTopicOrder.handleOrder(
                orderDO, orderHandleBaseDTO,
                INVALID_USER_NAME, true
        );
        Assert.assertNotEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void handleOrder2RefuseTest() {
        Mockito.when(orderService.updateOrderById(Mockito.any())).thenReturn(1);

        OrderDO orderDO = getOrderDO();
        orderDO.setStatus(OrderStatusEnum.WAIT_DEAL.getCode());
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        orderHandleBaseDTO.setStatus(OrderStatusEnum.REFUSED.getCode());
        ResultStatus resultStatus = applyTopicOrder.handleOrder(
                orderDO, orderHandleBaseDTO,
                ADMIN, true
        );
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

}
