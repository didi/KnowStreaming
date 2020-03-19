package com.xiaojukeji.kafka.manager.service.collector;

import org.slf4j.Logger;

/**
 * @author zengqiao
 * @date 19/12/25
 */
public abstract class BaseCollectTask implements Runnable {
    protected Logger logger;

    protected Long clusterId;

    public BaseCollectTask(Logger logger, Long clusterId) {
        this.logger = logger;
        this.clusterId = clusterId;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        try {
            collect();
        } catch (Throwable t) {
            logger.error("collect failed, clusterId:{}.", clusterId, t);
            return;
        }
        long endTime = System.currentTimeMillis();
        logger.info("collect finish, clusterId:{} costTime:{}", clusterId, endTime - startTime);
    }

    public abstract void collect();
}