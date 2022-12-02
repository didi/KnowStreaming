package com.xiaojukeji.know.streaming.km.common.utils;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.common.FutureTaskDelayQueueData;

import java.util.concurrent.*;

/**
 * Future 提交任务后，无需等待结果的非工具类
 * 会有一个移步线程周期检查任务
 * @param <T>
 */
public class FutureNoWaitUtil<T> {
    private static final ILog LOGGER = LogFactory.getLog(FutureNoWaitUtil.class);

    private ThreadPoolExecutor executor;

    private DelayQueue<FutureTaskDelayQueueData<T>> delayQueueData;

    private Thread checkDelayQueueThread;

    private FutureNoWaitUtil() {
    }

    public static <T> FutureNoWaitUtil<T> init(String threadPoolName, int corePoolSize, int maxPoolSize, int queueSize) {
        FutureNoWaitUtil<T> futureUtil = new FutureNoWaitUtil<>();

        // 创建任务线程池
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

        futureUtil.delayQueueData = new DelayQueue<>();

        // 创建检查延迟队列的线程并启动
        futureUtil.checkDelayQueueThread = new Thread(() -> futureUtil.runCheck(), threadPoolName + "-CheckTaskTimeout");
        futureUtil.checkDelayQueueThread.setDaemon(true);
        futureUtil.checkDelayQueueThread.start();

        return futureUtil;
    }

    public FutureNoWaitUtil<T> runnableTask(String taskName, Integer timeoutUnisMs, Callable<T> callable) {
        this.delayQueueData.put(new FutureTaskDelayQueueData<>(taskName, executor.submit(callable), timeoutUnisMs + System.currentTimeMillis()));

        return this;
    }

    public FutureNoWaitUtil<T> runnableTask(String taskName, Integer timeoutUnisMs, Runnable runnable) {
        this.delayQueueData.put(new FutureTaskDelayQueueData<T>(taskName, (Future<T>) executor.submit(runnable), timeoutUnisMs + System.currentTimeMillis()));

        return this;
    }

    private void runCheck() {
        while (true) {
            FutureTaskDelayQueueData<T> data = null;
            try {
                LOGGER.debug("method=runCheck||delayQueueSize={}", delayQueueData.size());

                while (true) {
                    data = delayQueueData.take();

                    if (data.getFutureTask().isDone()) {
                        // 当前任务已完成
                        continue;
                    }

                    data.getFutureTask().cancel(true);
                    break;
                }

                // 停1000ms
                Thread.sleep(1000);
            } catch (Exception e) {
                LOGGER.error("method=runCheck||taskName={}||errMsg=exception!", data == null? "": data.getTaskName(), e);
            }
        }
    }
}
