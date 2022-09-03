package com.xiaojukeji.know.streaming.km.task.service;

import com.xiaojukeji.know.streaming.km.common.utils.FutureNoWaitUtil;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 为了尽量避免大任务的执行，由LogIJob的线程执行，
 * 因此，在Task模块，需要有自己的线程池来执行相关任务，
 * 而 TaskThreadPoolService 的职责就是负责任务的执行。
 */
@Service
@NoArgsConstructor
public class TaskThreadPoolService {
    /**
     * 较重任务，比如指标采集
     */
    private FutureNoWaitUtil<Object> heavenTaskThreadPool;


    @Value(value = "${thread-pool.task.heaven.thread-num:12}")
    private Integer heavenTaskThreadNum;

    @Value(value = "${thread-pool.task.heaven.queue-size:1000}")
    private Integer heavenTaskQueueSize;

    @PostConstruct
    private void init() {
        heavenTaskThreadPool = FutureNoWaitUtil.init("heavenTaskThreadPool", heavenTaskThreadNum, heavenTaskThreadNum, heavenTaskQueueSize);
    }

    public void submitHeavenTask(String taskName, Integer timeoutUnisMs, Runnable runnable) {
        heavenTaskThreadPool.runnableTask(taskName, timeoutUnisMs, runnable);
    }
}
