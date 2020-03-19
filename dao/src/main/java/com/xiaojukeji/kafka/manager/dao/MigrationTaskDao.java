package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.po.MigrationTaskDO;

import java.util.List;

/**
 * migrate topic task dao
 * @author zengqiao_cn@163.com
 * @date 19/4/16
 */
public interface MigrationTaskDao {

    /**
     * 增加一个迁移任务
     */
    int addMigrationTask(MigrationTaskDO migrationTaskDO);

    /**
     * 查询迁移任务
     */
    MigrationTaskDO getById(Long id);

    /**
     * 查询所有的迁移任务
     */
    List<MigrationTaskDO> listAll();

    /**
     * 查询所有的迁移任务
     */
    List<MigrationTaskDO> getByStatus(Integer status);

    /**
     * 修改任务
     */
    int updateById(Long id, Integer status, Long throttle);
}
