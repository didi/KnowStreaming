package com.xiaojukeji.kafka.manager.web.inteceptor;

import com.xiaojukeji.kafka.manager.common.entity.bizenum.AccountRoleEnum;
import com.xiaojukeji.kafka.manager.service.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 拦截器
 * @author huangyiminghappy@163.com
 * @date 19/4/29
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {
    private final static Logger logger = LoggerFactory.getLogger(PermissionInterceptor.class);

    @Autowired
    private LoginService loginService;

    /**
     * 拦截预处理
     * @author zengqiao
     * @date 19/4/29
     * @return boolean false:拦截, 不向下执行, true:放行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (!uri.contains("api")) {
            response.sendRedirect("/login");
            return false;
        }
        if (uri.contains("api/v1/login")) {
            return true;
        }

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        AccountRoleEnum userRoleEnum = (AccountRoleEnum) session.getAttribute("role");
        if (userRoleEnum == null || AccountRoleEnum.UNKNOWN.equals(userRoleEnum) || !loginService.isLogin(username)) {
            response.sendRedirect("/login");
            return false;
        }

        if (uri.contains("admin") && userRoleEnum.getRole() <= AccountRoleEnum.NORMAL.getRole()) {
            // SRE及以上的用户, 才可查看admin相关的接口
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "无权限访问");
            return false;
        }
        if (uri.contains("admin/accounts") && !AccountRoleEnum.ADMIN.equals(userRoleEnum)) {
            // 非Admin用户, 不可查看账号信息
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "无权限访问");
            return false;
        }

        return true;
    }
}
