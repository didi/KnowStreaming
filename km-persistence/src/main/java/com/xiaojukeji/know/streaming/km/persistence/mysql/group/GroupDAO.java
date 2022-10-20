package com.xiaojukeji.know.streaming.km.persistence.mysql.group;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.group.GroupPO;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupDAO extends BaseMapper<GroupPO> {
}
