package com.xiaojukeji.kafka.manager.web.inteceptor;

import com.xiaojukeji.kafka.manager.account.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器
 * @author huangyiminghappy, zengqiao
 * @date 19/4/29
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    private LoginService loginService;

    /**
     * 拦截预处理
     * @return boolean false:拦截, 不向下执行, true:放行
     * @author zengqiao
     * @date 19/4/29
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        return loginService.checkLogin(request, response);
    }
}
