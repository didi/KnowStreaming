package com.xiaojukeji.kafka.manager.common.utils;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.LoginConstant;
import com.xiaojukeji.kafka.manager.common.constant.TrickLoginConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author huangyiminghappy@163.com
 * @date 2019-05-08
 */
@Service
@Lazy(false)
@Order(value = 1)
public class SpringTool implements ApplicationContextAware, DisposableBean {
    private static ApplicationContext applicationContext = null;

    private static Logger logger = LoggerFactory.getLogger(SpringTool.class);

    /**
     * 去的存储在静态变量中的ApplicationContext
     */
    private static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 从静态变量applicationContext中去的Bean，自动转型为所复制对象的类型
     */
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return getApplicationContext().getBeansOfType(type);
    }

    /**
     * 清除SpringContextHolder中的ApplicationContext为Null
     */
    public static void clearHolder() {
        if (logger.isDebugEnabled()) {
            logger.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
        }
        applicationContext = null;
    }

    /**
     * 实现ApplicationContextAware接口，注入Context到静态变量
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringTool.applicationContext = context;
    }

    /**
     * 实现DisposableBean接口，在Context关闭时清理静态变量
     */
    @Override
    public void destroy() throws Exception {
        SpringTool.clearHolder();
    }

    public static String getUserName(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String username = null;
        if (TrickLoginConstant.TRICK_LOGIN_SWITCH_ON.equals(request.getHeader(TrickLoginConstant.TRICK_LOGIN_SWITCH))) {
            // trick登录方式的获取用户
            username = request.getHeader(TrickLoginConstant.TRICK_LOGIN_USER);
        } else {
            // 走页面登录方式登录的获取用户
            HttpSession session = request.getSession();
            username = (String) session.getAttribute(LoginConstant.SESSION_USERNAME_KEY);
        }

        if (ValidateUtils.isNull(username)) {
            return Constant.DEFAULT_USER_NAME;
        }
        return username;
    }

    /**
     * 发布一个事件
     */
    public static void publish(ApplicationEvent event) {
        getApplicationContext().publishEvent(event);
    }
}
