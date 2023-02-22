package com.xiaojukeji.know.streaming.km.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Callable;

/**
 * @author wyb
 * @date 2023/2/22
 */
@Service
public class ApiCallFutureWaitUtil {

    @Value(value = "${thread-pool.api.future-util.core-size:2}")
    private Integer corePoolSize;

    @Value(value = "${thread-pool.api.future-util.max-size:8}")
    private Integer maxPoolSize;

    @Value(value = "${thread-pool.api.future-util.queue-size:500}")
    private Integer queueSize;


    private static FutureWaitUtil<Object> apiFutureUtil;

    @PostConstruct
    private void init() {
        apiFutureUtil = FutureWaitUtil.init(
                "apiFutureThreadPool",
                corePoolSize,
                maxPoolSize,
                queueSize
        );
    }

    public static void runnableTask(String taskName, Integer timeoutUnisMs, Callable<Object> callable) {
        apiFutureUtil.runnableTask(taskName, timeoutUnisMs, callable);
    }

    public static void waitResult(Integer stepWaitTimeUnitMs) {
        apiFutureUtil.waitResult(stepWaitTimeUnitMs);
    }
}