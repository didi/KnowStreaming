package com.xiaojukeji.know.streaming.km.persistence.mysql.broker;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.broker.BrokerPO;
import org.springframework.stereotype.Repository;

@Repository
public interface BrokerDAO extends BaseMapper<BrokerPO> {
}
