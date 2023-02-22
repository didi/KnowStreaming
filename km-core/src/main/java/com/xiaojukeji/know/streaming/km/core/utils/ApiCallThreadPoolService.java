package com.xiaojukeji.know.streaming.km.core.utils;

import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Callable;

/**
 * @author wyb
 * @date 2023/2/22
 */
@Service
@NoArgsConstructor
public class ApiCallThreadPoolService {
    @Value(value = "${thread-pool.api.thread-num:2}")
    private Integer threadNum;

    @Value(value = "${thread-pool.api.queue-size:500}")
    private Integer queueSize;

    private static FutureWaitUtil<Object> apiFutureUtil;

    @PostConstruct
    private void init() {
        apiFutureUtil = FutureWaitUtil.init(
                "ApiCallTP",
                threadNum,
                threadNum,
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