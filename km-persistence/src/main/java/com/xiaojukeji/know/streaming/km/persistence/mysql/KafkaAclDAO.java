package com.xiaojukeji.know.streaming.km.persistence.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaAclPO;
import org.springframework.stereotype.Repository;

@Repository
public interface KafkaAclDAO extends BaseMapper<KafkaAclPO> {
}
