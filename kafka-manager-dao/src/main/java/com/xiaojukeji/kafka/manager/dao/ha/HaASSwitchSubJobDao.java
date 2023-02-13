package com.xiaojukeji.kafka.manager.dao.ha;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASSwitchSubJobDO;
import org.springframework.stereotype.Repository;

/**
 * 主备关系切换子任务
 */
@Repository
public interface HaASSwitchSubJobDao extends BaseMapper<HaASSwitchSubJobDO> {
}
