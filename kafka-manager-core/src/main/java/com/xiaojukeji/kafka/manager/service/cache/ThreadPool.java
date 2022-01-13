package com.xiaojukeji.kafka.manager.service.cache;

import com.xiaojukeji.kafka.manager.common.utils.factory.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zengqiao
 * @date 20/8/24
 */
@Service
public class ThreadPool {

    @Value(value = "${thread-pool.collect-metrics.thread-num:256}")
    private Integer collectMetricsThreadNum;

    @Value(value = "${thread-pool.collect-metrics.queue-size:10000}")
    private Integer collectMetricsQueueSize;

    @Value(value = "${thread-pool.api-call.thread-num:16}")
    private Integer apiCallThreadNum;

    @Value(value = "${thread-pool.api-call.queue-size:10000}")
    private Integer apiCallQueueSize;

    private ThreadPoolExecutor collectMetricsThreadPool;

    private ThreadPoolExecutor apiCallThreadPool;

    @PostConstruct
    public void init() {
        collectMetricsThreadPool = new ThreadPoolExecutor(
                collectMetricsThreadNum,
                collectMetricsThreadNum,
                120L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(collectMetricsQueueSize),
                new DefaultThreadFactory("TaskThreadPool")
        );

        apiCallThreadPool = new ThreadPoolExecutor(
                apiCallThreadNum,
                apiCallThreadNum,
                120L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(apiCallQueueSize),
                new DefaultThreadFactory("ApiThreadPool")
        );
    }

    public void submitCollectMetricsTask(Long clusterId, Runnable collectMetricsTask) {
        collectMetricsThreadPool.submit(collectMetricsTask);
    }

    public void submitApiCallTask(Long clusterId, Runnable apiCallTask) {
        apiCallThreadPool.submit(apiCallTask);
    }
}
