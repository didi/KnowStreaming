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
    Result<Account> login(HttpServletRequest request, HttpServletResponse response, LoginDTO dto);

    void logout(HttpServletRequest request, HttpServletResponse response, Boolean needJump2LoginPage);

    boolean checkLogin(HttpServletRequest request, HttpServletResponse response);
}