package com.xiaojukeji.know.streaming.km.common.utils;

import java.util.concurrent.*;

/**
 * Future工具类
 * @param <T>
 */
public class FutureUtil<T> {
    private ThreadPoolExecutor executor;

    public static final FutureUtil<Void> quickStartupFutureUtil = FutureUtil.init("QuickStartupTP", 8, 8, 10240);

    private FutureUtil() {
    }

    public static <T> FutureUtil<T> init(String threadPoolName, int corePoolSize, int maxPoolSize, int queueSize) {
        FutureUtil<T> futureUtil = new FutureUtil<>();

        futureUtil.executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                300,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(queueSize),
                new NamedThreadFactory(threadPoolName),
                new ThreadPoolExecutor.DiscardOldestPolicy() //对拒绝任务不抛弃，而是抛弃队列里面等待最久的一个线程，然后把拒绝任务加到队列。
        );
        futureUtil.executor.allowCoreThreadTimeOut(true);

        return futureUtil;
    }

    public FutureUtil<T> submitTask(Runnable runnable) {
        executor.submit(runnable);
        return this;
    }
}
