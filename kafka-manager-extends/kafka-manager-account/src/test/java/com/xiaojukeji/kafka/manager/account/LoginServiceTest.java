package com.xiaojukeji.kafka.manager.account;

import com.xiaojukeji.kafka.manager.account.component.AbstractSingleSignOn;
import com.xiaojukeji.kafka.manager.account.component.login.trick.TrickLoginService;
import com.xiaojukeji.kafka.manager.account.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.bizenum.AccountRoleEnum;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.constant.LoginConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.*;

/**
 * @author wyc
 * @Date 2021/12/29
 */
public class LoginServiceTest extends BaseTest {
    @Autowired
    @InjectMocks
    private LoginService loginService;

    @Mock
    private AbstractSingleSignOn singleSignOn;

    @Mock
    private AccountService accountService;

    @Mock
    private TrickLoginService trickLoginService;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    private Account getAccount() {
        Account account = new Account();
        account.setUsername("username");
        return account;
    }

    private HttpServletRequest getHttpServletRequest() {
        HttpServletRequest request = new MockHttpServletRequest();
        return request;
    }

    private HttpServletResponse getHttpServletResponse() {
        HttpServletResponse response = new MockHttpServletResponse();
        return response;
    }



    @Test
    public void loginTest() {
        // 失败测试
        login2FailureTest();

        // 成功测试
        HttpServletRequest request = getHttpServletRequest();
        HttpServletResponse response = getHttpServletResponse();
        login2SuccessTest(request, response);
    }

    private void login2SuccessTest(HttpServletRequest request, HttpServletResponse response) {
        Account account = getAccount();
        Result<Account> expectResult = Result.buildSuc(account);
        Result<String> midResult = Result.buildSuc();
        Mockito.when(singleSignOn.loginAndGetLdap(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(midResult);
        Mockito.when(accountService.getAccountFromCache(Mockito.any())).thenReturn(account);
        Assert.assertEquals(loginService.login(request, response, null).toString(), expectResult.toString());
    }

    private void login2FailureTest() {
        Result<String> result = new Result<>();
        result.setCode(ResultStatus.FAIL.getCode());
        result.setMessage(ResultStatus.FAIL.getMessage());
        Mockito.when(singleSignOn.loginAndGetLdap(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(result);
        Result<Account> actualResult = loginService.login(null, null, null);
        Assert.assertEquals(actualResult.toString(), result.toString());
    }


    @Test
    public void checkLoginTest() {
        // classRequestMappingValue为null测试
        checkLogin2ClassValueIsNullTest();

        // 白名单接口测试
        checkLogin2SSOTest();

        // trick登陆返回用户名为空
        checkLogin2TrickFalseTest();

        // 权限检查normal接口测试
        checkLogin2NormalTest();

        // 权限检查RD接口, 成功测试
        checkLogin2RDSuccessTest();

        // 权限检查RD接口, 失败测试
        checkLogin2RDFailureTest();

        // 权限检查OP接口, 成功测试
        checkLogin2OPSuccessTest();

        // 权限检查OP接口，失败测试
        checkLogin2OPFailureTest();
    }

    private void checkLogin2ClassValueIsNullTest() {
        HttpServletResponse response = getHttpServletResponse();
        HttpServletRequest request = getHttpServletRequest();
        Mockito.doNothing().when(singleSignOn).setRedirectToLoginPage(Mockito.any());
        Assert.assertFalse(loginService.checkLogin(request, response, null));
        Assert.assertEquals(response.getStatus(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private void checkLogin2SSOTest() {
        HttpServletResponse response = getHttpServletResponse();
        HttpServletRequest request = getHttpServletRequest();
        Assert.assertTrue(loginService.checkLogin(request, response, ApiPrefix.API_V1_SSO_PREFIX));
    }

    private void checkLogin2TrickFalseTest() {
        HttpServletResponse response = getHttpServletResponse();
        HttpServletRequest request = getHttpServletRequest();
        Mockito.when(trickLoginService.isTrickLoginOn(Mockito.any())).thenReturn(true);
        Mockito.when(trickLoginService.checkTrickLogin(Mockito.any())).thenReturn("");
        Mockito.doNothing().when(singleSignOn).setRedirectToLoginPage(response);
        Assert.assertFalse(loginService.checkLogin(request, response, "string"));
        Assert.assertEquals(response.getStatus(), HttpServletResponse.SC_UNAUTHORIZED);
    }

    private void checkLogin2NormalTest() {
        HttpServletResponse response = getHttpServletResponse();
        HttpServletRequest request = getHttpServletRequest();
        Mockito.when(trickLoginService.isTrickLoginOn(Mockito.any())).thenReturn(true);
        Mockito.when(trickLoginService.checkTrickLogin(Mockito.any())).thenReturn("username");
        Assert.assertTrue(loginService.checkLogin(request, response, ApiPrefix.API_V1_NORMAL_PREFIX));
        Assert.assertEquals(request.getSession().getAttribute(LoginConstant.SESSION_USERNAME_KEY), "username");
    }

    private void checkLogin2RDSuccessTest() {
        HttpServletResponse response = getHttpServletResponse();
        HttpServletRequest request = getHttpServletRequest();
        Mockito.when(trickLoginService.isTrickLoginOn(Mockito.any())).thenReturn(true);
        Mockito.when(trickLoginService.checkTrickLogin(Mockito.any())).thenReturn("username");
        Mockito.when(accountService.getAccountRoleFromCache(Mockito.any())).thenReturn(AccountRoleEnum.OP);
        Assert.assertTrue(loginService.checkLogin(request, response, ApiPrefix.API_V1_RD_PREFIX));
        Assert.assertEquals(request.getSession().getAttribute(LoginConstant.SESSION_USERNAME_KEY), "username");
    }

    private void checkLogin2RDFailureTest() {
        HttpServletResponse response = getHttpServletResponse();
        HttpServletRequest request = getHttpServletRequest();
        Mockito.when(trickLoginService.isTrickLoginOn(Mockito.any())).thenReturn(true);
        Mockito.when(trickLoginService.checkTrickLogin(Mockito.any())).thenReturn("username");
        Mockito.when(accountService.getAccountRoleFromCache(Mockito.any())).thenReturn(AccountRoleEnum.NORMAL);
        Assert.assertFalse(loginService.checkLogin(request, response, ApiPrefix.API_V1_RD_PREFIX));
        Assert.assertEquals(response.getStatus(), HttpServletResponse.SC_FORBIDDEN);
    }

    private void checkLogin2OPSuccessTest() {
        HttpServletResponse response = getHttpServletResponse();
        HttpServletRequest request = getHttpServletRequest();
        Mockito.when(trickLoginService.isTrickLoginOn(Mockito.any())).thenReturn(true);
        Mockito.when(trickLoginService.checkTrickLogin(Mockito.any())).thenReturn("username");
        Mockito.when(accountService.getAccountRoleFromCache(Mockito.any())).thenReturn(AccountRoleEnum.OP);
        Assert.assertTrue(loginService.checkLogin(request, response, ApiPrefix.API_V1_OP_PREFIX));
        Assert.assertEquals(request.getSession().getAttribute(LoginConstant.SESSION_USERNAME_KEY), "username");
    }

    private void checkLogin2OPFailureTest() {
        HttpServletResponse response = getHttpServletResponse();
        HttpServletRequest request = getHttpServletRequest();
        Mockito.when(trickLoginService.isTrickLoginOn(Mockito.any())).thenReturn(true);
        Mockito.when(trickLoginService.checkTrickLogin(Mockito.any())).thenReturn("username");
        Mockito.when(accountService.getAccountRoleFromCache(Mockito.any())).thenReturn(AccountRoleEnum.NORMAL);
        Assert.assertFalse(loginService.checkLogin(request, response, ApiPrefix.API_V1_OP_PREFIX));
        Assert.assertEquals(response.getStatus(), HttpServletResponse.SC_FORBIDDEN);
    }



}
