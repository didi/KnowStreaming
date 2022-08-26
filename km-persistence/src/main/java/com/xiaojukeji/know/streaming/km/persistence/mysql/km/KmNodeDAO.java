package com.xiaojukeji.know.streaming.km.persistence.mysql.km;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.km.KmNodePO;
import org.springframework.stereotype.Repository;

@Repository
public interface KmNodeDAO extends BaseMapper<KmNodePO> {
}
