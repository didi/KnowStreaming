package com.xiaojukeji.kafka.manager.account;

import com.xiaojukeji.kafka.manager.account.common.EnterpriseStaff;
import com.xiaojukeji.kafka.manager.account.component.AbstractEnterpriseStaffService;
import com.xiaojukeji.kafka.manager.account.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.bizenum.AccountRoleEnum;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.common.entity.pojo.AccountDO;
import com.xiaojukeji.kafka.manager.dao.AccountDao;
import com.xiaojukeji.kafka.manager.service.service.ConfigService;
import com.xiaojukeji.kafka.manager.service.service.OperateRecordService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wyc
 * @Date 2021/12/29
 */
public class AccountServiceTest extends BaseTest {
    /*
    此测试不能一起运行，因为一些test中会执行一次flush()，执行完毕后，缓存就不为null
    后面的测试中本来应该再次刷新缓存，但由于缓存不为null，就不会再执行flush
     */
    @Autowired
    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountDao accountDao;

    @Mock
    private OperateRecordService operateRecordService;

    @Mock
    private AbstractEnterpriseStaffService enterpriseStaffService;

    @Mock
    private ConfigService configService;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    private AccountDO getAccountDO() {
        AccountDO accountDO = new AccountDO();
        accountDO.setUsername("test_username");
        accountDO.setPassword("test_password");
        accountDO.setRole(0);
        return accountDO;
    }

    private EnterpriseStaff getEnterpriseStaff() {
        EnterpriseStaff staff = new EnterpriseStaff("username", "ChineseName", "department");
        return staff;
    }

    private Account getAccount() {
        Account account = new Account();
        return account;
    }

    @Test
    public void createAccountTest() {
        AccountDO accountDO = getAccountDO();
        // 创建成功测试
        createAccount2SuccessTest(accountDO);

        // 主键重复测试
        createAccount2DuplicateKeyExceptionTest(accountDO);
    }

    private void createAccount2SuccessTest(AccountDO accountDO) {
        Mockito.when(accountDao.addNewAccount(Mockito.any())).thenReturn(1);
        Assert.assertEquals(accountService.createAccount(accountDO), ResultStatus.SUCCESS);
    }

    private void createAccount2DuplicateKeyExceptionTest(AccountDO accountDO) {
        Mockito.when(accountDao.addNewAccount(Mockito.any())).thenThrow(DuplicateKeyException.class);
        Assert.assertEquals(accountService.createAccount(accountDO), ResultStatus.RESOURCE_ALREADY_EXISTED);
    }

    private void createAccount2MySQLErrorTest(AccountDO accountDO) {
        Mockito.when(accountDao.addNewAccount(Mockito.any())).thenReturn(-1);
        Assert.assertEquals(accountService.createAccount(accountDO), ResultStatus.MYSQL_ERROR);
    }


    @Test
    public void deleteByNameTest() {
        // 删除成功测试
        deleteName2SuccessTest();

        // MySQL_ERROR错误测试
        deleteName2MySQLErrorTest();
    }

    private void deleteName2SuccessTest() {
        Mockito.when(accountDao.deleteByName(Mockito.anyString())).thenReturn(1);
        Assert.assertEquals(accountService.deleteByName("username", "admin"), ResultStatus.SUCCESS);
    }

    private void deleteName2MySQLErrorTest() {
        Mockito.when(accountDao.deleteByName(Mockito.anyString())).thenReturn(-1);
        Assert.assertEquals(accountService.deleteByName("username", "admin"), ResultStatus.MYSQL_ERROR);
    }

    @Test
    public void updateAccountTest() {
        // 账号不存在测试
        updateAccount2AccountNotExistTest();

        // 更新成功测试
        updateAccount2SuccessTest();

        // MySQL_ERROR测试
        updateAccount2MySQLErrorTest();
    }

    private void updateAccount2AccountNotExistTest() {
        AccountDO accountDO = getAccountDO();
        Mockito.when(accountDao.getByName(Mockito.anyString())).thenReturn(null);
        Assert.assertEquals(accountService.updateAccount(accountDO), ResultStatus.ACCOUNT_NOT_EXIST);
    }

    private void updateAccount2SuccessTest() {
        AccountDO accountDO = getAccountDO();
        Mockito.when(accountDao.getByName(Mockito.anyString())).thenReturn(accountDO);
        Mockito.when(accountDao.updateByName(Mockito.any())).thenReturn(1);
        Assert.assertEquals(accountService.updateAccount(accountDO), ResultStatus.SUCCESS);
    }

    private void updateAccount2MySQLErrorTest() {
        AccountDO accountDO = getAccountDO();
        Mockito.when(accountDao.getByName(Mockito.anyString())).thenReturn(accountDO);
        Mockito.when(accountDao.updateByName(Mockito.any())).thenReturn(-1);
        Assert.assertEquals(accountService.updateAccount(accountDO), ResultStatus.MYSQL_ERROR);
    }

    @Test
    public void getAccountDOTest() {
        AccountDO accountDO = getAccountDO();
        Result<AccountDO> expectedResult = Result.buildSuc(accountDO);

        Mockito.when(accountDao.getByName(Mockito.anyString())).thenReturn(accountDO);
        Assert.assertEquals(accountService.getAccountDO(accountDO.getUsername()).toString(), expectedResult.toString());
    }

    @Test
    public void listTest() {
        AccountDO accountDO = getAccountDO();
        List<AccountDO> list = new ArrayList<>();
        list.add(accountDO);
        Mockito.when(accountDao.list()).thenReturn(list);
        List<AccountDO> actualResult = accountService.list();
        Assert.assertTrue(!actualResult.isEmpty() && actualResult.stream().allMatch(account -> account.getUsername().equals(accountDO.getUsername()) &&
                account.getPassword().equals(accountDO.getPassword())));
    }

    @Test
    public void searchAccountByPrefixTest() {
        EnterpriseStaff staff = getEnterpriseStaff();
        List<EnterpriseStaff> expectedResult = new ArrayList<>();
        expectedResult.add(staff);
        Mockito.when(enterpriseStaffService.searchEnterpriseStaffByKeyWord(Mockito.anyString())).thenReturn(expectedResult);
        List<EnterpriseStaff> actualResult = accountService.searchAccountByPrefix("prefix");

        Assert.assertTrue(!actualResult.isEmpty() && actualResult.stream().allMatch(enterpriseStaff -> enterpriseStaff.getUsername().equals(staff.getUsername())));
    }

    // 因为flush只会执行一次，因此需要分开测试
    @Test(description = "普通角色测试")
    public void getAccountRoleFromCache2NormalTest() {
        Assert.assertEquals(accountService.getAccountRoleFromCache("username"), AccountRoleEnum.NORMAL);
    }

    @Test(description = "op角色测试")
    public void getAccountRoleFromCache2OpTest() {
        AccountDO accountDO = getAccountDO();
        accountDO.setUsername("admin");
        accountDO.setRole(2);

        Mockito.when(accountDao.list()).thenReturn(Arrays.asList(accountDO));
        Assert.assertEquals(accountService.getAccountRoleFromCache("admin"), AccountRoleEnum.OP);
    }


    @Test(description = "自动审批测试")
    public void getAccountFromCache2AutoHandleTest() {
        Account account = getAccount();
        account.setUsername(Constant.AUTO_HANDLE_USER_NAME);
        account.setChineseName(Constant.AUTO_HANDLE_CHINESE_NAME);
        account.setAccountRoleEnum(AccountRoleEnum.OP);
        Assert.assertEquals(accountService.getAccountFromCache(Constant.AUTO_HANDLE_USER_NAME).toString(), account.toString());
    }

    @Test(description = "staff为null测试")
    public void getAccountFromCache2EnterpriseStaffIsNullTest() {
        Account account = getAccount();
        account.setUsername("username1");
        account.setChineseName("username1");
        account.setAccountRoleEnum(AccountRoleEnum.NORMAL);

        Mockito.when(enterpriseStaffService.getEnterpriseStaffByName(Mockito.anyString())).thenReturn(null);
        Assert.assertEquals(accountService.getAccountFromCache("username1").toString(), account.toString());
    }

    @Test(description = "staff不为null测试")
    public void getAccountFromCache2EnterpriseStaffIsNotNullTest() {
        Account account = getAccount();
        account.setUsername("username");
        account.setChineseName("ChineseName");
        account.setAccountRoleEnum(AccountRoleEnum.NORMAL);
        account.setDepartment("department");
        EnterpriseStaff enterpriseStaff = getEnterpriseStaff();

        Mockito.when(enterpriseStaffService.getEnterpriseStaffByName(Mockito.any())).thenReturn(enterpriseStaff);
        Assert.assertEquals(accountService.getAccountFromCache("username").toString(), account.toString());
    }



    @Test(description = "op角色测试")
    public void isAdminOrderHandler2OpTest() {
        AccountDO accountDO = getAccountDO();
        accountDO.setUsername("admin");
        accountDO.setRole(2);

        Mockito.when(accountDao.list()).thenReturn(Arrays.asList(accountDO));
        Assert.assertTrue(accountService.isAdminOrderHandler("admin"));
    }

    @Test(description = "ADMIN_ORDER_HANDLER_CACHE包含用户名测试")
    public void isAdminOrderHandler2CacheContainsTest() {
        Mockito.when(configService.getArrayByKey(Mockito.anyString(), Mockito.any())).thenReturn(Arrays.asList("username"));
        Mockito.when(accountDao.list()).thenReturn(new ArrayList<>());
        Assert.assertTrue(accountService.isAdminOrderHandler("username"));
    }


    @Test(description = "普通角色测试")
    public void isAdminOrderHandler2NormalTest() {
        Mockito.when(accountDao.list()).thenReturn(new ArrayList<>());
        Assert.assertFalse(accountService.isAdminOrderHandler("username"));
    }

    @Test(description = "op角色测试")
    public void isOpOrRd2OpTest() {
        AccountDO accountDO = getAccountDO();
        accountDO.setUsername("admin");
        accountDO.setRole(2);

        Mockito.when(accountDao.list()).thenReturn(Arrays.asList(accountDO));
        Assert.assertTrue(accountService.isOpOrRd("admin"));
    }

    @Test
    public void isOpOrRdTest2RdTest() {
        AccountDO accountDO = getAccountDO();
        accountDO.setUsername("admin");
        accountDO.setRole(1);
        Mockito.when(accountDao.list()).thenReturn(Arrays.asList(accountDO));
        Assert.assertTrue(accountService.isOpOrRd("admin"));
    }

    @Test
    public void getAdminOrderHandlerFromCacheTest() {
        AccountDO accountDO = getAccountDO();
        accountDO.setUsername("username");
        accountDO.setRole(2);

        Mockito.when(accountDao.list()).thenReturn(Arrays.asList(accountDO));
        List<Account> actualList = accountService.getAdminOrderHandlerFromCache();
        Assert.assertTrue(!actualList.isEmpty() && actualList.stream().allMatch(account -> account.getUsername().equals(accountDO.getUsername())));
    }

}
