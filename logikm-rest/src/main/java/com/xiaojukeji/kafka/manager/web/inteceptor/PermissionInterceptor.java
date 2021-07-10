package com.xiaojukeji.kafka.manager.web.inteceptor;

import com.xiaojukeji.kafka.manager.account.LoginService;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionInterceptor.class);

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

        String classRequestMappingValue = null;
        try {
            classRequestMappingValue = getClassRequestMappingValue(handler);
        } catch (Exception e) {
            LOGGER.error("class=PermissionInterceptor||method=preHandle||uri={}||msg=parse class request-mapping failed", request.getRequestURI(), e);
        }
        return loginService.checkLogin(request, response, classRequestMappingValue);
    }

    private String getClassRequestMappingValue(Object handler) {
        RequestMapping classRM = null;
        if(handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod)handler;
            classRM = hm.getMethod().getDeclaringClass().getAnnotation(RequestMapping.class);
        } else if(handler instanceof org.springframework.web.servlet.mvc.Controller) {
            org.springframework.web.servlet.mvc.Controller hm = (org.springframework.web.servlet.mvc.Controller)handler;
            Class<? extends org.springframework.web.servlet.mvc.Controller> hmClass = hm.getClass();
            classRM = hmClass.getAnnotation(RequestMapping.class);
        } else {
            classRM = handler.getClass().getAnnotation(RequestMapping.class);
        }
        if (ValidateUtils.isNull(classRM) || classRM.value().length < 0) {
            return null;
        }
        return classRM.value()[0];
    }
}
