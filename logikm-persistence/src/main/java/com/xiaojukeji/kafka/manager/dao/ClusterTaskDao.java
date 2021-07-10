package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterTaskDO;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/19
 */
public interface ClusterTaskDao {
    int insert(ClusterTaskDO clusterTaskDO);

    ClusterTaskDO getById(Long taskId);

    List<ClusterTaskDO> listAll();

    int updateTaskState(Long taskId, Integer taskStatus);

    int updateRollback(ClusterTaskDO clusterTaskDO);
}