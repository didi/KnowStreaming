package com.xiaojukeji.kafka.manager.bpm.order;

import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailDeleteAuthorityDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.ClusterNameDTO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.service.service.gateway.TopicConnectionService;
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

import java.util.Date;

/**
 * @author xuguang
 * @Date 2021/12/29
 */
public class DeleteAuthorityOrderTest extends BaseTest {

    private final static String EXTENSIONS = "{\"clusterId\":7,\"topicName\":\"moduleTest\",\"appId\":\"dkm_admin\",\"access\":\"3\"}";

    @Value("${test.phyCluster.id}")
    private final static Long REAL_CLUSTER_ID_IN_MYSQL = 1L;

    private static final Long ORDER_ID = 1L;

    @Value("${test.admin}")
    private String ADMIN;

    private static final Integer APPLY_TOPIC_TYPE = 0;

    @Value("${test.app.id}")
    private String APP_ID;

    private static final String INVALIDE_USER = "xxxx";

    /**
     * 工单状态, 0:待审批, 1:通过, 2:拒绝, 3:取消
     */
    private static final Integer ORDER_PASSED_STATUS = 1;

    @Autowired
    @Qualifier("deleteAuthorityOrder")
    @InjectMocks
    private AbstractOrder deleteAuthorityOrder;

    @Mock
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Mock
    private TopicConnectionService connectionService;

    @Mock
    private AuthorityService authorityService;

    @Mock
    private AppService appService;

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

    private AppDO getAppDO() {
        AppDO appDO = new AppDO();
        appDO.setId(4L);
        appDO.setAppId(APP_ID);
        appDO.setName("moduleTestApp");
        appDO.setPassword("moduleTestApp");
        appDO.setType(1);
        appDO.setApplicant(ADMIN);
        appDO.setPrincipals(ADMIN);
        appDO.setDescription("moduleTestApp");
        appDO.setCreateTime(new Date());
        appDO.setModifyTime(new Date());
        return appDO;
    }

    private ClusterNameDTO getClusterNameDTO() {
        ClusterNameDTO clusterNameDTO = new ClusterNameDTO();
        clusterNameDTO.setLogicalClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        clusterNameDTO.setPhysicalClusterName("");
        clusterNameDTO.setLogicalClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        clusterNameDTO.setLogicalClusterName("");
        return clusterNameDTO;
    }

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void handleOrderDetailTest() {
        // cluster not exist
        handleOrderDetail2ClusterNotExistTest();
        // OperationForbidden
        handleOrderDetail2OperationForbiddenTest();
        // success
        handleOrderDetail2SuccessTest();
    }

    private void handleOrderDetail2ClusterNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.any())).thenReturn(null);
        OrderDO orderDO = getOrderDO();

        ResultStatus resultStatus = deleteAuthorityOrder.handleOrderDetail(orderDO, new OrderHandleBaseDTO(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void handleOrderDetail2OperationForbiddenTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.any()))
                .thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(connectionService.isExistConnection(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);

        OrderDO orderDO = getOrderDO();
        ResultStatus resultStatus = deleteAuthorityOrder.handleOrderDetail(orderDO, new OrderHandleBaseDTO(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.OPERATION_FORBIDDEN.getCode());
    }

    private void handleOrderDetail2SuccessTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.any()))
                .thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(connectionService.isExistConnection(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(false);
        Mockito.when(authorityService.deleteSpecifiedAccess(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(ResultStatus.SUCCESS);

        OrderDO orderDO = getOrderDO();
        ResultStatus resultStatus = deleteAuthorityOrder.handleOrderDetail(orderDO, new OrderHandleBaseDTO(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void getOrderExtensionDetailDataTest() {
        // app is null
        getOrderExtensionDetailData2AppIsNullTest();
        // app is not null
        getOrderExtensionDetailData2AppNotNullTest();
    }

    private void getOrderExtensionDetailData2AppIsNullTest() {
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(null);
        ClusterNameDTO clusterNameDTO = getClusterNameDTO();
        Mockito.when(clusterService.getClusterName(Mockito.any())).thenReturn(clusterNameDTO);
        OrderDetailDeleteAuthorityDTO data = (OrderDetailDeleteAuthorityDTO) deleteAuthorityOrder.getOrderExtensionDetailData(EXTENSIONS);
        org.testng.Assert.assertNotNull(data);
        org.testng.Assert.assertNull(data.getAppName());
    }

    private void getOrderExtensionDetailData2AppNotNullTest() {
        AppDO appDO = getAppDO();
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(appDO);
        ClusterNameDTO clusterNameDTO = getClusterNameDTO();
        Mockito.when(clusterService.getClusterName(Mockito.any())).thenReturn(clusterNameDTO);
        OrderDetailDeleteAuthorityDTO data = (OrderDetailDeleteAuthorityDTO) deleteAuthorityOrder.getOrderExtensionDetailData(EXTENSIONS);
        org.testng.Assert.assertNotNull(data);
        org.testng.Assert.assertEquals(data.getAppName(), appDO.getName());
    }

}
