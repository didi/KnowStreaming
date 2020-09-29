package com.xiaojukeji.kafka.manager.task.component;

import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zengqiao
 * @date 20/8/10
 */
public class BaseBizTask<E extends Comparable> implements Runnable {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    private E task;

    private AbstractScheduledTask scheduledTask;

    public BaseBizTask(E task, AbstractScheduledTask scheduledTask) {
        this.task = task;
        this.scheduledTask = scheduledTask;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        LOGGER.info("scheduled task scheduleName:{} start", scheduledTask.getScheduledName());
        try {
            scheduledTask.processTask(task);
        } catch (Throwable t) {
            LOGGER.error("scheduled task scheduleName:{} execute failed, task:{}", scheduledTask.getScheduledName(), task, t);
        }
        LOGGER.info("scheduled task scheduleName:{} finished, cost-time:{}ms.", scheduledTask.getScheduledName(), System.currentTimeMillis() - startTime);
    }
}