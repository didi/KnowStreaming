package com.xiaojukeji.kafka.manager.service.service.gateway;

import com.xiaojukeji.kafka.manager.common.bizenum.TopicAuthorityEnum;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xuguang
 * @Date 2021/12/6
 */
public class AuthorityServiceTest extends BaseTest {

    @Autowired
    private AuthorityService authorityService;

    @DataProvider(name = "provideAuthorityDO")
    public Object[][] provideAuthorityDO() {
        AuthorityDO authorityDO = new AuthorityDO();
        authorityDO.setId(4L);
        authorityDO.setAppId("appIdModuleTest");
        authorityDO.setClusterId(1L);
        authorityDO.setTopicName("topicModuleTest");
        authorityDO.setAccess(2);
        authorityDO.setCreateTime(new Date(1638786493173L));
        authorityDO.setModifyTime(new Date(1638786493173L));
        return new Object[][] {{authorityDO}};
    }

    public TopicQuota getTopicQuota() {
        TopicQuota topicQuotaDO = new TopicQuota();
        topicQuotaDO.setAppId("testAppId");
        topicQuotaDO.setClusterId(1L);
        topicQuotaDO.setTopicName("moduleTest");
        topicQuotaDO.setProduceQuota(100000L);
        topicQuotaDO.setConsumeQuota(100000L);
        return topicQuotaDO;
    }

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(dataProvider = "provideAuthorityDO")
    public void addAuthorityTest(AuthorityDO authorityDO) {
        // 测试新增权限对象
        addNewAuthority(authorityDO);
        // 测试新旧对象权限一致
        newAccessEqualOldAccessTest(authorityDO);
        // 测试在原有对象上新增权限
        addNewAuthorityAccessTest(authorityDO);
        // 测试新增权限失败
        addNewAuthority2Failure();
    }

    private void addNewAuthority(AuthorityDO authorityDO) {
        int result = authorityService.addAuthority(authorityDO);
        Assert.assertEquals(result, 1);
    }

    private void newAccessEqualOldAccessTest(AuthorityDO authorityDO) {
        int result = authorityService.addAuthority(authorityDO);
        Assert.assertEquals(result, 0);
    }

    private void addNewAuthorityAccessTest(AuthorityDO authorityDO) {
        authorityDO.setAccess(3);
        int result = authorityService.addAuthority(authorityDO);
        Assert.assertEquals(result, 1);
    }

    private void addNewAuthority2Failure() {
        int result = authorityService.addAuthority(new AuthorityDO());
        Assert.assertEquals(result, 0);
    }

    @Test(dataProvider = "provideAuthorityDO", description = "测试删除权限对象")
    public void deleteSpecifiedAccess(AuthorityDO authorityDO) {
        // 测试删除权限对象时无该对象
        deleteSpecifiedAccess2AuthorityNotExist();
        // 测试删除权限对象时参数错误
        deleteSpecifiedAccess2ParamIllegal(authorityDO);
        // 测试删除权限对象成功
        deleteSpecifiedAccess2Success(authorityDO);
    }

    private void deleteSpecifiedAccess2AuthorityNotExist() {
        ResultStatus result = authorityService.deleteSpecifiedAccess("xxx", 1L, "moduleTest", 2, "admin");
        Assert.assertEquals(ResultStatus.AUTHORITY_NOT_EXIST.getCode(), result.getCode());
    }

    private void deleteSpecifiedAccess2ParamIllegal(AuthorityDO authorityDO) {
        authorityService.addAuthority(authorityDO);

        ResultStatus result = authorityService.deleteSpecifiedAccess(
                authorityDO.getAppId(),
                authorityDO.getClusterId(),
                authorityDO.getTopicName(),
                3, "admin"
        );
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void deleteSpecifiedAccess2Success(AuthorityDO authorityDO) {
        authorityDO.setAccess(3);
        authorityDO.setAppId("sss");
        authorityService.addAuthority(authorityDO);

        ResultStatus result = authorityService.deleteSpecifiedAccess(
                authorityDO.getAppId(),
                authorityDO.getClusterId(),
                authorityDO.getTopicName(),
                3, "admin"
        );
        Assert.assertEquals(ResultStatus.SUCCESS.getCode(), result.getCode());
    }

    @Test(dataProvider = "provideAuthorityDO", description = "测试查询")
    public void getAuthorityTest(AuthorityDO authorityDO) {
        // 测试查询成功
        getAuthority2SuccessTest(authorityDO);
        // 测试查询为null
        getAuthority2NullTest();
    }

    private void getAuthority2SuccessTest(AuthorityDO authorityDO) {
        authorityService.addAuthority(authorityDO);

        AuthorityDO result = authorityService.getAuthority(authorityDO.getClusterId(), authorityDO.getTopicName(), authorityDO.getAppId());
        Assert.assertEquals(result.getClusterId(), authorityDO.getClusterId());
        Assert.assertEquals(result.getAppId(), authorityDO.getAppId());
        Assert.assertEquals(result.getTopicName(), authorityDO.getTopicName());
        Assert.assertEquals(result.getAccess(), authorityDO.getAccess());
    }

    private void getAuthority2NullTest() {
        AuthorityDO result = authorityService.getAuthority(10L, "moduleTest", "testAppId");
        Assert.assertNull(result);
    }

    @Test(dataProvider = "provideAuthorityDO", description = "测试查询")
    public void getAuthorityByTopicTest(AuthorityDO authorityDO) {
        // 测试查询成功
        getAuthorityByTopic2SuccessTest(authorityDO);
        // 测试查询为null
        getAuthorityByTopic2NullTest();
    }

    private void getAuthorityByTopic2SuccessTest(AuthorityDO authorityDO) {
        authorityService.addAuthority(authorityDO);

        List<AuthorityDO> result = authorityService.getAuthorityByTopic(authorityDO.getClusterId(), authorityDO.getTopicName());
        Assert.assertNotNull(result);
        Assert.assertTrue(result.stream()
                .allMatch(authorityDO1 -> authorityDO1.getTopicName().equals(authorityDO.getTopicName()) &&
                        authorityDO1.getClusterId().equals(authorityDO.getClusterId())));
    }

    private void getAuthorityByTopic2NullTest() {
        List<AuthorityDO> result = authorityService.getAuthorityByTopic(100L, "moduleTestxxx");
        Assert.assertTrue(result.isEmpty());
    }

    @Test(dataProvider = "provideAuthorityDO", description = "测试查询")
    public void getAuthorityByAppIdTest(AuthorityDO authorityDO) {
        // 测试查询成功
        getAuthorityByAppId2SuccessTest(authorityDO);
        // 测试查询为null
        getAuthorityByAppId2NullTest();
    }

    private void getAuthorityByAppId2SuccessTest(AuthorityDO authorityDO) {
        authorityService.addAuthority(authorityDO);

        List<AuthorityDO> result = authorityService.getAuthority(authorityDO.getAppId());
        Assert.assertNotNull(result);
        Assert.assertTrue(result.stream().
                allMatch(authorityDO1 -> authorityDO1.getAppId().equals(authorityDO.getAppId()) &&
                        !authorityDO1.getAccess().equals(TopicAuthorityEnum.DENY.getCode())));
    }

    private void getAuthorityByAppId2NullTest() {
        List<AuthorityDO> result = authorityService.getAuthority("xxx");
        Assert.assertTrue(result.isEmpty());
    }

    @Test(dataProvider = "provideAuthorityDO", description = "添加权限和quota")
    public void addAuthorityAndQuotaTest(AuthorityDO authorityDO) {
        // 添加权限和quota成功
        addAuthorityAndQuota2SuccessTest(authorityDO);
        // 添加权限和quota失败
        addAuthorityAndQuota2FaliureTest(authorityDO);
    }

    private void addAuthorityAndQuota2SuccessTest(AuthorityDO authorityDO) {
        int result = authorityService.addAuthorityAndQuota(authorityDO, getTopicQuota());
        Assert.assertEquals(result, 1);
    }

    private void addAuthorityAndQuota2FaliureTest(AuthorityDO authorityDO) {
        authorityService.addAuthority(authorityDO);
        // 重复插入
        int result2 = authorityService.addAuthorityAndQuota(authorityDO, getTopicQuota());
        Assert.assertEquals(result2, 0);
    }

    @Test(dataProvider = "provideAuthorityDO", description = "测试删除")
    public void deleteAuthorityByTopicTest(AuthorityDO authorityDO) {
        // 测试删除成功
        deleteAuthorityByTopic2SuccessTest(authorityDO);
        // 测试删除失败
        deleteAuthorityByTopic2FailureTest();
    }

    private void deleteAuthorityByTopic2SuccessTest(AuthorityDO authorityDO) {
        authorityService.addAuthority(authorityDO);
        int result = authorityService.deleteAuthorityByTopic(authorityDO.getClusterId(), authorityDO.getTopicName());
        Assert.assertEquals(result, 1);
    }

    private void deleteAuthorityByTopic2FailureTest() {
        int result = authorityService.deleteAuthorityByTopic(100L, "moduleTest");
        Assert.assertEquals(result, 0);
    }
}
