package com.xiaojukeji.know.streaming.km.persistence.mysql.connect;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.WorkerConnectorPO;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerConnectorDAO extends BaseMapper<WorkerConnectorPO> {
}
