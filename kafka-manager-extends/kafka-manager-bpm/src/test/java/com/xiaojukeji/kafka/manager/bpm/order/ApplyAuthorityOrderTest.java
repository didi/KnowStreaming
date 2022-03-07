package com.xiaojukeji.kafka.manager.bpm.order;

import com.xiaojukeji.kafka.manager.account.AccountService;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailApplyAuthorityDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
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
import java.util.List;

/**
 * @author xuguang
 * @Date 2021/12/29
 */
public class ApplyAuthorityOrderTest extends BaseTest {

    private final static String EXTENSIONS = "{\"clusterId\":7,\"topicName\":\"moduleTest\",\"appId\":\"dkm_admin\",\"access\":\"3\"}";

    private final static String TOPIC_NOT_EXIST_EXTENSIONS = "{\"clusterId\":7,\"topicName\":\"xxxx\",\"appId\":\"dkm_admin\",\"access\":\"3\"}";

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

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
    @Qualifier("applyAuthorityOrder")
    @InjectMocks
    private AbstractOrder applyAuthorityOrder;

    @Mock
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Mock
    private AppService appService;

    @Mock
    private TopicManagerService topicManagerService;

    @Mock
    private AccountService accountService;

    @Mock
    private AuthorityService authorityService;

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


    @Test
    public void checkExtensionFieldsAndGenerateTitleTest() {
        // paramIllegal
        checkExtensionFieldsAndGenerateTitle2ParamIllegalTest();
        // cluster not exist
        checkExtensionFieldsAndGenerateTitle2ClusterNotExistTest();
        // topic not exist
        checkExtensionFieldsAndGenerateTitle2TopicNotExistTest();
        // app not exist
        checkExtensionFieldsAndGenerateTitle2AppNotExistTest();
        // success
        checkExtensionFieldsAndGenerateTitle2SuccessTest();
    }

    private void checkExtensionFieldsAndGenerateTitle2ParamIllegalTest() {
        Result<String> result = applyAuthorityOrder.checkExtensionFieldsAndGenerateTitle("{}");
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2ClusterNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(null);
        Result<String> result = applyAuthorityOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2TopicNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Result<String> result = applyAuthorityOrder.checkExtensionFieldsAndGenerateTitle(TOPIC_NOT_EXIST_EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.TOPIC_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2AppNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(null);

        Result<String> result = applyAuthorityOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.APP_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2SuccessTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(new AppDO());

        Result<String> result = applyAuthorityOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void checkAuthorityTest() {
        // JsonParseError
        checkAuthority2JsonParseErrorTest();
        // cluster not exist
        checkAuthority2ClusterNotExistTest();
        // topic not exist
        checkAuthority2TopicNotExistTest();
        // user without authority
        checkAuthority2UserWithoutAuthorityTest();
        // success
        checkAuthority2SuccessTest();
    }

    private void checkAuthority2JsonParseErrorTest() {
        OrderDO orderDO = getOrderDO();
        orderDO.setExtensions("{");
        ResultStatus resultStatus = applyAuthorityOrder.checkAuthority(orderDO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.JSON_PARSER_ERROR.getCode());
    }

    private void checkAuthority2ClusterNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(null);

        OrderDO orderDO = getOrderDO();
        ResultStatus resultStatus = applyAuthorityOrder.checkAuthority(orderDO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void checkAuthority2TopicNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(topicManagerService.getByTopicName(Mockito.any(), Mockito.any())).thenReturn(null);

        OrderDO orderDO = getOrderDO();
        ResultStatus resultStatus = applyAuthorityOrder.checkAuthority(orderDO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.TOPIC_BIZ_DATA_NOT_EXIST.getCode());
    }

    private void checkAuthority2UserWithoutAuthorityTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        TopicDO topicDO = new TopicDO();
        topicDO.setAppId(APP_ID);
        Mockito.when(topicManagerService.getByTopicName(Mockito.any(), Mockito.any())).thenReturn(topicDO);
        AppDO appDO = new AppDO();
        appDO.setPrincipals("");
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(appDO);

        OrderDO orderDO = getOrderDO();
        ResultStatus resultStatus = applyAuthorityOrder.checkAuthority(orderDO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.USER_WITHOUT_AUTHORITY.getCode());
    }

    private void checkAuthority2SuccessTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        TopicDO topicDO = new TopicDO();
        topicDO.setAppId(APP_ID);
        Mockito.when(topicManagerService.getByTopicName(Mockito.any(), Mockito.any())).thenReturn(topicDO);
        AppDO appDO = new AppDO();
        appDO.setPrincipals(ADMIN);
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(appDO);

        OrderDO orderDO = getOrderDO();
        ResultStatus resultStatus = applyAuthorityOrder.checkAuthority(orderDO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void getApproverListTest() {
        // emptyList
        getApproverList2EmptyListTest();
        // not empty list
        getApproverList2NotEmptyListTest();
    }

    private void getApproverList2EmptyListTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(topicManagerService.getByTopicName(Mockito.any(), Mockito.any())).thenReturn(null);
        List<Account> approverList = applyAuthorityOrder.getApproverList(EXTENSIONS);
        Assert.assertTrue(approverList.isEmpty());

        TopicDO topicDO = new TopicDO();
        topicDO.setAppId(APP_ID);
        Mockito.when(topicManagerService.getByTopicName(Mockito.any(), Mockito.any())).thenReturn(topicDO);
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(null);
        List<Account> approverList2 = applyAuthorityOrder.getApproverList(EXTENSIONS);
        Assert.assertTrue(approverList2.isEmpty());
    }

    private void getApproverList2NotEmptyListTest() {
        TopicDO topicDO = new TopicDO();
        topicDO.setAppId(APP_ID);
        Mockito.when(topicManagerService.getByTopicName(Mockito.any(), Mockito.any())).thenReturn(topicDO);
        AppDO appDO = getAppDO();
        appDO.setPrincipals(ADMIN + "," + INVALIDE_USER);
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(appDO);
        Mockito.when(accountService.getAccountFromCache(ADMIN)).thenReturn(new Account());
        Mockito.when(accountService.getAccountFromCache(INVALIDE_USER)).thenReturn(null);

        List<Account> approverList = applyAuthorityOrder.getApproverList(EXTENSIONS);
        Assert.assertFalse(approverList.isEmpty());
    }

    @Test
    public void handleOrderDetailTest() {
        // cluster not exist
        handleOrderDetail2ClusterNotExistTest();
        // operationFailed
        handleOrderDetail2OperationFailedTest();
        // success
        handleOrderDetail2SuccessTest();
    }

    private void handleOrderDetail2ClusterNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(null);

        OrderDO orderDO = getOrderDO();
        ResultStatus resultStatus = applyAuthorityOrder.handleOrderDetail(orderDO, new OrderHandleBaseDTO(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void handleOrderDetail2OperationFailedTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(authorityService.addAuthorityAndQuota(Mockito.any(), Mockito.any())).thenReturn(-1);

        OrderDO orderDO = getOrderDO();
        ResultStatus resultStatus = applyAuthorityOrder.handleOrderDetail(orderDO, new OrderHandleBaseDTO(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.OPERATION_FAILED.getCode());
    }

    private void handleOrderDetail2SuccessTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong(), Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Mockito.when(authorityService.addAuthorityAndQuota(Mockito.any(), Mockito.any())).thenReturn(1);

        OrderDO orderDO = getOrderDO();
        ResultStatus resultStatus = applyAuthorityOrder.handleOrderDetail(orderDO, new OrderHandleBaseDTO(), ADMIN);
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
        LogicalClusterDO logicalClusterDO = new LogicalClusterDO();
        logicalClusterDO.setId(REAL_CLUSTER_ID_IN_MYSQL);
        logicalClusterDO.setName("");
        Mockito.when(logicalClusterMetadataManager.getLogicalCluster(Mockito.anyLong())).thenReturn(logicalClusterDO);
        OrderDetailApplyAuthorityDTO data = (OrderDetailApplyAuthorityDTO) applyAuthorityOrder.getOrderExtensionDetailData(EXTENSIONS);
        Assert.assertNotNull(data);
        Assert.assertNull(data.getAppName());
    }

    private void getOrderExtensionDetailData2AppNotNullTest() {
        AppDO appDO = getAppDO();
        Mockito.when(appService.getByAppId(Mockito.any())).thenReturn(appDO);
        LogicalClusterDO logicalClusterDO = new LogicalClusterDO();
        logicalClusterDO.setId(REAL_CLUSTER_ID_IN_MYSQL);
        logicalClusterDO.setName("");
        Mockito.when(logicalClusterMetadataManager.getLogicalCluster(Mockito.anyLong())).thenReturn(logicalClusterDO);
        OrderDetailApplyAuthorityDTO data = (OrderDetailApplyAuthorityDTO) applyAuthorityOrder.getOrderExtensionDetailData(EXTENSIONS);
        Assert.assertNotNull(data);
        Assert.assertNotNull(data.getAppName());
        Assert.assertEquals(data.getAppName(), appDO.getName());
    }

}
