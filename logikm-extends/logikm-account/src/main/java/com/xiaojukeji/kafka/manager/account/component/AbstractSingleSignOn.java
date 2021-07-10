package com.xiaojukeji.kafka.manager.account.component;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.LoginDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录抽象类
 * @author zengqiao
 * @date 20/8/20
 */
public abstract class AbstractSingleSignOn {
    /**
     * HttpServletResponse.SC_UNAUTHORIZED
     */
    protected static final Integer REDIRECT_CODE = 401;

    protected static final String HEADER_REDIRECT_KEY = "location";

    public abstract Result<String> loginAndGetLdap(HttpServletRequest request, HttpServletResponse response, LoginDTO dto);

    public abstract void logout(HttpServletRequest request, HttpServletResponse response, Boolean needJump2LoginPage);

    public abstract String checkLoginAndGetLdap(HttpServletRequest request);

    public abstract void setRedirectToLoginPage(HttpServletResponse response);
}