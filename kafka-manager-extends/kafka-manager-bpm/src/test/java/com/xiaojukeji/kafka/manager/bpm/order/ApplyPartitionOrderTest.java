package com.xiaojukeji.kafka.manager.bpm.order;

import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.PartitionOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.AdminService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
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
 * @Date 2021/12/30
 */
public class ApplyPartitionOrderTest extends BaseTest {

    private final static String EXTENSIONS = "{\"clusterId\":1,\"topicName\":\"moduleTest\",\"needIncrPartitionNum\":20}";

    private final static String INVALIDE_TOPIC_EXTENSIONS = "{\"clusterId\":1,\"topicName\":\"xxxx\",\"needIncrPartitionNum\":20}";

    private static final String APPROVE_ORDER_APPLY_DETAIL = "{\"brokerIdList\":[3],\"partitionNum\":1,\"regionId\":1}";

    private static final String INVALIDE_APPROVE_ORDER_APPLY_DETAIL = "{\"brokerIdList\":[3],\"partitionNum\":0,\"regionId\":1}";

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    /**
     * 工单状态, 0:待审批, 1:通过, 2:拒绝, 3:取消
     */
    private static final Integer ORDER_PASSED_STATUS = 1;

    @Value("${test.admin}")
    private String ADMIN;

    private static final String INVALID_USER_NAME = "xxxxx";

    private static final Integer INVALID_ORDER_TYPE = -1;

    private static final Integer APPLY_TOPIC_TYPE = 0;

    private static final Long ORDER_ID = 1L;

    private static final Long INVALID_ORDER_ID = -1L;

    @Autowired
    @Qualifier("applyPartitionOrder")
    @InjectMocks
    private AbstractOrder applyPartitionOrder;

    @Mock
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Mock
    private AdminService adminService;

    @Mock
    private ClusterService clusterService;

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

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void checkExtensionFieldsAndGenerateTitleTest() {
        // paramIllegal
        checkExtensionFieldsAndGenerateTitle2ParamIllegalTest();
        // cluster not exist
        checkExtensionFieldsAndGenerateTitle2ClusterNotExistTest();
        // topic not exist
        checkExtensionFieldsAndGenerateTitle2TopicNotExistTest();
        // success
        checkExtensionFieldsAndGenerateTitle2SuccessTest();
    }

    private void checkExtensionFieldsAndGenerateTitle2ParamIllegalTest() {
        Result<String> stringResult = applyPartitionOrder.checkExtensionFieldsAndGenerateTitle("{}");
        Assert.assertEquals(stringResult.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2ClusterNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.anyLong(), Mockito.any())).thenReturn(null);

        Result<String> stringResult = applyPartitionOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(stringResult.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2TopicNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);

        Result<String> stringResult = applyPartitionOrder.checkExtensionFieldsAndGenerateTitle(INVALIDE_TOPIC_EXTENSIONS);
        Assert.assertEquals(stringResult.getCode(), ResultStatus.TOPIC_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2SuccessTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);

        Result<String> stringResult = applyPartitionOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(stringResult.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void handleOrderDetailTest() {
        // cluster not exist
        handleOrderDetail2ClusterNotExistTest();
        // topic not exist
        handleOrderDetail2TopicNotExistTest();
        // operation failed
        handleOrderDetail2OperationFailedTest();
        // success
        handleOrderDetail2SuccessTest();
    }

    private void handleOrderDetail2ClusterNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.anyLong(), Mockito.any())).thenReturn(null);

        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = applyPartitionOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void handleOrderDetail2TopicNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);

        OrderDO orderDO = getOrderDO();
        orderDO.setExtensions(INVALIDE_TOPIC_EXTENSIONS);
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = applyPartitionOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.TOPIC_NOT_EXIST.getCode());
    }

    private void handleOrderDetail2OperationFailedTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);

        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        orderHandleBaseDTO.setDetail(INVALIDE_APPROVE_ORDER_APPLY_DETAIL);
        ResultStatus resultStatus = applyPartitionOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.OPERATION_FAILED.getCode());
    }

    private void handleOrderDetail2SuccessTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(adminService.expandPartitions(
                Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(ResultStatus.SUCCESS);

        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = applyPartitionOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void getOrderExtensionDetailDataTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        LogicalClusterDO logicalCluster = new LogicalClusterDO();
        logicalCluster.setId(REAL_CLUSTER_ID_IN_MYSQL);
        logicalCluster.setName("");
        Mockito.when(logicalClusterMetadataManager.getLogicalCluster(
                Mockito.anyLong(), Mockito.any())).thenReturn(logicalCluster);

        ClusterDO clusterDO = new ClusterDO();
        clusterDO.setId(REAL_CLUSTER_ID_IN_MYSQL);
        clusterDO.setClusterName("");
        Mockito.when(clusterService.getById(Mockito.any())).thenReturn(clusterDO);
        PartitionOrderDetailData data2 = (PartitionOrderDetailData) applyPartitionOrder.getOrderExtensionDetailData(EXTENSIONS);
        Assert.assertNotNull(data2);
        Assert.assertNotNull(data2.getPhysicalClusterId());
        Assert.assertNotNull(data2.getLogicalClusterId());
    }
}
