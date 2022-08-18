package com.xiaojukeji.know.streaming.km.persistence.mysql.reassign;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.reassign.ReassignJobPO;
import org.springframework.stereotype.Repository;

@Repository
public interface ReassignJobDAO extends BaseMapper<ReassignJobPO> {
    int addAndSetId(ReassignJobPO po);
}