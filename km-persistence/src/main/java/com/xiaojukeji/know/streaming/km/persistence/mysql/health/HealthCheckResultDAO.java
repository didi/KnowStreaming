package com.xiaojukeji.know.streaming.km.persistence.mysql.health;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.health.HealthCheckResultPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthCheckResultDAO extends BaseMapper<HealthCheckResultPO> {
    int replace(HealthCheckResultPO healthCheckResultPO);

    int batchReplace(List<HealthCheckResultPO> healthCheckResultPos);
}
