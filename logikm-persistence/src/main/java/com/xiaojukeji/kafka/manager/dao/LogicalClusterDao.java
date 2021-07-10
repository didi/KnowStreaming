package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/6/28
 */
public interface LogicalClusterDao {
    int insert(LogicalClusterDO logicalClusterDO);

    int deleteById(Long id);

    int updateById(LogicalClusterDO logicalClusterDO);

    LogicalClusterDO getById(Long id);

    List<LogicalClusterDO> getByClusterId(Long clusterId);

    List<LogicalClusterDO> listAll();
}
