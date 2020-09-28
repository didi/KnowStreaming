package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;

import java.util.List;

public interface ClusterDao {
    int insert(ClusterDO clusterDO);

    int deleteById(Long id);

    int updateById(ClusterDO clusterDO);

    ClusterDO getById(Long id);

    List<ClusterDO> list();

    List<ClusterDO> listAll();
}