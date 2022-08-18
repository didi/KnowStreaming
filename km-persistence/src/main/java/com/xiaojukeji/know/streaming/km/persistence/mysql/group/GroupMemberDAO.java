package com.xiaojukeji.know.streaming.km.persistence.mysql.group;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.group.GroupMemberPO;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberDAO extends BaseMapper<GroupMemberPO> {
    int replace(GroupMemberPO po);
}
