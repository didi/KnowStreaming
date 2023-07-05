package com.xiaojukeji.know.streaming.km.task.connect.health;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.AbstractHealthCheckService;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.connect.HealthCheckConnectClusterService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wyc
 * @date 2022/11/9
 */
@NoArgsConstructor
@AllArgsConstructor
@Task(name = "ConnectClusterHealthCheckTask",
        description = "ConnectCluster健康检查",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class ConnectClusterHealthCheckTask extends AbstractHealthCheckTask {

    @Autowired
    private HealthCheckConnectClusterService healthCheckConnectClusterService;

    @Override
    public AbstractHealthCheckService getCheckService() {
        return healthCheckConnectClusterService;
    }
}
