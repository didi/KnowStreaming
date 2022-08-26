package com.xiaojukeji.know.streaming.km.persistence.mysql.kafkacontroller;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.kafkacontrollr.KafkaControllerPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KafkaControllerDAO extends BaseMapper<KafkaControllerPO> {
    List<KafkaControllerPO> listAllLatest();
}
