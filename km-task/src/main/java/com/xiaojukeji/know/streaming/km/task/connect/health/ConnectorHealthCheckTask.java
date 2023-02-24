package com.xiaojukeji.know.streaming.km.task.connect.health;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.AbstractHealthCheckService;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.connect.HealthCheckConnectorService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wyb
 * @date 2022/11/8
 */
@NoArgsConstructor
@AllArgsConstructor
@Task(name = "ConnectorHealthCheckTask",
        description = "Connector健康检查",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class ConnectorHealthCheckTask extends AbstractHealthCheckTask {

    @Autowired
    HealthCheckConnectorService healthCheckConnectorService;

    @Override
    public AbstractHealthCheckService getCheckService() {
        return healthCheckConnectorService;
    }
}
