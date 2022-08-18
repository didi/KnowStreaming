package com.xiaojukeji.know.streaming.km.persistence.mysql.cluster;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.cluster.ClusterPhyPO;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterPhyDAO extends BaseMapper<ClusterPhyPO> {
    int addAndSetId(ClusterPhyPO clusterPhyPO);
}
