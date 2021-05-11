package com.xiaojukeji.kafka.manager.account.impl;

import com.xiaojukeji.kafka.manager.account.AccountService;
import com.xiaojukeji.kafka.manager.account.component.AbstractSingleSignOn;
import com.xiaojukeji.kafka.manager.account.LoginService;
import com.xiaojukeji.kafka.manager.common.bizenum.AccountRoleEnum;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.constant.LoginConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.LoginDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author zengqiao
 * @date 20/8/20
 */
@Service("loginService")
public class LoginServiceImpl implements LoginService {
    private final static Logger LOGGER = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private AbstractSingleSignOn singleSignOn;

    @Override
    public Result<Account> login(HttpServletRequest request, HttpServletResponse response, LoginDTO loginDTO) {
        Result<String> userResult = singleSignOn.loginAndGetLdap(request, response, loginDTO);
        if (ValidateUtils.isNull(userResult) || userResult.failed()) {
            logout(request, response, false);
            return new Result<>(userResult.getCode(), userResult.getMessage());
        }
        Account account = accountService.getAccountFromCache(userResult.getData());
        initLoginContext(request, response, account);
        return Result.buildSuc(account);
    }

    private void initLoginContext(HttpServletRequest request, HttpServletResponse response, Account account) {
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(LoginConstant.COOKIE_OR_SESSION_MAX_AGE_UNIT_MS);
        session.setAttribute(LoginConstant.SESSION_USERNAME_KEY, account.getUsername());

        Cookie cookieChineseName = new Cookie(LoginConstant.COOKIE_CHINESE_USERNAME_KEY, account.getChineseName());
        cookieChineseName.setMaxAge(LoginConstant.COOKIE_OR_SESSION_MAX_AGE_UNIT_MS);
        cookieChineseName.setPath("/");
        response.addCookie(cookieChineseName);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Boolean needJump2LoginPage) {
        singleSignOn.logout(request, response, needJump2LoginPage);
    }

    @Override
    public boolean checkLogin(HttpServletRequest request, HttpServletResponse response, String classRequestMappingValue) {
        if (ValidateUtils.isNull(classRequestMappingValue)) {
            LOGGER.error("class=LoginServiceImpl||method=checkLogin||msg=uri illegal||uri={}", request.getRequestURI());
            singleSignOn.setRedirectToLoginPage(response);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        if (classRequestMappingValue.equals(ApiPrefix.API_V1_SSO_PREFIX)
                || classRequestMappingValue.equals(ApiPrefix.API_V1_THIRD_PART_PREFIX)
                || classRequestMappingValue.equals(ApiPrefix.API_V1_THIRD_PART_OP_PREFIX)
                || classRequestMappingValue.equals(ApiPrefix.API_V1_THIRD_PART_NORMAL_PREFIX)
                || classRequestMappingValue.equals(ApiPrefix.GATEWAY_API_V1_PREFIX)) {
            // 白名单接口直接true
            return true;
        }

        String username = singleSignOn.checkLoginAndGetLdap(request);
        if (ValidateUtils.isBlank(username)) {
            // 未登录, 则返回false, 同时重定向到登录页面
            singleSignOn.setRedirectToLoginPage(response);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        boolean status = checkAuthority(classRequestMappingValue, accountService.getAccountRoleFromCache(username));
        if (status) {
            HttpSession session = request.getSession();
            session.setAttribute(LoginConstant.SESSION_USERNAME_KEY, username);
            return true;
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }

    private boolean checkAuthority(String classRequestMappingValue, AccountRoleEnum accountRoleEnum) {
        if (classRequestMappingValue.equals(ApiPrefix.API_V1_NORMAL_PREFIX)) {
            // normal 接口都可以访问
            return true;
        }

        if (classRequestMappingValue.equals(ApiPrefix.API_V1_RD_PREFIX) ) {
            // RD 接口, OP 或者 RD 可以访问
            return AccountRoleEnum.RD.equals(accountRoleEnum) || AccountRoleEnum.OP.equals(accountRoleEnum);
        }

        if (classRequestMappingValue.equals(ApiPrefix.API_V1_OP_PREFIX)) {
            // OP 接口只有 OP 可以访问
            return AccountRoleEnum.OP.equals(accountRoleEnum);
        }
        return true;
    }
}
