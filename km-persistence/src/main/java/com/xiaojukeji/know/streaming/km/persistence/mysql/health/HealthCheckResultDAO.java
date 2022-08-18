package com.xiaojukeji.know.streaming.km.persistence.mysql.health;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.health.HealthCheckResultPO;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthCheckResultDAO extends BaseMapper<HealthCheckResultPO> {
    int replace(HealthCheckResultPO healthCheckResultPO);
}
