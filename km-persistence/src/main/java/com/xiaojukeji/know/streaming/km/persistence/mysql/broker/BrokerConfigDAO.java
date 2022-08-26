package com.xiaojukeji.know.streaming.km.persistence.mysql.broker;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.broker.BrokerConfigPO;
import org.springframework.stereotype.Repository;

@Repository
public interface BrokerConfigDAO extends BaseMapper<BrokerConfigPO> {
    int replace(BrokerConfigPO po);
}
