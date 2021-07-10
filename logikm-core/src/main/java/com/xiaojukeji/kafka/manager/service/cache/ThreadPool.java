package com.xiaojukeji.kafka.manager.service.cache;

import com.xiaojukeji.kafka.manager.common.utils.factory.DefaultThreadFactory;

import java.util.concurrent.*;

/**
 * @author zengqiao
 * @date 20/8/24
 */
public class ThreadPool {
    private static final ExecutorService COLLECT_METRICS_THREAD_POOL = new ThreadPoolExecutor(
            256,
            256,
            120L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(),
            new DefaultThreadFactory("Collect-Metrics-Thread")
    );

    private static final ExecutorService API_CALL_THREAD_POOL = new ThreadPoolExecutor(
            16,
            16,
            120L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(),
            new DefaultThreadFactory("Api-Call-Thread")
    );

    public static void submitCollectMetricsTask(Runnable collectMetricsTask) {
        COLLECT_METRICS_THREAD_POOL.submit(collectMetricsTask);
    }

    public static void submitApiCallTask(Runnable apiCallTask) {
        API_CALL_THREAD_POOL.submit(apiCallTask);
    }
}
