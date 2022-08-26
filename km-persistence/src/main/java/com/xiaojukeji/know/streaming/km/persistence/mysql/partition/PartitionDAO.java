package com.xiaojukeji.know.streaming.km.persistence.mysql.partition;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.partition.PartitionPO;
import org.springframework.stereotype.Repository;

@Repository
public interface PartitionDAO extends BaseMapper<PartitionPO> {
    int replace(PartitionPO po);
}
