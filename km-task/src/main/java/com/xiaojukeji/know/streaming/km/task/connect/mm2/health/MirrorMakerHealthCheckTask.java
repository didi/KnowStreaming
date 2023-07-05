package com.xiaojukeji.know.streaming.km.task.connect.mm2.health;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.AbstractHealthCheckService;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.connect.mm2.HealthCheckMirrorMakerService;
import com.xiaojukeji.know.streaming.km.task.connect.health.AbstractHealthCheckTask;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wyb
 * @date 2022/12/21
 */
@NoArgsConstructor
@AllArgsConstructor
@Task(name = "MirrorMakerHealthCheckTask",
        description = "MirrorMaker健康检查",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class MirrorMakerHealthCheckTask extends AbstractHealthCheckTask {

    @Autowired
    private HealthCheckMirrorMakerService healthCheckMirrorMakerService;

    @Override
    public AbstractHealthCheckService getCheckService() {
        return healthCheckMirrorMakerService;
    }
}
