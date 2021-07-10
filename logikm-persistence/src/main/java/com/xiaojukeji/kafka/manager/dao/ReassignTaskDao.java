package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;

import java.util.Date;
import java.util.List;

/**
 * migrate topic task dao
 * @author zengqiao_cn@163.com
 * @date 19/4/16
 */
public interface ReassignTaskDao {

    /**
     * 创建新的迁移任务
     * @param doList 迁移任务信息
     * @author zengqiao
     * @date 20/4/2
     * @return int
     */
    int batchCreate(List<ReassignTaskDO> doList);

    /**
     * 查询迁移任务
     * @param taskId 任务ID
     * @author zengqiao
     * @date 20/4/2
     * @return ReassignTaskDO
     */
    List<ReassignTaskDO> getByTaskId(Long taskId);

    ReassignTaskDO getSubTask(Long subTaskId);

    /**
     * 查询所有的迁移任务
     * @author zengqiao
     * @date 20/4/2
     * @return ReassignTaskDO
     */
    List<ReassignTaskDO> listAll();

    List<ReassignTaskDO> listAfterTime(Date gmtCreate);

    /**
     * 修改任务
     * @param reassignTaskDO 任务信息
     * @author zengqiao
     * @date 20/4/2
     * @return int
     */
    int updateById(ReassignTaskDO reassignTaskDO);

    /**
     * 批量修改
     * @param doList 任务
     * @author zengqiao
     * @date 20/6/11
     */
    void batchUpdate(List<ReassignTaskDO> doList);
}
