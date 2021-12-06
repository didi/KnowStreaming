package com.xiaojukeji.kafka.manager.service.service.gateway;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.AppTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.AppDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
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

    private AppDO getAppDO() {
        AppDO appDO = new AppDO();
        appDO.setId(100L);
        appDO.setAppId("testAppId");
        appDO.setName("testApp");
        appDO.setPassword("testApp");
        appDO.setType(1);
        appDO.setApplicant("admin");
        appDO.setPrincipals("admin");
        appDO.setDescription("testApp");
        appDO.setCreateTime(new Date());
        appDO.setModifyTime(new Date());
        return appDO;
    }
    
    private AppDTO getAppDTO() {
        AppDTO appDTO = new AppDTO();
        appDTO.setAppId("testAppId");
        appDTO.setName("testApp");
        appDTO.setPrincipals("admin");
        appDTO.setDescription("testApp");
        return appDTO;
    }

    @Test
    public void addAppTest() {
        addApp2SuccessTest();
        addApp2DuplicateKeyTest();
        addApp2MysqlErrorTest();
    }

    @Rollback(false)
    private void addApp2SuccessTest() {
        AppDO appDO = getAppDO();
        ResultStatus addAppResult = appService.addApp(appDO, "admin");
        Assert.assertEquals(addAppResult.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void addApp2DuplicateKeyTest() {
        AppDO appDO = getAppDO();
        ResultStatus addAppResult = appService.addApp(appDO, "admin");
        Assert.assertEquals(addAppResult.getCode(), ResultStatus.RESOURCE_ALREADY_EXISTED.getCode());
    }

    @Rollback()
    private void addApp2MysqlErrorTest() {
        ResultStatus addAppResult = appService.addApp(new AppDO(), "admin");
        Assert.assertEquals(addAppResult.getCode(), ResultStatus.MYSQL_ERROR.getCode());
    }

    @Test
    public void deleteAppTest() {
        deleteApp2SuccessTest();
        deleteApp2FailureTest();
    }

    @Rollback()
    private void deleteApp2SuccessTest() {
        AppDO appDO = getAppDO();
        int result = appService.deleteApp(appDO, "admin");
        Assert.assertEquals(result, 1);
    }

    @Rollback()
    private void deleteApp2FailureTest() {
        int result = appService.deleteApp(new AppDO(), "admin");
        Assert.assertEquals(result, 0);
    }

    @Test
    public void updateByAppIdTest() {
        updateByAppId2AppNotExistTest();
        updateByAppId2UserWithoutAuthorityTest();
        updateByAppId2SucessTest();
    }

    private void updateByAppId2AppNotExistTest() {
        ResultStatus result = appService.updateByAppId(new AppDTO(), "admin", true);
        Assert.assertEquals(result.getCode(), ResultStatus.APP_NOT_EXIST.getCode());
    }
    
    private void updateByAppId2UserWithoutAuthorityTest() {
        ResultStatus result = appService.updateByAppId(getAppDTO(), "xxx", false);
        Assert.assertEquals(result.getCode(), ResultStatus.USER_WITHOUT_AUTHORITY.getCode());
    }

    @Rollback()
    private void updateByAppId2SucessTest() {
        ResultStatus result1 = appService.updateByAppId(getAppDTO(), "admin", false);
        Assert.assertEquals(result1.getCode(), ResultStatus.SUCCESS.getCode());

        ResultStatus result2 = appService.updateByAppId(getAppDTO(), "admin", true);
        Assert.assertEquals(result2.getCode(), ResultStatus.SUCCESS.getCode());

        ResultStatus result3 = appService.updateByAppId(getAppDTO(), "xxx", true);
        Assert.assertEquals(result3.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void getAppByUserAndIdTest() {
        getAppByUserAndId2NullTest();
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
        boolean result = appService.verifyAppIdByPassword("dkm_admin", "km_kMl4N8as1Kp0CCY");
        Assert.assertTrue(result);
    }

    @Test(dataProvider = "provideAppIdAndPassword")
    private void verifyAppIdByPassword2False(String appId, String password) {
        boolean result = appService.verifyAppIdByPassword(appId, password);
        Assert.assertFalse(result);
    }

    @DataProvider(name = "provideAppIdAndPassword")
    private Object[][] provideAppIdAndPassword() {
        return new Object[][] {{"", ""}, {"dkm_admin", ""}, {"xxx", "km_kMl4N8as1Kp0CCY"}, {"dkm_admin", "xxx"}};
    }

    @Test
    public void getAppTopicDTOList2Test() {
        getAppTopicDTOList2EmptyList();
    }

    private void getAppTopicDTOList2EmptyList() {
        List<AppTopicDTO> result = appService.getAppTopicDTOList("xxx", true);
        Assert.assertTrue(result.isEmpty());
    }
}
