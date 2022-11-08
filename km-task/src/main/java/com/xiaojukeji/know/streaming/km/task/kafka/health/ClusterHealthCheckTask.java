package com.xiaojukeji.know.streaming.km.task.kafka.health;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.AbstractHealthCheckService;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.cluster.HealthCheckClusterService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@NoArgsConstructor
@AllArgsConstructor
@Task(name = "ClusterHealthCheckTask",
        description = "Cluster健康检查",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class ClusterHealthCheckTask extends AbstractHealthCheckTask {
    @Autowired
    private HealthCheckClusterService healthCheckClusterService;

    @Override
    public AbstractHealthCheckService getCheckService() {
        return healthCheckClusterService;
    }
}
