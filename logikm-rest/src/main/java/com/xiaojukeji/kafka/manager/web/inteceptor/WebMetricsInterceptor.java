package com.xiaojukeji.kafka.manager.web.inteceptor;

import com.codahale.metrics.Timer;
import com.xiaojukeji.kafka.manager.common.annotations.ApiLevel;
import com.xiaojukeji.kafka.manager.common.constant.ApiLevelContent;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.api.ApiCount;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.web.metrics.MetricsRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Web Api Metrics信息统计拦截器
 * @author zengqiao
 * @date 20/1/11
 */
@Aspect
@Component
public class WebMetricsInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebMetricsInterceptor.class);

    /**
     * 各级别接口允许的最大线程数
     */
    private static final Map<Integer, ApiCount> API_COUNT_MAP = new ConcurrentHashMap<>();

    public WebMetricsInterceptor(@Value("${server.tomcat.max-threads}") int maxThreads) {
        // 0级端口不限流
        API_COUNT_MAP.put(
                ApiLevelContent.LEVEL_VIP_1,
                new ApiCount(ApiLevelContent.LEVEL_VIP_1, Integer.MAX_VALUE, new AtomicInteger(0))
        );

        // 1级端口
        API_COUNT_MAP.put(
                ApiLevelContent.LEVEL_IMPORTANT_2,
                new ApiCount(ApiLevelContent.LEVEL_IMPORTANT_2,maxThreads / 4, new AtomicInteger(0))
        );

        // 2级端口
        API_COUNT_MAP.put(
                ApiLevelContent.LEVEL_NORMAL_3,
                new ApiCount(ApiLevelContent.LEVEL_NORMAL_3,maxThreads / 8, new AtomicInteger(0))
        );

        // 其他接口
        API_COUNT_MAP.put(
                ApiLevelContent.LEVEL_DEFAULT_4,
                new ApiCount(ApiLevelContent.LEVEL_DEFAULT_4,maxThreads / 4, new AtomicInteger(0))
        );
    }

    /**
     * 切入点
     */
    private static final String PointCut = "execution(* com.xiaojukeji.kafka.manager.web.api..*.*(..))";

    @Pointcut(value = PointCut)
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();
        int apiLevel = ApiLevelContent.LEVEL_DEFAULT_4;

        // 获取方法的api level
        MethodSignature msig = (MethodSignature) proceedingJoinPoint.getSignature();
        String methodName = msig.getName();
        Object target = proceedingJoinPoint.getTarget();
        Method currentMethod = target.getClass().getMethod(methodName, msig.getParameterTypes());
        ApiLevel annotation = currentMethod.getAnnotation(ApiLevel.class);
        if (!ValidateUtils.isNull(annotation)) {
            apiLevel = annotation.level();
        }

        ApiCount apiCount = API_COUNT_MAP.get(apiLevel);
        if (ValidateUtils.isNull(apiCount)) {
            apiCount = API_COUNT_MAP.get(ApiLevelContent.LEVEL_DEFAULT_4);
        }

        Object methodResult = null;
        try {
            if (apiCount.incAndCheckIsOverFlow()) {
                return returnErrorDirect(methodName, apiCount);
            }
            methodResult = proceedingJoinPoint.proceed();
        } catch (Throwable t) {
            LOGGER.error("error occurred when proceed method:{}.", methodName, t);
        } finally {
            apiCount.decPresentNum();
            metricsRecord(methodName, beginTime);
        }
        return methodResult;
    }

    private Object returnErrorDirect(String methodName, ApiCount apiCount) {
        LOGGER.warn("api limiter, methodName:{}, apiLevel:{}, currentNum:{}, maxNum:{}, return directly.",
                methodName, apiCount.getApiLevel(), apiCount.getCurrentNum(), apiCount.getMaxNum());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String uri = attributes.getRequest().getRequestURI();
        if (uri.contains(ApiPrefix.GATEWAY_API_V1_PREFIX)) {
            return Result.buildGatewayFailure("api limited");
        }
        return new Result<>(ResultStatus.OPERATION_FORBIDDEN);
    }

    private void metricsRecord(String methodName, long startTime) {
        long costTime = System.currentTimeMillis() - startTime;

        String metricsName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1) + "-Timer";
        Timer apiTimer = MetricsRegistry.newTimer(metricsName);
        apiTimer.update(costTime, TimeUnit.MILLISECONDS);

        Timer sumTimer = MetricsRegistry.newTimer("All_Timer");
        sumTimer.update(costTime, TimeUnit.MILLISECONDS);
    }
}
