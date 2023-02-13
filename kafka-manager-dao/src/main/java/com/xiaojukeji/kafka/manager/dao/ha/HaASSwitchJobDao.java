package com.xiaojukeji.kafka.manager.dao.ha;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASSwitchJobDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 主备关系切换任务
 */
@Repository
public interface HaASSwitchJobDao extends BaseMapper<HaASSwitchJobDO> {
    int addAndSetId(HaASSwitchJobDO jobDO);

    List<HaASSwitchJobDO> listAllLatest();
}
