package com.xiaojukeji.kafka.manager.account;

import com.xiaojukeji.kafka.manager.account.common.EnterpriseStaff;
import com.xiaojukeji.kafka.manager.account.component.AbstractEnterpriseStaffService;
import com.xiaojukeji.kafka.manager.account.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.entity.pojo.AccountDO;
import com.xiaojukeji.kafka.manager.dao.AccountDao;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wyc
 * @date 2021/12/30
 */
public class AbstractEnterpriseStaffServiceTest extends BaseTest {

    @Autowired
    @InjectMocks
    private AbstractEnterpriseStaffService abstractEnterpriseStaffService;

    @Mock
    private AccountDao accountDao;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }


    private EnterpriseStaff getEnterpriseStaff() {
        EnterpriseStaff staff = new EnterpriseStaff("username", "username", "department");
        return staff;
    }

    private AccountDO getAccountDO() {
        AccountDO accountDO = new AccountDO();
        accountDO.setUsername("username");
        return accountDO;
    }

    @Test
    public void getEnterpriseStaffByNameTest() {
        // 返回null测试
        getEnterpriseStaffByName2NullTest();

        // 成功测试
        getEnterpriseStaffByName2SuccessTest();
    }

    private void getEnterpriseStaffByName2NullTest() {
        Mockito.when(accountDao.getByName(Mockito.anyString())).thenReturn(null);
        Assert.assertNull(abstractEnterpriseStaffService.getEnterpriseStaffByName("username"));
    }

    private void getEnterpriseStaffByName2SuccessTest() {
        AccountDO accountDO = getAccountDO();
        EnterpriseStaff staff = getEnterpriseStaff();
        Mockito.when(accountDao.getByName(Mockito.anyString())).thenReturn(accountDO);
        EnterpriseStaff result = abstractEnterpriseStaffService.getEnterpriseStaffByName("username");
        Assert.assertTrue(result.getUsername().equals(staff.getUsername()) && result.getChineseName().equals(staff.getChineseName()));
    }


    @Test
    public void searchEnterpriseStaffByKeyWordTest() {
        // 返回空集合测试
        searchEnterpriseStaffByKeyWord2EmptyTest();

        // 返回非空集合测试
        searchEnterpriseStaffByKeyWord2AllTest();
    }

    private void searchEnterpriseStaffByKeyWord2EmptyTest() {
        Mockito.when(accountDao.searchByNamePrefix(Mockito.anyString())).thenReturn(new ArrayList<>());
        Assert.assertTrue(abstractEnterpriseStaffService.searchEnterpriseStaffByKeyWord("username").isEmpty());
    }

    private void searchEnterpriseStaffByKeyWord2AllTest() {
        AccountDO accountDO = getAccountDO();
        Mockito.when(accountDao.searchByNamePrefix(Mockito.anyString())).thenReturn(Arrays.asList(accountDO));

        EnterpriseStaff staff = getEnterpriseStaff();
        List<EnterpriseStaff> result = abstractEnterpriseStaffService.searchEnterpriseStaffByKeyWord("username");
        Assert.assertTrue(!result.isEmpty() && result.stream().allMatch(enterpriseStaff -> enterpriseStaff.getChineseName().equals(staff.getChineseName()) &&
                enterpriseStaff.getUsername().equals(staff.getUsername())));
    }


}
