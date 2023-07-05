package com.xiaojukeji.know.streaming.km.persistence.mysql.connect;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectClusterPO;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectClusterDAO extends BaseMapper<ConnectClusterPO> {
}
