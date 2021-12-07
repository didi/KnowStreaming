package com.xiaojukeji.kafka.manager.service.service.gateway;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.testng.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
        authorityDO.setAppId("testAppId");
        authorityDO.setClusterId(1L);
        authorityDO.setTopicName("moduleTest");
        authorityDO.setAccess(2);
        authorityDO.setCreateTime(new Date(1638786493173L));
        authorityDO.setModifyTime(new Date(1638786493173L));
        return new Object[][] {{authorityDO}};
    }

    @Test(dataProvider = "provideAuthorityDO")
    @Rollback(value = false)
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
        Assert.assertEquals( 1, result);
    }

    private void newAccessEqualOldAccessTest(AuthorityDO authorityDO) {
        int result = authorityService.addAuthority(authorityDO);
        Assert.assertEquals( 0, result);
    }

    private void addNewAuthorityAccessTest(AuthorityDO authorityDO) {
        authorityDO.setAccess(3);
        int result = authorityService.addAuthority(authorityDO);
        Assert.assertEquals( 1, result);
    }

    private void addNewAuthority2Failure() {
        int result = authorityService.addAuthority(new AuthorityDO());
        Assert.assertEquals( 0, result);
    }

    public void deleteSpecifiedAccess() {
        // 测试删除权限对象时无该对象
        deleteSpecifiedAccess2AuthorityNotExist();

        // 测试删除权限对象时参数错误, 传入的access为3，数据库中为2
        deleteSpecifiedAccess2ParamIllegal();

        // 测试删除权限对象成功，传入的access为3，数据库中为3
        deleteSpecifiedAccess2Success();

    }

    private void deleteSpecifiedAccess2AuthorityNotExist() {
        ResultStatus result = authorityService.deleteSpecifiedAccess("xxx", 1L, "moduleTest", 2, "admin");
        Assert.assertEquals(ResultStatus.AUTHORITY_NOT_EXIST.getCode(), result.getCode());
    }

    private void deleteSpecifiedAccess2ParamIllegal() {
        ResultStatus result = authorityService.deleteSpecifiedAccess("dkm_admin", 1L, "xgTest", 3, "admin");
        Assert.assertEquals(ResultStatus.PARAM_ILLEGAL.getCode(), result.getCode());
    }

    private void deleteSpecifiedAccess2Success() {
        ResultStatus result = authorityService.deleteSpecifiedAccess("dkm_admin", 1L, "xgTest", 3, "admin");
        Assert.assertEquals(ResultStatus.SUCCESS.getCode(), result.getCode());
    }

    @Test(dataProvider = "provideAuthorityDO")
    public void getAuthorityTest(AuthorityDO authorityDO) {
        // 测试查询成功
        getAuthority2SuccessTest(authorityDO);
        // 测试查询为null
        getAuthority2NullTest(authorityDO);

    }

    private void getAuthority2SuccessTest(AuthorityDO authorityDO) {
        AuthorityDO result = authorityService.getAuthority(1L, "moduleTest", "testAppId");
        Assert.assertEquals(result.toString(), authorityDO.toString());
    }

    private void getAuthority2NullTest(AuthorityDO authorityDO) {
        AuthorityDO result = authorityService.getAuthority(10L, "moduleTest", "testAppId");
        Assert.assertNull(result);
    }

    @Test(dataProvider = "provideAuthorityDO")
    public void getAuthorityByTopic(AuthorityDO authorityDO) {
        // 测试查询成功
        getAuthorityByTopic2SuccessTest(authorityDO);
        // 测试查询为null
        getAuthorityByTopic2NullTest();
    }

    private void getAuthorityByTopic2SuccessTest(AuthorityDO authorityDO) {
        List<AuthorityDO> result = authorityService.getAuthorityByTopic(1L, "moduleTest");
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get(0).toString(), authorityDO.toString());
    }

    private void getAuthorityByTopic2NullTest() {
        List<AuthorityDO> result = authorityService.getAuthorityByTopic(10L, "moduleTest");
        Assert.assertTrue(result.isEmpty());
    }

    @Test(dataProvider = "provideAuthorityDO")
    public void getAuthorityByAppIdTest(AuthorityDO authorityDO) {
        // 测试查询成功
        getAuthorityByAppId2SuccessTest(authorityDO);

        // 测试查询为null
        getAuthorityByAppId2NullTest();
    }

    private void getAuthorityByAppId2SuccessTest(AuthorityDO authorityDO) {
        List<AuthorityDO> result = authorityService.getAuthority("testAppId");
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get(0).toString(), authorityDO.toString());
    }

    private void getAuthorityByAppId2NullTest() {
        List<AuthorityDO> result = authorityService.getAuthority("xxx");
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void listAllTest() {
        List<AuthorityDO> result = authorityService.listAll();
        Assert.assertEquals(result.size(), 2);
    }

    @Test
    public void addAuthorityAndQuotaTest(AuthorityDO authorityDO) {

    }

    private void addAuthorityAndQuota2SuccessTest(AuthorityDO authorityDO) {

    }

    @Test
    public void getAllAuthorityTest() {
        Map<String, Map<Long, Map<String, AuthorityDO>>> allAuthority = authorityService.getAllAuthority();
        Assert.assertEquals(allAuthority.size(), 2);
    }

    @Test
    public void deleteAuthorityByTopicTest() {
        // 测试查询成功
        deleteAuthorityByTopic2SuccessTest();
        // 测试查询为null
        deleteAuthorityByTopic2FailureTest();
    }

    private void deleteAuthorityByTopic2SuccessTest() {
        int result = authorityService.deleteAuthorityByTopic(1L, "moduleTest");
        Assert.assertEquals(result, 1);
    }

    private void deleteAuthorityByTopic2FailureTest() {
        int result = authorityService.deleteAuthorityByTopic(10L, "moduleTest");
        Assert.assertEquals(result, 0);
    }
}
