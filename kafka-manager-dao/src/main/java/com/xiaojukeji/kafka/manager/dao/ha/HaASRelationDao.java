package com.xiaojukeji.kafka.manager.dao.ha;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASRelationDO;
import org.springframework.stereotype.Repository;

/**
 * 主备关系信息
 */
@Repository
public interface HaASRelationDao extends BaseMapper<HaASRelationDO> {
}
