package com.xiaojukeji.kafka.manager.account;

import com.xiaojukeji.kafka.manager.account.component.AbstractSingleSignOn;
import com.xiaojukeji.kafka.manager.account.component.ldap.LdapAuthentication;
import com.xiaojukeji.kafka.manager.account.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.bizenum.AccountRoleEnum;
import com.xiaojukeji.kafka.manager.common.constant.LoginConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.LoginDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.AccountDO;
import com.xiaojukeji.kafka.manager.common.utils.EncryptUtil;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * @author wyc
 * @date 2021/12/30
 */
public class AbstractSingleSignOnTest extends BaseTest {
    @Autowired
    @InjectMocks
    private AbstractSingleSignOn abstractSingleSignOn;

    @Mock
    private AccountService accountService;

    @Mock
    private LdapAuthentication ldapAuthentication;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    private HttpServletRequest getHttpServletRequest() {
        HttpServletRequest request = new MockHttpServletRequest();
        return request;
    }

    private HttpServletResponse getHttpServletResponse() {
        HttpServletResponse response = new MockHttpServletResponse();
        return response;
    }

    private LoginDTO getLoginDTO() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("username");
        dto.setPassword("password");
        return dto;
    }

    private AccountDO getAccountDO() {
        AccountDO accountDO = new AccountDO();
        accountDO.setUsername("username");
        accountDO.setPassword("password");

        return accountDO;
    }

    @Test
    public void loginAndGetLdapTest() throws NoSuchFieldException {
        // username为null测试
        loginAndGetLdap2ParamNullTest();

        // LDAP未激活测试，从最后的return返回
        loginAndGetLdap2LdapDisabledTest();

        // LDAP激活，返回false测试
        loginAndGetLdap2LdapEnabledReturnFalseTest();

        // LDAP激活，返回true测试
        loginAndGetLdap2LdapEnabledReturnTrueTest();

        loginAndGetLdap2FailureTest();

        // name illegal 测试
        loginAndGetLdap2NameIllegalTest();

        // password illegal 测试
        loginAndGetLdap2PasswordIllegalTest();


    }

    private void resetAbstractSingleSignOn() throws NoSuchFieldException {
        // 通过反射将abstractSingleSignOn内属性的值置于初始状态,因为每个private测试中会通过反射改变abstractSingleSignOn成员变量的值
        Field accountLdapEnabled = abstractSingleSignOn.getClass().getDeclaredField("accountLdapEnabled");
        FieldSetter.setField(abstractSingleSignOn, accountLdapEnabled, null);

        Field authUserRegistrationRole = abstractSingleSignOn.getClass().getDeclaredField("authUserRegistrationRole");
        FieldSetter.setField(abstractSingleSignOn, authUserRegistrationRole, null);

        Field authUserRegistration = abstractSingleSignOn.getClass().getDeclaredField("authUserRegistration");
        FieldSetter.setField(abstractSingleSignOn, authUserRegistration, false);
    }

    private void loginAndGetLdap2ParamNullTest() throws NoSuchFieldException {
        resetAbstractSingleSignOn();
        HttpServletRequest request = getHttpServletRequest();
        HttpServletResponse response = getHttpServletResponse();
        LoginDTO loginDTO = getLoginDTO();
        loginDTO.setUsername(null);
        Assert.assertEquals(abstractSingleSignOn.loginAndGetLdap(request, response, loginDTO).toString(), Result.buildFailure("Missing parameters").toString());
    }

    private void loginAndGetLdap2LdapDisabledTest() throws NoSuchFieldException {
        resetAbstractSingleSignOn();
        HttpServletRequest request = getHttpServletRequest();
        HttpServletResponse response = getHttpServletResponse();

        LoginDTO loginDTO = getLoginDTO();
        String pass = EncryptUtil.md5(loginDTO.getPassword());

        AccountDO accountDO1 = getAccountDO();
        accountDO1.setPassword(pass);

        Result<AccountDO> result = Result.buildSuc(accountDO1);
        Mockito.when(accountService.getAccountDO(Mockito.any())).thenReturn(result);
        Assert.assertEquals(abstractSingleSignOn.loginAndGetLdap(request, response, loginDTO).toString(), Result.buildSuc(result.getData().getUsername()).toString());
    }

    private void loginAndGetLdap2LdapEnabledReturnFalseTest() throws NoSuchFieldException {
        resetAbstractSingleSignOn();
        HttpServletRequest request = getHttpServletRequest();
        HttpServletResponse response = getHttpServletResponse();
        LoginDTO dto = getLoginDTO();
        // 通过反射将abstractSingleSignOn对象中accountLdapEnabled属性设置为true
        Field accountLdapEnabled = abstractSingleSignOn.getClass().getDeclaredField("accountLdapEnabled");
        FieldSetter.setField(abstractSingleSignOn, accountLdapEnabled, true);

        Mockito.when(ldapAuthentication.authenticate(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        Assert.assertEquals(abstractSingleSignOn.loginAndGetLdap(request, response, dto).toString(), Result.buildFrom(ResultStatus.LDAP_AUTHENTICATION_FAILED).toString());
    }

    private void loginAndGetLdap2LdapEnabledReturnTrueTest() throws NoSuchFieldException {
        resetAbstractSingleSignOn();
        HttpServletRequest request = getHttpServletRequest();
        HttpServletResponse response = getHttpServletResponse();
        LoginDTO dto = getLoginDTO();

        Mockito.when(accountService.getAccountDO(Mockito.any())).thenReturn(null);

        // 通过反射将abstractSingleSignOn对象中accountLdapEnabled属性设置为true
        Field accountLdapEnabled = abstractSingleSignOn.getClass().getDeclaredField("accountLdapEnabled");
        FieldSetter.setField(abstractSingleSignOn, accountLdapEnabled, true);

        Mockito.when(ldapAuthentication.authenticate(Mockito.anyString(), Mockito.anyString())).thenReturn(new HashMap<>());

        // 通过反射初始化成员变量，防止出现空指针异常
        Field authUserRegistrationRole = abstractSingleSignOn.getClass().getDeclaredField("authUserRegistrationRole");
        FieldSetter.setField(abstractSingleSignOn, authUserRegistrationRole, AccountRoleEnum.NORMAL.getMessage());

        Field authUserRegistration = abstractSingleSignOn.getClass().getDeclaredField("authUserRegistration");
        FieldSetter.setField(abstractSingleSignOn, authUserRegistration, true);


        Assert.assertEquals(abstractSingleSignOn.loginAndGetLdap(request, response, dto).toString(), Result.buildSuc(dto.getUsername()).toString());
    }

    private void loginAndGetLdap2FailureTest() throws NoSuchFieldException {
        resetAbstractSingleSignOn();
        HttpServletRequest request = getHttpServletRequest();
        HttpServletResponse response = getHttpServletResponse();
        LoginDTO dto = getLoginDTO();
        Result result = Result.buildFailure("fail");
        Mockito.when(accountService.getAccountDO(Mockito.any())).thenReturn(result);
        Assert.assertEquals(abstractSingleSignOn.loginAndGetLdap(request, response, dto).toString(), result.toString());
    }

    private void loginAndGetLdap2NameIllegalTest() throws NoSuchFieldException {
        resetAbstractSingleSignOn();
        HttpServletRequest request = getHttpServletRequest();
        HttpServletResponse response = getHttpServletResponse();
        LoginDTO dto = getLoginDTO();

        Result result = Result.buildSuc();
        Mockito.when(accountService.getAccountDO(Mockito.any())).thenReturn(result);
        Assert.assertEquals(abstractSingleSignOn.loginAndGetLdap(request, response, dto).toString(), Result.buildFailure("username illegal").toString());
    }

    private void loginAndGetLdap2PasswordIllegalTest() throws NoSuchFieldException {
        resetAbstractSingleSignOn();
        HttpServletRequest request = getHttpServletRequest();
        HttpServletResponse response = getHttpServletResponse();
        LoginDTO dto = getLoginDTO();
        AccountDO accountDO = getAccountDO();
        Result<AccountDO> result = Result.buildSuc(accountDO);
        Mockito.when(accountService.getAccountDO(Mockito.any())).thenReturn(result);
        Assert.assertEquals(abstractSingleSignOn.loginAndGetLdap(request, response, dto).toString(), Result.buildFailure("password illegal").toString());
    }


    @Test
    public void logoutTest() {
        HttpServletRequest request = getHttpServletRequest();
        HttpServletResponse response = getHttpServletResponse();
        abstractSingleSignOn.logout(request, response, true);
        Assert.assertEquals(response.getStatus(), 401);
        Assert.assertEquals(response.getHeader("location"), "");
    }

    @Test
    public void checkLoginAndGetLdapTest() {
        // 返回null测试
        checkLoginAndGetLdap2NullTest();

        // 成功测试
        checkLoginAndGetLdap2SuccessTest();
    }

    private void checkLoginAndGetLdap2NullTest() {
        HttpServletRequest request = getHttpServletRequest();
        Assert.assertNull(abstractSingleSignOn.checkLoginAndGetLdap(request));
    }

    private void checkLoginAndGetLdap2SuccessTest() {
        HttpServletRequest request = getHttpServletRequest();
        request.getSession().setAttribute(LoginConstant.SESSION_USERNAME_KEY, "username");
        Assert.assertEquals(abstractSingleSignOn.checkLoginAndGetLdap(request), "username");
    }

    @Test
    public void setRedirectToLoginPageTest() {
        HttpServletResponse response = getHttpServletResponse();
        response.setStatus(401);
        response.setHeader("location", "");
        Assert.assertEquals(response.getStatus(), 401);
        Assert.assertEquals(response.getHeader("location"), "");
    }
}
