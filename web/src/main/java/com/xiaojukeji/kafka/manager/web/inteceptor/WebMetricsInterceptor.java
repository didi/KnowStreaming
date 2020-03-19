package com.xiaojukeji.kafka.manager.web.inteceptor;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Web Api Metrics信息统计拦截器
 * @author zengqiao
 * @date 20/1/11
 */
@Aspect
@Component
public class WebMetricsInterceptor {
    private final static Logger logger = LoggerFactory.getLogger(Constant.API_METRICS_LOGGER);

    /**
     * 切入点
     */
    private static final String PointCut = "execution(* com.xiaojukeji.kafka.manager.web.api.versionone.*..*(..))";

    @Pointcut(value = PointCut)
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object object = proceedingJoinPoint.proceed();


        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        logger.info("URI:{} HTTP-Method:{} Remote-IP:{} Time-Cost:{}ms", request.getRequestURI(), request.getMethod(), request.getRemoteAddr(), System.currentTimeMillis() - startTime);
        return object;
    }
}
