package com.xiaojukeji.know.streaming.km.persistence.mysql.ha;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.ha.HaActiveStandbyRelationPO;
import org.springframework.stereotype.Repository;

@Repository
public interface HaActiveStandbyRelationDAO extends BaseMapper<HaActiveStandbyRelationPO> {
}
