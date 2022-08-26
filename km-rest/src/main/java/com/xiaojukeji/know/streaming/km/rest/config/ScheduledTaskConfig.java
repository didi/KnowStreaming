package com.xiaojukeji.know.streaming.km.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ScheduledTaskConfig {
    @Value(value = "${thread-pool.scheduled.thread-num:2}")
    private Integer scheduledTaskThreadNum;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(scheduledTaskThreadNum);
        return taskScheduler;
    }
}
