package com.xiaojukeji.know.streaming.km.task.kafka.health;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.AbstractHealthCheckService;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.topic.HealthCheckTopicService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@NoArgsConstructor
@AllArgsConstructor
@Task(name = "TopicHealthCheckTask",
        description = "Topic健康检查",
        cron = "30 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class TopicHealthCheckTask extends AbstractHealthCheckTask {

    @Autowired
    private HealthCheckTopicService healthCheckTopicService;

    @Override
    public AbstractHealthCheckService getCheckService() {
        return healthCheckTopicService;
    }
}
