package com.xiaojukeji.know.streaming.km.rest.interceptor;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.constant.Constants;
import com.didiglobal.logi.security.service.LoginService;
import com.xiaojukeji.know.streaming.km.account.login.trick.TrickJumpLoginService;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static com.didiglobal.logi.security.util.HttpRequestUtil.*;

/**
 * 登陆拦截 && 权限校验
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {
    private static final ILog       LOGGER            = LogFactory.getLog(PermissionInterceptor.class);

    private static final String     LOGIN_URL         = Constants.ACCOUNT_LOGIN;
    private static final String     OPEN_URL_PREFIX   = ApiPrefix.API_V3_OPEN_PREFIX;

    @Autowired
    private LoginService loginService;

    @Autowired
    private TrickJumpLoginService trickJumpLoginService;

    /**
     * 拦截预处理
     * @return boolean false:拦截, 不向下执行, true:放行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (hasNoInterceptor(request)) {
            return true;
        }

        // 检查是否免登录调用
        if (trickJumpLoginService.isOpenTrickJumpLogin(request)) {
            String userName = trickJumpLoginService.checkTrickJumpLogin(request);
            if (userName != null) {
                // 允许免登录调用接口
                initLoginContext(request, response, userName, Constant.INVALID_CODE);
                return true;
            }
        }

        String classRequestMappingValue = null;
        try {
            classRequestMappingValue = getClassRequestMappingValue(handler);
        } catch (Exception e) {
            LOGGER.error(
                "method=preHandle||uri={}||msg=parse class request-mapping failed",
                request.getRequestURI(), e);
        }

        List<String> whiteMappingValues = new ArrayList<>();
        whiteMappingValues.add(LOGIN_URL);
        whiteMappingValues.add(OPEN_URL_PREFIX);

        return loginService.interceptorCheck(request, response, classRequestMappingValue, whiteMappingValues);
    }

    /**************************************************** private method ****************************************************/

    /**
     * 通过反射获取带有@RequestMapping的Controller
     * @param handler 请求处理器
     * @return @RequestMapping的value
     */
    private String getClassRequestMappingValue(Object handler) {
        RequestMapping requestMapping;
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            requestMapping = hm.getMethod().getDeclaringClass().getAnnotation(RequestMapping.class);
        } else if (handler instanceof org.springframework.web.servlet.mvc.Controller) {
            org.springframework.web.servlet.mvc.Controller hm = (org.springframework.web.servlet.mvc.Controller) handler;
            Class<? extends org.springframework.web.servlet.mvc.Controller> hmClass = hm.getClass();
            requestMapping = hmClass.getAnnotation(RequestMapping.class);
        } else {
            requestMapping = handler.getClass().getAnnotation(RequestMapping.class);
        }
        if ((null == requestMapping) || requestMapping.value().length == 0) {
            return null;
        }
        return requestMapping.value()[0];
    }

    /**
     * 是否需要拦截
     * just for test
     */
    private boolean hasNoInterceptor(HttpServletRequest request) {
        return Boolean.FALSE;
    }

    /**
     * @see com.didiglobal.logi.security.service.impl.LoginServiceImpl@initLoginContext
     * 由于initLoginContext是私有方法，因此当前拷贝了一份代码出来
     */
    private void initLoginContext(HttpServletRequest request, HttpServletResponse response, String userName, Integer userId) {
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval( COOKIE_OR_SESSION_MAX_AGE_UNIT_SEC );
        session.setAttribute(USER, userName);
        session.setAttribute(USER_ID, userId);

        Cookie cookieUserName = new Cookie(USER, userName);
        cookieUserName.setMaxAge(COOKIE_OR_SESSION_MAX_AGE_UNIT_SEC);
        cookieUserName.setPath("/");

        Cookie cookieUserId = new Cookie(USER_ID, userId.toString());
        cookieUserId.setMaxAge(COOKIE_OR_SESSION_MAX_AGE_UNIT_SEC);
        cookieUserId.setPath("/");

        response.addCookie(cookieUserName);
        response.addCookie(cookieUserId);
    }
}
