package com.xiaojukeji.know.streaming.km.persistence.mysql.changerecord;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.changerecord.KafkaChangeRecordPO;
import org.springframework.stereotype.Repository;

@Repository
public interface KafkaChangeRecordDAO extends BaseMapper<KafkaChangeRecordPO> {
}
