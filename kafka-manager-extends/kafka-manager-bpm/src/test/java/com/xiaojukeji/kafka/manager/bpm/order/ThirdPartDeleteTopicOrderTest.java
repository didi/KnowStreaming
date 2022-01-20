package com.xiaojukeji.kafka.manager.bpm.order;

import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailDeleteTopicDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.ClusterNameDTO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.AdminService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
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

import java.util.Collections;
import java.util.Date;

/**
 * @author xuguang
 * @Date 2021/12/31
 */
public class ThirdPartDeleteTopicOrderTest extends BaseTest {
    @Value("${test.admin}")
    private String ADMIN;

    private static final String INVALID_USER_NAME = "xxxxx";

    private static final Integer INVALID_ORDER_TYPE = -1;

    private static final Integer APPLY_TOPIC_TYPE = 0;

    private static final Long ORDER_ID = 1L;

    private static final Long INVALID_ORDER_ID = -1L;

    private static final String EXTENSIONS = "{\"clusterId\":7,\"topicName\":\"moduleTest\",\"appId\":\"dkm_admin\",\"password\":\"123456\"}";

    private static final String TOPIC_NOT_EXIST_EXTENSIONS = "{\"clusterId\":7,\"topicName\":\"xxxx\",\"appId\":\"dkm_admin\",\"password\":\"123456\"}";

    private static final String APPROVE_ORDER_APPLY_DETAIL = "{\"brokerIdList\":[3],\"partitionNum\":1,\"replicaNum\":1,\"retentionTime\":12}";

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    private static final Long INVALID_CLUSTER_ID = -1L;

    @Value("${test.app.id}")
    private String APP_ID;

    /**
     * 工单状态, 0:待审批, 1:通过, 2:拒绝, 3:取消
     */
    private static final Integer ORDER_PASSED_STATUS = 1;


    @Autowired
    @Qualifier("thirdPartDeleteTopicOrder")
    @InjectMocks
    private AbstractOrder thirdPartDeleteTopicOrder;

    @Mock
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Mock
    private TopicConnectionService connectionService;

    @Mock
    private AdminService adminService;

    @Mock
    private ClusterService clusterService;

    @Mock
    private TopicManagerService topicManagerService;

    @Mock
    private AppService appService;

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

    private OrderHandleBaseDTO getOrderHandleBaseDTO() {
        OrderHandleBaseDTO orderHandleBaseDTO = new OrderHandleBaseDTO();
        orderHandleBaseDTO.setId(ORDER_ID);
        orderHandleBaseDTO.setStatus(ORDER_PASSED_STATUS);
        orderHandleBaseDTO.setDetail(APPROVE_ORDER_APPLY_DETAIL);
        return orderHandleBaseDTO;
    }

    private ClusterNameDTO getClusterNameDTO() {
        ClusterNameDTO clusterNameDTO = new ClusterNameDTO();
        clusterNameDTO.setPhysicalClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        clusterNameDTO.setPhysicalClusterName("physicalClusterName");
        clusterNameDTO.setLogicalClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        clusterNameDTO.setLogicalClusterName("logicalClusterName");
        return clusterNameDTO;
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
        // user without authority
        checkExtensionFieldsAndGenerateTitle2WithoutAuthority();
        // success
        checkExtensionFieldsAndGenerateTitle2Success();
    }

    private void checkExtensionFieldsAndGenerateTitle2ParamIllegal() {
        Result<String> result = thirdPartDeleteTopicOrder.checkExtensionFieldsAndGenerateTitle("{}");
        org.testng.Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2ClusterNotExist() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(null);
        Result<String> result = thirdPartDeleteTopicOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        org.testng.Assert.assertEquals(result.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2TopicNotExist() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Result<String> result = thirdPartDeleteTopicOrder.checkExtensionFieldsAndGenerateTitle(TOPIC_NOT_EXIST_EXTENSIONS);
        org.testng.Assert.assertEquals(result.getCode(), ResultStatus.TOPIC_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2AppNotExist() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(null);

        Result<String> result = thirdPartDeleteTopicOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        org.testng.Assert.assertEquals(result.getCode(), ResultStatus.APP_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2WithoutAuthority() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        AppDO appDO = new AppDO();
        appDO.setPassword("xxx");
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(appDO);

        Result<String> result = thirdPartDeleteTopicOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        org.testng.Assert.assertEquals(result.getCode(), ResultStatus.USER_WITHOUT_AUTHORITY.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2Success() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        AppDO appDO = new AppDO();
        appDO.setPassword("123456");
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(appDO);

        Result<String> result = thirdPartDeleteTopicOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试处理删除topic工单")
    public void handleOrderDetail() {
        // cluster not exist
        handleOrderDetail2ClusterNotExistTest();
        // topic not exist
        handleOrderDetail2TopicNotExistTest();
        // operation forbidden, 因为最近topic还有生产和消费操作
        handleOrderDetail2OperationForbiddenTest();
        // app not exist
        handleOrderDetail2AppNotExistTest();
        // user without authority
        handleOrderDetail2UserWithoutAuthorityTest();
        // delete success
        handleOrderDetail2DeleteSuccessTest();
        // delete not success
        handleOrderDetail2DeleteNotSuccessTest();
    }

    private void handleOrderDetail2ClusterNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.anyLong(), Mockito.any())).thenReturn(null);

        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = thirdPartDeleteTopicOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void handleOrderDetail2TopicNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.anyLong(), Mockito.any())).thenReturn(INVALID_CLUSTER_ID);

        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = thirdPartDeleteTopicOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.TOPIC_NOT_EXIST.getCode());
    }

    private void handleOrderDetail2OperationForbiddenTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(connectionService.isExistConnection(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);

        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = thirdPartDeleteTopicOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.OPERATION_FORBIDDEN.getCode());
    }

    private void handleOrderDetail2AppNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(connectionService.isExistConnection(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(false);
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(null);

        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = thirdPartDeleteTopicOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.APP_NOT_EXIST.getCode());
    }

    private void handleOrderDetail2UserWithoutAuthorityTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(connectionService.isExistConnection(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(false);
        AppDO appDO = new AppDO();
        appDO.setPassword("xxx");
        appDO.setPrincipals(ADMIN);
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(appDO);

        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = thirdPartDeleteTopicOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.USER_WITHOUT_AUTHORITY.getCode());
    }

    private void handleOrderDetail2DeleteSuccessTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(connectionService.isExistConnection(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(false);
        Mockito.when(adminService.deleteTopic(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(ResultStatus.SUCCESS);
        AppDO appDO = new AppDO();
        appDO.setPassword("123456");
        appDO.setPrincipals(ADMIN);
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(appDO);

        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = thirdPartDeleteTopicOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void handleOrderDetail2DeleteNotSuccessTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(connectionService.isExistConnection(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(false);
        Mockito.when(adminService.deleteTopic(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(ResultStatus.FAIL);
        AppDO appDO = new AppDO();
        appDO.setPassword("123456");
        appDO.setPrincipals(ADMIN);
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(appDO);

        OrderDO orderDO = getOrderDO();
        OrderHandleBaseDTO orderHandleBaseDTO = getOrderHandleBaseDTO();
        ResultStatus resultStatus = thirdPartDeleteTopicOrder.handleOrderDetail(orderDO, orderHandleBaseDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.FAIL.getCode());
    }

    @Test(description = "")
    public void getOrderExtensionDetailDataTest() {
        // 获取成功
        getOrderExtensionDetailData2SuccessTest();
    }

    private void getOrderExtensionDetailData2SuccessTest() {
        ClusterDO clusterDO = new ClusterDO();
        clusterDO.setId(REAL_CLUSTER_ID_IN_MYSQL);
        clusterDO.setClusterName("");
        Mockito.when(clusterService.getById(Mockito.any())).thenReturn(clusterDO);
        Mockito.when(connectionService.getByTopicName(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Collections.emptyList());
        TopicDO topicDO = new TopicDO();
        topicDO.setAppId(APP_ID);
        Mockito.when(topicManagerService.getByTopicName(Mockito.any(), Mockito.any())).thenReturn(topicDO);
        AppDO appDO = new AppDO();
        appDO.setAppId(APP_ID);
        appDO.setName("");
        appDO.setPrincipals("");
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(appDO);

        OrderDetailDeleteTopicDTO data = (OrderDetailDeleteTopicDTO)thirdPartDeleteTopicOrder.getOrderExtensionDetailData(EXTENSIONS);
        Assert.assertNotNull(data);
        Assert.assertNotNull(data.getAppId());
        Assert.assertNotNull(data.getAppName());
    }
}
