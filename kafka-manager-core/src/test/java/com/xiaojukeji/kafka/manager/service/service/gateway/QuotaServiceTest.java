package com.xiaojukeji.kafka.manager.service.service.gateway;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author xuguang
 * @Date 2021/12/6
 */
public class QuotaServiceTest extends BaseTest {
    
    @Autowired
    @InjectMocks
    private QuotaService quotaService;

    @Mock
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Mock
    private AuthorityService authorityService;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider(name = "provideTopicQuota")
    public static Object[][] provideTopicQuota() {
        TopicQuota topicQuotaDO = new TopicQuota();
        topicQuotaDO.setAppId("testAppId");
        topicQuotaDO.setClusterId(1L);
        topicQuotaDO.setTopicName("moduleTest");
        topicQuotaDO.setProduceQuota(100000L);
        topicQuotaDO.setConsumeQuota(100000L);
        return new Object[][] {{topicQuotaDO}};
    }

    private AuthorityDO getAuthority() {
        AuthorityDO authorityDO = new AuthorityDO();
        authorityDO.setAccess(0);

        return authorityDO;
    }

    @Test(dataProvider = "provideTopicQuota")
    public void addTopicQuotaTest(TopicQuota topicQuotaDO) {
        // 测试新增成功
        addTopicQuota2SuccessTest(topicQuotaDO);
        // 测试新增失败
        addTopicQuota2FailureTest(topicQuotaDO);
    }

    private void addTopicQuota2SuccessTest(TopicQuota topicQuotaDO) {
        int result = quotaService.addTopicQuota(topicQuotaDO);
        Assert.assertEquals(result, 1);
    }

    private void addTopicQuota2FailureTest(TopicQuota topicQuotaDO) {
        topicQuotaDO.setClusterId(10L);
        int result = quotaService.addTopicQuota(topicQuotaDO);
        Assert.assertEquals(result, 0);
    }

    @Test(dataProvider = "provideTopicQuota")
    public void addTopicQuotaWithAccessTest(TopicQuota topicQuotaDO) {
        // 测试新增成功
        addTopicQuotaWithAccess2SuccessTest(topicQuotaDO);
        // 测试新增失败
        addTopicQuotaWithAccess2FailureTest(topicQuotaDO);
    }

    private void addTopicQuotaWithAccess2SuccessTest(TopicQuota topicQuotaDO) {
        int result = quotaService.addTopicQuota(topicQuotaDO, 2);
        Assert.assertEquals(result, 1);
    }

    private void addTopicQuotaWithAccess2FailureTest(TopicQuota topicQuotaDO) {
        topicQuotaDO.setClusterId(10L);
        int result = quotaService.addTopicQuota(topicQuotaDO, 2);
        Assert.assertEquals(result, 0);
    }

    @Test(dataProvider = "provideTopicQuota")
    public void getQuotaFromZkTest(TopicQuota topicQuotaDO) {
        // 测试查询成功
        getQuotaFromZk2SuccessTest(topicQuotaDO);
        // 测试查询失败
        getQuotaFromZk2FailureTest();
    }

    private void getQuotaFromZk2SuccessTest(TopicQuota topicQuotaDO) {
        TopicQuota result = quotaService.getQuotaFromZk(1L, "moduleTest", "testAppId");
        Assert.assertNotNull(result);
        Assert.assertEquals(result.toString(), topicQuotaDO.toString());
    }

    private void getQuotaFromZk2FailureTest() {
        TopicQuota result = quotaService.getQuotaFromZk(10L, "moduleTest", "testAppId");
        Assert.assertNull(result);
    }

    @Test
    public void modifyProduceQuotaTest() {
        // 测试修改成功
        modifyProduceQuota2SuccessTest();
        // 测试修改失败
        modifyProduceQuota2FailureTest();
    }

    private void modifyProduceQuota2SuccessTest() {
        Boolean result = quotaService.modifyProduceQuota(1L, "moduleTest", "testAppId", 100L);
        Assert.assertTrue(result);
    }

    private void modifyProduceQuota2FailureTest() {
        Boolean result1 = quotaService.modifyProduceQuota(10L, "moduleTest", "testAppId", 100L);
        Assert.assertFalse(result1);
    }

    @Test(dataProvider = "provideTopicQuota")
    public void addTopicQuotaByAuthorityTest(TopicQuota topicQuotaDO) {
        // 测试新增时，无相应集群异常
        addTopicQuotaByAuthority2ClusterNotExistTest(topicQuotaDO);
        // 测试新增时，无权限异常
        addTopicQuotaByAuthority2UserWithoutAuthority1Test(topicQuotaDO);
        // 测试新增成功，包含三个流程，access为1，2，3时，通过数据库修改
        addTopicQuotaByAuthority2SuccessTest(topicQuotaDO);
        // 测试新增时，无法写入zk异常(关闭zk)，包含三个流程，access为1，2，3时，通过数据库修改
//        addTopicQuotaByAuthority2ZookeeperWriteFailedTest(topicQuotaDO);
    }

    private void addTopicQuotaByAuthority2SuccessTest(TopicQuota topicQuotaDO) {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.any())).thenReturn(1L);
        AuthorityDO authority = getAuthority();
        authority.setAccess(2);
        Mockito.when(authorityService.getAuthority(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(authority);
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.any())).thenReturn(1L);
        ResultStatus resultStatus = quotaService.addTopicQuotaByAuthority(topicQuotaDO);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void addTopicQuotaByAuthority2ClusterNotExistTest(TopicQuota topicQuotaDO) {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.any())).thenReturn(null);
        ResultStatus resultStatus = quotaService.addTopicQuotaByAuthority(topicQuotaDO);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void addTopicQuotaByAuthority2UserWithoutAuthority1Test(TopicQuota topicQuotaDO) {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.any())).thenReturn(1L);
        Mockito.when(authorityService.getAuthority(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(null);
        topicQuotaDO.setTopicName("xxx");
        ResultStatus resultStatus1 = quotaService.addTopicQuotaByAuthority(topicQuotaDO);
        Assert.assertEquals(resultStatus1.getCode(), ResultStatus.USER_WITHOUT_AUTHORITY.getCode());
    }

    private void addTopicQuotaByAuthority2ZookeeperWriteFailedTest(TopicQuota topicQuotaDO) {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.any())).thenReturn(1L);
        ResultStatus resultStatus = quotaService.addTopicQuotaByAuthority(topicQuotaDO);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.ZOOKEEPER_WRITE_FAILED.getCode());
    }
}
