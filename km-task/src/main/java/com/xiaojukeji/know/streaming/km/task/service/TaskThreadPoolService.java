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
     * metrics任务，比如指标采集
     */
    private FutureNoWaitUtil<Object> metricsTaskThreadPool;

    @Value(value = "${thread-pool.task.metrics.thread-num:18}")
    private Integer metricsTaskThreadNum;

    @Value(value = "${thread-pool.task.metrics.queue-size:180}")
    private Integer metricsTaskQueueSize;


    /**
     * metadata任务
     */
    private FutureNoWaitUtil<Object> metadataTaskThreadPool;

    @Value(value = "${thread-pool.task.metadata.thread-num:27}")
    private Integer metadataTaskThreadNum;

    @Value(value = "${thread-pool.task.metadata.queue-size:270}")
    private Integer metadataTaskQueueSize;

    /**
     * common任务
     */
    private FutureNoWaitUtil<Object> commonTaskThreadPool;

    @Value(value = "${thread-pool.task.common.thread-num:15}")
    private Integer commonTaskThreadNum;

    @Value(value = "${thread-pool.task.common.queue-size:150}")
    private Integer commonTaskQueueSize;

    @PostConstruct
    private void init() {
        metricsTaskThreadPool = FutureNoWaitUtil.init(
                "MetricsTaskTP",
                metricsTaskThreadNum,
                metricsTaskThreadNum,
                metricsTaskQueueSize
        );

        metadataTaskThreadPool = FutureNoWaitUtil.init(
                "MetadataTaskTP",
                metadataTaskThreadNum,
                metadataTaskThreadNum,
                metadataTaskQueueSize
        );

        commonTaskThreadPool = FutureNoWaitUtil.init(
                "CommonTaskTP",
                commonTaskThreadNum,
                commonTaskThreadNum,
                commonTaskQueueSize
        );
    }

    public void submitMetricsTask(String taskName, Integer timeoutUnisMs, Runnable runnable) {
        metricsTaskThreadPool.runnableTask(taskName, timeoutUnisMs, runnable);
    }

    public void submitMetadataTask(String taskName, Integer timeoutUnisMs, Runnable runnable) {
        metadataTaskThreadPool.runnableTask(taskName, timeoutUnisMs, runnable);
    }

    public void submitCommonTask(String taskName, Integer timeoutUnisMs, Runnable runnable) {
        commonTaskThreadPool.runnableTask(taskName, timeoutUnisMs, runnable);
    }
}
