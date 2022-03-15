package com.xiaojukeji.kafka.manager.bpm.order;

import com.xiaojukeji.kafka.manager.bpm.OrderService;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.QuotaOrderDetailData;
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
import com.xiaojukeji.kafka.manager.service.service.RegionService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.QuotaService;
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
 * @Date 2021/12/30
 */
public class ApplyQuotaOrderTest extends BaseTest {

    @Value("${test.admin}")
    private String ADMIN;

    private static final String INVALID_USER_NAME = "xxxxx";

    private static final Integer INVALID_ORDER_TYPE = -1;

    private static final Integer APPLY_TOPIC_TYPE = 0;

    private static final Long ORDER_ID = 1L;

    private static final Long INVALID_ORDER_ID = -1L;

    private static final String EXTENSIONS = "{\"appId\":\"dkm_admin\",\"clusterId\":7,\"partitionNum\":2,\"produceQuota\":104857600,\"consumeQuota\":104857600,\"regionId\":1,\"topicName\":\"moduleTest\",\"brokerIdList\":[3]}";

    private static final String TOPIC_NOT_EXIST_EXTENSIONS = "{\"appId\":\"dkm_admin\",\"clusterId\":7,\"partitionNum\":2,\"produceQuota\":104857600,\"consumeQuota\":104857600,\"regionId\":1,\"retentionTime\":12,\"topicName\":\"xxx\",\"brokerIdList\":[3]}";

    private static final String APPROVE_ORDER_APPLY_DETAIL = "{\"brokerIdList\":[3],\"partitionNum\":1,\"regionId\":1}";

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    /**
     * 工单状态, 0:待审批, 1:通过, 2:拒绝, 3:取消
     */
    private static final Integer ORDER_PASSED_STATUS = 1;


    @Autowired
    @Qualifier("applyQuotaOrder")
    @InjectMocks
    private AbstractOrder applyQuotaOrder;

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

    @Mock
    private QuotaService quotaService;

    @Mock
    private TopicManagerService topicManagerService;

    @Mock
    private RegionService regionService;

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

    @Test(description = "测试检查扩展字段并生成工单的Title")
    public void checkExtensionFieldsAndGenerateTitle() {
        // paramIllegal
        checkExtensionFieldsAndGenerateTitle2ParamIllegal();
        // cluster not exist
        checkExtensionFieldsAndGenerateTitle2ClusterNotExist();
        // topic not exist
        checkExtensionFieldsAndGenerateTitle2TopicNotExist();
        // app not exist
        checkExtensionFieldsAndGenerateTitle2AppNotExist();
        // success
        checkExtensionFieldsAndGenerateTitle2Success();
    }

    private void checkExtensionFieldsAndGenerateTitle2ParamIllegal() {
        Result<String> result = applyQuotaOrder.checkExtensionFieldsAndGenerateTitle("{}");
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2ClusterNotExist() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(null);
        Result<String> result = applyQuotaOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2TopicNotExist() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Result<String> result = applyQuotaOrder.checkExtensionFieldsAndGenerateTitle(TOPIC_NOT_EXIST_EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.TOPIC_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2AppNotExist() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(null);

        Result<String> result = applyQuotaOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.APP_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2Success() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(new AppDO());

        Result<String> result = applyQuotaOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void handleOrderDetail() {
        // app not exist
        handleOrderDetail2AppNotExist();
        // cluster not exist
        handleOrderDetail2ClusterNotExist();
        // topic not exist
        handleOrderDetail2TopicNotExist();
        //  operation fobidden
        handleOrderDetail2OperationFobiddenTest();
        // success
        handleOrderDetail2SuccessTest();
        //
        handleOrderDetail2OperationFailedTest();
    }

    private void handleOrderDetail2AppNotExist() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(null);

        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = applyQuotaOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.APP_NOT_EXIST.getCode());
    }

    private void handleOrderDetail2ClusterNotExist() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(new AppDO());
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.anyLong(), Mockito.any())).thenReturn(null);

        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = applyQuotaOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void handleOrderDetail2TopicNotExist() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(new AppDO());
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);

        OrderDO orderDO = getOrderDO();
        orderDO.setExtensions(TOPIC_NOT_EXIST_EXTENSIONS);
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = applyQuotaOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.TOPIC_NOT_EXIST.getCode());
    }

    private void handleOrderDetail2OperationFobiddenTest() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(new AppDO());
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(adminService.expandPartitions(
                Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(ResultStatus.OPERATION_FORBIDDEN);

        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = applyQuotaOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.OPERATION_FORBIDDEN.getCode());
    }

    private void handleOrderDetail2SuccessTest() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(new AppDO());
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(adminService.expandPartitions(
                Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(ResultStatus.SUCCESS);
        Mockito.when(quotaService.addTopicQuota(Mockito.any())).thenReturn(1);

        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = applyQuotaOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void handleOrderDetail2OperationFailedTest() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(new AppDO());
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(adminService.expandPartitions(
                Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(ResultStatus.SUCCESS);
        Mockito.when(quotaService.addTopicQuota(Mockito.any())).thenReturn(0);

        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = applyQuotaOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.OPERATION_FAILED.getCode());
    }

    @Test
    public void getOrderExtensionDetailData() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        AppDO appDO = getAppDO();
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(appDO);
        Mockito.when(topicManagerService.getTopicStatistic(
                Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any())).thenReturn(Collections.emptyList());
        Mockito.when(regionService.getRegionListByTopicName(Mockito.any(), Mockito.any())).thenReturn(Collections.emptyList());
        ClusterDO clusterDO = new ClusterDO();
        clusterDO.setId(REAL_CLUSTER_ID_IN_MYSQL);
        clusterDO.setClusterName("");
        Mockito.when(clusterService.getById(Mockito.any())).thenReturn(clusterDO);
        LogicalClusterDO logicalClusterDO = new LogicalClusterDO();
        logicalClusterDO.setId(REAL_CLUSTER_ID_IN_MYSQL);
        logicalClusterDO.setName("");
        Mockito.when(logicalClusterMetadataManager.getLogicalCluster(
                Mockito.anyLong(), Mockito.any())).thenReturn(logicalClusterDO);

        QuotaOrderDetailData data = (QuotaOrderDetailData) applyQuotaOrder.getOrderExtensionDetailData(EXTENSIONS);
        Assert.assertEquals(data.getLogicalClusterId(), REAL_CLUSTER_ID_IN_MYSQL);
    }
}
