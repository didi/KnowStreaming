package com.xiaojukeji.kafka.manager.account;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.LoginDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zengqiao
 * @date 20/8/20
 */
public interface LoginService {
    /**
     * 登录
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param dto 登录信息
     * @return 登录结果
     */
    Result<Account> login(HttpServletRequest request, HttpServletResponse response, LoginDTO dto);

    /**
     * 登出
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param needJump2LoginPage 是否需要跳转到登录页
     */
    void logout(HttpServletRequest request, HttpServletResponse response, Boolean needJump2LoginPage);

    /**
     * 检查是否登录
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param classRequestMappingValue request-mapping的value
     * @return 检查结果, false:未登录或无权限, true:已登录并且有权限
     */
    boolean checkLogin(HttpServletRequest request, HttpServletResponse response, String classRequestMappingValue);
}