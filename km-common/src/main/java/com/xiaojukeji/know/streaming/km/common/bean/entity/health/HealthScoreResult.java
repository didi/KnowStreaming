package com.xiaojukeji.know.streaming.km.common.bean.entity.health;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.po.health.HealthCheckResultPO;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class HealthScoreResult extends HealthCheckAggResult {
    private BaseClusterHealthConfig baseConfig;

    public HealthScoreResult(HealthCheckNameEnum checkNameEnum,
                             BaseClusterHealthConfig baseConfig,
                             List<HealthCheckResultPO> poList) {
        super(checkNameEnum, poList);
        this.baseConfig = baseConfig;
    }
}
