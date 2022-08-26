package com.xiaojukeji.know.streaming.km.account.login.trick;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zengqiao
 * @date 21/5/18
 */
public interface TrickJumpLoginService {
    /**
     * 是否开启trick的方式登录
     */
    boolean isOpenTrickJumpLogin(HttpServletRequest request);

    /**
     * 开启trick方式登录后，当前用户是否可以登录
     */
    String checkTrickJumpLogin(HttpServletRequest request);
}
