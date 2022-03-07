package com.xiaojukeji.kafka.manager.service.service.gateway;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.AppTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.AppDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

/**
 * @author xuguang
 * @Date 2021/12/6
 */
public class AppServiceTest extends BaseTest {

    @Autowired
    private AppService appService;

    @DataProvider(name = "provideAppDO")
    public static Object[][] provideAppDO() {
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
        return new Object[][] {{appDO}};
    }

    @DataProvider(name = "provideAppDTO")
    public Object[][] provideAppDTO() {
        AppDTO appDTO = new AppDTO();
        appDTO.setAppId("testAppId");
        appDTO.setName("testApp");
        appDTO.setPrincipals("admin");
        appDTO.setDescription("testApp");
        return new Object[][] {{appDTO}};
    }

    private AppDO getAppDO() {
        AppDO appDO = new AppDO();
        appDO.setId(4L);
        appDO.setAppId("testAppId");
        appDO.setName("testApp");
        appDO.setPassword("password");
        appDO.setType(1);
        appDO.setApplicant("admin");
        appDO.setPrincipals("admin");
        return appDO;
    }

    @Test(dataProvider = "provideAppDO")
    public void addAppTest(AppDO appDO) {
        // 测试app添加成功
        addApp2SuccessTest(appDO);
        // 测试app添加失败，键重复
        addApp2DuplicateKeyTest(appDO);
        // 测试app添加失败
        addApp2MysqlErrorTest();
    }

    private void addApp2SuccessTest(AppDO appDO) {
        ResultStatus addAppResult = appService.addApp(appDO, "admin");
        Assert.assertEquals(addAppResult.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void addApp2DuplicateKeyTest(AppDO appDO) {
        ResultStatus addAppResult = appService.addApp(appDO, "admin");
        Assert.assertEquals(addAppResult.getCode(), ResultStatus.RESOURCE_ALREADY_EXISTED.getCode());
    }

    private void addApp2MysqlErrorTest() {
        ResultStatus addAppResult = appService.addApp(new AppDO(), "admin");
        Assert.assertEquals(addAppResult.getCode(), ResultStatus.MYSQL_ERROR.getCode());
    }

    @Test(dataProvider = "provideAppDO")
    public void deleteAppTest(AppDO appDO) {
        appService.addApp(appDO, "admin");

        // 测试删除app成功
        deleteApp2SuccessTest(appDO);
        // 测试删除app失败
        deleteApp2FailureTest(appDO);
    }

    private void deleteApp2SuccessTest(AppDO appDO) {
        appService.addApp(appDO, "admin");

        int result = appService.deleteApp(appDO, "admin");
        Assert.assertEquals(result, 1);
    }

    private void deleteApp2FailureTest(AppDO appDO) {
        appService.addApp(appDO, "admin");

        int result = appService.deleteApp(new AppDO(), "admin");
        Assert.assertEquals(result, 0);
    }

    @Test(dataProvider = "provideAppDTO")
    public void updateByAppIdTest(AppDTO appDTO) {
        // 测试更新app时，app不存在
        updateByAppId2AppNotExistTest();
        // 测试更新app时，用户无权限
        AppDO appDO = getAppDO();
        appService.addApp(appDO, "admin");
        updateByAppId2UserWithoutAuthorityTest(appDTO);
        // 测试更新app成功
        updateByAppId2SuccessTest(appDTO);
    }

    private void updateByAppId2AppNotExistTest() {
        ResultStatus result = appService.updateByAppId(new AppDTO(), "admin", true);
        Assert.assertEquals(result.getCode(), ResultStatus.APP_NOT_EXIST.getCode());
    }
    
    private void updateByAppId2UserWithoutAuthorityTest(AppDTO appDTO) {
        ResultStatus result = appService.updateByAppId(appDTO, "xxx", false);
        Assert.assertEquals(result.getCode(), ResultStatus.USER_WITHOUT_AUTHORITY.getCode());
    }

    private void updateByAppId2SuccessTest(AppDTO appDTO) {
        ResultStatus result1 = appService.updateByAppId(appDTO, "admin", false);
        Assert.assertEquals(result1.getCode(), ResultStatus.SUCCESS.getCode());

        ResultStatus result2 = appService.updateByAppId(appDTO, "admin", true);
        Assert.assertEquals(result2.getCode(), ResultStatus.SUCCESS.getCode());

        ResultStatus result3 = appService.updateByAppId(appDTO, "xxx", true);
        Assert.assertEquals(result3.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void getAppByUserAndIdTest() {
        // 测试查询app为空
        getAppByUserAndId2NullTest();
        // 测试查询app成功
        getAppByUserAndId2SuccessTest();
    }

    private void getAppByUserAndId2NullTest() {
        AppDO result1 = appService.getAppByUserAndId("xxx", "admin");
        Assert.assertNull(result1);

        AppDO result2 = appService.getAppByUserAndId("dkm_admin", "xxx");
        Assert.assertNull(result2);
    }

    private void getAppByUserAndId2SuccessTest() {
        AppDO result1 = appService.getAppByUserAndId("dkm_admin", "admin");
        Assert.assertNotNull(result1);
    }

    @Test
    public void verifyAppIdByPassword2DSucessTest() {
        // 测试验证app成功
        boolean result = appService.verifyAppIdByPassword("dkm_admin", "km_kMl4N8as1Kp0CCY");
        Assert.assertTrue(result);
    }

    @Test(dataProvider = "provideAppIdAndPassword")
    public void verifyAppIdByPassword2False(String appId, String password) {
        // 测试验证app失败情况
        boolean result = appService.verifyAppIdByPassword(appId, password);
        Assert.assertFalse(result);
    }

    @DataProvider(name = "provideAppIdAndPassword")
    public Object[][] provideAppIdAndPassword() {
        return new Object[][] {{"", ""}, {"dkm_admin", ""}, {"xxx", "km_kMl4N8as1Kp0CCY"}, {"dkm_admin", "xxx"}};
    }

    @Test
    public void getAppTopicDTOList2Test() {
        // 测试获取的集合为空
        getAppTopicDTOList2EmptyList();

        // 测试查询成功，且集合不为空
        getAppTopicDTOList2Success();

        // TODO 查询的其他分支
    }

    private void getAppTopicDTOList2EmptyList() {
        List<AppTopicDTO> result1 = appService.getAppTopicDTOList("xxx", true);
        Assert.assertTrue(result1.isEmpty());

        List<AppTopicDTO> result2 = appService.getAppTopicDTOList("dkm_admin", false);
        Assert.assertTrue(result2.isEmpty());

        List<AppTopicDTO> result3 = appService.getAppTopicDTOList("testAppId", true);
        Assert.assertTrue(result3.isEmpty());

        List<AppTopicDTO> result4 = appService.getAppTopicDTOList("testAppId", false);
        Assert.assertTrue(result4.isEmpty());
    }

    private void getAppTopicDTOList2Success() {
        List<AppTopicDTO> result = appService.getAppTopicDTOList("dkm_admin", true);
        Assert.assertFalse(result.isEmpty());
    }
}
